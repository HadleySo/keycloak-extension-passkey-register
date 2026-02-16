package com.hadleyso.keycloak.keyprompt;

import java.util.ArrayList;
import java.util.List;
import org.keycloak.Config.Scope;
import org.keycloak.authentication.FormAction;
import org.keycloak.authentication.FormActionFactory;
import org.keycloak.authentication.FormContext;
import org.keycloak.authentication.ValidationContext;
import org.keycloak.authentication.forms.RegistrationPage;
import org.keycloak.events.Errors;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.WebAuthnCredentialModel;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.provider.ProviderConfigProperty;
import jakarta.ws.rs.core.MultivaluedMap;
import lombok.extern.jbosslog.JBossLog;
import org.jboss.logging.Logger;

@JBossLog
public class UserRegistrationPasskey implements FormAction, FormActionFactory {
    private static final Logger logger = Logger.getLogger(UserRegistrationPasskey.class);

    public static final String PROVIDER_ID = "ext-user-create-passkey-register";

    private static final List<ProviderConfigProperty> properties = new ArrayList<>();
    static {
        ProviderConfigProperty enableFallbackPassword = new ProviderConfigProperty();
        enableFallbackPassword.setName("enableFallbackPassword");
        enableFallbackPassword.setLabel("Enable password fallback");
        enableFallbackPassword.setHelpText("If enabled, users can set a password if their devices does not support Passkeys.");
        enableFallbackPassword.setType(ProviderConfigProperty.BOOLEAN_TYPE);

        properties.add(enableFallbackPassword);
    }

    private static AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
        AuthenticationExecutionModel.Requirement.REQUIRED,
        AuthenticationExecutionModel.Requirement.DISABLED
    };

    @Override
    public void close() {
    }

    @Override
    public FormAction create(KeycloakSession session) {
        return this;
    }

    @Override
    public void init(Scope config) {
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getDisplayType() {
        return "Passkey Registration";
    }

    @Override
    public String getReferenceCategory() {
        return WebAuthnCredentialModel.TYPE_PASSWORDLESS;
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override
    public String getHelpText() {
        return "Validates that the client device supports Passkeys. Store Passkey in user's credential store.";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return properties;
    }

    @Override
    public void buildPage(FormContext context, LoginFormsProvider form) {

        // Check if fallback enabled
        boolean enableFallbackPassword = false;
        if (context.getAuthenticatorConfig() != null) {
            String value = context.getAuthenticatorConfig().getConfig().get("enableFallbackPassword");
            enableFallbackPassword = Boolean.parseBoolean(value);
        }
        form.setAttribute("passkeyCompatibilityCheckRequired", true);
        form.setAttribute("enableFallbackPassword", enableFallbackPassword);

    }

    @Override
    public void validate(ValidationContext context) {

        // Check if fallback enabled
        boolean enableFallbackPassword = false;
        if (context.getAuthenticatorConfig() != null) {
            String value = context.getAuthenticatorConfig().getConfig().get("enableFallbackPassword");
            enableFallbackPassword = Boolean.parseBoolean(value);
        }

        // Get form data
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        String compatValue = formData.getFirst("passkeyCompatibilityCheck");

        List<FormMessage> errors = new ArrayList<>();

        // Compatibility check not run
        if (compatValue == null || compatValue.isBlank()) {
            context.error(Errors.INVALID_REGISTRATION);
            errors.add(new FormMessage(RegistrationPage.FIELD_USERNAME, "Your device is not compatible. A user-verifying platform authenticator like Touch ID, Face ID, or Windows Hello is required."));
            context.error(Errors.INVALID_REGISTRATION);
            context.validationError(formData, errors);
            return;
        }


        // NOT Compatible
        boolean passkeyCompatible = "yes".equalsIgnoreCase(compatValue);
        if (!passkeyCompatible) {
            logger.info("ext-user-create-passkey-register validate() Not passkey compatible");

            if (enableFallbackPassword) {
                context.getAuthenticationSession().setAuthNote("com-hadleyso-ext-user-create-passkey-register-method", "PASSWORD"); 
                context.success();
            } else {
                errors.add(new FormMessage(RegistrationPage.FIELD_USERNAME, "Your device is not compatible. A user-verifying platform authenticator like Touch ID, Face ID, or Windows Hello is required."));
                context.error(Errors.INVALID_REGISTRATION);
                context.validationError(formData, errors);
            }
            return;
        }


        // YES Compatible
        logger.info("ext-user-create-passkey-register validate() YES passkey compatible");
        boolean opedOut = formData.containsKey("passkeyOptOut") && formData.getFirst("passkeyOptOut").equals("opt-out");
        logger.info("ext-user-create-passkey-register validate() Opt Out: " + opedOut);
        logger.info("ext-user-create-passkey-register validate() Password Fall Back: " + enableFallbackPassword);

        if (opedOut) {
            if (enableFallbackPassword) {
                // Opt out & fallback enabled
                context.getAuthenticationSession().setAuthNote("com-hadleyso-ext-user-create-passkey-register-method", "PASSWORD"); 
                context.success();
            } else {
                errors.add(new FormMessage(RegistrationPage.FIELD_USERNAME, "Your device is not compatible. A user-verifying platform authenticator like Touch ID, Face ID, or Windows Hello is required."));
                context.error(Errors.INVALID_REGISTRATION);
                context.validationError(formData, errors);
            }
            return;
        }

        context.getAuthenticationSession().setAuthNote("com-hadleyso-ext-user-create-passkey-register-method", "PASSKEY"); 
        context.success();
        return;
    }

    @Override
    public void success(FormContext context) {
        String authMethod = context.getAuthenticationSession().getAuthNote("com-hadleyso-ext-user-create-passkey-register-method");
        logger.info("ext-user-create-passkey-register success() Opt Out: " + authMethod);
        if (authMethod == "PASSKEY") {
            context.getAuthenticationSession().addRequiredAction("webauthn-register-passwordless");   
        } else {
            context.getAuthenticationSession().addRequiredAction(UserModel.RequiredAction.UPDATE_PASSWORD);
        }  
    }

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {

    }
    
}

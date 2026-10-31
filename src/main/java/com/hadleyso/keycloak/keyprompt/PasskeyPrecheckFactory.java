package com.hadleyso.keycloak.keyprompt;

import java.util.ArrayList;
import java.util.List;

import org.keycloak.Config.Scope;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.AuthenticationExecutionModel.Requirement;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

public class PasskeyPrecheckFactory implements AuthenticatorFactory {

    public static final String PROVIDER_ID = "ext-passkey-precheck";

    private static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
        AuthenticationExecutionModel.Requirement.REQUIRED,
        AuthenticationExecutionModel.Requirement.DISABLED
    };

    @Override
    public void close() {
    }

    @Override
    public Authenticator create(KeycloakSession arg0) {
        return new PasskeyPrecheck();
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public void init(Scope arg0) {
    }

    @Override
    public void postInit(KeycloakSessionFactory arg0) {
    }

    @Override
    public String getDisplayType() {
        return "Precheck Passkey for Registering User";
    }

    @Override
    public String getReferenceCategory() {
        return null;
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        List<ProviderConfigProperty> configProperties = new ArrayList<>();

        ProviderConfigProperty failIncompatible = new ProviderConfigProperty();
        failIncompatible.setName("compatibility.require");
        failIncompatible.setLabel("Fail Incompatible");
        failIncompatible.setType(ProviderConfigProperty.BOOLEAN_TYPE);
        failIncompatible.setHelpText(
                "Fail registration when devices are incompatible.");
        failIncompatible.setDefaultValue(false);
        failIncompatible.setRequired(true);
        configProperties.add(failIncompatible);

        return configProperties;
    }

    @Override
    public String getHelpText() {
        return "If the device supports passkeys, ask if the user would like to setup passkeys. This can be set up before the username is selected";
    }
    
    
}
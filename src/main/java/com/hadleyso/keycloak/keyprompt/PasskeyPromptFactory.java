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

public class PasskeyPromptFactory implements AuthenticatorFactory {

    public static final String PROVIDER_ID = "ext-passkey-register-prompt";

    private static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
        AuthenticationExecutionModel.Requirement.REQUIRED,
        AuthenticationExecutionModel.Requirement.DISABLED
    };

    private static final List<ProviderConfigProperty> properties = new ArrayList<ProviderConfigProperty>(PromptUtils.configProperties);        


    @Override
    public void close() {
    }

    @Override
    public Authenticator create(KeycloakSession arg0) {
        return new PasskeyPrompt();
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
        return "Prompt User to Setup Passkey";
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
        
        return properties;
    }

    @Override
    public String getHelpText() {
        return "If the device supports passkeys and the user did not use a passkey, ask if the user would like to setup passkeys.";
    }
    
    
}
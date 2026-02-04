package com.hadleyso.keycloak.keyprompt;

import java.util.stream.Stream;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.PasswordCredentialModel;

import jakarta.ws.rs.core.MultivaluedMap;
import lombok.extern.jbosslog.JBossLog;


@JBossLog
public class PasskeyReset implements Authenticator {
    private static final Logger logger = Logger.getLogger(PasskeyReset.class);

    @Override
    public void close() {
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        log.info("PasskeyReset.action");

        // Check if client compatible with Passkeys
        MultivaluedMap<String, String> params = context.getHttpRequest().getDecodedFormParameters(); 
        String continueAction = params.getFirst("continueAction");
        if (!"setPasskey".equals(continueAction)) {
            context.failure(AuthenticationFlowError.GENERIC_AUTHENTICATION_ERROR);
        }

        UserModel user = context.getUser();

        if (logger.isTraceEnabled()) {
            logger.tracef("Removing all passwords and setting Passkey requirement - ", user.getId());
        }

        // Remove all password credentials
        Stream<CredentialModel> passwords = user.credentialManager().getStoredCredentialsByTypeStream(PasswordCredentialModel.TYPE);
        passwords.forEach(cred -> { user.credentialManager().removeStoredCredentialById(cred.getId());}); 
        
        // Require Passkey setup
        context.getAuthenticationSession().addRequiredAction("webauthn-register-passwordless");
        
        context.success();
        return;
    }

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        log.info("PasskeyReset.authenticate");

        // Remove required password change
        UserModel user = context.getUser();
        user.removeRequiredAction(UserModel.RequiredAction.UPDATE_PASSWORD);

        context.challenge(
            context.form()
                .setAttribute("includeSkip", "no")
                .setAttribute("includeDelay", "no")
                .createForm("passkey-prompt.ftl"));
    }

    @Override
    public boolean configuredFor(KeycloakSession arg0, RealmModel arg1, UserModel arg2) {
        return true;
    }

    @Override
    public boolean requiresUser() {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession arg0, RealmModel arg1, UserModel arg2) {
    }


    
}
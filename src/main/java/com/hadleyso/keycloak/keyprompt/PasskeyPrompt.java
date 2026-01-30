package com.hadleyso.keycloak.keyprompt;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
public class PasskeyPrompt implements Authenticator {
    private static final Logger logger = Logger.getLogger(PasskeyPrompt.class);

    @Override
    public void close() {
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        log.info("PasskeyPrompt.action");
        return;
    }

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        log.info("PasskeyPrompt.authenticate");
        if (logger.isTraceEnabled()) {
            logger.tracef("Flow not set up", context.toString());
        }
        context.failure(null);
    }

    @Override
    public boolean configuredFor(KeycloakSession arg0, RealmModel arg1, UserModel arg2) {
        return true;
    }

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public void setRequiredActions(KeycloakSession arg0, RealmModel arg1, UserModel arg2) {
    }


    
}
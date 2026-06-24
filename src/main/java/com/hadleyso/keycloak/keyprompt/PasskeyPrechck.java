package com.hadleyso.keycloak.keyprompt;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import lombok.extern.jbosslog.JBossLog;

import jakarta.ws.rs.core.MultivaluedMap;


@JBossLog
public class PasskeyPrechck implements Authenticator {

    @Override
    public void close() {
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        log.info("PasskeyPrechck.action");

        // Get response
        MultivaluedMap<String, String> params = context.getHttpRequest().getDecodedFormParameters(); 
        String continueAction = params.getFirst("continueAction");

        if ("noAction".equals(continueAction)) {
            context.getAuthenticationSession().addRequiredAction(UserModel.RequiredAction.UPDATE_PASSWORD);
            context.success();
        } else if ("setPasskey".equals(continueAction)){
            context.getAuthenticationSession().addRequiredAction("webauthn-register-passwordless");
            context.success();
        } 
        context.success();
        return;
    }

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        log.info("PasskeyPrechck.authenticate");

        String skipProperty = "yes";
        String delayProperty = "no";

        context.challenge(
            context.form()
                .setAttribute("includeSkip", skipProperty)
                .setAttribute("includeDelay", delayProperty)
                .createForm("passkey-prompt.ftl"));
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
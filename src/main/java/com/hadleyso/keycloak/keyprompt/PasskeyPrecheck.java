package com.hadleyso.keycloak.keyprompt;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import lombok.extern.jbosslog.JBossLog;

import jakarta.ws.rs.core.MultivaluedMap;


@JBossLog
public class PasskeyPrecheck implements Authenticator {

    @Override
    public void close() {
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        log.info("PasskeyPrecheck.action");

        AuthenticatorConfigModel config = context.getAuthenticatorConfig();
        boolean requireCompat = false;
        if (config != null) {
            if (config.getConfig().get("compatibility.require") != null) {
                requireCompat = Boolean.parseBoolean(config.getConfig().get("compatibility.require"));
            }
        }

        // Get response
        MultivaluedMap<String, String> params = context.getHttpRequest().getDecodedFormParameters(); 
        String continueAction = params.getFirst("continueAction");

        if ("noAction".equals(continueAction)) {
            if (requireCompat) {
                context.failure(
                    AuthenticationFlowError.GENERIC_AUTHENTICATION_ERROR,
                    null,
                    "CUSTOM_AUTH_ERROR",
                    "Device must support passkeys to continue."
                );
                return;
            }
            context.getAuthenticationSession().addRequiredAction(UserModel.RequiredAction.UPDATE_PASSWORD);
            context.success();
        } else if ("setPasskey".equals(continueAction)){
            context.getAuthenticationSession().addRequiredAction("webauthn-register-passwordless");
            context.success();
        } else if (requireCompat) {
            context.failure(
                AuthenticationFlowError.GENERIC_AUTHENTICATION_ERROR,
                null,
                "CUSTOM_AUTH_ERROR",
                "Device must support passkeys to continue."
            );
            return;
        }

        context.success();
        return;
    }

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        log.info("PasskeyPrecheck.authenticate");

        String skipProperty = "yes";
        String delayProperty = "no";

        AuthenticatorConfigModel config = context.getAuthenticatorConfig();
        boolean requireCompat = false;
        if (config != null) {
            if (config.getConfig().get("compatibility.require") != null) {
                requireCompat = Boolean.parseBoolean(config.getConfig().get("compatibility.require"));
            }
        }
        context.challenge(
            context.form()
                .setAttribute("includeSkip", skipProperty)
                .setAttribute("includeDelay", delayProperty)
                .setAttribute("requireCompat", requireCompat ? "yes" : "no")
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
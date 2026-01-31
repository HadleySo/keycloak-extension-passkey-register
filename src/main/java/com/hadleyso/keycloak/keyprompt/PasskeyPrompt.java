package com.hadleyso.keycloak.keyprompt;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Stream;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorUtil;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import lombok.extern.jbosslog.JBossLog;

import jakarta.ws.rs.core.MultivaluedMap;


@JBossLog
public class PasskeyPrompt implements Authenticator {
    private static final Logger logger = Logger.getLogger(PasskeyPrompt.class);

    @Override
    public void close() {
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        log.info("PasskeyPrompt.action");

        // Get config
        AuthenticatorConfigModel config = context.getAuthenticatorConfig();
        long daysDelay = 90;
        if (config != null) {
            if (config.getConfig().get("prompt.delay") != null) {
                daysDelay = Long.parseLong(config.getConfig().get("prompt.delay"));
            }
        }

        // Get response
        MultivaluedMap<String, String> params = context.getHttpRequest().getDecodedFormParameters(); 
        String continueAction = params.getFirst("continueAction");

        if ("noAction".equals(continueAction)) {
            context.success();
        } else if ("setPasskey".equals(continueAction)){
            context.getAuthenticationSession().addRequiredAction("webauthn-register-passwordless");
            context.success();
        } else if ("noNever".equals(continueAction)){
            UserModel user = context.getUser();
            
            String timestamp = DateTimeFormatter.ISO_INSTANT.format( Instant.now().plus(daysDelay, ChronoUnit.DAYS) );
            user.setSingleAttribute(PromptUtils.DELAY_PROMPT, timestamp);
            context.success();
        }
        context.success();
        return;
    }

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        log.info("PasskeyPrompt.authenticate");

        UserModel user = context.getUser();
        AuthenticatorConfigModel config = context.getAuthenticatorConfig();

        Stream<CredentialModel> passkeys = user.credentialManager().getStoredCredentialsByTypeStream("webauthn-passwordless");
        boolean hasPasskeys = passkeys.count() > 0;


        // If configured to skip if passkey set up, and user has passkey
        if (config != null) {
            if (Boolean.valueOf(config.getConfig().get("hasAnyPasskey.skip"))) {
                if (hasPasskeys) {
                    if (logger.isTraceEnabled()) {
                        logger.tracef("User has at least one passkey - ", user.getId());
                    }
                    context.success();
                    return;
                }
            }
        }

        // If Passkey was used
        List<String> authCredentials = AuthenticatorUtil.getAuthnCredentials(context.getAuthenticationSession());
        if (authCredentials.contains("webauthn-passwordless")) {
            if (logger.isTraceEnabled()) {
                logger.tracef("User used passkey - ", user.getId());
            }
            context.success();
            return;
        }


        // If user opted out        
        if (PromptUtils.promptDelayDisabled(context)) {
            context.success();
            if (logger.isTraceEnabled()) {
                logger.tracef("User has delay prompt - ", user.getId());
            }
            return;
        }

        if (logger.isTraceEnabled()) {
            logger.tracef("User is getting prompt - ", user.getId());
        }

        String skipProperty = "yes";
        String delayProperty = "yes";
        if (config != null) {
            if (config.getConfig().get("prompt.button.skip") != null) {
                skipProperty = Boolean.parseBoolean(config.getConfig().get("prompt.button.skip")) ? "yes" : "no";
            }
            if (config.getConfig().get("prompt.button.delay") != null) {
                delayProperty = Boolean.parseBoolean(config.getConfig().get("prompt.button.delay")) ? "yes" : "no";
            }

            if (delayProperty == "no" & skipProperty == "no") {
                skipProperty = "yes";
            }
        }

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
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession arg0, RealmModel arg1, UserModel arg2) {
    }


    
}
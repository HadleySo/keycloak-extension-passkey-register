package com.hadleyso.keycloak.keyprompt;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.keycloak.utils.StringUtil;

public class PromptUtils {
    public static final String DELAY_PROMPT = "COM-HADLEYSO-KEYCLOAK-KEYPROMPT-DELAY";
    public static final List<ProviderConfigProperty> configProperties = new ArrayList<ProviderConfigProperty>();

    static {
        ProviderConfigProperty delayProperty = new ProviderConfigProperty();
        delayProperty.setName("prompt.delay");
        delayProperty.setLabel("Re-prompt delay");
        delayProperty.setType(ProviderConfigProperty.INTEGER_TYPE);
        delayProperty.setHelpText(
                "Days to wait before asking when user selects 'don't ask again'.");
        delayProperty.setDefaultValue(90);
        delayProperty.setRequired(true);
        configProperties.add(delayProperty);

        ProviderConfigProperty skipProperty = new ProviderConfigProperty();
        skipProperty.setName("prompt.button.skip");
        skipProperty.setLabel("Enable skip for this login");
        skipProperty.setType(ProviderConfigProperty.BOOLEAN_TYPE);
        skipProperty.setHelpText(
                "Enable skipping of prompt just for this login. If enabled 'Not now' will be shown.");
        skipProperty.setDefaultValue(true);
        skipProperty.setRequired(true);
        configProperties.add(skipProperty);

        ProviderConfigProperty delayButtonProperty = new ProviderConfigProperty();
        delayButtonProperty.setName("prompt.button.delay");
        delayButtonProperty.setLabel("Enable delay");
        delayButtonProperty.setType(ProviderConfigProperty.BOOLEAN_TYPE);
        delayButtonProperty.setHelpText(
                "Enable delay of prompt for multiple days, as configured above. If enabled 'No, don't ask' will be shown.");
        delayButtonProperty.setDefaultValue(true);
        delayButtonProperty.setRequired(true);
        configProperties.add(delayButtonProperty);

        ProviderConfigProperty anyPasskeySkip = new ProviderConfigProperty();
        anyPasskeySkip.setName("hasAnyPasskey.skip");
        anyPasskeySkip.setLabel("Skip if user has Passkey");
        anyPasskeySkip.setType(ProviderConfigProperty.BOOLEAN_TYPE);
        anyPasskeySkip.setHelpText(
                "Don't prompt if the user has any Passkeys set up, even if a Passkey was not used to authenticate.");
        anyPasskeySkip.setDefaultValue(false);
        anyPasskeySkip.setRequired(true);
        configProperties.add(anyPasskeySkip);
    }

    public static boolean promptDelayDisabled(AuthenticationFlowContext context) {
        AuthenticationSessionModel authSession = context.getAuthenticationSession();
        String timeout = authSession.getAuthNote(DELAY_PROMPT);

        if (StringUtil.isNotBlank(timeout)) {
            ZonedDateTime maxTimestamp = ZonedDateTime.parse(timeout);
            return maxTimestamp.isBefore(ZonedDateTime.now());

        }

        return false;
    }


}

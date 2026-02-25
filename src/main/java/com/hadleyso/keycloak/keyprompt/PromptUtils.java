package com.hadleyso.keycloak.keyprompt;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.models.UserModel;
import org.keycloak.provider.ProviderConfigProperty;

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

    public static boolean skipPrompt(AuthenticationFlowContext context) {
        UserModel user = context.getUser();
        Stream<String> timeoutStream = user.getAttributeStream(DELAY_PROMPT);
        Optional<String> timeout = timeoutStream.findFirst();
        
        if (timeout.isPresent()) {
            ZonedDateTime maxTimestamp = ZonedDateTime.parse(timeout.get());
            return maxTimestamp.isAfter(ZonedDateTime.now());

        }

        return false;
    }


}

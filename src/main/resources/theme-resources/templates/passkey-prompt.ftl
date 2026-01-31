<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=true; section>
    <#if section = "title">
        ${msg("doPromptChecking")}
    <#elseif section = "header">
        ${msg("doPromptChecking")}
    <#elseif section = "form">

        <form id="com-hadleyso-passkey-register"
              class="${properties.kcFormClass!}"
              action="${url.loginAction}"
              style="display: none;"
              method="post">

            <div class="${properties.kcContentWrapperClass!}">
                <p>${msg("infoPromptPasskeyCompatible")}<p>
            </div>
            <div class="${properties.kcContentWrapperClass!}">
                <p>${msg("infoPromptPasskeySetup")}<p>
            </div>

            <div>
                <button type="submit"
                        name="continueAction"
                        value="setPasskey"
                        class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonLargeClass!}">
                    ${msg("doPromptEnroll")}
                </button>

                <button type="submit"
                        name="continueAction"
                        value="noAction"
                        id="com-hadleyso-passkey-register-noAction"
                        style='<#if includeSkip == "no">display: none;</#if>'
                        class="${properties.kcButtonClass!} ${properties.kcButtonDefaultClass!} ${properties.kcButtonSecondaryClass!} ${properties.kcButtonLargeClass!}">
                    ${msg("doPromptLater")}
                </button>

                <button type="submit"
                        name="continueAction"
                        value="noNever"
                        style='<#if includeDelay == "no">display: none;</#if>'
                        class="${properties.kcButtonClass!} ${properties.kcButtonDefaultClass!} ${properties.kcButtonSecondaryClass!} ${properties.kcButtonLargeClass!}">
                    ${msg("doPromptPasskeyNever")}
                </button>
            </div>

        </form>

        <script>
            PublicKeyCredential.isUserVerifyingPlatformAuthenticatorAvailable()
                .then((available) => {
                    if (available) {
                        // Trigger AIA
                        document.getElementById("com-hadleyso-passkey-register").style.display = "block";
                    } else {
                        // Respond ok and continue
                        document.getElementById("com-hadleyso-passkey-register-noAction").click();
                    }
                })
                .catch((err) => {
                    // Something went wrong
                    document.getElementById("com-hadleyso-passkey-register-noAction").click();
                });
        </script>

    </#if>
</@layout.registrationLayout>

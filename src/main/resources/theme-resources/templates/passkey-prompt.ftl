<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=true; section>
    <#if section = "title">
        ${msg("doPromptChecking")}
    <#elseif section = "header">
        ${msg("doPromptChecking")}
    <#elseif section = "form">

        <script>
            PublicKeyCredential.isUserVerifyingPlatformAuthenticatorAvailable()
                .then((available) => {
                    if (available) {
                        // Trigger AIA
                    } else {
                        // Respond ok and continue
                    }
                })
                .catch((err) => {
                    // Something went wrong
                    console.error(err);
                });
        </script>

    </#if>
</@layout.registrationLayout>

# Keycloak Extension - Encourage Passkey Enrollment  

 [![License: AGPL v3](https://img.shields.io/badge/License-AGPL_v3-blue.svg)](https://www.gnu.org/licenses/agpl-3.0)

As users authenticate, encourage them to set up Passkeys. This extension uses client side JavaScript to check if **the client device is Passkey compatible**, if so the user is asked if they want to set up a Passkey.

If the user authenticated with a Passkey, the prompt is not shown. This works for both `WebAuthn Passwordless Authenticator` execution and the built in `Passkey` button on the username password form.

Users also have the **option to dismiss the prompt** for a configured number of days. Or just skip it for this login. Both buttons can be configured to be hidden or shown.



<p align="center">
    <img src="docs/img/prompt-page.png" width="550">
</p>


## Compatibility

|               | **v0.1.x**         |
|---------------|--------------------|
| **KC 26.5.x** | :white_check_mark: |

:white_check_mark: - Compatible  
:heavy_minus_sign: - Patch only  


## Installation

1. Download the latest compatible release from the [releases page](https://github.com/HadleySo/keycloak-extension-passkey-register/releases)
2. Save the downloaded JAR file into the `providers/` directory inside Keycloak installation folder
3. Restart Keycloak 
4. Add `Prompt User to Setup Passkey` to the browser flow, after MFA or password forms

Conditional flow to check existing passkey usage is not needed.

## Configuration

**No configuration** is required.

**Re-prompt delay**:  
Days to wait before asking when user selects 'don't ask again'.

**Enable skip for this login**:  
Enable skipping of prompt just for this login. If enabled the 'Not now' button will be shown.

**Enable delay**:  
Enable delay of prompt for multiple days. If enabled the 'No, don't ask' button will be shown.

**Skip if user has Passkey**:  
Don't prompt if the user has any Passkeys set up, even if a Passkey was not used to authenticate.


## Templates
The [ftl templates](src/main/resources/theme-resources/templates) can be overridden. This is optional.

`passkey-prompt.ftl` - checks if the client has a user-verifying platform authenticator built into the client.
- Based on template.ftl from keycloak.v2 theme
- Requires a form submission with key `continueAction` and value:
    -  `setPasskey` - Enroll Passkey
    - `noAction` - Skip this time
    - `noNever` - Don't ask for 90 (or configured) days

#### Messages

Only English and German is provided, see [messages_en.properties](src/main/resources/theme-resources/messages/messages_en.properties) and [messages_de.properties](src/main/resources/theme-resources/messages/messages_de.properties).

## License  

Keycloak QR Code Authentication (keycloak-extension-passkey-register / com.hadleyso.keycloak.keyprompt) is distributed under [GNU Affero General Public License v3.0](https://www.gnu.org/licenses/agpl-3.0.txt). Copyright (c) 2026 Hadley So.


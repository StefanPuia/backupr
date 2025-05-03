---
title: "Azure Service Account"
summary: "Authenticate using client-secret OAuth credentials of a MS Entra application."
eleventyNavigation:
  key: Azure Service Account
  parent: Credentials
  order: 44
---

## Description

The `Azure Service Account` [credential](/getting-started/features/#credentials) is a credential type that uses
the [client credentials OAuth flow](https://learn.microsoft.com/en-us/entra/identity-platform/v2-oauth2-client-creds-grant-flow)
to authenticate to Azure resources.

<div class="alert alert--warning" role="alert">
  <div class="alert__caption">
    <p>The Service principal must have the relevant permissions to the Azure resources.</p>
  </div>
</div>

## Configuration

```json5
{
  "credentials": [
    {
      "name": "myAzSpCreds",
      "tenantId": "00000000-0000-0000-0000-000000000000",
      "clientId": "00000000-0000-0000-0000-000000000000",
      "clientSecret": "abc123"
    }
  ],
  "remotes": [
    {
      "name": "remote-with-inline-azsp-credentials",
      // ...
      "credentials": {
        "tenantId": "00000000-0000-0000-0000-000000000000",
        "clientId": "00000000-0000-0000-0000-000000000000",
        "clientSecret": "abc123"
      }
    },
    {
      "name": "remote-with-global-azsp-credentials",
      // ...
      "credentials": "myAzSpCreds"
    }
  ]
}
```

### Available configuration options

{% include 'partials/credentials/common-configuration.md' %}

#### tenantId

- required{.field-chip-required}
- template enabled{.field-chip-template}
  {.field-chips}

Specify the tenant ID to be used during authentication.

#### clientId

- required{.field-chip-required}
- template enabled{.field-chip-template}
  {.field-chips}

Specify the client (application) ID to be used during authentication.

#### clientSecret

- required{.field-chip-required}
- template enabled{.field-chip-template}
  {.field-chips}

Specify the client secret to be used during authentication.

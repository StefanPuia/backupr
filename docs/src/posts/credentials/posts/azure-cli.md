---
title: "Azure CLI"
summary: "Authenticate using the local installation of Azure CLI."
eleventyNavigation:
  key: Azure CLI
  parent: Credentials
  order: 43
---

## Description

The `Azure CLI` [credential](/getting-started/features/#credentials) is a credential type that uses a
locally-available [Azure CLI](https://learn.microsoft.com/en-us/cli/azure/) installation to authenticate to Azure
resources.

<div class="alert alert--warning" role="alert">
  <div class="alert__caption">
    <p>The Azure CLI logged-in user must have the relevant permissions to the Azure resources.
      <code>Storage Blob Data Owner</code> is recommended.</p>
  </div>
</div>

## Configuration

```json5
{
  "credentials": [
    {
      "name": "myAzCliCreds",
      "cli": true,
      "tenantId": "00000000-0000-0000-0000-000000000000"
    }
  ],
  "remotes": [
    {
      "name": "remote-with-inline-azcli-credentials",
      // ...
      "credentials": {
        "cli": true
      }
    },
    {
      "name": "remote-with-global-azcli-credentials",
      // ...
      "credentials": "myAzCliCreds"
    }
  ]
}
```

### Available configuration options

{% include 'partials/credentials/common-configuration.md' %}

#### cli

- required{.field-chip-required}
- constant{.field-chip-constant}
  {.field-chips}

This field is required due to a technical limitation of the JSON deserializer. The value should be `true`.

#### tenantId

- template enabled{.field-chip-template}
  {.field-chips}

Specify the tenant ID that should be used during authentication.

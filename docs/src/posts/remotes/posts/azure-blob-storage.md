---
title: "Azure Blob"
summary: "Upload the files to an Azure Blob Storage container."
eleventyNavigation:
  key: Azure Blob
  parent: Remotes
  order: 33
---

## Description

The `Azure Blob Storage` [remote](/getting-started/features/#remote)
uses [Azure Blob Storage](https://learn.microsoft.com/en-us/azure/storage/blobs/storage-blobs-introduction) to upload
the outputs of the
backup process to a storage container.

## Configuration

```json5
{
  "remotes": [
    {
      "name": "myRemote",
      "type": "AZURE_STORAGE_BLOB",
      "endpoint": "https://mybackupstorage.blob.core.windows.net/",
      "container": "backups",
      "overwrite": true,
      "blobPrefixPattern": "latest",
      // no credentials provided
      "cleanup": "my-cleanup"
    }
  ],
  "sources": [
    {
      "name": "source-with-inline-az-blob-remote",
      // ...
      "remotes": [
        {
          "name": "myRemote",
          "type": "AZURE_STORAGE_BLOB",
          "endpoint": "https://mybackupstorage.blob.core.windows.net/",
          "container": "backups",
          "credentials": "myAzureCreds"
        }
      ]
    },
    {
      "name": "source-with-global-az-blob-remote",
      // ...
      "remotes": [
        "myRemote"
      ]
    }
  ]
}

```

### Available configuration options

{% include 'partials/remotes/common-configuration-before.md' %}

#### type

- required{.field-chip-required}
- constant{.field-chip-constant}
  {.field-chips}

A constant that tells the engine what kind of remote this object is. This **must** be set to `"AZURE_STORAGE_BLOB"`.

#### endpoint

- required{.field-chip-required}
- template enabled{.field-chip-template}
  {.field-chips}

The storage account
blob [endpoint](https://learn.microsoft.com/en-us/azure/storage/common/storage-account-get-info?tabs=portal#get-service-endpoints-for-the-storage-account).

#### container

- required{.field-chip-required}
- template enabled{.field-chip-template}
  {.field-chips}

The container name where the backups should be placed.

#### overwrite

- default value{.field-chip-default}
  {.field-chips}

Whether to overwrite any existing blobs in the container when names clash. Defaults to `false`. If this is `false` and a
clash occurs, the process will fail.

#### blobPrefixPattern

- default value{.field-chip-default}
  {.field-chips}

The prefix for all blobs created during the same backup session. Supports templating with [context](#context). Defaults to
`"<context.sourceName>/<context.nowYear>/<context.nowMonth>/<context.nowDay>"`.

{% include 'partials/remotes/common-configuration-after.md' %}

{% include 'partials/remotes/common-configuration-creds.md' %}

{% include 'partials/remotes/common-configuration-cleanup.md' %}

### Valid credentials

#### None

When defining this remote, defining the [none](/credentials/none) credential, or not providing any credentials will
attempt to use
the [default azure credential strategy](https://learn.microsoft.com/en-us/java/api/com.azure.identity.defaultazurecredential?view=azure-java-stable).

#### Azure CLI

[Azure CLI](/credentials/azure-cli) credentials can be provided.

#### Azure Service Account

[Azure Service Account](/credentials/azure-service-account) credentials can be provided.

### Context

The field templating context extends the [default context](/getting-started/configuration/#default-context) with
additional values:

| Variable             | Description            |
|----------------------|------------------------|
| `context.sourceName` | Name of current source |

{.context-variable-table}

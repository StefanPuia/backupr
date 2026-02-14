---
title: "Local"
summary: "Copy backed up files to another location on the same machine."
eleventyNavigation:
  key: Local
  parent: Remotes
  order: 31
---

## Description

The `local` [remote](/getting-started/features/#remote) places the output of the backup process in a directory on the
current machine.

## Configuration

```json5
{
  "remotes": [
    {
      "name": "myRemote",
      "type": "LOCAL",
      "location": "/var/backups",
      "cleanup": "my-cleanup"
    }
  ],
  "sources": [
    {
      "name": "source-with-inline-local-remote",
      // ...
      "remotes": [
        {
          "type": "LOCAL",
          "location": "/var/backups"
        }
      ]
    },
    {
      "name": "source-with-global-local-remote",
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

A constant that tells the engine what kind of remote this object is. This **must** be set to `"LOCAL"`.

#### location

- required{.field-chip-required}
- template enabled{.field-chip-template}
  {.field-chips}

The target directory of the generated backups. This must be an absolute path.

{% include 'partials/remotes/common-configuration-after.md' %}

{% include 'partials/remotes/common-configuration-cleanup.md' %}

---
title: "Parameterized"
summary: "Controlled cleanup of old backups."
eleventyNavigation:
  key: Parameterized
  parent: Cleanup
  order: 52
---

## Description

The `parameterized` [cleanup](/getting-started/features/#cleanup) allows fine grained control over which backups are
deleted, based on the backup's metadata and the remote's state.

## Configuration

```json5
{
  "cleanup": [
    {
      "name": "myBasicCleanup",
      "keepCount": 5,
      "keepDays": 30
    }
  ],
  "remotes": [
    {
      "name": "remote-with-inline-cleanup",
      // ...
      "cleanup": {
        "keepCount": 5,
      }
    },
    {
      "name": "remote-with-inline-cleanup-2",
      // ...
      "cleanup": {
        "keepDays": 20,
      }
    },
    {
      "name": "remote-with-global-cleanup",
      // ...
      "cleanup": "myBasicCleanup"
    }
  ]
}
```

### Available configuration options

{% include 'partials/cleanup/common-configuration.md' %}

#### keepCount

- required{.field-chip-required}
  {.field-chips}

The number of backups to keep. This option takes precedence over `keepDays`.

#### keepDays

- required{.field-chip-required}
  {.field-chips}

The minimum age of a backup to keep. Anything older will be deleted.

---
title: "Keep Everything"
summary: "Does not remove any old backups."
hideInList: false
eleventyNavigation:
  key: Keep Everything
  parent: Cleanup
  order: 51
---

## Description

The `keep-all` [cleanup rule](/getting-started/features/#cleanup) is a rule used implicitly when no
other cleanup type is provided. When this rule is used, no backups will be removed from the remote.

It can also be specifically defined using an empty json object: `{}`.

## Configuration

```json5
{
  "cleanup": [
    {
      "name": "myKeepAllRule",
    }
  ],
  "remotes": [
    {
      // will use the none cleanup implicitly
      "name": "remote-without-cleanup",
      // ...
      // "cleanups" is not defined
    },
    {
      // will resolve as the none cleanup
      "name": "remote-with-inline-none-cleanups",
      // ...
      "cleanup": {}
    },
    {
      // will resolve as the global myKeepAllRule cleanup
      "name": "remote-with-global-none-cleanups",
      // ...
      "cleanup": "myKeepAllRule"
    }
  ]
}
```

### Available configuration options

{% include 'partials/cleanup/common-configuration.md' %}

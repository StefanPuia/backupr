---
title: "None"
hideInList: true
eleventyNavigation:
  key: None
  parent: Credentials
  order: 41
---

## Description

The `none` [credential](/getting-started/features/#credentials) is a credential type that is used implicitly when no
other credential type is provided. It can also be specifically defined using an empty json object: `{}`.

## Configuration

```json5
{
  "credentials": [
    {
      "name": "myNoneCred"
    }
  ],
  "remotes": [
    {
      // will use the none credential implicitly
      "name": "remote-without-credentials",
      // ...
      // "credentials" is not defined
    },
    {
      // will resolve as the none credential
      "name": "remote-with-inline-none-credentials",
      // ...
      "credentials": {}
    },
    {
      // will resolve as the global myNoneCred credential
      "name": "remote-with-global-none-credentials",
      // ...
      "credentials": "myNoneCred"
    }
  ]
}
```

### Available configuration options

{% include 'partials/credentials/common-configuration.md' %}

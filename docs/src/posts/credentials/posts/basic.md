---
title: "Basic"
summary: "Basic authentication using a username and an optional password."
eleventyNavigation:
  key: Basic
  parent: Credentials
  order: 42
---

## Description

The `basic` [credential](/getting-started/features/#credentials) is a credential type that uses a username and,
optionally, a password.

## Configuration

```json5
{
  "credentials": [
    {
      "name": "myBasicCred",
      "username": "foo",
      "password": "bar"
    }
  ],
  "remotes": [
    {
      "name": "remote-with-inline-basic-credentials",
      // ...
      "credentials": {
        "username": "username-only-resource",
      }
    },
    {
      "name": "remote-with-global-basic-credentials",
      // ...
      "credentials": "myBasicCred"
    }
  ]
}
```

### Available configuration options

{% include 'partials/credentials/common-configuration.md' %}

#### username

- required{.field-chip-required}
- template enabled{.field-chip-template}
  {.field-chips}

The basic auth username.

#### password

- template enabled{.field-chip-template}
  {.field-chips}

The basic auth password.

---
title: "Git"
summary: "Use a git repository to back up the files."
eleventyNavigation:
  key: Git
  parent: Remotes
  order: 32
---

## Description

The `git` [remote](/getting-started/features/#remote) uses [git](https://git-scm.com/) to publish the outputs of the
backup process.

This handler will fetch the remote shallowly on the specified branch, add all files (respecting the original directory
structure) commit, then push them to the remote.

## Configuration

```json5
{
  "remotes": [
    {
      "name": "myRemote",
      "type": "GIT",
      "url": "https://github.com/foo/bar.git",
      "branch": "main",
      "credentials": {
        "username": "bob",
        "password": "Password123"
      }
    }
  ],
  "sources": [
    {
      "name": "source-with-inline-git-remote",
      // ...
      "remotes": [
        {
          "name": "myRemote",
          "type": "GIT",
          "url": "git@github.com:foo/bar.git",
          "branch": "main",
          // no credentials provided
        }
      ]
    },
    {
      "name": "source-with-global-git-remote",
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

A constant that tells the engine what kind of remote this object is. This **must** be set to `"GIT"`.

#### url

- required{.field-chip-required}
- template enabled{.field-chip-template}
  {.field-chips}

The git remote URL. This can be either HTTP or SSH.

#### branch

- required{.field-chip-required}
- template enabled{.field-chip-template}
  {.field-chips}

The repository branch to be used for backup.

{% include 'partials/remotes/common-configuration-after.md' %}

{% include 'partials/remotes/common-configuration-creds.md' %}

### Valid credentials

#### None

When defining this remote, defining the [none](/credentials/none) credential, or not providing any credentials will
attempt to use the [default git behaviour](https://git-scm.com/docs/gitcredentials).

#### Basic

[Basic](/credentials/basic) credentials can be provided.

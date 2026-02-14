---
title: "Docker Copy Source"
summary: "Use 'docker cp' to source files that are inside a running or stopped container."
eleventyNavigation:
  key: Docker Copy Source
  parent: Sources
  order: 12
---

## Description

The `docker copy` [source](/getting-started/features/#source) uses the [
`docker cp`](https://docs.docker.com/reference/cli/docker/container/cp/) command to source files that are inside a
running or stopped container.

## Configuration

```json5
{
  "sources": {
    "name": "my-docker-cp-source",
    "type": "DOCKER_CP",
    "disabled": false,
    "container": "compassionate_darwin",
    "paths": [
      "var/config/",
      "var/data/database.db"
    ],
    "remotes": [
      "my-remote",
      {
        // ...
      }
    ],
    "transformers": [
      // ...
    ],
    "cleanup": "my-cleanup"
  }
}

```

### Available configuration options

{% include 'partials/sources/common-configuration-before.md' %}

#### type

- required{.field-chip-required}
- constant{.field-chip-constant}
  {.field-chips}

A constant that tells the engine what kind of source this object is. This **must** be set to `"DOCKER_CP"`.

#### container

- required{.field-chip-required}
- template enabled{.field-chip-template}
  {.field-chips}

A string that can identify a container (name or partial container ID). Sourcing will stop if a container cannot be
found.

#### paths

- required (min 1 item){.field-chip-required}
- template enabled{.field-chip-template}
  {.field-chips}

An array of paths, relative to the root directory of the container, that should be used to match any files to be backed
up.

#### allowNotFoundPaths

- default value{.field-chip-default}
  {.field-chips}

Skip paths that do not exist in the container. Sourcing will stop if set to `false` and path does not exist. Defaults to
`false`.

{% include 'partials/sources/common-configuration-after.md' %}

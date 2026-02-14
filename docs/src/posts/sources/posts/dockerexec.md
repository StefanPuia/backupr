---
title: "Docker Exec Source"
summary: "Use 'docker cp' to source files that are inside a running or stopped container."
eleventyNavigation:
  key: Docker Exec Source
  parent: Sources
  order: 13
---

## Description

The `docker exec` [source](/getting-started/features/#source) uses the [
`docker exec`](https://docs.docker.com/reference/cli/docker/container/exec/) command to execute one or more commands and
capture the standard output as a file to be backed up.

## Configuration

```json5
{
  "sources": {
    "name": "my-docker-exec-source",
    "type": "DOCKER_EXEC",
    "disabled": false,
    "container": "compassionate_darwin",
    "commands": [
      {
        "file": "out.sql",
        "command": [
          "sh",
          "-c",
          "mysqldump -u root -p$MYSQL_ROOT_PASSWORD --all-databases 2>/dev/null"
        ]
      }
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

A constant that tells the engine what kind of source this object is. This **must** be set to `"DOCKER_EXEC"`.

#### container

- required{.field-chip-required}
- template enabled{.field-chip-template}
  {.field-chips}

A string that can identify a container (name or partial container ID). Sourcing will stop if a container cannot be
found.

#### commands

- required (min 1 item){.field-chip-required}
  {.field-chips}

An array of commands to be run using `docker exec`.

#### commands[].file

- required{.field-chip-required}
- template enabled{.field-chip-template}
  {.field-chips}

The output file path of a respective command. The path can contain multiple directory levels.

#### commands[].command

- required (min 1 item){.field-chip-required}
- template enabled{.field-chip-template}
  {.field-chips}

An array of command and arguments to be passed to `docker exec`.

{% include 'partials/sources/common-configuration-after.md' %}

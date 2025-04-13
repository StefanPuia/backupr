---
title: "tar.gz"
summary: "Archiving transformer using tar.gz compression."
eleventyNavigation:
  key: tar.gz
  parent: Transformers
  order: 22
---

## Description

The `.tar.gz` [transformer](/getting-started/features/#transformer) is a simple archiving transformer that will add all
source files to a single .tar.gz archive file.

_The original directory structure of the source files will be preserved inside the archive._

## Configuration

### Available configuration options

```json5
{
  "sources": {
    "name": "some-source",
    // ...
    "transformers": [
      {
        "targz": {
          "filenamePattern": "<context.sourceName>-<context.now>.tar.gz"
        }
      }
    ]
  }
}

```

#### filenamePattern

- default value{.field-chip-default}
- template enabled{.field-chip-template}
  {.field-chips}

Pattern to be used as filename for the created tar.gz file. Supports templating with [context](#context). Defaults to
`"<context.sourceName>-<context.now>.tar.gz"`.

### Default configuration

```json5
{
  "sources": {
    "name": "some-source",
    // ...
    "transformers": [
      "TARGZ"
    ]
  }
}

```

Using just the `"TARGZ"` constant in the `"transformers"` array will use the default transformer configuration
(shown [above](#available-configuration-options)).

### Context

The field templating context extends the [default context](/getting-started/configuration/#default-context) with
additional values:

| Variable             | Description            |
|----------------------|------------------------|
| `context.sourceName` | Name of current source |

{.context-variable-table}

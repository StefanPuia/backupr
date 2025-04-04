---
title: "zip"
summary: ""
eleventyNavigation:
  key: zip
  parent: Transformers
  order: 1
---

## The .zip transformer

The .zip [transformer](/getting-started/features/#transformer) is a simple archiving transformer that will add all
source files to a single .zip archive file.

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
        "zip": {
          "filenamePattern": "<context.sourceName>-<context.now>.zip"
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

Pattern to be used as filename for the created zip file. Supports templating with [context](#context). Defaults to
`"<context.sourceName>-<context.now>.zip"`.

### Default configuration

```json5
{
  "sources": {
    "name": "some-source",
    // ...
    "transformers": [
      "ZIP"
    ]
  }
}

```

Using just the `"ZIP"` constant in the `"transformers"` array will use the default transformer configuration
(shown [above](#available-configuration-options)).

### Context

The field templating context extends the [default context](/getting-started/configuration/#default-context) with
additional values:

| Variable             | Description            |
|----------------------|------------------------|
| `context.sourceName` | Name of current source |

{.context-variable-table}

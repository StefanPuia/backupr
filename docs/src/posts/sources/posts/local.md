---
title: "Local Source"
summary: "A source type that uses the current machine's file system to look up files for backing up."
eleventyNavigation:
  key: Local Source
  parent: Sources
  order: 11
---

## Description

The `local` [source](/getting-started/features/#source) is a source type that uses the current machine's file system to
look up files for backing up.

## Configuration

```json5
{
  "sources": {
    "name": "some-source",
    "type": "LOCAL",
    "disabled": false,
    "directory": "/var/foo",
    "files": [
      "**/*.json",
      "bar/*.txt"
    ],
    "remotes": [
      "my-remote",
      {
        "type": "GIT",
        //..
      }
    ],
    "transformers": [
      "ZIP"
    ]
  }
}

```

### Available configuration options

{% include 'partials/sources/common-configuration-before.md' %}

#### type

- required{.field-chip-required}
- constant{.field-chip-constant}
  {.field-chips}

A constant that tells the engine what kind of source this object is. This **must** be set to `"LOCAL"`.

#### directory

- required{.field-chip-required}
- template enabled{.field-chip-template}
  {.field-chips}

The base directory for the engine to start looking for files to back up. This must be an absolute path.

#### files

- required{.field-chip-required}
- template enabled{.field-chip-template}
  {.field-chips}

An array of file patterns, relative to [directory](#directory), that should be used to match any files to be backed up.

Available matchers, given the following file structure:

```html
[directory, specified above]/
├─ dir-a/
│  ├─ a-file-1.txt
├─ dir-b/
├─ dir-c/
│  ├─ c-dir1/
│  │  ├─ c1-dir-1/
│  │  │  ├─ c11-file-1.txt
│  ├─ c-dir2/
│  ├─ c-dir3/
│  ├─ c-file-1.txt
│  ├─ c-file-2.json
│  ├─ c-file-3.sh
├─ root-file-a.txt
├─ root-file-b.json
├─ root-file-c.sh

```

| Matcher             | Files matched                                                    |
|---------------------|------------------------------------------------------------------|
| `**`                | all files                                                        |
| `**/*.txt`          | `a-file-1.txt` `c11-file-1.txt` `c-file-1.txt` `root-file-a.txt` |
| `dir-a/**`          | `a-file-1.txt`                                                   |
| `dir-c/**`          | `c11-file-1.txt` `c-file-1.txt` `c-file-2.json` `c-file-3.sh`    |
| `dir-c/c-file-3.sh` | `c-file-3.sh`                                                    |
| `dir-b/**`          | nothing                                                          |
| `not-a-file.any`    | nothing                                                          |

{.table .table--striped}

{% include 'partials/sources/common-configuration-after.md' %}

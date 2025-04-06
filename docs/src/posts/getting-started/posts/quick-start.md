---
title: "Quick Start"
summary: "Get started using Backupr straight away."
eleventyNavigation:
  key: Quick Start
  parent: Getting Started
  order: 2
---

To get started straight away, follow these steps:

## Download

Head to the [releases](https://github.com/StefanPuia/backupr/releases/latest){target="_blank" rel="noopener"} and
download the latest version of the executable for your operating system.

## Configure

Create a `.backupr.jsonc` [configuration](/getting-started/configuration) file in your _home directory_ with the
following contents, making sure to replace the `[[placeholders]]`:

```json5
{
  "$schema": "https://backupr.stefanpuia.co.uk/backupr.schema.json",
  "sources": [
    {
      "name": "quickstart",
      "type": "LOCAL",
      "directory": "[[enter a directory to back up]]",
      "files": [
        "**"
      ],
      "transformers": [
        "ZIP"
      ],
      "remotes": [
        {
          type: "LOCAL",
          location: "[[enter a target directory for your backup]]"
        }
      ]
    }
  ]
}
```

## Create a backup

```shell
backupr backup --verbose
```

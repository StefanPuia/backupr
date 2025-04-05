---
title: "Configuration"
summary: "The basic structure of the configuration file."
eleventyNavigation:
  key: Configuration
  parent: Getting Started
  order: 4
---

## JSON Schema

The Backupr configuration is expected in a JSON (or JSONC) format. The schema is available [here](/backupr.schema.json).

## File locations

The configuration can be provided through one of the following methods. The default lookup order is

1. Command flag
2. Environment variable
3. Default location

### Default location

By not specifying any location, Backupr will use `{user_home}/.backupr.jsonc` as the default configuration.

### Environment variable

The `BACKUPR_CONFIG_LOCATION` environment variable can be provided to overwrite the default location.

### Command flag

The configuration location can also be sent via the `--config` flag. This will overwrite any other locations. More
information in [CLI Reference](/getting-started/cli-reference).

## Identifier

An identifier is a string (usually used as a name for one of the configuration elements) that has to follow a specific
pattern:

```regexp
^[0-9a-zA-Z\-_.]+$
```

This is due to the fact that these values will end up being used in filenames and other references, so it is required
that they are safe values.

There is no limit on the length of this field, but be aware that long filenames may cause issues due
to [Windows Maximum Path Length Limitation](https://learn.microsoft.com/en-us/windows/win32/fileio/maximum-file-path-limitation?tabs=registry).

## Templating

### Template fields

Certain fields, marked with `template enabled`{.field-chip-template}, can have parts of the value (or the whole thing)
replaced when the configuration is read.

```json5
{
  "sources": [
    {
      "name": "my-source",
      "templateEnabledField1": "Normal text <myVariable> <env.SOME_ENVIRONMENT_VARIABLE> <context.valueRelatedToCurrentStep>"
    }
  ]
}
```

### Variables

A root-level `"variables"` configuration object can be created. This is a single-level, string key to string value map
that allows setting global values which can be reused in template enabled fields.

```json5
{
  "variables": {
    "myVar1": "some value to be reused",
    //...
  }
}
```

Variables are available without a prefix, but `env` and `context` are reserved words, and variables named either of
those will be overwritten.

### Environment variables

All environment variables available when the engine starts, are available via the `env` template object (`<env.foo>`).
This is
case-sensitive, so, depending on the operating system, `<env.my_var>` and `<env.MY_VAR>` may have different values.

### Context

During specific tasks, a `context` object will be available (`<context.foo>`). These are described in each component,
where relevant (e.g.: [zip transformer](/transformers/zip/#context)). Wherever the context is enabled, a set of default
values are also available:

#### Default context

| Variable            | Example value       | Description                  |
|---------------------|---------------------|------------------------------|
| `context.now`       | 2025-03-24-15-09-55 | The current date and time    |
| `context.nowDate`   | 2025-03-24          | The current date             |
| `context.nowTime`   | 15-09-55            | The current time             |
| `context.nowYear`   | 2025                | The current year             |
| `context.nowMonth`  | 03                  | The current month            |
| `context.nowDay`    | 24                  | The current day of the month |
| `context.nowHour`   | 15                  | The current hour             |
| `context.nowMinute` | 09                  | The current minute           |
| `context.nowSecond` | 55                  | The current second           |

{.context-variable-table}

_* all temporal values are in local time_

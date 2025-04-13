---
title: "CLI Reference"
summary: "List of all available CLI commands and flags."
eleventyNavigation:
  key: CLI Reference
  parent: Getting Started
  order: 5
---

## backup

Start the backup process.

### Usage

```shell
backupr backup [OPTIONS]
```

#### dry

Does not perform persistent steps in the backup process. It may, however, authenticate or connect to certain remotes, as
well as create temporary files.

Alias: `-d`

```shell
backupr backup --dry
```

#### config

Used to specify a different [configuration](/getting-started/configuration) file location.

Alias: `-c`

```shell
backupr backup --config "/custom/config/location.json"
```

#### verbose

Prints out steps of the process. Helpful to understand and debug the backup contents and process.

Alias: `-v`

```shell
backupr backup --verbose
```

#### help

Prints out the manual for this command.

Alias: `-h`

```shell
backupr backup --help
```

## validate

Validates a configuration file.

### Usage

```shell
backupr validate [OPTIONS]
```

#### config

Used to specify a different [configuration](/getting-started/configuration) file location.

Alias: `-c`

```shell
backupr validate --config "/custom/config/location.json"
```

#### verbose

Prints out steps of the process.

Alias: `-v`

```shell
backupr validate --verbose
```

#### help

Prints out the manual for this command.

Alias: `-h`

```shell
backupr validate --help
```

## schema

Prints the [JSON schema](/getting-started/configuration/#json-schema) to the console. Useful if you can't use the online
version.

### Usage

```shell
backupr schema
```

#### help

Prints out the manual for this command.

Alias: `-h`

```shell
backupr schema --help
```

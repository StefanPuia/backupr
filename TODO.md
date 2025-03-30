## List of things to implement

# Docs

* create docs layout
    * md - 11ty?

# Tech debt

* pipeline
* remotes config parser coverage
* azure blob config parsing test coverage

# Config

* placeholders in config e.g. user home - this may already be possible with env vars
* handle com.fasterxml.jackson.databind.exc exceptions - may need a custom exception to string converter
* validate correct type of credentials are used with remotes (e.g. only azure creds for azure remote)

# CLI

* default command - run help?
* generate config - later
* create/instruct scheduled task
* generate schema to file

# Engine

* do not fail fast on remote handling - or add to config how fast to fail
* notifications on success/failures?
    * webhook?

# Credentials

# Sources

* better file filtering
* more sources:
    * databases?

# Transformers

## ZIP

## TARGZ

## 7ZIP

* implement

# Remotes

* implement aws?
* implement google storage?
* delete old backups?

# Bugs

* file patterns without a directory will scan recursively / feature?

# Future

* restore backup

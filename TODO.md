## List of things to implement

# Config

* placeholders in config e.g. user home - this may already be possible with env vars
* handle com.fasterxml.jackson.databind.exc exceptions - may need a custom exception to string converter

# CLI

* default command - run help?
* generate config - later
* create/instruct scheduled task

# Engine

* do not fail fast on remote handling - or add to config how fast to fail
* notifications on success/failures?
    * webhook?

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

* implement azure blob
* implement aws?
* implement google storage?
* delete old backups?

# Bugs

* file patterns without a directory will scan recursively / feature?

# Future

* restore backup

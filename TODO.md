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

# Sources

* better file filtering
* more sources:
  * databases?

# Transformers

* add more data? - i.e. naming pattern, etc

## ZIP

*

## TAR

* implement
  * https://commons.apache.org/proper/commons-compress/
  * https://mkyong.com/java/how-to-create-tar-gz-in-java/

## 7Zip

* implement

# Remotes

* implement git
* implement azure blob
* implement aws?
* implement google storage?
* delete old backups?

# Bugs

* file patterns without a directory will scan recursively / feature?

# Future

* restore backup

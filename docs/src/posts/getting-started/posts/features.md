---
title: "Features"
summary: "Components of the Backupr engine and backup process."
eleventyNavigation:
  key: Features
  parent: Getting Started
  order: 3
---

## Source

Sources are places where Backupr will look for things to back up. They can reference globally defined remotes via their
identifier, or can have bespoke, inline remotes specified.

Sources are handled in sequence, in the order they are defined in the [configuration](/getting-started/configuration/).

[List of available sources](/sources).

## Transformer

Transformers are utilities that convert the output of a source (or other transformers), before the sources are sent to
the remote.

These are defined in each source, as required, and are executed in the order they are defined in.

If multiple transformers are defined for the same source, the output of each former will be passed to the latter:

```json5
{
  "sources": [
    {
      "name": "local",
      "type": "LOCAL",
      //...
      "transformers": [
        "TARGZ",
        "ZIP"
      ]
    }
  ]
}
```

In the example above, assuming the `local` source outputs a single, `my-file.txt` file, the resulting file will be a
`.zip` archive with the following structure:

```html
generated-name.zip
├─ generated-name.tar.gz
│  ├─ my-file.txt

```

[List of available transformers](/transformers).

## Remote

Remotes are locations or services where sources can be uploaded to. These can be internet resources (e.g.: git, storage
buckets, etc.), or [local](/remotes/local) to the machine Backupr is running on.

Some of these resources will require authentication. To do this, either a reference to a globally-defined credential
identifier can be used, or one can be provided inline.

[List of available remotes](/remotes).

## Credentials

Credentials are ways to authenticate to a remote. Due to the sensitive nature of their contents, it is recommended
using [environment variable templates](/getting-started/configuration/#environment-variables), as well as protecting the
configuration file using file permissions and/or placing it in a safe location.

[List of available credentials](/credentials).

## Cleanup

Cleanup rules are used to remove old backups from remotes. These can be defined for each remote and each source
independently.

<div class="alert alert--danger" role="alert">
  <div class="alert__caption">
    <p>Cleanup is only available when a backup <a href="/getting-started/state-management">state</a> is configured.
      <br/>
      Not all remotes support cleanup.</p>
  </div>
</div>

[List of available cleanup rules](/cleanup).

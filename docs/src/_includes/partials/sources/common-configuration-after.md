#### disabled

- default value{.field-chip-default}
  {.field-chips}

Whether to ignore this source during the backup process. Defaults to `false`.

#### remotes

- default value{.field-chip-default}
  {.field-chips}

An array of [remote](/getting-started/features/#remote) [identifiers](/getting-started/configuration/#identifier), or
inline configurations. If using an identifier, a remote with the respective name must exist in the root configuration.
Defaults to no remotes.

See [remotes](/remotes) for a list of available remotes.

#### transformers

- default value{.field-chip-default}
  {.field-chips}

An array of [transformer](/getting-started/features/#transformer) types, or inline configurations. Transformers will be
executed in the order they are defined in the array. Defaults to no transformers.

See [transformers](/transformers) for a list of available transformers.

---
title: "State Management"
summary: "How Backupr manages its state between runs"
eleventyNavigation:
  key: State Management
  parent: Getting Started
  order: 6
---

Some features of Backupr are stateful (e.g.: [cleaning up old backups](/getting-started/features/#cleanup)).

To manage state between runs, Backupr uses a state file stored using a [remote](/remotes) configuration. Configuring
this is optional, however some features may not work without it.

## Configuration

```json5
{
  "state": {
    "remote": {
      "type": "LOCAL",
      "location": "/foo/bar"
    }
  }
}

```

```json5
{
  // or reference using a named remote
  "state": {
    "remote": "otherRemote"
  },
  "remotes": [
    {
      "name": "otherRemote",
      // ...
    }
  ]
}
```

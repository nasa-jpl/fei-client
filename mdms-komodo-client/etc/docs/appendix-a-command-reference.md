# Appendix A — FEI Command Reference

> Back to: [User's Guide](users-guide.md)

This appendix provides a concise reference for all FEI client command-line utilities. All commands are located in `$FEI5/bin/` and require the `$FEI5` environment variable to be set.

FEI commands use **bare keyword tokens** — there are no dash-prefixed flags. Append `help` to any command to display its usage.

---

## Common Keywords

The following keywords are accepted by most file operation commands:

| Keyword | Description |
|---|---|
| `user <name>` | FEI username (overrides stored credentials) |
| `password <pass>` | FEI password or encrypted token (overrides stored credentials) |
| `before <datetime>` | Filter: only files added/modified before this date |
| `after <datetime>` | Filter: only files added/modified after this date |
| `between <dt1> and <dt2>` | Filter: only files in this date range |
| `format '<fmt>'` | Date format string for date filters |
| `crc` | Enable CRC checksum verification |
| `receipt` | Request a delivery receipt from the server |
| `filehandler` | Invoke a configured file handler |
| `help` | Display command usage |

**Date/time format:**
```
YYYY-MM-DD HH:MM:SS
```
Example: `2024-06-15 00:00:00`

---

## Authentication Commands

### fei5kinit

Log in to an FEI server group and store an encrypted credential token in `~/.komodo/login`.

```
fei5kinit [<username> [<server group>]]
```

Both arguments are optional; omitted values are prompted interactively. Run once per server group before using any file operation commands.

---

### fei5kdestroy

Remove stored credentials for a server group.

```
fei5kdestroy [<server group>]
```

If no server group is given, removes all stored credentials.

---

### fei5klist

List stored credential tokens and their expiry dates.

```
fei5klist
```

---

### fei5changepassword

Change your password on an FEI server group. Prompts for current and new passwords.

```
fei5changepassword <server group>
```

---

## File Operation Commands

### fei5add

Add files to an FEI file type.

```
fei5add [servergroup:]filetype <file expression>
        [before|after <datetime>] | [between <datetime1> and <datetime2>]
        [format '<date format>'] [comment '<comment text>']
        [crc] [receipt] [autodelete] [filehandler] [help]

fei5add using <option file>
```

| Keyword | Description |
|---|---|
| `comment '<text>'` | Attach a comment to the added file(s) |
| `autodelete` | Delete the local file after successful add |
| `crc` | Compute and store CRC checksum |
| `receipt` | Request delivery receipt |
| `before`/`after`/`between...and` | Filter by file modification date |

---

### fei5get

Retrieve files from an FEI file type.

```
fei5get [servergroup:]filetype ['<file expression>']
        [output <path>]
        [before|after <datetime>] | [between <datetime1> and <datetime2>]
        [format '<date format>'] [crc] [saferead] [receipt]
        [replace|version] [diff] [query <queryfile>]
        [replicate] [replicateroot <rootdir>] [filehandler] [help]

fei5get using <option file>
```

| Keyword | Description |
|---|---|
| `output <path>` | Local directory for downloaded files |
| `replace` | Overwrite local file if it exists |
| `version` | Download into a versioned filename |
| `saferead` | Lock file on server during download |
| `diff` | Only get files that differ from local copies |
| `replicate` | Preserve server-side directory structure |
| `replicateroot <dir>` | Root directory for replicated structure |
| `query <file>` | Apply a query filter file |

---

### fei5list

List files in an FEI file type.

```
fei5list [servergroup:]filetype ['<file expression>']
         [before|after <datetime>] | [between <datetime1> and <datetime2>]
         [format '<date format>'] [long | verylong]
         [query <queryfile>] [filehandler] [help]
```

| Keyword | Description |
|---|---|
| `long` | Include size, date, and comment |
| `verylong` | Include size, date, comment, and CRC |
| `query <file>` | Apply a query filter file |

---

### fei5delete

Delete one or more files from an FEI file type.

```
fei5delete [servergroup:]filetype '<file expression>'
           [filehandler] [help]

fei5delete using <option file>
```

> **Caution:** Deletion is permanent.

---

### fei5replace

Replace an existing file in an FEI file type with a new version.

```
fei5replace [servergroup:]filetype <file expression>
            [before|after <datetime>] | [between <datetime1> and <datetime2>]
            [format '<date format>'] [comment '<comment text>']
            [crc] [receipt] [autodelete] [diff] [filehandler] [help]
```

---

### fei5rename

Rename a file within an FEI file type.

```
fei5rename [servergroup:]filetype <old name> <new name> [help]
```

---

### fei5comment

Add or update a comment on a file.

```
fei5comment [servergroup:]filetype <filename> '<comment text>' [help]
```

---

### fei5crc

Compute and display CRC checksums for files in an FEI file type.

```
fei5crc [servergroup:]filetype ['<file expression>'] [help]
```

---

### fei5check

Check for discrepancies between the server catalog and stored files.

```
fei5check [servergroup:]filetype [help]
```

---

### fei5checkfiles

Check file integrity within an FEI file type.

```
fei5checkfiles [servergroup:]filetype [help]
```

---

### fei5display

Print a file's contents to standard output.

```
fei5display [servergroup:]filetype <filename> [help]
```

---

## Subscription Commands

### fei5subscribe

Subscribe to an FEI file type for continuous file delivery.

```
fei5subscribe [servergroup:]filetype
              [output <path>] [restart] [using <option file>]
              [pull|push] [replace|version] [format '<date format>']
              [query <queryfile>] [replicate] [replicateroot <rootdir>]
              [filehandler] [diff] [help]
```

| Keyword | Description |
|---|---|
| `output <path>` | Local directory for received files |
| `restart` | Resume from last known position after reconnect |
| `pull` / `push` | Delivery mode |
| `using <file>` | Load options from a file |

**Option file keywords** (one per line):

| Keyword | Description |
|---|---|
| `crc` | Verify CRC on receipt |
| `diff` | Only retrieve files that differ locally |
| `invoke <command>` | Run this command after each file is received |
| `invokeExitOnError` | Stop subscription if invoked command exits non-zero |
| `invokeAsync` | Run invoked command asynchronously |
| `logFile <filename>` | Write subscription log to this file |
| `logFileRolling <interval>` | Roll log: `monthly` `weekly` `daily` `hourly` `minutely` `halfdaily` |
| `mailMessageFrom <addr>` | Per-file email notification sender address |
| `mailMessageTo <addr,...>` | Per-file email notification recipient(s) |
| `mailReportAt <hh:mm am/pm,...>` | Send summary report at these times |
| `mailReportTo <addr,...>` | Summary report recipient(s) |
| `mailSMTPHost <host>` | SMTP relay host |
| `mailSilentReconnect` | Suppress reconnection notification emails |
| `receipt` | Request delivery receipt from server |
| `replace` | Overwrite local file if it already exists |
| `saferead` | Lock file on server during download |
| `version` | Download into a versioned filename |

---

### fei5guardian

Auto-restarting subscription daemon (Unix/Linux/macOS only). Wraps `fei5subscribe` and restarts it automatically on network errors or server disconnects.

```
fei5guardian [servergroup:]filetype [output <path>] [using <option file>] [help]
```

---

### fei5notify

Subscribe to file-arrival notifications without downloading files.

```
fei5notify [servergroup:]filetype [help]
```

---

## Information and Utility Commands

### fei5filetypes

List file types or server groups.

```
fei5filetypes '[servergroup:][<filetype expression>]'
fei5filetypes srvgroups
```

`srvgroups` lists server groups; omitting it lists file types within a server group.

---

### fei5showhandlers

Display the list of configured file handlers.

```
fei5showhandlers
```

---

### fei5makeclean

Remove locally cached FEI state files (subscription cursors, etc.).

```
fei5makeclean [servergroup:]filetype [help]
```

---

### fei5reference

Display a quick reference card for all FEI commands.

```
fei5reference
```

---

### fei5gui

Launch the Savannah graphical user interface.

```
fei5gui
```

Requires `$FEI5` to be set. See [User's Guide §7](users-guide.md#7-graphical-user-interface-savannah) for details.

---

## Administration Commands

### fei5register

Register a new file type on an FEI server group. Requires administrative privileges.

```
fei5register [servergroup:]filetype [help]
```

---

### fei5unregister

Unregister a file type. Requires administrative privileges.

```
fei5unregister [servergroup:]filetype [help]
```

---

### fei5locktype

Lock an FEI file type to prevent add and delete operations. Requires administrative privileges.

```
fei5locktype [servergroup:]filetype [help]
```

---

### fei5unlocktype

Unlock a previously locked file type. Requires administrative privileges.

```
fei5unlocktype [servergroup:]filetype [help]
```

---

### fei5accept

Accept a pending user operation request. Requires administrative privileges.

```
fei5accept [servergroup:]filetype for <add|replace|get|delete>
           [output <path>] [crc] [saferead] [autodelete]
           [replace|version] [diff] [help]
```

---

### fei5admin

General administration interface.

```
fei5admin [help]
```

---

### fei5publish

Publish a file type definition to the FEI server.

```
fei5publish [help]
```

---

### pwdclient

Password management utility.

```
pwdclient [help]
```

---

*Back to [User's Guide](users-guide.md) | See also [Appendix B — Troubleshooting](appendix-b-troubleshooting.md)*

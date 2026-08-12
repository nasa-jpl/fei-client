# FEI Komodo Client — User's Guide

> **Note:** This document was originally published in March 2004 for FEI version 5.1.4.
> Content has been updated where applicable. For current version information, see
> [CHANGELOG.md](../../../../CHANGELOG.md) in the repository root.
>
> See also:
> - [Appendix A — Command Reference](appendix-a-command-reference.md)
> - [Appendix B — Troubleshooting](appendix-b-troubleshooting.md)

---

## Table of Contents

1. [Introduction](#1-introduction)
2. [Getting Started](#2-getting-started)
3. [Authentication](#3-authentication)
4. [File Operations](#4-file-operations)
5. [Interactive Session Mode (fei5)](#5-interactive-session-mode-fei5)
6. [Subscription and Notification](#6-subscription-and-notification)
7. [Administration Commands](#7-administration-commands)
8. [Graphical User Interface (Savannah)](#8-graphical-user-interface-savannah)

---

## 1. Introduction

The File Exchange Interface (FEI) is a client-server system developed at JPL's Mission Data Management Service (MDMS) for transferring files between clients and a central server. FEI provides:

- Reliable file add, get, delete, replace, and rename operations
- File type management and access control
- Event-driven subscription and notification
- SSL-secured communications
- Both command-line and graphical (Savannah) interfaces

### Key Concepts

**Server group** — A named FEI server instance. Clients locate servers via `domain.fei`, a configuration file in `$FEI5/config/` that maps server group names to hostnames and ports.

**File type** — A named container on a server group for storing and retrieving files. Most commands address a file type as `servergroup:filetype`.

**`$FEI5`** — An environment variable that must point to the directory containing `domain.fei` and the SSL keystore files. All scripts in `bin/` check for this variable at startup.

---

## 2. Getting Started

### Prerequisites

- FEI client installed per the [Installation Guide](installation.md)
- `$FEI5` environment variable set and `$FEI5/bin` on your `PATH`:
  ```bash
  source fei5/use_FEI5.sh      # bash/sh
  source fei5/use_FEI5.csh     # csh/tcsh
  ```
- `domain.fei` and SSL keystore files in `$FEI5/config/`
- A valid FEI username and password (obtained from your server administrator)

### Command Syntax

FEI commands use **bare keyword tokens**, not dash-prefixed flags. For example:

```
fei5get servergroup:filetype '*.fits' output /data/output after '2024-01-01 00:00:00'
```

Get help for any command by appending `help`:

```
fei5get help
fei5add help
```

### Quick Workflow

```bash
# 1. Log in (stores credentials for subsequent commands)
fei5kinit

# 2. See what file types are available
fei5filetypes servergroup

# 3. List files in a file type
fei5list servergroup:filetype

# 4. Download files
fei5get servergroup:filetype output /local/dir

# 5. Log out when done
fei5kdestroy
```

---

## 3. Authentication

FEI authentication is a two-step process: first obtain a session token with `fei5kinit`, then run file operations. Commands read the stored token automatically so you do not need to supply credentials each time.

### Step 1 — Log In (`fei5kinit`)

```
fei5kinit [<username> [<server group>]]
```

Both arguments are optional. If omitted, `fei5kinit` prompts interactively:

```
Server group>> mymission
User name>> jsmith
Password>>
```

The password prompt is masked. `fei5kinit` authenticates against the server using the server's public key for encryption, then writes an encrypted token to `~/.komodo/login`. This token is used automatically by all subsequent commands.

If your server has multiple server groups, run `fei5kinit` once per server group.

### Step 2 — Run Commands

After `fei5kinit`, all commands read credentials from `~/.komodo/login` automatically using the server group name as the key. No credential arguments are needed on the command line:

```
fei5list servergroup:filetype
fei5get  servergroup:filetype '*.fits' output /data/output
```

If no cached credentials exist for the server group, the command will fail with:
```
Please acquire credentials with login utility.
```
Run `fei5kinit` first in that case.

### Listing Stored Credentials

```
fei5klist
```

Shows which server groups have active stored tokens and their expiry.

### Logging Out (`fei5kdestroy`)

```
fei5kdestroy [<server group>]
```

Removes stored credentials for the specified server group. If no server group is given, removes all stored credentials.

### Changing Your Password

```
fei5changepassword <server group>
```

Prompts for your current password, then for a new password (entered twice). Updates both the server and the local credential store.

---

## 4. File Operations

### 4.1 Adding Files

```
fei5add [servergroup:]filetype <file expression>
        [before|after <datetime>] | [between <datetime1> and <datetime2>]
        [format '<date format>'] [comment '<comment text>']
        [crc] [receipt] [autodelete] [filehandler] [help]

fei5add using <option file>
```

**Examples:**

```bash
# Add a single file
fei5add mymission:science_data data001.fits

# Add all .fits files with a comment
fei5add mymission:science_data '*.fits' comment 'daily downlink'

# Add files modified after a date, with CRC verification
fei5add mymission:science_data '*.fits' after '2024-01-01 00:00:00' crc

# Add from an option file (batch mode)
fei5add using /path/to/options.txt
```

| Keyword | Description |
|---|---|
| `comment '<text>'` | Attach a comment to the added file(s) |
| `crc` | Compute and store a CRC checksum on add |
| `receipt` | Request a delivery receipt from the server |
| `autodelete` | Delete the local file after successful add |
| `filehandler` | Use a configured file handler |
| `before`/`after`/`between...and` | Filter files by modification date |
| `format '<fmt>'` | Date format string for the date filter |

### 4.2 Retrieving Files

```
fei5get [servergroup:]filetype ['<file expression>']
        [output <path>] [before|after <datetime>] | [between <datetime1> and <datetime2>]
        [format '<date format>'] [crc] [saferead] [receipt]
        [replace|version] [diff] [query <queryfile>]
        [replicate] [replicateroot <rootdir>] [filehandler] [help]

fei5get using <option file>
```

**Examples:**

```bash
# Get all files into current directory
fei5get mymission:science_data

# Get files matching a pattern to a specific directory
fei5get mymission:science_data '*.fits' output /data/output

# Get files added after a date, replacing any local copies
fei5get mymission:science_data after '2024-01-01 00:00:00' replace

# Get with CRC verification
fei5get mymission:science_data crc
```

| Keyword | Description |
|---|---|
| `output <path>` | Local directory for downloaded files (default: current directory) |
| `before`/`after`/`between...and` | Filter by server-side add date |
| `replace` | Overwrite local file if it exists |
| `version` | Download into a versioned filename |
| `crc` | Verify checksum after download |
| `saferead` | Lock file on server during download |
| `receipt` | Request a delivery receipt |
| `diff` | Only get files that differ from local copies |
| `replicate` | Preserve server-side directory structure |
| `replicateroot <dir>` | Root directory for replicated structure |
| `query <file>` | Apply a query filter file |
| `filehandler` | Invoke a configured file handler on receipt |

### 4.3 Listing Files

```
fei5list [servergroup:]filetype ['<file expression>']
         [before|after <datetime>] | [between <datetime1> and <datetime2>]
         [format '<date format>'] [long | verylong]
         [query <queryfile>] [filehandler] [help]
```

**Examples:**

```bash
# List all files
fei5list mymission:science_data

# List files matching a pattern
fei5list mymission:science_data '*.fits'

# Long listing (includes size, date, comment)
fei5list mymission:science_data long

# Very long listing (includes CRC)
fei5list mymission:science_data verylong

# List files added after a date
fei5list mymission:science_data after '2024-01-01 00:00:00'
```

### 4.4 Deleting Files

```
fei5delete [servergroup:]filetype '<file expression>'
           [filehandler] [help]

fei5delete using <option file>
```

> **Caution:** Deletion is permanent.

```bash
fei5delete mymission:science_data 'data001.fits'
fei5delete mymission:science_data '*.tmp'
```

### 4.5 Replacing Files

```
fei5replace [servergroup:]filetype <file expression>
            [before|after <datetime>] | [between <datetime1> and <datetime2>]
            [format '<date format>'] [comment '<comment text>']
            [crc] [receipt] [autodelete] [diff] [filehandler] [help]
```

```bash
fei5replace mymission:science_data data001.fits comment 'v2 correction'
```

### 4.6 Renaming Files

```
fei5rename [servergroup:]filetype <old name> <new name> [help]
```

```bash
fei5rename mymission:science_data data001_raw.fits data001.fits
```

### 4.7 Adding Comments

```
fei5comment [servergroup:]filetype <filename> '<comment text>' [help]
```

```bash
fei5comment mymission:science_data data001.fits 'reprocessed 2024-06-01'
```

### 4.8 Checking File Integrity

Compute and display CRC checksums for files on the server:

```
fei5crc [servergroup:]filetype ['<file expression>'] [help]
```

Check for discrepancies between the server catalog and stored files:

```
fei5check [servergroup:]filetype [help]
fei5checkfiles [servergroup:]filetype [help]
```

### 4.9 Displaying File Contents

Print a file's contents to standard output (useful for small text files):

```
fei5display [servergroup:]filetype <filename> [help]
```

### 4.10 Listing File Types

```
fei5filetypes '[servergroup:][<filetype expression>]'
fei5filetypes srvgroups
```

`srvgroups` lists server groups instead of file types within a server group.

---

## 5. Interactive Session Mode (fei5)

Running `fei5` with no arguments launches an interactive session. This is an alternative to the individual wrapper commands (`fei5add`, `fei5get`, etc.) — both reach the same server operations, but the interactive mode lets you stay connected across multiple commands, reuse an open file type connection, and use a shorter command syntax.

```
$ fei5
TESTGRP:>>
```

The prompt shows the current server group and, once a file type is selected with `use`, the active file type:

```
TESTGRP:>> use science_data
Using file type TESTGRP:science_data
TESTGRP:science_data>>
```

### 5.1 Launching and Logging In

```
fei5
```

On startup the session attempts to read credentials from `~/.komodo/login` (the same cache written by `fei5kinit`). If no cached credentials exist you are prompted:

```
TESTGRP:>> login
Server group>> TESTGRP
User name>> jsmith
Password>>
```

You can also pass the server group on the command line to skip that prompt:

```
TESTGRP:>> login jsmith mypassword TESTGRP
```

(In batch files: `login <username> [<password>] [<servergroup>]`)

Typing `abort` at any prompt during login returns to the session without logging in. Typing `exit`, `quit`, or `bye` at any prompt terminates the client.

### 5.2 Selecting a File Type

Before running file operations you must connect to a file type with `use`:

```
use [<servergroup>:]<filetype>
```

```
TESTGRP:>> use science_data
Using file type TESTGRP:science_data
TESTGRP:science_data>>
```

To switch to a file type on a different server group:

```
TESTGRP:science_data>> use OTHERGRP:telemetry
Using file type OTHERGRP:telemetry
OTHERGRP:telemetry>>
```

Previously opened connections are cached for the session; switching back to one does not re-open it.

### 5.3 File Operations

All commands operate on the file type selected with `use`. Short aliases are shown in parentheses.

**Listing files**

```
show <file expression>            (alias: s)
showLatest [<file expression>]
showAfter <yyyy-MM-ddThh:mm:ss.SSS>
showBetween <datetime1> and <datetime2>
```

```
TESTGRP:science_data>> show *.fits
TESTGRP:science_data>> showAfter 2024-01-01T00:00:00.000
```

**Downloading files**

```
get <file expression>             (alias: g)
getLatest [<file expression>]
getAfter <yyyy-MM-ddThh:mm:ss.SSS>
getBetween <datetime1> and <datetime2>
```

```
TESTGRP:science_data>> get *.fits
TESTGRP:science_data>> getLatest
```

Append `invoke "<shell command>"` to any `get`/`show` variant to run a command on each received file:

```
TESTGRP:science_data>> get *.fits invoke "process.sh"
```

**Uploading files**

```
add <file expression> ["comment"]    (alias: a)
replace <file expression> ["comment"] (alias: r)
```

```
TESTGRP:science_data>> add data001.fits
TESTGRP:science_data>> add *.fits "daily downlink"
TESTGRP:science_data>> replace data001.fits "v2 correction"
```

**Other file operations**

```
delete <file expression>          (alias: d)
rename <old name> <new name>      (alias: n)
comment <filename> "<comment>"    (alias: c)
checksum <local filename>
archive <filename>
```

### 5.4 Session Settings

Use `set` to toggle behavioural options that apply to all subsequent commands in the session. Run `set` with no arguments to see all current values.

```
set <parameter> {on|off}
```

| Parameter | Default | Effect |
|---|---|---|
| `replaceFile` | off | Overwrite local file on `get` if it already exists |
| `versionFile` | off | Download into a versioned filename |
| `computeChecksum` | off | Verify CRC on every transfer |
| `autoDelete` | off | Delete local file after successful `add` |
| `safeRead` | off | Lock file on server during `get` |
| `receipt` | off | Request delivery receipt from server |
| `diff` | off | Only transfer files that differ from local copies |
| `replicate` | off | Preserve server-side directory structure on `get` |
| `restart` | off | Resume subscriptions from last known position |
| `verbose` | off | Print per-file transfer details |
| `veryVerbose` | off | Print extended transfer details |
| `abort` | off | Abort batch file on first error |
| `echo` | on | Echo commands in batch mode |
| `log` | on | Write session activity to the log |
| `preserve` | on | Preserve local file timestamps |
| `test` | off | Dry-run mode — parse commands but do not execute |
| `timer` | off | Print elapsed time after each command |

### 5.5 Utility Commands

```
showTypes                         List all file types on the current server group
setDefaultGroup <group>           Switch server group without changing file type
  (alias: defaultGroup)
showDomainFile                    Print the contents of domain.fei
dateFormat ["<format>"]           Set or show the date format used in output
logFile <filename>                Write session transcript to a file
  (alias: log)
logCmds <filename>                Write commands (not output) to a file
history <number>                  Repeat the last N commands
cd [<directory>]                  Change local working directory
ls                                List local directory contents
pwd                               Print local working directory
version  (alias: v)               Print the FEI client version
changePassword                    Change your password interactively
```

### 5.6 Batch Mode

Pass a script file to `fei5` with the `-b` flag to run commands non-interactively:

```bash
fei5 -b /path/to/script.fei5
```

Lines beginning with `#` are treated as comments. The `batch` command can also be issued from within an interactive session:

```
TESTGRP:>> batch /path/to/script.fei5
```

Repeat scheduling is supported:

```
batch <filename> repeatAt <hh:mm> {am|pm}
batch <filename> repeatEvery <hh:mm> [<hh:mm> {am|pm}]
```

Set `abort on` in a batch script to stop execution on the first error.

### 5.7 Getting Help

```
help                              List all commands by category
help <command>                    Show usage for a specific command
help <type>                       List commands in a category (filetype, vft, utility, settings)
help types                        List available category names
```

Aliases: `?` and `h`.

### 5.8 Exiting

```
exit    (aliases: quit, bye, lo)
```

Closes all open file type connections, terminates the session, and exits.

---

## 6. Subscription and Notification

FEI supports event-driven file delivery. When subscribed to a file type, the client continuously polls the server and automatically downloads files as they are added.

### Subscribing (`fei5subscribe`)

```
fei5subscribe [servergroup:]filetype
              [output <path>] [restart] [using <option file>]
              [pull|push] [replace|version] [format '<date format>']
              [query <queryfile>] [replicate] [replicateroot <rootdir>]
              [filehandler] [diff] [help]
```

The subscription runs continuously until interrupted (Ctrl-C). Files added to the file type after the subscription starts are automatically downloaded to `output`.

**Examples:**

```bash
# Subscribe and write files to /data/incoming
fei5subscribe mymission:science_data output /data/incoming

# Subscribe with restart (resume from last known position after reconnect)
fei5subscribe mymission:science_data output /data/incoming restart

# Subscribe and invoke a script on each received file
fei5subscribe mymission:science_data using /path/to/subscribe.opts
```

**Option file keywords** (one per line in the option file):

| Keyword | Description |
|---|---|
| `crc` | Verify CRC on receipt |
| `diff` | Only retrieve files that differ locally |
| `invoke <command>` | Shell command to run after each file is received |
| `invokeExitOnError` | Stop subscription if the invoked command exits non-zero |
| `invokeAsync` | Run invoked command asynchronously |
| `logFile <filename>` | Write subscription log to file |
| `logFileRolling <interval>` | Roll log file at: `monthly`, `weekly`, `daily`, `hourly`, `minutely`, `halfdaily` |
| `mailMessageFrom <addr>` | Send per-file email notification from this address |
| `mailMessageTo <addr,...>` | Send per-file email notification to these addresses |
| `mailReportAt <hh:mm am/pm,...>` | Send summary report at these times |
| `mailReportTo <addr,...>` | Send summary report to these addresses |
| `mailSMTPHost <host>` | SMTP relay host for email notifications |
| `mailSilentReconnect` | Suppress reconnection notification emails |
| `receipt` | Request delivery receipt from server |
| `replace` | Overwrite local file if it already exists |
| `saferead` | Lock file on server during download |
| `version` | Download into a versioned filename |

### Auto-Restarting Subscription Daemon (`fei5guardian`)

`fei5guardian` wraps `fei5subscribe` and automatically restarts it if it exits due to a network error or server disconnect:

```
fei5guardian [servergroup:]filetype [output <path>] [using <option file>] [help]
```

Available on Unix/Linux/macOS only.

### Notification Without Download (`fei5notify`)

Prints notification events to stdout without downloading files:

```
fei5notify [servergroup:]filetype [help]
```

### File Handlers

FEI supports pluggable handlers that execute automatically on file receipt. To list handlers configured for your installation:

```
fei5showhandlers
```

See `mdms-komodo-client-handler-examples/` in the source repository for example handler implementations.

---

## 7. Administration Commands

These commands require administrative privileges on the FEI server.

### 7.1 Non-Interactive Admin Wrappers

```bash
# Register a new file type
fei5register [servergroup:]filetype [help]

# Unregister a file type
fei5unregister [servergroup:]filetype [help]

# Lock a file type — disables add and delete
fei5locktype [servergroup:]filetype [help]

# Unlock a file type
fei5unlocktype [servergroup:]filetype [help]

# Accept a pending user operation request
fei5accept [servergroup:]filetype for <add|replace|get|delete>
           [output <path>] [crc] [saferead] [autodelete]
           [replace|version] [diff] [help]
```

Display a quick reference card:

```
fei5reference
```

### 7.2 Interactive Admin Session (fei5admin)

`fei5admin` works like `fei5` (see [§5](#5-interactive-session-mode-fei5)) but launches a separate interactive session with a full set of server administration sub-commands. It uses the same `CLProcessor` infrastructure: interactive prompt, `login`, `set`, `help`, `batch`, `history`, etc. are all available.

```
$ fei5admin
TESTGRP:>>
```

The session requires admin credentials. Log in the same way as in the regular interactive session:

```
TESTGRP:>> login
Server group>> TESTGRP
User name>> adminuser
Password>>
```

Connect to a specific server within the group, or switch between servers:

```
TESTGRP:>> connect <server>      Open an admin connection to a server
TESTGRP:>> focus <server>        Switch active connection to an already-connected server
TESTGRP:>> showServers           List all servers in the domain
TESTGRP:>> showConnections       Show all active admin connections
TESTGRP:>> connections           Show connection count on the current server
```

#### User management

```
addUser <name> <password> [<privilege>] ["p"]
```

Interactive prompts ask for username, password (entered twice), a privilege level, and whether to grant VFT privilege. The loop continues asking "Add another user?" until you answer `N`.

**User privilege levels** (set at `addUser` time, modified later with `modifyUserAccess`):

| Value | Meaning |
|---|---|
| `a` | Admin — full server administration access |
| `r` | Read — `get`, `show`, subscribe operations only |
| `w` | Write — read plus `add`, `replace`, `delete`, `rename` |
| `p` | VFT — Virtual File Type access (granted as an add-on to the above) |

```
delUser <name>
showUsers [<name>]
addUserToRole <name> <role>
delUserFromRole <name> <role>
showRolesForUser <user>
modifyUserAccess <user> <access level> {on|off}
```

`modifyUserAccess` access levels: `admin`, `read`, `write`, `vft`.

#### Role management

Roles are user-defined names — there are no predefined system roles. A role bundles a set of capabilities and is then assigned to file types (`addFileTypeToRole`) and users (`addUserToRole`). A user's effective permissions on a file type are the intersection of their privilege level and the capabilities of any role they hold for that file type.

```
addRole <role> <capabilities list> [<external role>]
delRole <role>
modifyRole <role> <operation> <capabilities list>
showRoles [<role>]
showUsersForRole [<role>]
```

`modifyRole` operation must be one of: `add` (add capabilities to role), `delete` (remove capabilities from role), `set` (replace all capabilities).

The optional `<external role>` argument on `addRole` maps the FEI role to an external identity provider group (e.g. an LDAP group name).

**Allowed capabilities** (comma-separated, case-insensitive):

| Capability | Description | Note |
|---|---|---|
| `get` | Download files | |
| `add` | Upload files | Also grants `get` implicitly |
| `replace` | Replace existing files | Also grants `add` and `get` implicitly |
| `delete` | Delete files | |
| `rename` | Rename files | |
| `archive` | Archive files | |
| `locktype` | Lock/unlock file types | |
| `offline` | Offline access | |
| `push-subscribe` | Push-mode subscriptions | |
| `vft` | Virtual File Type operations | |
| `qaaccess` | QA access | |
| `receipt` | Request delivery receipts | |
| `register` | Register/unregister file types | |
| `replicate` | Replicate server-side directory structure | |
| `subtype` | Subtype operations | |

Example — create a read-only role, then assign it to a file type and a user:

```
TESTGRP:>> addRole readonly get,receipt
TESTGRP:>> addFileTypeToRole science_data readonly
TESTGRP:>> addUserToRole jsmith readonly
```

#### File type management

```
addFileType <name> <directory> "<comment>" <spaceReserved> <threshold>
            <qaInterval> <checksum> <logDeleteRecord> <receipt> <xmlSchema>
addFileTypeToRole <filetype> <role>
delFileType <filetype>
delFileTypeFromRole <filetype> <role>
showFileTypes [<filetype expression>]
showFiletypesForRole <role> [<filetype expression>]
showRolesForFileType <filetype expression> [<role>]
modifyFileType <filetype> <field> <value>
```

`modifyFileType` fields and values:

| Field | Values |
|---|---|
| `checksum` | `on` / `off` |
| `location` | filesystem path |
| `logdeleterecord` | `on` / `off` |
| `receipt` | `on` / `off` |
| `spacereserved` | size in MB |
| `threshold` | size in MB |

#### File and lock management

```
move <source filetype> <dest filetype> <file expression>
showLocks <filetype expression> <file name expression> [<lock value>]
setLock <filetype> <filename> <lock value>
```

Lock values: `none`, `get`, `replace`, `delete`, `reserved`, `link`, `rename`, `logdelete`, `move`, `movepersist`.

#### Server operations

```
hotboot                           Reload server configuration (equivalent to SIGHUP)
shutdown <timeout>                Shut down the server (timeout: 0–300 seconds)
fSync [<filetype>] [<datetime>]   Sync database → filesystem
dSync [<filetype>] [<datetime>]   Sync filesystem → database
showMemory                        Show server JVM memory usage
showServerParameters [<server>]   Show server configuration parameters
logMessage "<message>"            Write a message to the server log
```

Datetime format for `fSync`/`dSync`: `yyyy-MM-ddThh:mm:ss.SSS`

#### Batch mode

Pass a script with `-b` to run commands non-interactively:

```bash
fei5admin -b /path/to/admin-script.txt
```

All `CLProcessor` utility commands (`set`, `help`, `history`, `logFile`, `batch`, etc.) are available. See [§5.6](#56-batch-mode) for batch syntax details.

---

## 8. Graphical User Interface (Savannah)

FEI includes a Swing-based GUI called **Savannah**.

### Launching

```bash
fei5gui
```

Savannah requires `$FEI5` to be set. A 3-second splash screen is shown while the application initializes.

### Authentication

On launch, Savannah presents a login dialog for the server group, username, and password. Three authentication modes are supported depending on server configuration:

- **Standard** — username and password
- **Encrypted** — password is encrypted with the server's public key before transmission
- **Token** — uses an existing session token (interoperates with `fei5kinit`)

### Main Window

The main window is divided into two primary panels:

**Remote (FEI server) panel:**
- Displays the connected server group and available file types
- Lists files in the selected file type (name, size, date, comment, CRC)
- Filter bar for searching by filename pattern and/or date range (`long`/`verylong` detail levels)
- Toolbar buttons for common operations: get, add, delete, replace, rename, comment

**Local directory panel:**
- Browse the local filesystem
- Drag files from local panel to the remote panel to upload (add)
- Drag files from the remote panel to local panel to download (get)

### Subscriptions

The subscription panel (accessible from the menu) allows you to configure and monitor continuous file-delivery subscriptions against one or more file types. Each subscription entry shows its status (running/stopped/error) and a live count of received files.

### Logging

An in-application log panel shows real-time client activity. Log level and output file are configured via `$FEI5/config/mdmsgui.lcf`.

### Look and Feel

The GUI look and feel can be changed with the JVM property `komodo.ui.lookandfeel`:

```bash
# Use the native platform look and feel
java -Dkomodo.ui.lookandfeel=native ... fei5gui

# Use the cross-platform (Java Metal) look and feel (default)
```

---

*For command-line reference, see [Appendix A](appendix-a-command-reference.md).*
*For troubleshooting, see [Appendix B](appendix-b-troubleshooting.md).*

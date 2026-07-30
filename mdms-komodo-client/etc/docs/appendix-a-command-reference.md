# Appendix A — FEI Command Reference

> Back to: [User's Guide](users-guide.md)

This appendix provides a concise reference for all FEI client command-line utilities. All commands are located in `$FEI5/bin/` and require the `$FEI5` environment variable to be set.

---

## Common Options

Most FEI commands accept the following common options:

| Option | Description |
|---|---|
| `-u <username>` | FEI username |
| `-p <password>` | FEI password (plaintext or encrypted token) |
| `-d <domain>` | FEI domain name (alternative to `<domain>:<filetype>` syntax) |
| `-v` | Verbose output |
| `--help` | Display command usage |

---

## Command Reference

### fei5

General FEI client entry point. Displays help and available subcommands.

```
fei5 [--help]
```

---

### fei5accept

Accept a pending user registration on an FEI domain.

```
fei5accept [options] <domain> <username>
```

---

### fei5add

Add files to an FEI file type.

```
fei5add [options] <domain>:<filetype> <file-expression>
```

| Option | Description |
|---|---|
| `-c <comment>` | Attach comment to the added file(s) |
| `-R` | Recursive — add files in subdirectories |
| `-r` | Replace if file already exists |
| `-s <datetime>` | Only add files modified after this date |
| `-e <datetime>` | Only add files modified before this date |

---

### fei5admin

Perform administrative operations on an FEI domain.

```
fei5admin [options] <domain>
```

---

### fei5changepassword

Change your FEI password.

```
fei5changepassword [options] <domain>
```

---

### fei5check

Check for discrepancies in an FEI file type (metadata vs. stored files).

```
fei5check [options] <domain>:<filetype>
```

---

### fei5checkfiles

Check file integrity within an FEI file type.

```
fei5checkfiles [options] <domain>:<filetype>
```

---

### fei5comment

Add or update a comment on a file in an FEI file type.

```
fei5comment [options] <domain>:<filetype> <filename> "<comment>"
```

---

### fei5crc

Compute and display CRC checksums for files in an FEI file type.

```
fei5crc [options] <domain>:<filetype> [file-expression]
```

---

### fei5delete

Delete one or more files from an FEI file type.

```
fei5delete [options] <domain>:<filetype> <file-expression>
```

> **Caution:** Deletion is permanent.

---

### fei5display

Print the contents of a file from an FEI file type to standard output.

```
fei5display [options] <domain>:<filetype> <filename>
```

---

### fei5encrypt

Encrypt a password for use in automated scripts. Outputs an encrypted token string.

```
fei5encrypt
```

The resulting token can be passed as the value of the `-p` option in other commands.

---

### fei5filetypes

List all file types registered on an FEI domain.

```
fei5filetypes [options] <domain>
```

---

### fei5get

Retrieve files from an FEI file type.

```
fei5get [options] <domain>:<filetype> [file-expression]
```

| Option | Description |
|---|---|
| `-o <dir>` | Output directory (default: current directory) |
| `-s <datetime>` | Only get files added after this date |
| `-e <datetime>` | Only get files added before this date |
| `-n <count>` | Get newest N files |
| `-x` | Delete files from server after retrieval |
| `-R` | Recursive output directory creation |

---

### fei5guardian

Long-running subscription daemon. Restarts the subscription automatically on failure.

```
fei5guardian [options] <domain>:<filetype>
```

Available on Unix/Linux/macOS only.

---

### fei5gui

Launch the Savannah graphical user interface.

```
fei5gui [options]
```

---

### fei5kdestroy / fei5kinit / fei5klist

Kerberos credential management utilities (if Kerberos authentication is configured).

```
fei5kinit   [options]
fei5klist   [options]
fei5kdestroy [options]
```

---

### fei5list

List files in an FEI file type.

```
fei5list [options] <domain>:<filetype> [file-expression]
```

| Option | Description |
|---|---|
| `-c` | Include CRC checksums in listing |
| `-l` | Long listing (includes size, date, comment) |
| `-s <datetime>` | Only list files added after this date |
| `-e <datetime>` | Only list files added before this date |
| `-n <count>` | List newest N files only |

---

### fei5locktype

Lock an FEI file type to prevent add and delete operations.

```
fei5locktype [options] <domain>:<filetype>
```

---

### fei5makeclean

Remove locally cached FEI state files (subscription cursors, etc.).

```
fei5makeclean [options]
```

---

### fei5notify

Subscribe to file-arrival notifications without automatically downloading files.

```
fei5notify [options] <domain>:<filetype>
```

---

### fei5publish

Publish a file type definition to the FEI server.

```
fei5publish [options] <domain>
```

---

### fei5reference

Display a quick reference card for FEI commands.

```
fei5reference
```

---

### fei5register

Register a new file type on an FEI domain. Requires administrative privileges.

```
fei5register [options] <domain>:<filetype>
```

---

### fei5rename

Rename a file within an FEI file type.

```
fei5rename [options] <domain>:<filetype> <old-name> <new-name>
```

---

### fei5replace

Replace an existing file in an FEI file type with a new version.

```
fei5replace [options] <domain>:<filetype> <file>
```

---

### fei5showhandlers

Display the list of configured file handlers for the FEI client.

```
fei5showhandlers
```

---

### fei5subscribe

Subscribe to an FEI file type for continuous file delivery.

```
fei5subscribe [options] <domain>:<filetype>
```

| Option | Description |
|---|---|
| `-o <dir>` | Output directory for received files |
| `-n` | Retrieve newest available file on start |
| `-d` | Delete file from server after receipt |
| `-x <script>` | Execute script after each file is received |

---

### fei5unlocktype

Unlock an FEI file type that was previously locked.

```
fei5unlocktype [options] <domain>:<filetype>
```

---

### fei5unregister

Unregister (remove) a file type from an FEI domain. Requires administrative privileges.

```
fei5unregister [options] <domain>:<filetype>
```

---

### pwdclient

Utility for managing FEI passwords from the command line.

```
pwdclient [options]
```

---

## Date/Time Format

Commands that accept date/time filters use the format:

```
YYYY-MM-DD HH:MM:SS
```

Example: `2024-06-15 00:00:00`

---

*Back to [User's Guide](users-guide.md) | See also [Appendix B — Troubleshooting](appendix-b-troubleshooting.md)*

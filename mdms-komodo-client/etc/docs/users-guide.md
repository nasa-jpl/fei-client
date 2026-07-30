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
3. [File Operations](#3-file-operations)
4. [Subscription and Notification](#4-subscription-and-notification)
5. [Administration Commands](#5-administration-commands)
6. [Graphical User Interface](#6-graphical-user-interface)

---

## 1. Introduction

The File Exchange Interface (FEI) is a client-server system developed at JPL's Mission Data Management Service (MDMS) for transferring files between clients and a central server. FEI provides:

- Reliable file add, get, delete, and replace operations
- File type management and access control
- Event-driven notification and subscription
- SSL-secured communications
- Both command-line and graphical interfaces

### Architecture Overview

FEI uses a domain-based configuration model. A **domain** is a named FEI server instance. Clients locate servers via `domain.fei`, a configuration file that maps domain names to hostnames and ports.

All communications are encrypted using SSL/TLS. The keystore (`mdms-fei.keystore`) and server certificate (`public.der`) are obtained from your FEI server administrator.

---

## 2. Getting Started

### Prerequisites

- FEI client installed per the [Installation Guide](installation.md)
- `domain.fei` and SSL keystore files in `$FEI5/config/`
- A valid FEI username and password (obtained from your server administrator)
- `$FEI5` environment variable set and `$FEI5/bin` on your `PATH`

### Your First Command

List available file types on a domain:

```bash
fei5filetypes <domain>
```

List files in a file type:

```bash
fei5list <domain>:<filetype>
```

Example:

```bash
fei5list mymission:science_data
```

### Authentication

Most FEI commands accept credentials via:

1. **Interactive prompt** — omit `-u`/`-p` flags; you will be prompted
2. **Command-line flags** — `-u <username> -p <password>` (not recommended for scripts)
3. **User token** — see `fei5encrypt` for generating encrypted credential tokens

---

## 3. File Operations

### 3.1 Adding Files

Add one or more files to an FEI file type:

```bash
fei5add [options] <domain>:<filetype> <file-expression>
```

**Examples:**

```bash
# Add a single file
fei5add mymission:science_data data001.fits

# Add all .fits files
fei5add mymission:science_data "*.fits"

# Add with a comment
fei5add -c "daily downlink" mymission:science_data data001.fits
```

**Common options:**

| Option | Description |
|---|---|
| `-u <user>` | Username |
| `-p <pass>` | Password |
| `-c <comment>` | Attach a comment to the file |
| `-R` | Recursive (add files in subdirectories) |
| `-r` | Replace if file already exists |
| `-v` | Verbose output |

### 3.2 Retrieving Files

Download files from an FEI file type:

```bash
fei5get [options] <domain>:<filetype> [file-expression]
```

**Examples:**

```bash
# Get all files
fei5get mymission:science_data

# Get a specific file
fei5get mymission:science_data data001.fits

# Get files matching a pattern, output to a directory
fei5get -o /data/output mymission:science_data "data*.fits"

# Get only files newer than a date
fei5get -s "2024-01-01 00:00:00" mymission:science_data
```

**Common options:**

| Option | Description |
|---|---|
| `-o <dir>` | Output directory (default: current directory) |
| `-s <datetime>` | Start date filter (format: `YYYY-MM-DD HH:MM:SS`) |
| `-e <datetime>` | End date filter |
| `-n` | Retrieve newest N files |
| `-x` | Delete files from server after retrieval |
| `-v` | Verbose output |

### 3.3 Listing Files

```bash
fei5list [options] <domain>:<filetype> [file-expression]
```

**Examples:**

```bash
# List all files
fei5list mymission:science_data

# List files with checksums
fei5list -c mymission:science_data

# List files modified after a date
fei5list -s "2024-01-01 00:00:00" mymission:science_data
```

### 3.4 Deleting Files

```bash
fei5delete [options] <domain>:<filetype> <file-expression>
```

> **Caution:** Deletion is permanent. Verify the file expression before running.

### 3.5 Replacing Files

Replace an existing file with a new version:

```bash
fei5replace [options] <domain>:<filetype> <file>
```

### 3.6 Renaming Files

```bash
fei5rename [options] <domain>:<filetype> <old-name> <new-name>
```

### 3.7 Adding Comments

Attach a comment to an existing file:

```bash
fei5comment [options] <domain>:<filetype> <filename> "<comment text>"
```

### 3.8 Checking File Integrity

Verify CRC checksums of files in a file type:

```bash
fei5crc [options] <domain>:<filetype> [file-expression]
```

Check for file discrepancies:

```bash
fei5check [options] <domain>:<filetype>
fei5checkfiles [options] <domain>:<filetype>
```

### 3.9 Displaying File Contents

Print the contents of a file to stdout (for text files):

```bash
fei5display [options] <domain>:<filetype> <filename>
```

---

## 4. Subscription and Notification

FEI supports event-driven file delivery through subscriptions. When subscribed to a file type, the client is notified (and can automatically retrieve) files as they are added to the server.

### Subscribing

```bash
fei5subscribe [options] <domain>:<filetype>
```

The subscription process runs continuously. Files added to the file type after the subscription is established are automatically delivered to the local output directory.

**Common options:**

| Option | Description |
|---|---|
| `-o <dir>` | Output directory for received files |
| `-n` | Pull newest available file on subscribe |
| `-d` | Delete file from server after receipt |
| `-v` | Verbose output |

### Notification Without Retrieval

To receive notifications without downloading files:

```bash
fei5notify [options] <domain>:<filetype>
```

### Handlers

FEI supports pluggable file handlers that execute automatically on file receipt. See `mdms-komodo-client-handler-examples/` in the source repository for example handler implementations.

To list available handlers configured for your client:

```bash
fei5showhandlers
```

---

## 5. Administration Commands

These commands require administrative privileges on the FEI server.

### File Type Management

```bash
# Register a new file type
fei5register [options] <domain>:<filetype>

# Unregister a file type
fei5unregister [options] <domain>:<filetype>

# List all registered file types
fei5filetypes <domain>

# Lock a file type (disables add/delete)
fei5locktype [options] <domain>:<filetype>

# Unlock a file type
fei5unlocktype [options] <domain>:<filetype>
```

### User and Access Management

```bash
# General administration operations
fei5admin [options] <domain>

# Change your password
fei5changepassword [options] <domain>

# Accept a pending user registration
fei5accept [options] <domain> <username>
```

### Encryption and Credentials

Encrypt a password for use in automated scripts:

```bash
fei5encrypt
```

This outputs an encrypted token that can be used in place of a plaintext password with the `-p` option in other commands.

### Clean Up Local State

Remove locally cached FEI state files:

```bash
fei5makeclean [options]
```

### Reference and Help

Display the reference card for all commands:

```bash
fei5 --help
```

See [Appendix A — Command Reference](appendix-a-command-reference.md) for a complete listing of all commands and their options.

---

## 6. Graphical User Interface

FEI includes a Swing-based graphical client called **Savannah**.

### Launching the GUI

```bash
fei5gui
```

### Main Window

The Savannah GUI provides:

- A file browser panel showing FEI file types and their contents
- Drag-and-drop support for uploading and downloading files
- Filter controls for searching files by name, date range, or CRC
- A subscription panel for monitoring live file arrivals
- A configuration panel for managing server connections

### Authentication

On first launch, Savannah will prompt for a domain, username, and password. Credentials can be saved (encrypted) in the local configuration store.

### Configuration

Savannah stores its configuration in `~/.mdms/` (Unix/macOS) or `%USERPROFILE%\.mdms\` (Windows). This includes saved server profiles, window layout preferences, and cached credentials.

---

*For command-line reference, see [Appendix A](appendix-a-command-reference.md).*
*For troubleshooting, see [Appendix B](appendix-b-troubleshooting.md).*

# FEI Komodo Client — Installation Guide

> **Note:** This document was originally published in March 2004 for FEI version 5.1.4.
> Content has been updated where applicable. For current version information, see
> [CHANGELOG.md](../../../../CHANGELOG.md) in the repository root.

---

## Table of Contents

1. [Requirements](#1-requirements)
2. [Distribution Layout](#2-distribution-layout)
3. [Installation on Unix/Linux/macOS](#3-installation-on-unixlinuxmacos)
4. [Installation on Windows](#4-installation-on-windows)
5. [Verifying the Installation](#5-verifying-the-installation)
6. [Support](#6-support)

---

## 1. Requirements

### Java

FEI requires **OpenJDK 17 or higher** (Java 17+). Download from:

- <https://adoptium.net/>
- <https://openjdk.org/>

### Operating Systems

FEI has been tested on the following platforms:

| Platform | Version |
|---|---|
| Linux (RHEL/CentOS/Ubuntu) | Current supported releases |
| macOS | 10.15 (Catalina) and later |
| Windows | 10, 11 |

### Distribution Archive

The FEI client is distributed as:

- **Unix/Linux/macOS:** `mdms-komodo-client-<version>-fei5.tar.gz`
- **Windows:** `mdms-komodo-client-<version>-fei5.zip`

---

## 2. Distribution Layout

After extraction, the `fei5/` directory contains:

```
fei5/
├── README                               Top-level readme (this file in the archive)
├── use_FEI5.sh                          Environment setup script (bash/sh)
├── use_FEI5.csh                         Environment setup script (csh/tcsh)
├── bin/                                 Client command scripts
│   ├── fei5                             Main entry point
│   ├── fei5add                          Add files
│   ├── fei5get                          Retrieve files
│   ├── fei5list                         List files
│   ├── fei5delete                       Delete files
│   ├── fei5replace                      Replace files
│   ├── fei5rename                       Rename files
│   ├── fei5comment                      Add comments to files
│   ├── fei5crc                          Checksum verification
│   ├── fei5check / fei5checkfiles       Integrity checks
│   ├── fei5display                      Print file contents
│   ├── fei5subscribe                    Subscribe to file-arrival events
│   ├── fei5notify                       Notification without download
│   ├── fei5guardian                     Auto-restarting subscription daemon
│   ├── fei5filetypes                    List registered file types
│   ├── fei5register / fei5unregister    Register/unregister file types (admin)
│   ├── fei5locktype / fei5unlocktype    Lock/unlock file types (admin)
│   ├── fei5admin                        General administration
│   ├── fei5accept                       Accept user registrations (admin)
│   ├── fei5changepassword               Change password
│   ├── fei5encrypt                      Encrypt credentials for scripts
│   ├── fei5gui                          Launch Savannah graphical client
│   ├── fei5kinit / fei5klist / fei5kdestroy  Kerberos credential management
│   ├── fei5makeclean                    Remove local cached state
│   ├── fei5publish                      Publish file type definition
│   ├── fei5reference                    Quick reference card
│   ├── fei5showhandlers                 List configured file handlers
│   ├── pwdclient                        Password management utility
│   └── ...                             (each command also has a .sh variant)
├── config/                              Site configuration files
│   ├── domain.fei                       FEI domain/server map (site-specific)
│   ├── mdms-fei.keystore                SSL keystore (site-specific)
│   ├── public.der                       Server SSL certificate
│   ├── mdmsgui.lcf                      GUI logging configuration
│   ├── mdms.lcf                         Client logging configuration
│   ├── mdmsconfig.sh                    Shell environment config
│   ├── mdmsconfig.pm                    Perl environment config
│   ├── krb5.conf                        Kerberos configuration (if used)
│   └── pwdclient.conf                   Password client configuration
├── doc/                                 Documentation (Markdown)
│   ├── installation.md                  This guide
│   ├── users-guide.md                   User's guide
│   ├── appendix-a-command-reference.md  Command reference
│   ├── appendix-b-troubleshooting.md    Troubleshooting
│   └── pdf/                             Original PDF documentation
│       ├── komodo_installation.pdf
│       └── komodo_users.pdf
└── lib/                                 Java libraries
    ├── mdms-komodo-client-<version>.jar Main client JAR
    └── *.jar                            Runtime dependencies
```

> **Windows distribution** (`.zip`): same layout, but `use_FEI5.bat` replaces the shell scripts and `bin/` contains only `.bat` variants of each command.

---

## 3. Installation on Unix/Linux/macOS

### Step 1: Extract the Distribution

```bash
tar -xzf mdms-komodo-client-<version>-fei5.tar.gz
```

This creates the `fei5/` directory in the current working directory.

### Step 2: Set Environment Variables

Source the provided environment setup script for your shell:

**Bash/sh:**
```bash
source fei5/use_FEI5.sh
```

**csh/tcsh:**
```csh
source fei5/use_FEI5.csh
```

You may add this line to your shell profile (e.g., `~/.bashrc`, `~/.bash_profile`, `~/.cshrc`) to make it persistent.

The `use_FEI5.sh` script sets the `FEI5` environment variable to the `fei5/` directory and adds `fei5/bin` to your `PATH`.

### Step 3: Configure Domain and SSL

Copy or symlink your site-specific `domain.fei` and SSL keystore files into `fei5/config/`:

```bash
cp /path/to/domain.fei fei5/config/
cp /path/to/mdms-fei.keystore fei5/config/
```

The `$FEI5` environment variable must point to the directory containing `domain.fei` and the SSL keystore files.

---

## 4. Installation on Windows

### Step 1: Extract the Distribution

Use Windows Explorer or a ZIP utility to extract `mdms-komodo-client-<version>-fei5.zip`. This creates the `fei5\` directory.

### Step 2: Set Environment Variables

Run the provided setup script:

```cmd
fei5\use_FEI5.bat
```

You may add `fei5\bin` to your system `PATH` via **System Properties → Environment Variables**.

Set the `FEI5` environment variable to the full path of the `fei5\` directory.

### Step 3: Configure Domain and SSL

Copy your site-specific `domain.fei` and SSL keystore files into `fei5\config\`.

---

## 5. Verifying the Installation

After installation, verify that the client commands are accessible:

```bash
fei5list --help
```

If the command is found and displays usage information, the installation is successful.

---

## 6. Support

For questions, bug reports, or feature requests, please open an issue on GitHub:

<https://github.com/nasa-jpl/fei-client/issues>

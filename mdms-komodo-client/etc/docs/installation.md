# FEI Komodo Client — Installation Guide

> **Note:** This document was originally published in March 2004 for FEI version 5.1.4.
> Content has been updated where applicable. For current version information, see
> [CHANGELOG.md](../../../../CHANGELOG.md) in the repository root.

---

## Table of Contents

1. [Requirements](#1-requirements)
2. [Installation on Unix/Linux/macOS](#2-installation-on-unixlinuxmacos)
3. [Installation on Windows](#3-installation-on-windows)
4. [Verifying the Installation](#4-verifying-the-installation)
5. [Support](#5-support)

---

## 1. Requirements

### Java

FEI requires **OpenJDK 1.8 or higher** (Java 8+). Download from:

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

## 2. Installation on Unix/Linux/macOS

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

## 3. Installation on Windows

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

## 4. Verifying the Installation

After installation, verify that the client commands are accessible:

```bash
fei5list --help
```

If the command is found and displays usage information, the installation is successful.

---

## 5. Support

For questions, bug reports, or feature requests, please open an issue on GitHub:

<https://github.com/nasa-jpl/fei-client/issues>

# Appendix B — Troubleshooting

> Back to: [User's Guide](users-guide.md) | [Appendix A — Command Reference](appendix-a-command-reference.md)

---

## Common Issues

### Command Not Found

**Symptom:** Running any `fei5*` command produces `command not found` or a similar error.

**Resolution:**

1. Verify that `$FEI5` is set and points to the correct directory:
   ```bash
   echo $FEI5
   ```
2. Verify that `$FEI5/bin` is on your `PATH`:
   ```bash
   echo $PATH | tr ':' '\n' | grep fei5
   ```
3. Re-source the environment setup script:
   ```bash
   source $FEI5/use_FEI5.sh    # bash/sh
   source $FEI5/use_FEI5.csh   # csh/tcsh
   ```

---

### SSL / Keystore Errors

**Symptom:** Errors mentioning `SSLHandshakeException`, `certificate`, or `keystore`.

**Resolution:**

1. Verify that `domain.fei` and `mdms-fei.keystore` are present in `$FEI5/config/`:
   ```bash
   ls $FEI5/config/
   ```
2. Ensure the keystore file is not corrupted (obtain a fresh copy from your FEI server administrator).
3. Verify that your Java version is compatible (OpenJDK 1.8+):
   ```bash
   java -version
   ```

---

### Authentication Failures

**Symptom:** `Authentication failed`, `Invalid username or password`, or similar errors when running FEI commands.

**Resolution:**

1. Verify your username and password with your FEI server administrator.
2. If your password has expired, use `fei5changepassword` to set a new one.
3. If your credential token is stale (e.g., after a password change), re-run `fei5kinit` to refresh it.

---

### Cannot Connect to Server

**Symptom:** `Connection refused`, `Connection timed out`, or `UnknownHostException`.

**Resolution:**

1. Verify that `domain.fei` contains the correct hostname and port for your FEI domain.
2. Check network connectivity to the FEI server host:
   ```bash
   ping <server-hostname>
   telnet <server-hostname> <port>
   ```
3. Verify that the FEI server is running (contact your server administrator).
4. Check for firewall rules that may block the FEI port.

---

### Subscription Stops Receiving Files

**Symptom:** `fei5subscribe` or `fei5notify` stops receiving new files without an error.

**Resolution:**

1. Check network connectivity to the server.
2. Restart the subscription. Consider using `fei5guardian` on Unix/Linux/macOS, which automatically restarts the subscription on failure:
   ```bash
   fei5guardian servergroup:filetype output /output/dir
   ```
3. Check server-side logs with your FEI administrator.

---

### File Add / Get Fails with Checksum Error

**Symptom:** CRC or checksum mismatch error during file transfer.

**Resolution:**

1. Retry the transfer — transient network errors can cause this.
2. Verify the source file is not being modified while being transferred.
3. Use `fei5crc` to check the stored CRC on the server:
   ```bash
   fei5crc servergroup:filetype filename
   ```

---

### Java OutOfMemoryError

**Symptom:** `java.lang.OutOfMemoryError` when transferring large files or performing bulk operations.

**Resolution:**

Increase the Java heap size by setting `JAVA_OPTS` before running FEI commands:

```bash
export JAVA_OPTS="-Xmx512m"
fei5get mymission:science_data
```

Adjust the `-Xmx` value as needed for your workload.

---

### Enabling Verbose Logging

**Symptom:** An error occurs but the message is vague and does not point to a clear cause.

**Resolution:**

Increase the log level in `$FEI5/config/mdms.lcf` (command-line tools) or `$FEI5/config/mdmsgui.lcf` (Savannah GUI). Both files use Log4j 2 XML format. Change the `level` attribute on the `<Root>` element:

```xml
<!-- Default — informational messages only -->
<Root level="INFO" additivity="true">

<!-- More detail — include debug messages -->
<Root level="DEBUG" additivity="true">

<!-- Maximum detail — include trace-level messages with file and line numbers -->
<Root level="TRACE" additivity="true">
```

At `DEBUG` and `TRACE` levels the `TRACER` appender in `mdms.lcf` activates, which prints log output in the format:

```
TRACE [thread-name] (SourceFile.java:123) - message
```

This includes the source file name and line number for every log statement, which is useful for pinpointing where a failure occurs.

Remember to restore the level to `INFO` after diagnosing the issue, as `TRACE` output is very verbose.

---

### SSL Certificate Out of Date

**Symptom:** `SSLHandshakeException`, `PKIX path building failed`, `unable to find valid certification path`, or `Certificate expired` — even after confirming the keystore file is present.

**Resolution:**

The server's public certificate (`public.der`) may have been renewed since your installation. The client's keystore (`mdms-fei.keystore`) must contain the current server certificate to establish a trust chain.

1. Check the expiry date of the certificate currently in the keystore:
   ```bash
   keytool -list -v -keystore $FEI5/config/mdms-fei.keystore | grep -A2 "Valid from"
   ```
2. If the certificate has expired or the dates do not match the server's current certificate, obtain an updated `public.der` and `mdms-fei.keystore` from your FEI server administrator.
3. When opening a GitHub issue for SSL problems, include the output of the above `keytool` command so the administrator can confirm whether the certificate on file matches what the server is currently presenting.

---

### "File type not selected"

**Symptom:** Running a file operation command (`get`, `add`, `show`, `delete`, etc.) in the interactive `fei5` session produces the message `File type not selected`.

**Resolution:**

You must select a file type before issuing file operation commands. Use the `use` command:

```
fei5>> use mymission:science_data
mymission:science_data>> get *.fits
```

If you have not yet logged in, run `login` first:

```
fei5>> login jsmith mymission
mymission:>> use mymission:science_data
```

This error is client-side and never involves a server call.

---

### "Group `<name>` not found in domain!" / "Filetype `<name>` not found in domain!"

**Symptom:** A login or connection attempt fails with a message of the form `Group mymission not found in domain!` or `Filetype science_data not found in domain!`.

**Resolution:**

The name you supplied does not match anything in your `domain.fei` configuration file.

1. Verify the correct server group and file type names:
   ```bash
   fei5filetypes srvgroups          # list server groups
   fei5filetypes 'mymission:*'      # list file types in a group
   ```
2. Confirm that `domain.fei` in `$FEI5/config/` is the current one for your site. If in doubt, obtain a fresh copy from your FEI server administrator.

This error is client-side — the client checks `domain.fei` before attempting a server connection.

---

### "File already exists" during add or get

**Symptom:** An `add` or `fei5add` operation fails because a file with that name already exists on the server. During a `get` or `fei5get` operation, a local file with the same name already exists.

**Resolution:**

**Server-side duplicate (add):** The server returns this error when `add` is called for a filename that is already registered in the file type. Use `replace` (or `fei5replace`) to update an existing file rather than adding it again:

```bash
fei5replace mymission:science_data data001.fits
```

Or in an interactive session:

```
mymission:science_data>> replace data001.fits
```

**Local duplicate (get):** Use `replace` or `version` to control what happens when the downloaded filename already exists locally:

```bash
fei5get mymission:science_data data001.fits replace   # overwrite
fei5get mymission:science_data data001.fits version   # save as data001.fits.1
```

In an interactive session: `set replaceFile on` or `set versionFile on`.

---

### "Filetype operation access denied"

**Symptom:** The Savannah GUI displays `Filetype operation access denied` in the status bar and a dialog reading `Read access for this filetype is denied`.

**Resolution:**

Your account does not have read permission on the selected file type. This is a server-enforced access control (`DENIED` error code).

1. Confirm with your FEI server administrator that your account has been granted read (`r`) access on the file type.
2. To check which file types your account can access, run `fei5filetypes` — it will only list types you have at least read access to.
3. If you believe access should be granted, have the administrator use `fei5admin` → `addFileTypeToRole` and `addUserToRole` to configure the appropriate role.

---

## Getting Help

For issues not covered here, please open an issue on GitHub:

<https://github.com/nasa-jpl/fei-client/issues>

Include:
- The exact command you ran
- The full error message and stack trace (if any)
- Your Java version (`java -version`)
- Your operating system and version

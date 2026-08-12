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
3. If using an encrypted token (from `fei5encrypt`), regenerate it — tokens may become stale after a password change.

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

## Getting Help

For issues not covered here, please open an issue on GitHub:

<https://github.com/nasa-jpl/fei-client/issues>

Include:
- The exact command you ran
- The full error message and stack trace (if any)
- Your Java version (`java -version`)
- Your operating system and version

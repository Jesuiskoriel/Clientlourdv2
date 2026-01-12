package utils;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Map;

/**
 * Envoie un email simple via SMTP (AUTH LOGIN).
 * Compatible avec SMTP explicite (STARTTLS) ou SSL direct (port 465).
 * Si les variables d'environnement SMTP_* ne sont pas configurées, l'envoi est ignoré.
 */
public class MailService {

    private final String host;
    private final int port;
    private final String user;
    private final String pass;
    private final String from;
    private final boolean useStartTls;
    private final boolean useSsl;
    private final boolean enabled;
    private final int socketTimeoutMs;

    public MailService() {
        Map<String, String> env = System.getenv();
        this.host = env.getOrDefault("SMTP_HOST", "");
        this.port = parseInt(env.getOrDefault("SMTP_PORT", "587"), 587);
        this.user = env.getOrDefault("SMTP_USER", "");
        this.pass = env.getOrDefault("SMTP_PASS", "");
        this.from = env.getOrDefault("SMTP_FROM", this.user);
        this.useStartTls = Boolean.parseBoolean(env.getOrDefault("SMTP_STARTTLS", "true"));
        this.useSsl = Boolean.parseBoolean(env.getOrDefault("SMTP_SSL", "false"));
        this.enabled = !host.isBlank() && !user.isBlank() && !pass.isBlank();
        this.socketTimeoutMs = parseInt(env.getOrDefault("SMTP_TIMEOUT_MS", "30000"), 30000);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean sendEmail(String to, String subject, String body) {
        if (!enabled) {
            return false;
        }
        try {
            send(to, subject, body);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void send(String to, String subject, String body) throws IOException {
        // Ouverture de la connexion SMTP (SSL direct ou plain selon la config).
        SSLSocketFactory sslFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        Socket rawSocket = useSsl
                ? sslFactory.createSocket(host, port)
                : new Socket(host, port);
        configureSocket(rawSocket);
        BufferedReader reader = new BufferedReader(new InputStreamReader(rawSocket.getInputStream(), StandardCharsets.UTF_8));
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(rawSocket.getOutputStream(), StandardCharsets.UTF_8), true);

        logStep("waiting banner");
        expect(reader, 220, "banner");
        helo(writer, reader);

        if (useStartTls && !useSsl) {
            // Passage en TLS explicite (STARTTLS) puis nouveau EHLO.
            writeLine(writer, "STARTTLS");
            expect(reader, 220, "starttls");
            rawSocket = sslFactory.createSocket(rawSocket, host, port, true);
            configureSocket(rawSocket);
            reader = new BufferedReader(new InputStreamReader(rawSocket.getInputStream(), StandardCharsets.UTF_8));
            writer = new PrintWriter(new OutputStreamWriter(rawSocket.getOutputStream(), StandardCharsets.UTF_8), true);
            helo(writer, reader);
        }

        // Authentification LOGIN (base64) puis envoi de l'email.
        authLogin(writer, reader);

        writeLine(writer, "MAIL FROM:<" + from + ">");
        expect(reader, 250, "mail from");
        writeLine(writer, "RCPT TO:<" + to + ">");
        expect(reader, 250, "rcpt to");
        writeLine(writer, "DATA");
        expect(reader, 354, "data");

        String now = DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now());
        writer.printf("From: <%s>\r\n", from);
        writer.printf("To: <%s>\r\n", to);
        writer.printf("Subject: %s\r\n", subject);
        writer.print("MIME-Version: 1.0\r\nContent-Type: text/plain; charset=UTF-8\r\nContent-Transfer-Encoding: 8bit\r\nDate: " + now + "\r\n\r\n");
        writer.print(body + "\r\n.\r\n");
        writer.flush();
        expect(reader, 250, "data end");

        writeLine(writer, "QUIT");
        reader.close();
        writer.close();
        rawSocket.close();
    }

    private void helo(PrintWriter writer, BufferedReader reader) throws IOException {
        logStep("sending EHLO");
        writeLine(writer, "EHLO localhost");
        expect(reader, 250, "ehlo");
    }

    private void authLogin(PrintWriter writer, BufferedReader reader) throws IOException {
        logStep("auth login");
        writeLine(writer, "AUTH LOGIN");
        expect(reader, 334, "auth login");
        logStep("auth user");
        writeLine(writer, Base64.getEncoder().encodeToString(user.getBytes(StandardCharsets.UTF_8)));
        expect(reader, 334, "auth user");
        logStep("auth pass");
        writeLine(writer, Base64.getEncoder().encodeToString(pass.getBytes(StandardCharsets.UTF_8)));
        expect(reader, 235, "auth pass");
    }

    private void expect(BufferedReader reader, int expectedCode, String step) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.length() < 3) {
                continue;
            }
            int code = Integer.parseInt(line.substring(0, 3));
            if (line.length() > 3 && line.charAt(3) == '-') {
                // réponse multi-lignes, continue
                if (code != expectedCode) {
                    throw new IOException("SMTP code inattendu (" + step + "): " + code + " (" + line + ")");
                }
                continue;
            }
            if (code != expectedCode) {
                throw new IOException("SMTP code inattendu (" + step + "): " + code + " (" + line + ")");
            }
            break;
        }
        System.out.println("[SMTP OK] " + step);
    }

    private void logStep(String step) {
        System.out.println("[SMTP] " + step);
    }

    private void writeLine(PrintWriter writer, String cmd) {
        writer.print(cmd + "\r\n");
        writer.flush();
    }

    private void configureSocket(Socket socket) throws IOException {
        socket.setSoTimeout(socketTimeoutMs);
        if (socket instanceof SSLSocket ssl) {
            ssl.startHandshake();
        }
    }

    private int parseInt(String val, int fallback) {
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}

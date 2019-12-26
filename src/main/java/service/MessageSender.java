package service;

import java.util.List;
import java.util.Properties;

public class MessageSender {
    private final String username;
    private final String password;
    private final String host;
    private final Properties properties;

    public MessageSender(String username, String password) {
        this.username = username;
        this.password = password;
        this.host = "smtp." + username.split("@")[1];

        this.properties = new Properties();
        this.properties.put("mail.smtp.starttls.enable", "true");
        this.properties.put("mail.smtp.host", this.host);
        this.properties.put("mail.smtp.user", this.username);
        this.properties.put("mail.smtp.password", this.password);
        this.properties.put("mail.smtp.port", "587");
        this.properties.put("mail.smtp.auth", "true");
    }

    public void send(String subject, String message, Iterable<String> recipients) {
        new SendEmail(this).send(subject, message, recipients);
    }

    public void send(String subject, String message, String recipient) {
        new SendEmail(this).send(subject, message, List.of(recipient));
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getHost() {
        return host;
    }

    public Properties getProperties() {
        return properties;
    }
}

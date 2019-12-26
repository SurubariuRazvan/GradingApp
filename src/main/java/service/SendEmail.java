package service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class SendEmail extends Thread {
    private final Properties properties;
    private final String host;
    private final String username;
    private final String password;

    private String subject;
    private String message;
    private Iterable<String> recipients;

    public SendEmail(MessageSender ms) {
        this.properties = ms.getProperties();
        this.host = ms.getHost();
        this.username = ms.getUsername();
        this.password = ms.getPassword();
    }

    @Override
    public void run() {
        Session session = Session.getDefaultInstance(properties);
        try {
            MimeMessage mineMessage = new MimeMessage(session);
            mineMessage.setFrom(new InternetAddress(username));
            for (String recipient : recipients)
                mineMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));

            mineMessage.setSubject(subject);
            mineMessage.setText(message);

            Transport transport = session.getTransport("smtp");
            transport.connect(host, username, password);
            transport.sendMessage(mineMessage, mineMessage.getAllRecipients());
            transport.close();
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    public void send(String subject, String message, Iterable<String> recipient) {
        this.subject = subject;
        this.message = message;
        this.recipients = recipient;
        start();
    }
}

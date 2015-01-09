package com.home.CleverHome;

import java.io.IOException;
import java.security.Security;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.search.FlagTerm;

public class MailWorker extends Authenticator {
    private static final String TAG = "MailWorker";
    private String mailhost = "smtp.gmail.com";
    private String user;
    private String password;

    static {
        Security.addProvider(new com.home.CleverHome.JSSEProvider());
    }

    MailWorker(String user, String password) {
        this.user = user;
        this.password = password;
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password);
    }

    public synchronized String readMail(String subject) throws Exception {
        Properties props = new Properties();
        String info = new String ();
        props.setProperty("mail.store.protocol", "imaps");
        Session session = Session.getInstance(props, null);
        Store store = session.getStore();
        store.connect("imap.gmail.com", user, password);
        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_WRITE);
        inbox.getUnreadMessageCount();
        Message [] messages = inbox.getMessages();
        FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
        messages = inbox.search(ft);
        for (Message msg : messages) {
            if (msg.getSubject().equals(subject + " executed")) {
                try {
                    Multipart mp = (Multipart) msg.getContent();
                    BodyPart bp = mp.getBodyPart(0);
                    info = bp.getContent().toString();
                } catch (Exception e) {
                    try {
                        info = msg.getContent().toString();
                    } catch (Exception exp) {
                        throw new Exception(exp);
                    }
                }
                msg.setFlag(Flags.Flag.SEEN, true);
                break;
            }
        }
        return info;
    }

    public synchronized void sendMail(String subject, String body, String sender, String recipients, String filename) throws Exception {

        Multipart _multipart = new MimeMultipart();

        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", mailhost);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");

        Session session = Session.getDefaultInstance(props, this);

        MimeMessage message = new MimeMessage(session);

        message.setSender(new InternetAddress(sender));
        message.setSubject(subject);
        if (recipients.indexOf(',') > 0)
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
        else
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));

        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText(body);
        _multipart.addBodyPart(messageBodyPart);

        if (!filename.equalsIgnoreCase("")) {
            BodyPart attachBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(filename);
            attachBodyPart.setDataHandler(new DataHandler(source));
            attachBodyPart.setFileName(filename);

            _multipart.addBodyPart(attachBodyPart);
        }

        message.setContent(_multipart);

        Transport.send(message);
    }

}

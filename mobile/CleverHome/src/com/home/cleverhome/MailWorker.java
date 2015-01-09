package com.home.CleverHome;


import android.util.Log;

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

    public synchronized String readMail(String subject) {
        Properties props = new Properties();
        String info = new String ();
        props.setProperty("mail.store.protocol", "imaps");
        try {
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
                    Multipart mp = (Multipart) msg.getContent();
                    BodyPart bp = mp.getBodyPart(0);
                    info = bp.getContent().toString();
                    msg.setFlag(Flags.Flag.SEEN, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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

        try {
            MimeMessage message = new MimeMessage(session);

            // Кто
            message.setSender(new InternetAddress(sender));
            // О чем
            message.setSubject(subject);
            // Кому
            if (recipients.indexOf(',') > 0)
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(recipients));
            else
                message.setRecipient(Message.RecipientType.TO,
                        new InternetAddress(recipients));

            // Хочет сказать
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(body);
            _multipart.addBodyPart(messageBodyPart);

            // И что показать
            if (!filename.equalsIgnoreCase("")) {
                BodyPart attachBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(filename);
                attachBodyPart.setDataHandler(new DataHandler(source));
                attachBodyPart.setFileName(filename);

                _multipart.addBodyPart(attachBodyPart);
            }

            message.setContent(_multipart);

            Transport.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Ошибка отправки функцией sendMail!");
        }
    }

}

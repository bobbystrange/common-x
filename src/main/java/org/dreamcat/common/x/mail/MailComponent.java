package org.dreamcat.common.x.mail;

import lombok.AllArgsConstructor;
import org.dreamcat.common.util.ObjectUtil;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Create by tuke on 2019-01-27
 */
public class MailComponent {
    private static final String DEFAULT_BYTES_TYPE = "application/octet-stream";
    private final Properties properties;
    private transient String username;
    private transient String password;
    private transient MailAddress address;

    private MailComponent() {
        properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.starttls.required", "true");
        properties.put("mail.debug", "true");

        properties.put("mail.transport.protocol", "smtp");
        // smtp port
        properties.put("mail.port", 465);
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public static Multipart buildMultipart(String context, File file) throws MessagingException {
        Map<String, File> attachments = new HashMap<>();
        attachments.put(file.getName(), file);
        return buildMultipart(context, attachments);
    }

    public static Multipart buildMultipart(String context, String filename, byte[] attachment, String mimeType) throws MessagingException {
        Multipart multipart = new MimeMultipart();

        BodyPart contentPart = new MimeBodyPart();
        contentPart.setText(context);
        multipart.addBodyPart(contentPart);

        BodyPart attachmentPart = new MimeBodyPart();
        DataSource source = new ByteArrayDataSource(
                attachment, mimeType);
        attachmentPart.setDataHandler(new DataHandler(source));
        attachmentPart.setFileName(filename);

        return multipart;
    }

    public static Multipart buildMultipart(String context, Map<String, File> attachments) throws MessagingException {
        Multipart multipart = new MimeMultipart();

        BodyPart contentPart = new MimeBodyPart();
        contentPart.setText(context);
        multipart.addBodyPart(contentPart);
        if (attachments == null || attachments.isEmpty()) return multipart;

        for (String filename : attachments.keySet()) {
            File file = attachments.get(filename);

            BodyPart attachmentPart = new MimeBodyPart();
            DataSource source = new FileDataSource(file);
            attachmentPart.setDataHandler(new DataHandler(source));
            attachmentPart.setFileName(filename);

            multipart.addBodyPart(attachmentPart);
        }

        return multipart;
    }

    public static Multipart buildMultipart(String context, Map<String, byte[]> attachments, String mimeType) throws MessagingException {
        Multipart multipart = new MimeMultipart();

        BodyPart contentPart = new MimeBodyPart();
        contentPart.setText(context);
        multipart.addBodyPart(contentPart);
        if (attachments == null || attachments.isEmpty()) return multipart;

        for (String filename : attachments.keySet()) {
            byte[] bytes = attachments.get(filename);

            BodyPart attachmentPart = new MimeBodyPart();
            DataSource source = new ByteArrayDataSource(bytes, mimeType);
            attachmentPart.setDataHandler(new DataHandler(source));
            attachmentPart.setFileName(filename);

            multipart.addBodyPart(attachmentPart);
        }

        return multipart;
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    /**
     * @param username username of mail account
     * @param password password of mail account
     * @param host     such as smtp.gmail.com
     */
    public static Builder builder(String username, String password, String host) {
        return new Builder(username, password, host);
    }

    public MailComponent withAddress(MailAddress address) {
        this.address = address;
        return this;
    }

    public void openAndSend(String subject, String content) throws MessagingException {
        open().send(subject, content);
    }

    public void openAndSend(MailBody mailBody) throws MessagingException {
        Multipart multipart;
        Map<String, File> fileAttachments = mailBody.getFileAttachments();
        Map<String, byte[]> bytesAttachments = mailBody.getBytesAttachments();
        if (!fileAttachments.isEmpty()) {
            multipart = buildMultipart(mailBody.getContent(), fileAttachments);
        } else if (!bytesAttachments.isEmpty()) {
            multipart = buildMultipart(mailBody.getContent(), bytesAttachments, DEFAULT_BYTES_TYPE);
        } else {
            openAndSend(mailBody.getSubject(), mailBody.getContent());
            return;
        }

        open().send(mailBody.getSubject(), multipart);
    }

    public Op open() throws MessagingException {
        Session session = Session.getInstance(properties);
        Message message = new MimeMessage(session);

        // from
        message.setFrom(address.getFrom());
        // to
        message.setRecipients(Message.RecipientType.TO, address.getTo());
        // cc
        InternetAddress[] carbonCopy = address.getCarbonCopy();
        if (ObjectUtil.isNotEmpty(carbonCopy)) {
            message.setRecipients(Message.RecipientType.CC, carbonCopy);
        }
        // bcc
        InternetAddress[] blindCarbonCopy = address.getBlindCarbonCopy();
        if (ObjectUtil.isNotEmpty(blindCarbonCopy)) {
            message.setRecipients(Message.RecipientType.BCC, blindCarbonCopy);
        }
        // reply to
        InternetAddress[] replyTo = address.getReplyTo();
        if (ObjectUtil.isNotEmpty(replyTo)) {
            message.setReplyTo(replyTo);
        }

        Transport transport = session.getTransport();
        transport.connect(username, password);

        return new Op(this, message, transport);
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    @AllArgsConstructor
    public static class Op implements AutoCloseable {
        MailComponent self;
        Message message;
        Transport transport;

        public void send(String subject, String context) throws MessagingException {
            // subject
            message.setSubject(subject);
            message.setText(context);
            message.saveChanges();
            transport.sendMessage(message, message.getAllRecipients());
        }

        public void send(String subject, Multipart multipart) throws MessagingException {
            message.setSubject(subject);
            message.setContent(multipart);
            message.saveChanges();
            transport.sendMessage(message, message.getAllRecipients());
        }

        @Override
        public void close() throws Exception {
            transport.close();
        }
    }

    public static class Builder {

        private final MailComponent self;

        private Builder(String username, String password, String host) {
            self = new MailComponent();
            self.properties.put("mail.host", host);
            self.username = username;
            self.password = password;
        }

        public Builder address(MailAddress address) {
            self.address = address;
            return this;
        }

        public MailComponent build() {
            return self;
        }
    }

}

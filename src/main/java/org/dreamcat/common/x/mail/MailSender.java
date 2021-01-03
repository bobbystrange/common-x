package org.dreamcat.common.x.mail;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dreamcat.common.util.ObjectUtil;

/**
 * Create by tuke on 2019-01-27
 */
@AllArgsConstructor
public class MailSender {

    private static final String DEFAULT_BYTES_TYPE = "application/octet-stream";

    @Getter
    private final Properties properties;
    private final String host;
    private final String username;
    private final String password;

    public MailSender(String host, String username, String password, boolean debug) {
        this.properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.starttls.required", "true");
        properties.put("mail.debug", debug ? "true" : "false");
        properties.put("mail.transport.protocol", "smtp");
        // smtp port
        properties.put("mail.port", 465);

        this.host = host;
        this.username = username;
        this.password = password;
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public Op newOp() {
        return new Op(this);
    }

    public static class Op {

        private final MailSender sender;
        private final Session session;
        private final MimeMessage message;
        private Multipart multipart;

        private Op(MailSender sender) {
            this.sender = sender;
            this.session = Session.getInstance(sender.properties);
            this.message = new MimeMessage(session);
        }

        public Op from(String from) throws MessagingException {
            message.setFrom(toAddress(from));

            return this;
        }

        public Op to(String... to) throws MessagingException {
            if (ObjectUtil.isNotEmpty(to)) {
                message.setRecipients(Message.RecipientType.TO, toAddresses(to));
            }

            return this;
        }

        public Op to(List<String> to) throws MessagingException {
            return to(to.toArray(new String[0]));
        }

        public Op cc(String... carbonCopy) throws MessagingException {
            if (ObjectUtil.isNotEmpty(carbonCopy)) {
                message.setRecipients(Message.RecipientType.CC, toAddresses(carbonCopy));
            }

            return this;
        }

        public Op cc(List<String> carbonCopy) throws MessagingException {
            return cc(carbonCopy.toArray(new String[0]));
        }

        public Op bcc(String... blindCarbonCopy) throws MessagingException {
            if (ObjectUtil.isNotEmpty(blindCarbonCopy)) {
                message.setRecipients(Message.RecipientType.BCC, toAddresses(blindCarbonCopy));
            }

            return this;
        }

        public Op bcc(List<String> blindCarbonCopy) throws MessagingException {
            return bcc(blindCarbonCopy.toArray(new String[0]));
        }

        public Op replyTo(String... replyTo) throws MessagingException {
            if (ObjectUtil.isNotEmpty(replyTo)) {
                message.setReplyTo(toAddresses(replyTo));
            }

            return this;
        }

        public Op replyTo(List<String> replyTo) throws MessagingException {
            return replyTo(replyTo.toArray(new String[0]));
        }

        public Op subject(String subject) throws MessagingException {
            message.setSubject(subject);

            return this;
        }

        public Op content(String content) throws MessagingException {
            return content(content, true);
        }

        public Op content(String content, boolean html) throws MessagingException {
            message.setText(content, "utf-8", html ? "html" : "plain");

            return this;
        }

        public Op fileAttachments(Map<String, File> fileAttachments) throws MessagingException {
            for (Map.Entry<String, File> entry : fileAttachments.entrySet()) {
                String attachmentFileName = entry.getKey();
                File file = entry.getValue();
                fileAttachment(attachmentFileName, file);
            }

            return this;
        }

        public Op fileAttachment(String filename, File file) throws MessagingException {
            BodyPart attachmentPart = new MimeBodyPart();
            DataSource source = new FileDataSource(file);
            attachmentPart.setDataHandler(new DataHandler(source));
            attachmentPart.setFileName(filename);

            if (multipart == null) multipart = new MimeMultipart();
            multipart.addBodyPart(attachmentPart);

            return this;
        }

        public Op bytesAttachments(Map<String, byte[]> bytesAttachments, String mimeType)
                throws MessagingException {
            for (Map.Entry<String, byte[]> entry : bytesAttachments.entrySet()) {
                String attachmentFileName = entry.getKey();
                byte[] bytes = entry.getValue();
                bytesAttachment(attachmentFileName, bytes, mimeType);
            }

            return this;
        }

        public Op bytesAttachment(String filename, byte[] bytes, String mimeType)
                throws MessagingException {
            if (mimeType == null) mimeType = DEFAULT_BYTES_TYPE;

            BodyPart attachmentPart = new MimeBodyPart();
            DataSource source = new ByteArrayDataSource(bytes, mimeType);
            attachmentPart.setDataHandler(new DataHandler(source));
            attachmentPart.setFileName(filename);

            if (multipart == null) multipart = new MimeMultipart();
            multipart.addBodyPart(attachmentPart);

            return this;
        }

        public void send() throws MessagingException {
            message.saveChanges();
            try (Transport transport = session.getTransport()) {
                transport.connect(sender.host, sender.username, sender.password);
                transport.sendMessage(message, message.getAllRecipients());
            }
        }

        private static Address toAddress(String address) throws AddressException {
            return new InternetAddress(address);
        }

        private static Address[] toAddresses(String... addresses) throws AddressException {
            int length = addresses.length;
            Address[] a = new Address[length];
            for (int i = 0; i < length; i++) {
                a[i] = toAddress(addresses[i]);
            }
            return a;
        }
    }

}

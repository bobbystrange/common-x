package org.dreamcat.common.x.mail;

import lombok.AllArgsConstructor;
import lombok.Getter;

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
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Create by tuke on 2019-01-27
 */
@Getter
@AllArgsConstructor
public class MailSender {
    private static final String DEFAULT_BYTES_TYPE = "application/octet-stream";

    private final Properties properties;
    private transient String username;
    private transient String password;

    public MailSender(String username, String password, boolean debug) {
        this.properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.starttls.required", "true");
        properties.put("mail.debug", debug ? "true" : "false");
        properties.put("mail.transport.protocol", "smtp");
        // smtp port
        properties.put("mail.port", 465);

        this.username = username;
        this.password = password;
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    private static Address toAddress(String address) {
        try {
            return new InternetAddress(address);
        } catch (AddressException e) {
            throw new IllegalArgumentException(e);
        }
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    private static Address[] toAddresses(String... addresses) {
        int length = addresses.length;
        Address[] a = new Address[length];
        for (int i = 0; i < length; i++) {
            try {
                a[i] = new InternetAddress(addresses[i]);
            } catch (AddressException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return a;
    }

    public Op newOp() {
        return new Op(this);
    }

    public static class Op {
        private Session session;
        private MimeMessage message;
        private Multipart multipart;
        private transient String username;
        private transient String password;

        private Op(MailSender mailSender) {
            Properties properties = mailSender.getProperties();
            this.session = Session.getInstance(properties);
            this.message = new MimeMessage(session);
            this.username = mailSender.getUsername();
            this.password = mailSender.getPassword();
        }

        public Op from(String from) {
            try {
                message.setFrom(toAddress(from));
            } catch (MessagingException e) {
                throw new IllegalArgumentException(e);
            }
            return this;
        }

        public Op to(String... to) {
            try {
                message.setRecipients(Message.RecipientType.TO, toAddresses(to));
            } catch (MessagingException e) {
                throw new IllegalArgumentException(e);
            }
            return this;
        }

        public Op to(List<String> to) {
            return to(to.toArray(new String[0]));
        }

        public Op cc(String... carbonCopy) {
            try {
                message.setRecipients(Message.RecipientType.CC, toAddresses(carbonCopy));
            } catch (MessagingException e) {
                throw new IllegalArgumentException(e);
            }
            return this;
        }

        public Op cc(List<String> carbonCopy) {
            return cc(carbonCopy.toArray(new String[0]));
        }

        public Op bcc(String... blindCarbonCopy) {
            try {
                message.setRecipients(Message.RecipientType.BCC, toAddresses(blindCarbonCopy));
            } catch (MessagingException e) {
                throw new IllegalArgumentException(e);
            }
            return this;
        }

        public Op bcc(List<String> blindCarbonCopy) {
            return bcc(blindCarbonCopy.toArray(new String[0]));
        }

        public Op replyTo(String... replyTo) {
            try {
                message.setReplyTo(toAddresses(replyTo));
            } catch (MessagingException e) {
                throw new IllegalArgumentException(e);
            }
            return this;
        }

        public Op replyTo(List<String> replyTo) {
            return replyTo(replyTo.toArray(new String[0]));
        }

        public Op subject(String subject) {
            try {
                message.setSubject(subject);
            } catch (MessagingException e) {
                throw new IllegalArgumentException(e);
            }
            return this;
        }

        public Op content(String content) {
            return content(content, true);
        }

        public Op content(String content, boolean html) {
            try {
                message.setText(content, "uft-8",
                        html ? "html" : "plain");
            } catch (MessagingException e) {
                throw new IllegalArgumentException(e);
            }
            return this;
        }

        public Op fileAttachments(Map<String, File> fileAttachments) {
            for (String attachmentFileName : fileAttachments.keySet()) {
                File file = fileAttachments.get(attachmentFileName);
                fileAttachment(attachmentFileName, file);
            }
            return this;
        }

        public Op fileAttachment(String filename, File file) {
            try {
                BodyPart attachmentPart = new MimeBodyPart();
                DataSource source = new FileDataSource(file);
                attachmentPart.setDataHandler(new DataHandler(source));
                attachmentPart.setFileName(filename);

                if (multipart == null) multipart = new MimeMultipart();
                multipart.addBodyPart(attachmentPart);
            } catch (MessagingException e) {
                throw new IllegalArgumentException(e);
            }
            return this;
        }

        public Op bytesAttachments(Map<String, byte[]> bytesAttachments, String mimeType) {
            for (String attachmentFileName : bytesAttachments.keySet()) {
                byte[] bytes = bytesAttachments.get(attachmentFileName);
                bytesAttachment(attachmentFileName, bytes, mimeType);
            }
            return this;
        }

        public Op bytesAttachment(String filename, byte[] bytes, String mimeType) {
            try {
                if (mimeType == null) mimeType = DEFAULT_BYTES_TYPE;

                BodyPart attachmentPart = new MimeBodyPart();
                DataSource source = new ByteArrayDataSource(bytes, mimeType);
                attachmentPart.setDataHandler(new DataHandler(source));
                attachmentPart.setFileName(filename);

                if (multipart == null) multipart = new MimeMultipart();
                multipart.addBodyPart(attachmentPart);
            } catch (MessagingException e) {
                throw new IllegalArgumentException(e);
            }
            return this;
        }

        public void send() {
            try {
                message.saveChanges();
                try (Transport transport = session.getTransport()) {
                    transport.connect(username, password);
                    transport.sendMessage(message, message.getAllRecipients());
                }
            } catch (MessagingException e) {
                throw new IllegalArgumentException(e);
            }

        }
    }
}

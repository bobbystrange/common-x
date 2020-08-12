package org.dreamcat.common.x.mail;

import org.junit.Before;
import org.junit.Test;

import javax.mail.MessagingException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import static org.dreamcat.common.util.FormatUtil.println;

/**
 * Create by tuke on 2020/5/1
 */
public class MailSenderTest {
    private MailSender mailSender;
    private String username;

    @Before
    public void init() throws IOException {
        File localFile = new File("../local.properties");
        println("Loading properties from", localFile.getAbsolutePath());

        Properties properties = new Properties();
        properties.load(new FileReader(localFile));

        String host = properties.getProperty("mail.host");
        username = properties.getProperty("mail.username");
        String password = properties.getProperty("mail.password");
        mailSender = new MailSender(
                host, username, password,
                true);
    }

    @Test
    public void test() throws MessagingException {
        mailSender.newOp()
                .from(username)
                .to("421959560@qq.com")
                .content("Hello world " + new Date().toString())
                .send();
    }
}

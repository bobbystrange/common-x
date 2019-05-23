package com.tukeof.common.x.mail;

import com.tukeof.common.util.ObjectUtil;
import lombok.Data;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 * Create by tuke on 2019-01-27
 */
@Data
public class MailAddress {
    private InternetAddress from;
    private InternetAddress[] to;
    private InternetAddress[] carbonCopy;
    private InternetAddress[] blindCarbonCopy;
    private InternetAddress[] replyTo;

    public MailAddress(
            String from, String[] to,
            String[] carbonCopy)
            throws AddressException {
        this(from, to, carbonCopy, null, null);
    }

    public MailAddress(
            String from, String[] to,
            String[] carbonCopy, String[] blindCarbonCopy, String[] replyTo)
            throws AddressException {
        ObjectUtil.checkNotBlank(from, "from");
        ObjectUtil.checkNotEmpty(to, "to");

        this.from = new InternetAddress(from);
        this.to = buildAddresses(to);
        this.carbonCopy = buildAddresses(carbonCopy);
        this.blindCarbonCopy = buildAddresses(blindCarbonCopy);
        this.replyTo = buildAddresses(replyTo);

    }

    private static InternetAddress[] buildAddresses(String[] addresses) throws AddressException {
        if (!ObjectUtil.isNotEmpty(addresses)) return null;

        InternetAddress[] internetAddresses = new InternetAddress[addresses.length];
        for (int i = 0; i < addresses.length; i++) {
            internetAddresses[i] = new InternetAddress(addresses[i]);
        }
        return internetAddresses;
    }

}

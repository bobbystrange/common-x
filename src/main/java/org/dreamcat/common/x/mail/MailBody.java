package org.dreamcat.common.x.mail;

import lombok.Data;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Create by tuke on 2018/11/21
 */
@Data
public class MailBody {
    private String subject;
    private String content;

    private Map<String, File> fileAttachments;
    private Map<String, byte[]> bytesAttachments;

    public MailBody(String subject, String content) {
        this.subject = subject;
        this.content = content;

        this.fileAttachments = new HashMap<>();
        this.bytesAttachments = new HashMap<>();
    }
}

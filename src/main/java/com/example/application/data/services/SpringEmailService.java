package com.example.application.data.services;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author alejandro@vaadin.com
 **/
@Service
public class SpringEmailService {

    /**
     * Sends an email message with no attachments.
     *
     * @param from       email address from which the message will be sent.
     * @param recipients the recipients of the message.
     * @param subject    subject header field.
     * @param text       content of the message.
     * @throws MessagingException
     * @throws IOException
     */
    public static void send(String from, Collection<String> recipients, String subject, String text)
            throws MessagingException, IOException {
        send(from, recipients, subject, text, null, null, null);
    }

    /**
     * Sends an email message to one recipient with one attachment.
     *
     * @param from       email address from which the message will be sent.
     * @param recipient  the recipients of the message.
     * @param subject    subject header field.
     * @param text       content of the message.
     * @param attachment attachment to be included with the message.
     * @param fileName   file name of the attachment.
     * @param mimeType   mime type of the attachment.
     * @throws MessagingException
     * @throws IOException
     */
    public static void send(String from, String recipient, String subject, String text,
                            InputStream attachment, String fileName, String mimeType)
            throws MessagingException, IOException {
        send(from, Arrays.asList(recipient), subject, text, Arrays.asList(attachment), Arrays.asList(fileName),
                Arrays.asList(mimeType));
    }

    /**
     * Sends an email message with attachments.
     *
     * @param from        email address from which the message will be sent.
     * @param recipients  array of strings containing the recipients of the message.
     * @param subject     subject header field.
     * @param text        content of the message.
     * @param attachments attachments to be included with the message.
     * @param fileNames   file names for each attachment.
     * @param mimeTypes   mime types for each attachment.
     * @throws MessagingException
     * @throws IOException
     */
    public static void send(String from, Collection<String> recipients, String subject, String text,
                            List<InputStream> attachments, List<String> fileNames, List<String> mimeTypes)
            throws MessagingException, IOException {

        // check for null references
        Objects.requireNonNull(from);
        Objects.requireNonNull(recipients);

        // load email configuration from properties file
        String username = "szekrenyes.beni1@gmail.com";
        String password = "syfvxarcleidspib";

        // configure the connection to the SMTP server
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(from);
        helper.setSubject(subject);
        helper.setText(text, true);

        for (String recipient : recipients) {
            helper.addTo(recipient);
        }

        if (attachments != null) {
            for (int i = 0; i < attachments.size(); i++) {
                // create a data source to wrap the attachment and its mime type
                ByteArrayDataSource dataSource = new ByteArrayDataSource(attachments.get(i), mimeTypes.get(i));

                // add the attachment
                helper.addAttachment(fileNames.get(i), dataSource);
            }
        }

        mailSender.send(message);
    }

}

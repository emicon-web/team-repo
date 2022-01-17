package com.bezkoder.springjwt.mail;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Autowired     
    Configuration fmConfiguration;


    public void sendEmail(Mail mail, String templateName) throws IOException, TemplateException {
     MimeMessage mimeMessage = emailSender.createMimeMessage();
        try {
 
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
 
            mimeMessageHelper.setSubject(mail.getSubject());
            mimeMessageHelper.setFrom(mail.getFrom());
            mimeMessageHelper.setTo(mail.getMailTo());
            
			mimeMessageHelper.setText(getEmailContent(mail.getProps(), templateName), true);
			
 
            emailSender.send(mimeMessageHelper.getMimeMessage());
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
 
    private String getEmailContent(Map<String, Object> data, String templateName) throws IOException, TemplateException {
        StringWriter stringWriter = new StringWriter();
        fmConfiguration.getTemplate(templateName).process(data, stringWriter);
        return stringWriter.getBuffer().toString();
    }
}
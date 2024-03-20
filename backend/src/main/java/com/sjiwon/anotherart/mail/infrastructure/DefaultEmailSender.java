package com.sjiwon.anotherart.mail.infrastructure;

import com.sjiwon.anotherart.global.exception.GlobalException;
import com.sjiwon.anotherart.mail.application.adapter.EmailSender;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import static com.sjiwon.anotherart.global.exception.GlobalExceptionCode.UNEXPECTED_SERVER_ERROR;

@Slf4j
@Profile("default")
@Component
public class DefaultEmailSender implements EmailSender {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final String serviceEmail;

    public DefaultEmailSender(
            final JavaMailSender mailSender,
            final SpringTemplateEngine templateEngine,
            @Value("${spring.mail.username}") final String serviceEmail
    ) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.serviceEmail = serviceEmail;
    }

    @Async("emailAsyncExecutor")
    @Override
    public void sendAuthCodeForLoginId(final String targetEmail, final String authCode) {
        final Context context = new Context();
        context.setVariable("authCode", authCode);

        final String mailBody = templateEngine.process(EmailMetadata.AUTH_TEMPLATE, context);
        sendMail(
                EmailMetadata.LOGIN_ID_AUTH_CODE_TITLE,
                targetEmail,
                mailBody
        );
    }

    @Async("emailAsyncExecutor")
    @Override
    public void sendAuthCodeForPassword(final String targetEmail, final String authCode) {
        final Context context = new Context();
        context.setVariable("authCode", authCode);

        final String mailBody = templateEngine.process(EmailMetadata.AUTH_TEMPLATE, context);
        sendMail(
                EmailMetadata.PASSWORD_AUTH_CODE_TITLE,
                targetEmail,
                mailBody
        );
    }

    private void sendMail(final String subject, final String email, final String mailBody) {
        try {
            final MimeMessage message = mailSender.createMimeMessage();
            final MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setSubject(subject);
            helper.setTo(email);
            helper.setFrom(new InternetAddress(serviceEmail, "Another Art"));
            helper.setText(mailBody, true);

            mailSender.send(message);
        } catch (final Exception e) {
            log.warn("메일 전송 간 오류 발생...", e);
            throw new GlobalException(UNEXPECTED_SERVER_ERROR);
        }
    }
}

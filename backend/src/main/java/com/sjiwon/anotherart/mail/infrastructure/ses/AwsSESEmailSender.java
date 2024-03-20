package com.sjiwon.anotherart.mail.infrastructure.ses;

import com.sjiwon.anotherart.global.exception.GlobalException;
import com.sjiwon.anotherart.mail.application.adapter.EmailSender;
import com.sjiwon.anotherart.mail.infrastructure.EmailMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.Body;
import software.amazon.awssdk.services.ses.model.Content;
import software.amazon.awssdk.services.ses.model.Destination;
import software.amazon.awssdk.services.ses.model.Message;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;

import static com.sjiwon.anotherart.global.exception.GlobalExceptionCode.UNEXPECTED_SERVER_ERROR;

@Slf4j
@Profile("prod")
@Component
public class AwsSESEmailSender implements EmailSender {
    private final SesClient sesClient;
    private final SpringTemplateEngine templateEngine;
    private final String serviceEmail;

    public AwsSESEmailSender(
            final SesClient sesClient,
            final SpringTemplateEngine templateEngine,
            @Value("${spring.mail.username}") final String serviceEmail
    ) {
        this.sesClient = sesClient;
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
            sesClient.sendEmail(createEmailRequest(subject, email, mailBody));
        } catch (final Exception e) {
            log.error("메일 전송 간 오류 발생...", e);
            throw new GlobalException(UNEXPECTED_SERVER_ERROR);
        }
    }

    private SendEmailRequest createEmailRequest(
            final String subject,
            final String email,
            final String mailBody
    ) {
        return SendEmailRequest.builder()
                .source(serviceEmail)
                .destination(createDestination(email))
                .message(createMessage(subject, mailBody))
                .build();
    }

    private Destination createDestination(final String recipient) {
        return Destination.builder()
                .toAddresses(recipient)
                .build();
    }

    private Message createMessage(
            final String subject,
            final String body
    ) {
        final Content mailSubject = createContent(subject);
        final Body mailBody = Body.builder()
                .html(createContent(body))
                .build();

        return Message.builder()
                .subject(mailSubject)
                .body(mailBody)
                .build();
    }

    private Content createContent(final String data) {
        return Content.builder()
                .charset("UTF-8")
                .data(data)
                .build();
    }
}

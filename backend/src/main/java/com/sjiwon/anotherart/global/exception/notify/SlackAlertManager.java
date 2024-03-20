package com.sjiwon.anotherart.global.exception.notify;

import com.slack.api.Slack;
import com.slack.api.model.Attachment;
import com.slack.api.model.Field;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.sjiwon.anotherart.global.exception.notify.SlackMetadata.DATETIME_FORMAT;
import static com.sjiwon.anotherart.global.exception.notify.SlackMetadata.LOG_COLOR;
import static com.sjiwon.anotherart.global.exception.notify.SlackMetadata.TITLE_ERROR_MESSAGE;
import static com.sjiwon.anotherart.global.exception.notify.SlackMetadata.TITLE_REQUEST_IP;
import static com.sjiwon.anotherart.global.exception.notify.SlackMetadata.TITLE_REQUEST_URL;
import static com.sjiwon.anotherart.global.log.RequestMetadataExtractor.getClientIP;
import static com.sjiwon.anotherart.global.log.RequestMetadataExtractor.getRequestUriWithQueryString;
import static com.slack.api.webhook.WebhookPayloads.payload;

@Slf4j
@Component
public class SlackAlertManager {
    private static final Slack SLACK_CLIENT = Slack.getInstance();

    private final String slackWebhookUrl;

    public SlackAlertManager(@Value("${slack.webhook.url}") final String slackWebhookUrl) {
        this.slackWebhookUrl = slackWebhookUrl;
    }

    public void sendErrorLog(final HttpServletRequest request, final Exception e) {
        try {
            SLACK_CLIENT.send(
                    slackWebhookUrl,
                    payload(p -> p
                            .text("서버 에러 발생!!")
                            .attachments(List.of(
                                    generateSlackErrorAttachments(e, request)
                            ))
                    ));
        } catch (final IOException ex) {
            log.error("Slack API 통신 간 에러 발생", ex);
        }
    }

    private Attachment generateSlackErrorAttachments(
            final Exception e,
            final HttpServletRequest request
    ) {
        final String requestTime = DateTimeFormatter.ofPattern(DATETIME_FORMAT).format(LocalDateTime.now());
        return Attachment.builder()
                .color(LOG_COLOR)
                .title(requestTime + " 발생 에러 로그")
                .fields(List.of(
                        generateSlackField(TITLE_REQUEST_IP, getClientIP(request)),
                        generateSlackField(TITLE_REQUEST_URL, getRequestUriWithQueryString(request)),
                        generateSlackField(TITLE_ERROR_MESSAGE, e.toString())
                ))
                .build();
    }

    private Field generateSlackField(
            final String title,
            final String value
    ) {
        return Field.builder()
                .title(title)
                .value(value)
                .valueShortEnough(false)
                .build();
    }
}

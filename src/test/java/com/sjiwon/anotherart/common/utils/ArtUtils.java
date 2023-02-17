package com.sjiwon.anotherart.common.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ArtUtils {
    // 작품 초기 가격
    public static final int INIT_PRICE = 100_000;

    // 작품 해시태그
    public static final List<String> EMPTY_HASHTAGS = List.of();
    public static final List<String> HASHTAGS = List.of("A", "B", "C", "D", "E");
    public static final List<List<String>> COMMON_DEFAULT_HASHTAGS = List.of(
            HASHTAGS, HASHTAGS, HASHTAGS, HASHTAGS, HASHTAGS, HASHTAGS,
            HASHTAGS, HASHTAGS, HASHTAGS, HASHTAGS, HASHTAGS, HASHTAGS
    );
    public static final List<String> UPDATE_HASHTAGS = List.of("F", "G", "H", "I", "J");
    public static final List<String> OVERFLOW_HASHTAGS = List.of("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K");

    // 경매 작품 Period
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    public static final LocalDateTime currentTime3DayAgo = LocalDateTime.now().minusDays(3);
    public static final String currentTime3DayAgoToString = LocalDateTime.now().minusDays(3).format(formatter);
    public static final LocalDateTime currentTime1DayAgo = LocalDateTime.now().minusDays(1);
    public static final String currentTime1DayAgoToString = LocalDateTime.now().minusDays(1).format(formatter);
    public static final LocalDateTime currentTime1DayLater = LocalDateTime.now().plusDays(1);
    public static final String currentTime1DayLaterToString = LocalDateTime.now().plusDays(1).format(formatter);
    public static final LocalDateTime currentTime3DayLater = LocalDateTime.now().plusDays(3);
    public static final String currentTime3DayLaterToString = LocalDateTime.now().plusDays(3).format(formatter);
}

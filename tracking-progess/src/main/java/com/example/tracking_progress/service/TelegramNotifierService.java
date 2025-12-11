package com.example.tracking_progress.service;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramNotifierService {

    private final Dotenv dotenv;
    private final RestTemplate restTemplate = new RestTemplate();

    public void sendMessage(String message) {

        // Lấy từ file .env
        String botToken = dotenv.get("TELEGRAM_BOT_TOKEN");
        String chatId   = dotenv.get("TELEGRAM_CHAT_ID");

        if (botToken == null || chatId == null) {
            log.warn("⚠ Missing Telegram env variables: TELEGRAM_BOT_TOKEN / TELEGRAM_CHAT_ID");
            return;
        }

        try {
            String url =
                    "https://api.telegram.org/bot" + botToken + "/sendMessage"
                            + "?chat_id=" + chatId
                            + "&text=" + escape(message)
                            + "&parse_mode=Markdown";

            restTemplate.getForObject(url, String.class);

            log.info("📨 Telegram sent: {}", message);

        } catch (Exception e) {
            log.error("❌ Telegram error: {}", e.getMessage());
        }
    }

    /**
     * Escape ký tự đặc biệt để không lỗi Telegram Markdown
     */
    private String escape(String text) {
        return text
                .replace("_", "\\_")
                .replace("*", "\\*")
                .replace("[", "\\[")
                .replace("]", "\\]");
    }
}

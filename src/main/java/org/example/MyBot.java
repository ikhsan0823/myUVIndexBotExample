package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class MyBot extends TelegramLongPollingBot {

    @Override
    public String getBotUsername() {
        return "UVindexExampleBot";
    }

    @Override
    public String getBotToken() {
        return "6680841115:AAHrMmkZLPbOIspVOavwJ3iH4Kv8zisakzw";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String text = update.getMessage().getText();

            if ("/uvindex".equals(text)) {
                // Panggil kode Anda untuk mengambil data UV Index
                String uvIndexData = UVIndexApp.getDataFromUVIndexAPI();

                // Kirim data UV Index ke pengguna
                sendTextMessage(chatId, uvIndexData);
            }
        }
    }
    private void sendTextMessage(long chatId, String message) {
        SendMessage sendMessage = new SendMessage(Long.toString(chatId), message);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(new MyBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}

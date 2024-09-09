package com.example.service;

import com.example.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {
    final String HELP_TEXT = "Основной задачей бота является выдача ссылки на составление" +
                             " уравнений с одной неизвестной.\n\n" +
                             "Для получения ссылки нажмите в меню /equation";
    final BotConfig config;
    public TelegramBot (BotConfig config){
        this.config = config;
        List<BotCommand> listOfCommand = new ArrayList<>(Arrays.asList(
            new BotCommand("/start", "получить сообщение о приветствии"),
            new BotCommand("/equation", "получить ссылку на уравнения"),
            new BotCommand("/help", "информация о том, " +
                    "как использовать бота"))

        );

        try {
            this.execute(new SetMyCommands(listOfCommand, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list" + e.getMessage());
        }
    }
    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            switch (messageText) {
                case "/start":
                    startCommandReceived(chatId,
                            update.getMessage().getChat().getFirstName());
                    break;
                case "/equation":
                        EquationGenerator equationGenerator = config.equationGenerator();
                        sendMessage(chatId, "Ссылка на уравнения: "
                                + equationGenerator.getEquationUrl(determineTheDifficult()));
                    break;
                case "/help":
                    sendMessage(chatId, HELP_TEXT);
                    break;
                default:
                    sendMessage(chatId,
                            "Неизвестная команда! Попробуйте снова...");
            }
        }
    }
    private void startCommandReceived(long chatId, String name) {
        String answer = "Здравствуй, " + name + ", рад тебя видеть";
        log.info("Reply to user: " + name);
        sendMessage(chatId, answer);
    }
    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error" + e.getMessage());
        }
    }
    private int determineTheDifficult() {
        Random number = new Random();
        return number.nextInt(2);
    }

}

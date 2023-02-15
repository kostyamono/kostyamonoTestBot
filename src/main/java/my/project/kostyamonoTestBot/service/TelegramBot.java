package my.project.kostyamonoTestBot.service;

import my.project.kostyamonoTestBot.config.BotConfig;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScope;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    final BotConfig config;

    static final String HELP_TEXT = "Этот бот позволяет быстро рассчитать смету на готовые решения SoundGuard\n\n" +
            "Для просмотра списка доступных команд нажмите кномку меню слева\n";

    public TelegramBot(BotConfig config) {
        this.config = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "Запуск бота и приветсвие"));
        listOfCommands.add(new BotCommand("/help", "Информация о доступных коммандах бота"));
        listOfCommands.add(new BotCommand("/counts", "Запрос сметы на готовые решения SoundGuard"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException ex) {

        }


    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() & update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            switch (messageText) {
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                case "/help":
                    sendMessage(chatId, HELP_TEXT);
                    break;
                case "/counts":
                    sendMessage(chatId, "Для рассчета готового решения выберите поверхность, которую нужно звукоизолировать", replyMarkupForCounts());
                    break;
                case "CТЕНЫ":
                    sendMessage(chatId, "https://soundguard.ru/zvukoizolyaciya/steny/beskarkasnaya-sistema-zvukoizolyacii-steny-bazis/?area=13&pdf=1");
                default:
                    sendMessage(chatId, "Извините, данная команда не распознана.");

            }
        }
    }

    private void startCommandReceived(long chatId, String name) {
        String answer = "Здравствуйте, " + name + ", добро пожаловать!" +
                "\nКомпания \"Акустический Комфорт\" рада приветствовать Вас!\n\n" +
                "Для вывода списка доступных комманд введите /help";
        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        try {
            execute(message);
        } catch (TelegramApiException ex) {
            ex.printStackTrace();
        }
    }

    private void sendMessage(long chatId, String textToSend, ReplyKeyboardMarkup replyKeyboardMarkup) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        message.setReplyMarkup(replyKeyboardMarkup);
        try {
            execute(message);
        } catch (TelegramApiException ex) {
            ex.printStackTrace();
        }
    }

    private ReplyKeyboardMarkup replyMarkupForCounts() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("СТЕНЫ");
        row.add("ПОТОЛКИ");
        keyboardRows.add(row);
        row = new KeyboardRow();
        row.add("ПОЛЫ");
        row.add("ПЕРЕГОРОДКИ");
        keyboardRows.add(row);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }

}

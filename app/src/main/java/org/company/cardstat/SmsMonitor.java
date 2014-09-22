package org.company.cardstat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.util.Log;

import org.company.cardstat.domain.BankMessage;
import org.company.cardstat.domain.BankTransaction;
import org.company.cardstat.domain.BankTransactionType;
import org.company.cardstat.domain.BankTransactionTypeKeyword;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.provider.Telephony.Sms.Intents.getMessagesFromIntent;

/**
 * Отслеживает SMS и отправляет их на обработку
 */
public class SmsMonitor extends BroadcastReceiver {

    /** */
    private static final String TAG = "SmsMonitor";
    private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
    private static final String SAVE_ERROR_STRING = "Не удалось сохранить сообщение в БД. ";

    /** */
    private DatabaseHandler m_databaseHandler;

    /**
     *
     */
    public SmsMonitor() {

    }

    /**
     *
     * @param _context
     */
    public SmsMonitor(Context _context) {

        super();
        m_databaseHandler = new DatabaseHandler(_context);
        createKeyWorkDictionary();
    }

    /**
     * Наполняет базу ключевыми словами.
     * TODO: Убрать этот метод из release версии
     */
    private void createKeyWorkDictionary() {

        /** Пополнение счёта */
        String[] typeOneKeywords = { "пополнен", "пополнение" };

        /** */
        String[] typeTwoKeywords = { "расчёт", "оплата" };

        /** */
        String[] typeThreeKeywords = { "снятие", "cash" };

        try {

            long typeOneId = m_databaseHandler.addBankTransactionType("Пополнение");
            long typeTowId = m_databaseHandler.addBankTransactionType("Оплата");
            long typeThreeId = m_databaseHandler.addBankTransactionType("Наличные");

            for (int i = 0; i < typeOneKeywords.length; i++) {

                m_databaseHandler.addTransactionTypeKeyword(typeOneKeywords[i], typeOneId);
            }

            for (int i = 0; i < typeTwoKeywords.length; i++) {

                m_databaseHandler.addTransactionTypeKeyword(typeTwoKeywords[i], typeTowId);
            }

            for (int i = 0; i < typeThreeKeywords.length; i++) {

                m_databaseHandler.addTransactionTypeKeyword(typeThreeKeywords[i], typeThreeId);
            }
        } catch (DatabaseHandlerException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Собственно сам парсинг сообщения.
     * TODO: это можно вынести в отдельный класс парсера, но пока пусть будет так)
     * @param _message
     * @return
     */
    private BankMessage parseSmsMessage(SmsMessage _message) throws DatabaseHandlerException {

        BankMessage message = new BankMessage();
        message.setContent(_message.getMessageBody());
        message.setSender(_message.getOriginatingAddress());

        BankTransaction transaction = new BankTransaction();

        /** Ищем банк, который отправил сообщение
        List<Bank> banks = m_databaseHandler.getAllBanks();
        for (int j = 0; j < banks.size(); j++) {

            if (banks.get(j).getName() == message.getSender()) {
                transaction.setBankId(banks.get(j).getId());
            }
        }*/

        /** Сообщение от такого банка ещё не поступало */
        if (transaction.getId() < 0) {
            m_databaseHandler.addBank("Unknown" ,message.getSender());
        }

        List<BankTransactionTypeKeyword> keywords
                = m_databaseHandler.getAllTransactionTypeKeywords();

        /** Список слов сообщения */
        List<String> words = new ArrayList<String>();

        StringTokenizer token = new StringTokenizer(message.getContent());
        while (token.hasMoreTokens()) {
            words.add(token.nextToken());
        }

        /* Находим сумму с помощью Regex
        как дробное число с двумя знаками после запятой */
        Pattern pattern = Pattern.compile("([0-9]*(\\.|\\,)[0-9]{1,2})");
        Matcher m = pattern.matcher(message.getContent());
        if (m.matches())
        {
            transaction.setSum(Float.parseFloat(m.group(0)));
        }
        else
        {
            // TODO: Подумать на тем, как сообщать о том, что не удалось найти сумму
            Log.e("SmsMonitor","Не удалось найти сумму транзакции в сообщении");
            return message;
        }

        /** Перебираем содержимое сообщения по словам */
        for (int i = 0; i < words.size(); i++) {

            String word = words.get(i);

            for (int j = 0; j < keywords.size(); j++) {

                BankTransactionTypeKeyword keyword = keywords.get(j);
                if (keyword.getWord() == word) {

                    BankTransactionType type = m_databaseHandler
                            .getBankTransactionType(keyword.getTransactionTypeId());


                    message.setParsed(true);
                    break;
                }
            }
        }

        /** Тип транцакции не найден */
        if (transaction.getTypeId() < 0) {

            // TODO: установить имя типа транцакции
            long typeId = m_databaseHandler.addBankTransactionType("?????????????");

            /** Записываем новые ключевые слова для созданного типа */
            for (int i = 0; i < words.size(); i++) {
                m_databaseHandler.addTransactionTypeKeyword(words.get(i), typeId);
            }
        }

        return message;
    }

    /**
     * Выводит оповещение пользователю
     * @param context контекст
     * @param _title заголовок оповещения
     * @param _content содержание оповещения
     */
    private void showSmsNotification(final Context context, String _title, String _content) {

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MyActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(_title)
                        .setContentText(_content);
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }

    /**
     * Выводит все данные принятого сообщения. Нужно только для отладки.
     * @param _message сообщение
     */
    private void printSmsMessage(SmsMessage _message) {

        System.out.println("getEmailBody: " + _message.getEmailBody());
        System.out.println("getMessageBody: " + _message.getMessageBody());
        System.out.println("getEmailFrom: " + _message.getEmailFrom());
        System.out.println("getDisplayMessageBody: " + _message.getDisplayMessageBody());
        System.out.println("getDisplayOriginatingAddress: "
                + _message.getDisplayOriginatingAddress());

        System.out.println("getOriginatingAddress: " + _message.getOriginatingAddress());
        System.out.println("getPseudoSubject: " + _message.getPseudoSubject());
        System.out.println("getServiceCenterAddress: "
                + _message.getServiceCenterAddress());
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent != null && intent.getAction() != null
                && ACTION.compareToIgnoreCase(intent.getAction()) == 0) {

            showSmsNotification(context, MyActivity.APPLICATION_NAME,
                    "Обработка входящих сообщений");

            final SmsMessage[] messages = getMessagesFromIntent(intent);
            for (int i = 0; i < messages.length; i++) {

                printSmsMessage(messages[i]);
                // TODO: parseSmsMessage(messages[i]);
            }

            if (messages == null) {
                return;
            }
            new Runnable() {
                @Override
                public void run() {
                    /*ISmsParser parser = new FakeParser();
                    BankMessage parsedMessage = parser.Parse(messages);
                    if (parsedMessage != null) {
                        // Сохранить в БД
<<<<<<< HEAD
                        // TODO: написать сохранение в БД
                    }*/
/*
                        DatabaseHandler db = new DatabaseHandler(context);
                        try {
                            db.addBankMessage(parsedMessage);
                        }
                        catch (DatabaseHandlerException e) {
                            Log.e(TAG, SAVE_ERROR_STRING, e);
                        }
                    }*/
                }
            }.run();
        }
    }
}
package org.company.cardstat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

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

        /** Ищем банк, который отправил сообщение */
        List<Bank> banks = m_databaseHandler.getAllBanks();
        for (int j = 0; j < banks.size(); j++) {

            if (banks.get(j).getName() == message.getSender()) {
                transaction.setBankId(banks.get(j).getId());
            }
        }

        /** Сообщение от такого банка ещё не поступало */
        if (transaction.getId() < 0) {
            m_databaseHandler.addBank(message.getSender());
        }

        List<BankTransactionTypeKeyword> keywords
                = m_databaseHandler.getAllTransactionTypeKeywords();

        /** Список слов сообщения */
        List<String> words = new ArrayList<String>();

        StringTokenizer token = new StringTokenizer(message.getContent());
        while (token.hasMoreTokens()) {
            words.add(token.nextToken());
        }

        // TODO: найти сумму(руб.) транзакции

        /** Перебираем содержимое сообщения по словам */
        for (int i = 0; i < words.size(); i++) {

            String word = words.get(i);

            for (int j = 0; j < keywords.size(); j++) {

                BankTransactionTypeKeyword keyword = keywords.get(j);
                if (keyword.getWord() == word) {

                    BankTransactionType type = m_databaseHandler
                            .getBankTransactionType(keyword.getTransactionTypeId());

                    transaction.setTypeId(type.getId());

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

    @Override

    public void onReceive(final Context context, Intent intent) {
        if (intent != null && intent.getAction() != null && ACTION.compareToIgnoreCase(intent.getAction()) == 0) {

            final SmsMessage[] messages = getMessagesFromIntent(intent);
            for (int i = 0; i < messages.length; i++) {

                System.out.println("getEmailBody: " + messages[i].getEmailBody());
                System.out.println("getMessageBody: " + messages[i].getMessageBody());
                System.out.println("getEmailFrom: " + messages[i].getEmailFrom());
                System.out.println("getDisplayMessageBody: " + messages[i].getDisplayMessageBody());
                System.out.println("getDisplayOriginatingAddress: "
                        + messages[i].getDisplayOriginatingAddress());

                System.out.println("getOriginatingAddress: " + messages[i].getOriginatingAddress());
                System.out.println("getPseudoSubject: " + messages[i].getPseudoSubject());
                System.out.println("getServiceCenterAddress: "
                        + messages[i].getServiceCenterAddress());
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
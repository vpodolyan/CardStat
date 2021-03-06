package org.company.cardstat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;

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
     * Выводит оповещение пользователю
     * @param context контекст
     * @param _title заголовок оповещения
     * @param _content содержание оповещения
     */
    private void showSmsNotification(final Context context, String _title, String _content) {

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class), 0);

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

            showSmsNotification(context, "CardStat",
                    "Обработка входящих сообщений");

            final SmsMessage[] messages = getMessagesFromIntent(intent);
            for (int i = 0; i < messages.length; i++) {
                printSmsMessage(messages[i]);
            }

            // Parse sms and put transaction in DB
            new SmsHandler(messages, context).run();
        }
    }
}
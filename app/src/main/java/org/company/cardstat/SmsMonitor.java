package org.company.cardstat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.util.Log;

import static android.provider.Telephony.Sms.Intents.getMessagesFromIntent;

/**
 * Отслеживает SMS и отправляет их на обработку
 */
public class SmsMonitor extends BroadcastReceiver {
    private static final String TAG = "SmsMonitor";
    private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
    private static final String SAVE_ERROR_STRING = "Не удалось сохранить сообщение в БД. ";

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent != null && intent.getAction() != null && ACTION.compareToIgnoreCase(intent.getAction()) == 0) {
            final SmsMessage[] messages = getMessagesFromIntent(intent);
            if (messages == null)
                return;
            new Runnable() {
                @Override
                public void run() {
                    ISmsParser parser = new FakeParser();
                    BankMessage parsedMessage = parser.Parse(messages);
                    if (parsedMessage != null) {
                        // Сохранить в БД
                        DatabaseHandler db = new DatabaseHandler(context);
                        try {
                            db.addBankMessage(parsedMessage);
                        }
                        catch (DatabaseHandlerException e) {
                            Log.e(TAG, SAVE_ERROR_STRING.concat(e.getMessage()));
                        }
                    }
                }
            }.run();
         }
    }
}
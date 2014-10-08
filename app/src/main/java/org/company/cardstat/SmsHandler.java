package org.company.cardstat;

import android.content.Context;
import android.telephony.SmsMessage;
import android.util.Log;

import org.company.cardstat.domain.BankTransaction;
import org.company.cardstat.domain.TransactionType;

/**
 * Обрабатывает SMS-сообщение, выбирает парсер и создаёт транзакцию
 */
public class SmsHandler implements Runnable {
    private SmsMessage[] _messages;
    private Context _context;

    public SmsHandler(SmsMessage[] messages, Context context) {

        _messages = messages;
        _context = context;
    }

    @Override
    public void run() {
        if (_messages[0].getOriginatingAddress() != "Avangard")
            return;

        ISmsParser parser = new AvangardSmsParser();
        ParsedMessage msg;
        try {
            String body = "";
            for (SmsMessage m : _messages) {
                body += m.getMessageBody();
            }
            msg = parser.Parse(body);
        }
        catch (Exception e) {
            Log.e("CardStat", "Parse sms error! Exception " + e.getMessage());
            return;
        }
        // Создать транзакцию и добавить в БД
        DatabaseHandler db = new DatabaseHandler(_context);
        TransactionType transType = db.getTransactionType(msg.transactionName);
        BankTransaction transaction = new BankTransaction(msg.card, transType.getId(), msg.destination, msg.sum);
        try {
            db.addBankTransaction(transaction);
        }
        catch (DatabaseHandlerException e) {
            Log.e("CardStat", "Ошибка при добавлении транзакции в БД", e);
        }
    }
}

package org.company.cardstat;

import android.telephony.SmsMessage;

/**
 * Фейковый парсер для тестирования и заглушек
 */
public class FakeParser implements ISmsParser {

    public BankMessage Parse(SmsMessage[] messages) {

        return new BankMessage();
    }
}

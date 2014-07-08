package org.company.cardstat;

import android.telephony.SmsMessage;

import org.company.lib.BankMessage;

/**
 * Фейковый парсер для тестирования и заглушек
 */
public class FakeParser implements ISmsParser {
    public BankMessage Parse(SmsMessage[] messages)
    {
        return new BankMessage();
    }
}

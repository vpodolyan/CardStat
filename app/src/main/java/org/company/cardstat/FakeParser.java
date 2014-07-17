package org.company.cardstat;

import android.telephony.SmsMessage;

import java.util.List;

/**
 * Фейковый парсер для тестирования и заглушек
 */
public class FakeParser implements ISmsParser {


    /**
     *
     * @param message
     * @return
     */
    @Override
    public BankMessage Parse(SmsMessage message) {

        return new BankMessage();
    }
}

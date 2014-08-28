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
    public ParsedMessage Parse(SmsMessage message) {

        return new ParsedMessage();
    }
}

package org.company.cardstat;

import android.telephony.SmsMessage;

import java.util.List;

/**
 * Фейковый парсер для тестирования и заглушек
 */
public class FakeParser implements ISmsParser {
    @Override
    public ParsedMessage Parse(String message) {

        return new ParsedMessage();
    }
}

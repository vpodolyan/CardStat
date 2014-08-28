package org.company.cardstat;

import android.telephony.SmsMessage;

/**
 * Интерфейс для реализации парсера SMS
 */
public interface ISmsParser {

    /** */
    public ParsedMessage Parse(SmsMessage message) throws ParseSmsException;
}

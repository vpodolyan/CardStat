package org.company.cardstat;

import android.telephony.SmsMessage;

import org.company.lib.BankMessage;

/**
 * Интерфейс для реализации парсера SMS
 */
public interface ISmsParser {
    public BankMessage Parse(SmsMessage[] messages);
}

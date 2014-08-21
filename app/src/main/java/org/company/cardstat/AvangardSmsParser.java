package org.company.cardstat;

import android.telephony.SmsMessage;

import java.util.regex.Pattern;

public class AvangardSmsParser implements ISmsParser {
    /**
     * @param message
     */
    Pattern BUY_PATTERN = Pattern.compile("POKUPKA\\s(\\D*)\\,\\D*([0-9]+(\\.|\\,)[0-9]{1,2})\\s\\D*CARD\\*(\\d{4})");


    @Override
    public BankMessage Parse(SmsMessage message) {
        // Определить тип транзакции
        // Найти номер карточки
        // Определить назначение
        // Определить сумму
        // Вернуть объект BankMessage
        return null;
    }
}

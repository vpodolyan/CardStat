package org.company.cardstat;

import android.telephony.SmsMessage;

public class AvangardSmsParser implements ISmsParser {
    /**
     * @param message
     */
    @Override
    public BankMessage Parse(SmsMessage message) {
        // Найти номер карточки
        // Определить тип транзакции
        // Определить назначение
        // Определить сумму
        // Вернуть объект BankMessage
        return null;
    }
}

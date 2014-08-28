package org.company.cardstat;

import android.telephony.SmsMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AvangardSmsParser  implements ISmsParser
{
    /**
     * @param message
     */
    Pattern BUY_PATTERN = Pattern.compile("POKUPKA\\s(\\D*)\\,\\D*([0-9]+(\\.|\\,)[0-9]{1,2})\\s\\D*CARD\\*(\\d{4})");


    @Override
    public ParsedMessage Parse(SmsMessage msg) throws ParseSmsException {

        Matcher m = BUY_PATTERN.matcher(msg.getMessageBody());

        if (m.matches())
        {
            ParsedMessage parsedMsg = new ParsedMessage();
            // Определить тип транзакции
            parsedMsg.transactionName = "Оплата";
            // Найти номер карточки
            parsedMsg.card = Integer.parseInt(m.group(3));
            // Определить назначение
            parsedMsg.destination = m.group(0);
            // Определить сумму
            parsedMsg.sum = Double.parseDouble(m.group(1));
            return parsedMsg;
        }
        throw new ParseSmsException("Не удалось распарситть сообщение");
    }
}

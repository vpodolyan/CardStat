package org.company.cardstat;

import android.telephony.SmsMessage;

import org.company.lib.BankMessage;

public interface ISmsParser {
    public BankMessage Parse(SmsMessage[] messages);
}

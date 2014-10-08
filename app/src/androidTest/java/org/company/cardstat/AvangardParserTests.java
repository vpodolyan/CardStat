package org.company.cardstat;

import junit.framework.Assert;

import org.junit.Test;

public class AvangardParserTests {
    @Test
    public void test_Parse_buying_message_correctly() {
        AvangardSmsParser parser = new AvangardSmsParser();
        String smsBody = "POKUPKA CHICKEN HELL, RU 200 RUB, CARD*5222 IVAN IVANOV BAL 2000 RUB 10.09.14 14:31 END";
        try {
            ParsedMessage parsedMsg = parser.Parse(smsBody);
            Assert.assertEquals(5111, parsedMsg.card);
            Assert.assertEquals(200, parsedMsg.sum);
            Assert.assertEquals("CHICKEN HELL",parsedMsg.destination);
            Assert.assertEquals("POKUPKA", parsedMsg.transactionName);
        } catch (ParseSmsException e) {
            e.printStackTrace();
        }
    }
}

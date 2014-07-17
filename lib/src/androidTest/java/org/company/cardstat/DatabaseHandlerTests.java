package org.company.cardstat;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

public class DatabaseHandlerTests extends AndroidTestCase {
    private DatabaseHandler db;

    public void setUp() throws Exception{
        super.setUp();
        RenamingDelegatingContext context = new RenamingDelegatingContext(getContext(), "lib_test_");
        db = new DatabaseHandler(context);
    }

    public void testAddBankMessage() throws Exception{
        BankMessage originMsg = new BankMessage();
        originMsg.setSender("EasyMoney");

        long id = db.addBankMessage(originMsg);
        BankMessage storedMsg = db.getBankMessage(id);
        assertEquals(storedMsg.getSender(), originMsg.getSender());
    }

    public void tearDown() throws Exception{
        db.close();
        super.tearDown();
    }
}

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

    public void testAddBankMessage() throws Exception {
        BankMessage originMsg = new BankMessage();
        originMsg.setSender("EasyMoney");

        long id = db.addBankMessage(originMsg);
        BankMessage storedMsg = db.getBankMessage(id);
        assertEquals(storedMsg.getSender(), originMsg.getSender());
    }

    public void testAddBank() throws Exception {
        Bank originBank = new Bank();
        originBank.setName("EasyMoney");

        long id = db.addBank(originBank);
        Bank storedBank = db.getBank(id);
        assertEquals(storedBank.getName(), originBank.getName());
    }

    public void testAddTransaction() throws Exception {
        BankTransaction origin = new BankTransaction();
        origin.setSum(100);

        long id = db.addBankTransaction(origin);
        BankTransaction stored = db.getBankTransaction(id);
        assertEquals(stored.getSum(), origin.getSum());
    }

    public void tearDown() throws Exception{
        db.close();
        super.tearDown();
    }
}

package org.company.cardstat;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;


public class MyActivity extends Activity {

    private DatabaseHandler m_databaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        m_databaseHandler = new DatabaseHandler(this);
        if (m_databaseHandler != null) {
            // TODO: сделать нормальные логи
            System.out.println("=========good=========");
        } else {
            System.out.println("=========fail=========");
        }

        // С unit тестами я пока не разобрался, так что буду тестировать примо сдесь
        // TODO: убрать тесты
        try {
            List<Bank> banks = m_databaseHandler.getAllBanks();
            List<BankMessage> messages = m_databaseHandler.getAllBankMessages();
            List<BankTransaction> transactions = m_databaseHandler.getAllTransactions();
            List<BankTransactionType> types = m_databaseHandler.getAllBankTransactionTypes();
            //List<BankTransactionTypeKeyword> keywords = m_databaseHandler.get

            System.out.println("=========Банки:=========");
            for (int i = 0; i < banks.size(); i++) {
                System.out.println(banks.get(i).toString());
            }

            System.out.println("=========Сообщения:=========");
            for (int i = 0; i < messages.size(); i++) {
                System.out.println(messages.get(i).toString());
            }

            System.out.println("=========Транзакции:=========");
            for (int i = 0; i < transactions.size(); i++) {
                transactions.get(i).toString();
            }

        } catch (DatabaseHandlerException exception) {
            exception.printStackTrace();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

package org.company.cardstat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by klayman9 on 08.07.14.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    /** */
    private static final int DATABASE_VERSION = 1;

    /** */
    private static final String DATABASE_NAME = "CardStatDB";

    /**
     *
     * @param _context
     */
    public DatabaseHandler(Context _context) {

        super(_context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase _database) {

        String CREATE_MESSAGE_TABLE = "";
        String CREATE_TRANSACTION_TABLE = "";
        String CREATE_BANK_TABLE = "";
        String CREATE_TRANSACTION_TYPE_TABLE = "";

        _database.execSQL(CREATE_MESSAGE_TABLE);
        _database.execSQL(CREATE_TRANSACTION_TABLE);
        _database.execSQL(CREATE_BANK_TABLE);
        _database.execSQL(CREATE_TRANSACTION_TYPE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase _database, int _oldVersion, int _newVersion) {

        // TODO:
        // onCreate(_database);
    }

    /**
     *
     * @return
     * @throws DatabaseHandlerException
     */
    public List<BankMessage> getAllMessages() throws DatabaseHandlerException {

        List<BankMessage> messages = new ArrayList<BankMessage>();

        return  messages;
    }

    /**
     *
     * @throws DatabaseHandlerException
     */
    public void removeAllMessages() throws DatabaseHandlerException {

    }

    /**
     *
     * @param _content
     * @param _sender
     * @param _parsed
     * @return
     * @throws DatabaseHandlerException
     */
    public int addMessage(String _content, String _sender, boolean _parsed) throws DatabaseHandlerException {

        return -1;
    }

    /**
     *
     * @param _message
     * @return
     * @throws DatabaseHandlerException
     */
    public int addMessage(BankMessage _message) throws DatabaseHandlerException {

        return addMessage(_message.getContent(), _message.getSender(), _message.getParsed());
    }

    /**
     *
     * @param _id
     * @return
     * @throws DatabaseHandlerException
     */
    public BankMessage getMessage(int _id) throws DatabaseHandlerException {

        BankMessage message = new BankMessage();

        return  message;
    }

    /**
     *
     * @param _id
     * @param _content
     * @param _sender
     * @param _parsed
     */
    public void updateMessage(long _id, String _content, String _sender, boolean _parsed) {

    }

    /**
     *
     * @param _message
     */
    public void updateMessage(BankMessage _message) {

        updateMessage(_message.getId(), _message.getContent(), _message.getSender(),
                _message.getParsed());
    }

    /**
     *
     * @param _id
     * @throws DatabaseHandlerException
     */
    public void removeMessage(long _id) throws DatabaseHandlerException {

    }

    /**
     *
     * @param _message
     * @throws DatabaseHandlerException
     */
    public void removeMessage(BankMessage _message) throws DatabaseHandlerException {

        removeMessage(_message.getId());
    }

    /**
     *
     * @return
     */
    public List<BankTransaction> getAllTransactions() throws DatabaseHandlerException {

        List<BankTransaction> transactions = new ArrayList<BankTransaction>();

        SQLiteDatabase database = this.getReadableDatabase();
        String query = String.format("SELECT * FROM %s", BankTransaction.TABLE_NAME);
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {

            int id = cursor.getColumnIndex(BankTransaction.KEY_ID);
            int sum = cursor.getColumnIndex(BankTransaction.KEY_SUM);
            int typeId = cursor.getColumnIndex(BankTransaction.KEY_TYPE_ID);
            int bankId = cursor.getColumnIndex(BankTransaction.KEY_BANK_ID);

            do {
                BankTransaction transaction = new BankTransaction();
                transaction.setId(cursor.getLong(id));
                transaction.setSum(cursor.getFloat(sum));
                transaction.setTypeId(cursor.getLong(typeId));
                transaction.setBankId(cursor.getLong(bankId));

                transactions.add(transaction);

            } while (cursor.moveToNext());
        }

        database.close();

        return transactions;
    }

    /**
     *
     */
    public void removeAllTransactions() {

    }

    /**
     *
     * @param _sum
     * @param _type_id
     * @param _bank_id
     * @return
     */
    public int addTransaction(float _sum, long _type_id, long _bank_id) {

        return -1;
    }

    /**
     *
     * @param _transaction
     * @return
     */
    public int addTransaction(BankTransaction _transaction) {

        return addTransaction(_transaction.getSum(), _transaction.getTypeId(),
                _transaction.getBankId());
    }

    /**
     *
     * @param _id
     * @return
     */
    public BankTransaction getTransaction(int _id) {

        BankTransaction transaction = new BankTransaction();

        return transaction;
    }

    /**
     *
     * @param _id
     * @param _sum
     * @param _type_id
     * @param _bank_id
     */
    public void updateTransaction(long _id, float _sum, long _type_id, long _bank_id) {

    }

    /**
     *
     * @param _transaction
     */
    public void updateTransaction(BankTransaction _transaction) {

        updateTransaction(_transaction.getId(), _transaction.getSum(), _transaction.getTypeId(),
                _transaction.getBankId());
    }

    /**
     *
     * @param _id
     */
    public void removeTransaction(long _id) {

    }

    /**
     *
     * @param _transaction
     */
    public void removeTransaction(BankTransaction _transaction) {

        removeTransaction(_transaction.getId());
    }

    /**
     *
     * @return
     * @throws DatabaseHandlerException
     */
    public List<Bank> getAllBanks() throws DatabaseHandlerException {

        List<Bank> banks = new ArrayList<Bank>();

        SQLiteDatabase database = this.getReadableDatabase();
        String query = String.format("SELECT * FROM %s", Bank.TABLE_NAME);
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {

            int id = cursor.getColumnIndex(Bank.KEY_ID);
            int name = cursor.getColumnIndex(Bank.KEY_NAME);

            do {
                Bank bank = new Bank();
                bank.setId(cursor.getInt(id));
                bank.setName(cursor.getString(name));

                banks.add(bank);

            } while (cursor.moveToNext());
        }

        database.close();

        return banks;
    }

    /**
     *
     * @throws DatabaseHandlerException
     */
    public void removeAllBanks() throws DatabaseHandlerException {

        SQLiteDatabase database = this.getWritableDatabase();
        // TODO: database.delete()
        database.close();
    }

    /**
     *
     * @param _name
     * @return
     */
    public long addBank(String _name) throws DatabaseHandlerException {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Bank.KEY_NAME, _name);

        long id = database.insert(Bank.TABLE_NAME, null, values);
        database.close();

        if (id < 1) {
            throw new DatabaseHandlerException();
        }

        return id;
    }

    /**
     * Добавляет в базу новый банк.
     * @param _bank банк.
     * @return id банка.
     * @throws DatabaseHandlerException
     */
    public long addBank(Bank _bank) throws DatabaseHandlerException {

        return addBank(_bank.getName());
    }

    /**
     *
     * @return
     */
    public List<BankTransactionType> getAllOperationTypes() {

        List<BankTransactionType> types = new ArrayList<BankTransactionType>();

        SQLiteDatabase database = this.getReadableDatabase();
        String query = String.format("SELECT * FROM &s", BankTransactionType.TABLE_NAME);
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {

            int id = cursor.getColumnIndex(BankTransactionType.KEY_ID);
            int name = cursor.getColumnIndex(BankTransactionType.KEY_NAME);

            do {
                BankTransactionType type = new BankTransactionType();
                type.setId(cursor.getLong(id));
                type.setName(cursor.getString(name));

                types.add(type);

            } while (cursor.moveToNext());
        }

        database.close();

        return types;
    }
}

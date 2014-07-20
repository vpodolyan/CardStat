package org.company.cardstat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс для работы с базой
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    /** Версия базы данных */
    private static final int DATABASE_VERSION = 1;

    /** Имя базы данных */
    private static final String DATABASE_NAME = "CardStatDB";

    /**
     * Конструктор класса
     * @param _context контекст
     */
    public DatabaseHandler(Context _context) {

        super(_context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase _database) {

        /** bank (id, name) */
        String CREATE_BANK_TABLE = String.format("CREATE TABLE IF NOT EXISTS %s ( " +
                "%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT )", Bank.TABLE_NAME,
                Bank.KEY_ID, Bank.KEY_NAME);

        /** message (id, content, sender, parsed) */
        String CREATE_MESSAGE_TABLE = String.format("CREATE TABLE IF NOT EXISTS %s ( " +
                "%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s BOOLEAN )",
                BankMessage.TABLE_NAME, BankMessage.KEY_ID, BankMessage.KEY_CONTENT,
                BankMessage.KEY_SENDER, BankMessage.KEY_PARSED);

        /** transaction (id, sum, type_id, bank_id) */
        String CREATE_TRANSACTION_TABLE = String.format("CREATE TABLE IF NOT EXISTS %s ( " +
                "%s INTEGER PRIMARY KEY AUTOINCREMENT, %s FLOAT, %s INTEGER, %s INTEGER )",
                BankTransaction.TABLE_NAME, BankTransaction.KEY_ID, BankTransaction.KEY_SUM,
                BankTransaction.KEY_TYPE_ID, BankTransaction.KEY_BANK_ID);

        /** transaction_type (id, name) */
        String CREATE_TRANSACTION_TYPE_TABLE = String.format("CREATE TABLE IF NOT EXISTS %s ( " +
                "%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT )", BankTransactionType.TABLE_NAME,
                BankTransactionType.KEY_ID, BankTransactionType.KEY_NAME);

        /** transaction_type_keyword (id, word, transaction_type_id) */
        String CREATE_TRANSACTION_TYPE_KEYWORD_TABLE
                = String.format("CREATE TABLE IF NOT EXISTS %s ( " +
                "%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s INTEGER )",
                BankTransactionTypeKeyword.TABLE_NAME, BankTransactionTypeKeyword.KEY_ID,
                BankTransactionTypeKeyword.KEY_WORD, BankTransactionTypeKeyword.KEY_TYPE_ID);

        _database.execSQL(CREATE_BANK_TABLE);
        _database.execSQL(CREATE_MESSAGE_TABLE);
        _database.execSQL(CREATE_TRANSACTION_TABLE);
        _database.execSQL(CREATE_TRANSACTION_TYPE_TABLE);
        _database.execSQL(CREATE_TRANSACTION_TYPE_KEYWORD_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase _database, int _oldVersion, int _newVersion) {

        _database.execSQL(String.format("DROP TABLE IF EXISTS %s", Bank.TABLE_NAME));
        _database.execSQL(String.format("DROP TABLE IF EXISTS %s", BankMessage.TABLE_NAME));
        _database.execSQL(String.format("DROP TABLE IF EXISTS %s", BankTransaction.TABLE_NAME));
        _database.execSQL(String.format("DROP TABLE IF EXISTS %s", BankTransactionType.TABLE_NAME));
        _database.execSQL(String.format("DROP TABLE IF EXISTS %s",
                BankTransactionTypeKeyword.TABLE_NAME));

        onCreate(_database);
    }

    /**
     * Добавляет банк в базу данных
     * @param _name имя банка
     * @param _number номер телефона
     * @return ID
     * @throws DatabaseHandlerException что-то пошло не так
     */
    public long addBank(String _name, String _number) throws DatabaseHandlerException {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Bank.KEY_NAME, _name);
        values.put(Bank.KEY_NUMBER, _number);

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

        return addBank(_bank.getName(), _bank.getNumber());
    }

    /**
     * Возвращает банк по указанному ID
     * @param _id ID
     * @return банк
     * @throws DatabaseHandlerException такого банка нет
     */
    public Bank getBank(long _id) throws DatabaseHandlerException {

        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.query(Bank.TABLE_NAME, new String[] { Bank.KEY_ID, Bank.KEY_NAME },
                String.format("%s = ?", Bank.KEY_ID), new String[] { String.valueOf(_id) },
                null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        } else {
            throw  new DatabaseHandlerException();
        }

        Bank bank = getBank(cursor);

        database.close();

        return bank;
    }

    /**
     * Возвращает банк по указанному номеру телефона
     * @param _number номер телефона
     * @return банк
     * @throws DatabaseHandlerException банк не найден
     */
    public Bank getBank(String _number) throws DatabaseHandlerException {

        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.query(Bank.TABLE_NAME, new String[] { Bank.KEY_ID, Bank.KEY_NAME },
                String.format("%s = ?", Bank.KEY_NUMBER), new String[] { _number },
                null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        } else {
            throw  new DatabaseHandlerException();
        }

        Bank bank = getBank(cursor);

        return bank;
    }

    /**
     * Возвращает список банков
     * @return список банков
     * @throws DatabaseHandlerException исключение
     */
    public List<Bank> getAllBanks() throws DatabaseHandlerException {

        List<Bank> banks = new ArrayList<Bank>();

        SQLiteDatabase database = this.getReadableDatabase();
        String query = String.format("SELECT * FROM %s", Bank.TABLE_NAME);
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {

            do {
                Bank bank = getBank(cursor);
                banks.add(bank);

            } while (cursor.moveToNext());
        }

        database.close();

        return banks;
    }

    /**
     *
     * @param _cursor
     * @return
     * @throws DatabaseHandlerException
     */
    private Bank getBank(Cursor _cursor) throws DatabaseHandlerException {

        int id = _cursor.getColumnIndex(Bank.KEY_ID);
        int name = _cursor.getColumnIndex(Bank.KEY_NAME);
        int number = _cursor.getColumnIndex(Bank.KEY_NUMBER);

        Bank bank = new Bank();
        bank.setId(_cursor.getLong(id));
        bank.setName(_cursor.getString(name));
        bank.setNumber(_cursor.getString(number));

        return bank;
    }

    /**
     *
     * @param _id
     * @param _name
     * @param _number
     * @return
     * @throws DatabaseHandlerException
     */
    public long updateBank(long _id, String _name, String _number) throws DatabaseHandlerException {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Bank.KEY_NAME, _name);
        values.put(Bank.KEY_NUMBER, _number);

        long id = database.update(Bank.TABLE_NAME, values, String.format("%s = ?", Bank.KEY_ID),
                new String[] { String.valueOf(_id) });

        database.close();

        return id;
    }

    /**
     *
     * @param _bank
     * @return
     * @throws DatabaseHandlerException
     */
    public long updateBank(Bank _bank) throws DatabaseHandlerException {

        return updateBank(_bank.getId(), _bank.getName(), _bank.getNumber());
    }

    /**
     *
     * @param _id
     * @throws DatabaseHandlerException
     */
    public void deleteBank(long _id) throws DatabaseHandlerException {

        SQLiteDatabase database = this.getWritableDatabase();

        database.delete(Bank.TABLE_NAME, String.format("%s = ?", Bank.KEY_ID),
                new String[] { String.valueOf(_id) });

        database.close();
    }

    /**
     *
     * @param _bank
     * @throws DatabaseHandlerException
     */
    public void deleteBank(Bank _bank) throws DatabaseHandlerException {

        deleteBank(_bank.getId());
    }

    /**
     * Добавляет в базу новове сообщение.
     * @param _content полный текст сообщения.
     * @param _sender отправитель сообщения.
     * @param _parsed обработано ли сообщение.
     * @return ID.
     * @throws DatabaseHandlerException
     */
    public long addBankMessage(String _content, String _sender, boolean _parsed)
            throws DatabaseHandlerException {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BankMessage.KEY_CONTENT, _content);
        values.put(BankMessage.KEY_SENDER, _sender);
        values.put(BankMessage.KEY_PARSED, _parsed);

        long id = database.insert(BankMessage.TABLE_NAME, null, values);
        if (id < 1) {
            throw new DatabaseHandlerException();
        }

        database.close();

        return id;
    }

    /**
     *
     * @param _message
     * @return
     * @throws DatabaseHandlerException
     */
    public long addBankMessage(BankMessage _message) throws DatabaseHandlerException {

        return addBankMessage(_message.getContent(), _message.getSender(), _message.getParsed());
    }

    /**
     *
     * @param _id
     * @return
     * @throws DatabaseHandlerException
     */
    public BankMessage getBankMessage(long _id) throws DatabaseHandlerException {

        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.query(BankMessage.TABLE_NAME, new String[] { BankMessage.KEY_ID,
                BankMessage.KEY_CONTENT, BankMessage.KEY_SENDER, BankMessage.KEY_PARSED},
                String.format("%s = ?", BankMessage.KEY_ID), new String[] { String.valueOf(_id) },
                null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        } else {
            throw  new DatabaseHandlerException();
        }

        BankMessage message = getBankMessage(cursor);

        database.close();
        return  message;
    }

    /**
     * Возвращает список всех сообщений от банков
     * @return список всех сообщений от банков
     * @throws DatabaseHandlerException
     */
    public List<BankMessage> getAllBankMessages() throws DatabaseHandlerException {

        List<BankMessage> messages = new ArrayList<BankMessage>();

        SQLiteDatabase database = this.getReadableDatabase();
        String query = String.format("SELECT * FROM %s", BankMessage.TABLE_NAME);
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {

            do {
                BankMessage message = getBankMessage(cursor);
                messages.add(message);

            } while (cursor.moveToNext());
        }

        database.close();
        return  messages;
    }

    /**
     *
     * @param _cursor
     * @return
     * @throws DatabaseHandlerException
     */
    private BankMessage getBankMessage(Cursor _cursor) throws DatabaseHandlerException {

        int id = _cursor.getColumnIndex(BankMessage.KEY_ID);
        int content = _cursor.getColumnIndex(BankMessage.KEY_CONTENT);
        int sender = _cursor.getColumnIndex(BankMessage.KEY_SENDER);
        int parsed = _cursor.getColumnIndex(BankMessage.KEY_PARSED);

        BankMessage message = new BankMessage();
        message.setId(_cursor.getLong(id));
        message.setContent(_cursor.getString(content));
        message.setSender(_cursor.getString(sender));
        // TODO: любой тип в boolean
        //message.setParsed(_cursor.getInt(parsed));

        return message;
    }

    /**
     *
     * @param _id
     * @param _content
     * @param _sender
     * @param _parsed
     * @return
     * @throws DatabaseHandlerException
     */
    public long updateBankMessage(long _id, String _content, String _sender, boolean _parsed)
            throws DatabaseHandlerException {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BankMessage.KEY_CONTENT, _content);
        values.put(BankMessage.KEY_SENDER, _sender);
        values.put(BankMessage.KEY_PARSED, _parsed);

        long id = database.update(Bank.TABLE_NAME, values, String.format("%s = ?",
                BankMessage.KEY_ID), new String[] { String.valueOf(_id) });

        database.close();
        return id;
    }

    /**
     *
     * @param _message
     * @return
     * @throws DatabaseHandlerException
     */
    public long updateBankMessage(BankMessage _message) throws DatabaseHandlerException {

        return updateBankMessage(_message.getId(), _message.getContent(), _message.getSender(),
                _message.getParsed());
    }

    /**
     * Удаляет сообщение от банка из бызы данных.
     * @param _id ID сообщения.
     * @throws DatabaseHandlerException ошибка
     */
    public void deleteBankMessage(long _id) throws DatabaseHandlerException {

        SQLiteDatabase database = this.getWritableDatabase();

        database.delete(BankMessage.TABLE_NAME, String.format("%s = ?", BankMessage.KEY_ID),
                new String[] { String.valueOf(_id) });

        database.close();
    }

    /**
     *
     * @param _message
     * @throws DatabaseHandlerException
     */
    public void deleteBankMessage(BankMessage _message) throws DatabaseHandlerException {

        deleteBankMessage(_message.getId());
    }

    /**
     *
     * @param _sum
     * @param _type_id
     * @param _bank_id
     * @return
     * @throws DatabaseHandlerException
     */
    public long addBankTransaction(float _sum, long _type_id, long _bank_id)
            throws DatabaseHandlerException {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BankTransaction.KEY_SUM, _sum);
        values.put(BankTransaction.KEY_TYPE_ID, _type_id);
        values.put(BankTransaction.KEY_BANK_ID, _bank_id);

        long id = database.insert(BankTransaction.TABLE_NAME, null, values);
        if (id < 1) {
            throw new DatabaseHandlerException();
        }

        database.close();
        return id;
    }

    /**
     *
     * @param _transaction
     * @return
     * @throws DatabaseHandlerException
     */
    public long addBankTransaction(BankTransaction _transaction) throws DatabaseHandlerException {

        return addBankTransaction(_transaction.getSum(), _transaction.getTypeId(),
                _transaction.getBankId());
    }

    /**
     *
     * @param _id
     * @return
     * @throws DatabaseHandlerException
     */
    public BankTransaction getBankTransaction(long _id) throws DatabaseHandlerException {

        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.query(BankTransaction.TABLE_NAME,
                new String[] { BankTransaction.KEY_ID, BankTransaction.KEY_SUM,
                BankTransaction.KEY_TYPE_ID, BankTransaction.KEY_BANK_ID}, String.format("%s = ?",
                BankTransaction.KEY_ID), new String[] { String.valueOf(_id) },
                null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        } else {
            throw  new DatabaseHandlerException();
        }

        BankTransaction transaction = getBankTransaction(cursor);

        database.close();
        return  transaction;
    }

    /**
     *
     * @return
     * @throws DatabaseHandlerException
     */
    public List<BankTransaction> getAllTransactions() throws DatabaseHandlerException {

        List<BankTransaction> transactions = new ArrayList<BankTransaction>();

        SQLiteDatabase database = this.getReadableDatabase();
        String query = String.format("SELECT * FROM %s", BankTransaction.TABLE_NAME);
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {

            do {
                BankTransaction transaction = getBankTransaction(cursor);
                transactions.add(transaction);

            } while (cursor.moveToNext());
        }

        database.close();
        return transactions;
    }

    /**
     *
     * @param cursor
     * @return
     * @throws DatabaseHandlerException
     */
    private BankTransaction getBankTransaction(Cursor cursor) throws DatabaseHandlerException {

        int id = cursor.getColumnIndex(BankTransaction.KEY_ID);
        int sum = cursor.getColumnIndex(BankTransaction.KEY_SUM);
        int typeId = cursor.getColumnIndex(BankTransaction.KEY_TYPE_ID);
        int bankId = cursor.getColumnIndex(BankTransaction.KEY_BANK_ID);

        BankTransaction transaction = new BankTransaction();
        transaction.setId(cursor.getLong(id));
        transaction.setSum(cursor.getFloat(sum));
        transaction.setTypeId(cursor.getLong(typeId));
        transaction.setBankId(cursor.getLong(bankId));

        return transaction;
    }

    /**
     *
     * @param _id
     * @param _sum
     * @param _type_id
     * @param _bank_id
     * @return
     * @throws DatabaseHandlerException
     */
    public long updateBankTransaction(long _id, float _sum, long _type_id, long _bank_id)
            throws DatabaseHandlerException {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BankTransaction.KEY_SUM, _sum);
        values.put(BankTransaction.KEY_TYPE_ID, _type_id);
        values.put(BankTransaction.KEY_BANK_ID, _bank_id);

        long id = database.update(BankTransaction.TABLE_NAME, values, String.format("%s = ?",
                BankTransaction.KEY_ID), new String[] { String.valueOf(_id) });

        database.close();

        return id;
    }

    /**
     *
     * @param _transaction
     * @return
     * @throws DatabaseHandlerException
     */
    public long updateBankTransaction(BankTransaction _transaction)
            throws DatabaseHandlerException {

        return updateBankTransaction(_transaction.getId(), _transaction.getSum(),
                _transaction.getTypeId(), _transaction.getBankId());
    }

    /**
     *
     * @param _id
     * @throws DatabaseHandlerException
     */
    public void deleteBankTransaction(long _id) throws DatabaseHandlerException {

        SQLiteDatabase database = this.getWritableDatabase();

        database.delete(BankTransaction.TABLE_NAME, String.format("%s = ?", BankTransaction.KEY_ID),
                new String[] { String.valueOf(_id) });

        database.close();
    }

    /**
     *
     * @param _transaction
     * @throws DatabaseHandlerException
     */
    public void deleteBankTransaction(BankTransaction _transaction)
            throws DatabaseHandlerException {

        deleteBankTransaction(_transaction.getId());
    }

    /**
     *
     * @throws DatabaseHandlerException
     */
    public void removeAllTransactions() throws DatabaseHandlerException {

        // TODO:
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
     * @throws DatabaseHandlerException
     */
    public long addBankTransactionType(String _name) throws DatabaseHandlerException {

        SQLiteDatabase database = this.getWritableDatabase();


        ContentValues values = new ContentValues();
        values.put(BankTransactionType.KEY_NAME, _name);

        long id = database.insert(BankTransactionType.TABLE_NAME, null, values);
        if (id < 1) {
            throw new DatabaseHandlerException();
        }

        database.close();

        return id;
    }

    /**
     *
     * @param _transactionType
     * @return
     * @throws DatabaseHandlerException
     */
    public long addBankTransactionType(BankTransactionType _transactionType)
            throws DatabaseHandlerException {

        return addBankTransactionType(_transactionType.getName());
    }

    /**
     *
     * @param _id
     * @return
     * @throws DatabaseHandlerException
     */
    public BankTransactionType getBankTransactionType(long _id) throws DatabaseHandlerException {

        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.query(BankTransactionType.TABLE_NAME,
                new String[] { BankTransactionType.KEY_ID, BankTransactionType.KEY_NAME },
                String.format("%s = ?", BankTransactionType.KEY_ID),
                new String[] { String.valueOf(_id) }, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        } else {
            throw  new DatabaseHandlerException();
        }

        BankTransactionType transaction = getBankTransactionType(cursor);

        database.close();
        return transaction;
    }

    /**
     *
     * @return
     * @throws DatabaseHandlerException
     */
    public List<BankTransactionType> getAllBankTransactionTypes() throws DatabaseHandlerException {

        List<BankTransactionType> types = new ArrayList<BankTransactionType>();

        SQLiteDatabase database = this.getReadableDatabase();
        String query = String.format("SELECT * FROM %s", BankTransactionType.TABLE_NAME);
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {

            do {
                BankTransactionType type = getBankTransactionType(cursor);
                types.add(type);

            } while (cursor.moveToNext());
        }

        database.close();
        return types;
    }

    /**
     *
     * @param cursor
     * @return
     * @throws DatabaseHandlerException
     */
    private BankTransactionType getBankTransactionType(Cursor cursor)
            throws DatabaseHandlerException {

        int id = cursor.getColumnIndex(BankTransactionType.KEY_ID);
        int name = cursor.getColumnIndex(BankTransactionType.KEY_NAME);

        BankTransactionType type = new BankTransactionType();
        type.setId(cursor.getLong(id));
        type.setName(cursor.getString(name));

        return  type;
    }

    /**
     *
     * @param _id
     * @param _name
     * @return
     * @throws DatabaseHandlerException
     */
    public long updateBankTransactionType(long _id, String _name)
            throws DatabaseHandlerException {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BankTransactionType.KEY_NAME, _name);


        long id = database.update(BankTransactionType.TABLE_NAME, values, String.format("%s = ?",
                BankTransactionType.KEY_ID), new String[] { String.valueOf(_id) });

        database.close();

        return id;
    }

    /**
     *
     * @param _transactionType
     * @return
     * @throws DatabaseHandlerException
     */
    public long updateBankTransactionType(BankTransactionType _transactionType)
            throws DatabaseHandlerException {

        return updateBankTransactionType(_transactionType.getId(), _transactionType.getName());
    }

    /**
     *
     * @param _id
     * @throws DatabaseHandlerException
     */
    public void deleteBankTransactionType(long _id) throws DatabaseHandlerException {

        SQLiteDatabase database = this.getWritableDatabase();

        database.delete(BankTransactionType.TABLE_NAME,
                String.format("%s = ?", BankTransactionType.KEY_ID),
                new String[] { String.valueOf(_id) });

        database.close();
    }

    /**
     *
     * @param _bankTransactionType
     * @throws DatabaseHandlerException
     */
    public void deleteBankTransactionType(BankTransactionType _bankTransactionType)
            throws DatabaseHandlerException {

        deleteBank(_bankTransactionType.getId());
    }

    /**
     *
     * @param _keyword
     * @return
     * @throws DatabaseHandlerException
     */
    public long addTransactionTypeKeyword(String _keyword, long _typeId) throws DatabaseHandlerException {

        SQLiteDatabase database = this.getWritableDatabase();


        ContentValues values = new ContentValues();
        values.put(BankTransactionTypeKeyword.KEY_WORD, _keyword);
        values.put(BankTransactionTypeKeyword.KEY_TYPE_ID, _typeId);

        long id = database.insert(BankTransactionTypeKeyword.TABLE_NAME, null, values);
        if (id < 1) {
            throw new DatabaseHandlerException();
        }

        database.close();

        return id;
    }

    /**
     *
     * @param _keyword
     * @return
     * @throws DatabaseHandlerException
     */
    public long addTransactionTypeKeyword(BankTransactionTypeKeyword _keyword)
            throws DatabaseHandlerException {

        return addTransactionTypeKeyword(_keyword.getWord(), _keyword.getTransactionTypeId());
    }

    /**
     *
     * @param _id
     * @return
     * @throws DatabaseHandlerException
     */
    public BankTransactionTypeKeyword getTransactionTypeKeyword(long _id)
            throws DatabaseHandlerException {

        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.query(BankTransactionTypeKeyword.TABLE_NAME,
                new String[] { BankTransactionTypeKeyword.KEY_ID,
                        BankTransactionTypeKeyword.KEY_WORD },
                String.format("%s = ?", BankTransactionTypeKeyword.KEY_ID),
                new String[] { String.valueOf(_id) }, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        } else {
            throw  new DatabaseHandlerException();
        }

        BankTransactionTypeKeyword transactionTypeKeyword = getBankTransactionTypeKeyword(cursor);

        database.close();
        return transactionTypeKeyword;
    }

    /**
     *
     * @return
     * @throws DatabaseHandlerException
     */
    public List<BankTransactionTypeKeyword> getAllTransactionTypeKeywords()
            throws DatabaseHandlerException {

        List<BankTransactionTypeKeyword> keywords = new ArrayList<BankTransactionTypeKeyword>();

        SQLiteDatabase database = this.getReadableDatabase();
        String query = String.format("SELECT * FROM &s", BankTransactionTypeKeyword.TABLE_NAME);
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {

            int id = cursor.getColumnIndex(BankTransactionTypeKeyword.KEY_ID);
            int word = cursor.getColumnIndex(BankTransactionTypeKeyword.KEY_WORD);

            do {
                BankTransactionTypeKeyword keyword = getBankTransactionTypeKeyword(cursor);
                keywords.add(keyword);

            } while (cursor.moveToNext());
        }

        database.close();

        return keywords;
    }

    /**
     *
     * @param cursor
     * @return
     * @throws DatabaseHandlerException
     */
    private BankTransactionTypeKeyword getBankTransactionTypeKeyword(Cursor cursor)
            throws DatabaseHandlerException {

        int id = cursor.getColumnIndex(BankTransactionTypeKeyword.KEY_ID);
        int word = cursor.getColumnIndex(BankTransactionTypeKeyword.KEY_WORD);

        BankTransactionTypeKeyword keyword = new BankTransactionTypeKeyword();
        keyword.setId(cursor.getLong(id));
        keyword.setWord(cursor.getString(word));

        return keyword;
    }

    /**
     *
     * @param _id
     * @param _word
     * @return
     * @throws DatabaseHandlerException
     */
    public long updateTransactionTypeKeyword(long _id, String _word)
            throws DatabaseHandlerException {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BankTransactionTypeKeyword.KEY_WORD, _word);


        long id = database.update(BankTransactionTypeKeyword.TABLE_NAME, values,
                String.format("%s = ?", BankTransactionTypeKeyword.KEY_ID),
                new String[] { String.valueOf(_id) });

        database.close();

        return id;
    }

    /**
     *
     * @param _keyword
     * @return
     * @throws DatabaseHandlerException
     */
    public long updateTransactionTypeKeyword(BankTransactionTypeKeyword _keyword)
            throws DatabaseHandlerException {

        return updateTransactionTypeKeyword(_keyword.getId(), _keyword.getWord());
    }

    /**
     *
     * @param _id
     * @throws DatabaseHandlerException
     */
    public void deleteTransactionTypeKeyword(long _id) throws DatabaseHandlerException {

        SQLiteDatabase database = this.getWritableDatabase();

        database.delete(BankTransactionTypeKeyword.TABLE_NAME,
                String.format("%s = ?", BankTransactionTypeKeyword.KEY_ID),
                new String[] { String.valueOf(_id) });

        database.close();
    }

    /**
     *
     * @param _keyword
     * @throws DatabaseHandlerException
     */
    public void deleteTransactionTypeKeyword(BankTransactionTypeKeyword _keyword)
            throws DatabaseHandlerException {

        deleteTransactionTypeKeyword(_keyword.getId());
    }
}

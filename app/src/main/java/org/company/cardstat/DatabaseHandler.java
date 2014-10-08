package org.company.cardstat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.company.cardstat.domain.*;

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
                "%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT)", Bank.TABLE_NAME,
                Bank.KEY_ID, Bank.KEY_NAME, Bank.KEY_NUMBER);

        /** message (id, content, sender, parsed) */
        String CREATE_MESSAGE_TABLE = String.format("CREATE TABLE IF NOT EXISTS %s ( " +
                "%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s BOOLEAN )",
                BankMessage.TABLE_NAME, BankMessage.KEY_ID, BankMessage.KEY_CONTENT,
                BankMessage.KEY_SENDER, BankMessage.KEY_PARSED);

        /** transaction (id, sum, type_id, bank_id) */
        String CREATE_TRANSACTION_TABLE = String.format("CREATE TABLE IF NOT EXISTS %s ( " +
                "%s INTEGER PRIMARY KEY AUTOINCREMENT, %s FLOAT, %s INTEGER, %s INTEGER, %s TEXT )",
                BankTransaction.TABLE_NAME, BankTransaction.KEY_ID, BankTransaction.KEY_SUM,
                BankTransaction.KEY_TYPE_ID, BankTransaction.KEY_CARD_ID, BankTransaction.KEY_DESTINATION);

        /** transaction_type (id, name) */
        String CREATE_TRANSACTION_TYPE_TABLE = String.format("CREATE TABLE IF NOT EXISTS %s ( " +
                "%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT )", TransactionType.TABLE_NAME,
                TransactionType.KEY_ID, TransactionType.KEY_NAME);

        /** transaction_type_keyword (id, word, transaction_type_id) */
        String CREATE_TRANSACTION_TYPE_KEYWORD_TABLE
                = String.format("CREATE TABLE IF NOT EXISTS %s ( " +
                "%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s INTEGER )",
                BankTransactionTypeKeyword.TABLE_NAME, BankTransactionTypeKeyword.KEY_ID,
                BankTransactionTypeKeyword.KEY_WORD, BankTransactionTypeKeyword.KEY_TYPE_ID);

        String CREATE_BANK_CARD_TABLE = String.format("CREATE TABLE IF NOT EXISTS %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER, %s TEXT)",
                BankCard.TABLE_NAME, BankCard.KEY_ID, BankCard.KEY_NUMBER, BankCard.KEY_NAME);

        _database.execSQL(CREATE_BANK_TABLE);
        _database.execSQL(CREATE_BANK_CARD_TABLE);
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
        _database.execSQL(String.format("DROP TABLE IF EXISTS %s", TransactionType.TABLE_NAME));
        _database.execSQL(String.format("DROP TABLE IF EXISTS %s",
                BankTransactionTypeKeyword.TABLE_NAME));

        onCreate(_database);
    }

    /**
     * Добавляет банк в базу данных
     * @param _name имя банка
     * @param _number номер телефона
     * @return ID
     * @throws org.company.cardstat.DatabaseHandlerException что-то пошло не так
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
     * @throws org.company.cardstat.DatabaseHandlerException
     */
    public long addBank(Bank _bank) throws DatabaseHandlerException {

        return addBank(_bank.getName(), _bank.getNumber());
    }

    /**
     * Возвращает банк по указанному ID
     * @param _id ID
     * @return банк
     * @throws org.company.cardstat.DatabaseHandlerException такого банка нет
     */
    public Bank getBank(long _id) throws DatabaseHandlerException {

        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.query(Bank.TABLE_NAME, new String[] { Bank.KEY_ID, Bank.KEY_NAME, Bank.KEY_NUMBER },
                String.format("%s = ?", Bank.KEY_ID), new String[] { String.valueOf(_id) },
                null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        } else {
            throw  new DatabaseHandlerException();
        }

        database.close();
        return getBank(cursor);
    }

    /**
     * Возвращает банк по указанному номеру телефона
     * @param _number номер телефона
     * @return банк
     * @throws org.company.cardstat.DatabaseHandlerException банк не найден
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

        return getBank(cursor);
    }

    /**
     * Возвращает список банков
     * @return список банков
     * @throws org.company.cardstat.DatabaseHandlerException исключение
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
     * Возвращает банк из результата запроса
     * @param _cursor результат запроса
     * @return банк
     * @throws org.company.cardstat.DatabaseHandlerException
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
     * Обновляет банк
     * @param _id ID
     * @param _name имя
     * @param _number номер
     * @return ID
     * @throws org.company.cardstat.DatabaseHandlerException
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
     * Обновляет банк
     * @param _bank банк
     * @return ID
     * @throws org.company.cardstat.DatabaseHandlerException
     */
    public long updateBank(Bank _bank) throws DatabaseHandlerException {

        return updateBank(_bank.getId(), _bank.getName(), _bank.getNumber());
    }

    /**
     * Удаляет банк из базы данных
     * @param _id ID
     * @throws org.company.cardstat.DatabaseHandlerException
     */
    public void deleteBank(long _id) throws DatabaseHandlerException {

        SQLiteDatabase database = this.getWritableDatabase();

        database.delete(Bank.TABLE_NAME, String.format("%s = ?", Bank.KEY_ID),
                new String[] { String.valueOf(_id) });

        database.close();
    }

    /**
     * Удаляет банк из базы данных
     * @param _bank банк
     * @throws org.company.cardstat.DatabaseHandlerException
     */
    public void deleteBank(Bank _bank) throws DatabaseHandlerException {

        deleteBank(_bank.getId());
    }

    /**
     *
     * @param _name
     * @param _number
     * @return
     * @throws org.company.cardstat.DatabaseHandlerException
     */
    public long addBankCard(String _name, long _number)
            throws DatabaseHandlerException {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BankCard.KEY_NAME, _name);
        values.put(BankCard.KEY_NUMBER, _number);

        long id = database.insert(BankCard.TABLE_NAME, null, values);
        database.close();

        if (id < 1) {
            throw new DatabaseHandlerException();
        }

        return id;
    }

    /**
     *
     * @param _card
     * @return
     * @throws org.company.cardstat.DatabaseHandlerException
     */
    public long addBankCard(BankCard _card) throws DatabaseHandlerException {

        return addBankCard(_card.getName(), _card.getNumber());
    }

    /**
     *
     * @param _number Номер банковской карты
     * @return
     * @throws org.company.cardstat.DatabaseHandlerException
     */
    public BankCard getBankCard(long _number) throws DatabaseHandlerException {

        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.query(BankCard.TABLE_NAME,
                null,
                String.format("%s = ?", BankCard.KEY_NUMBER), new String[] { String.valueOf( _number ) },
                null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        } else {
            throw  new DatabaseHandlerException();
        }

        database.close();
        return getBankCard(cursor);
    }

    /**
     *
     * @param _number
     * @return
     * @throws org.company.cardstat.DatabaseHandlerException
     */
    public BankCard getBankCard(String _number) throws DatabaseHandlerException {

        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.query(BankCard.TABLE_NAME,
                new String[]{BankCard.KEY_ID, BankCard.KEY_NAME},
                String.format("%s = ?", BankCard.KEY_NUMBER), new String[]{_number},
                null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        } else {
            throw  new DatabaseHandlerException();
        }

        database.close();
        return getBankCard(cursor);
    }

    /**
     *
     * @return
     * @throws org.company.cardstat.DatabaseHandlerException
     */
    public List<BankCard> getAllBankCard() throws DatabaseHandlerException {

        List<BankCard> cards = new ArrayList<BankCard>();

        SQLiteDatabase database = this.getReadableDatabase();
        String query = String.format("SELECT * FROM %s", BankCard.TABLE_NAME);
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {

            do {
                BankCard card = getBankCard(cursor);
                cards.add(card);

            } while (cursor.moveToNext());
        }

        database.close();
        return cards;
    }

    /**
     *
     * @param _cursor
     * @return
     */
    public BankCard getBankCard(Cursor _cursor) {

        int id = _cursor.getColumnIndex(BankCard.KEY_ID);
        int name = _cursor.getColumnIndex(BankCard.KEY_NAME);
        int number = _cursor.getColumnIndex(BankCard.KEY_NUMBER);

        BankCard card = new BankCard();
        card.setId(_cursor.getLong(id));
        card.setName(_cursor.getString(name));
        card.setNumber(_cursor.getLong(number));
        return card;
    }

    /**
     *
     * @param _id
     * @param _name
     * @param _number
     * @return
     * @throws org.company.cardstat.DatabaseHandlerException
     */
    public long updateBankCard(long _id, String _name, long _number)
            throws DatabaseHandlerException {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BankCard.KEY_NAME, _name);
        values.put(BankCard.KEY_NUMBER, _number);

        long id = database.update(Bank.TABLE_NAME, values, String.format("%s = ?", Bank.KEY_ID),
                new String[] { String.valueOf(_id) });

        database.close();
        return id;
    }

    /**
     *
     * @param _card
     * @return
     * @throws org.company.cardstat.DatabaseHandlerException
     */
    public long updateBankCard(BankCard _card) throws DatabaseHandlerException {

        return updateBankCard(_card.getId(), _card.getName(), _card.getNumber());
    }

    /**
     *
     * @param _id
     * @throws org.company.cardstat.DatabaseHandlerException
     */
    public void deleteBankCard(long _id) throws DatabaseHandlerException {

        SQLiteDatabase database = this.getWritableDatabase();

        database.delete(BankCard.TABLE_NAME, String.format("%s = ?", BankCard.KEY_ID),
                new String[] { String.valueOf(_id) });

        database.close();
    }

    /**
     *
     * @param _card
     * @throws org.company.cardstat.DatabaseHandlerException
     */
    public void deleteBankCard(BankCard _card) throws DatabaseHandlerException {

        deleteBankCard(_card.getId());
    }

    /**
     * Добавляет в базу новове сообщение.
     * @param _content полный текст сообщения.
     * @param _sender отправитель сообщения.
     * @param _parsed обработано ли сообщение.
     * @return ID.
     * @throws org.company.cardstat.DatabaseHandlerException
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
     * Добавляет в базу сообщение от банка
     * @param _message сообщение
     * @return ID
     * @throws org.company.cardstat.DatabaseHandlerException
     */
    public long addBankMessage(BankMessage _message) throws DatabaseHandlerException {

        return addBankMessage(_message.getContent(), _message.getSender(), _message.getParsed());
    }

    /**
     * Возвращет сообщение от банка по ID
     * @param _id ID сообщения
     * @return сообщение
     * @throws org.company.cardstat.DatabaseHandlerException
     */
    public BankMessage getBankMessage(long _id) throws DatabaseHandlerException {

        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.query(BankMessage.TABLE_NAME, new String[]{BankMessage.KEY_ID,
                        BankMessage.KEY_CONTENT, BankMessage.KEY_SENDER, BankMessage.KEY_PARSED},
                String.format("%s = ?", BankMessage.KEY_ID), new String[]{String.valueOf(_id)},
                null, null, null, null
        );

        if (cursor != null) {
            cursor.moveToFirst();
        } else {
            throw  new DatabaseHandlerException();
        }

        database.close();
        return  getBankMessage(cursor);
    }

    /**
     * Возвращает список всех сообщений от банков
     * @return список всех сообщений от банков
     * @throws org.company.cardstat.DatabaseHandlerException
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
     * Возвращает сообщение от банка из результата запроса
     * @param _cursor результат запроса
     * @return сообщение от банка
     * @throws org.company.cardstat.DatabaseHandlerException
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
        message.setParsed(Boolean.parseBoolean(_cursor.getString(parsed)));

        return message;
    }

    /**
     * Обновляет сообщение
     * @param _id ID сообщения
     * @param _content содержание
     * @param _sender отправитель сообщения
     * @param _parsed распарсено или нет
     * @return ID
     * @throws org.company.cardstat.DatabaseHandlerException
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
     * Обновялет сообщение
     * @param _message сообщение
     * @return ID
     * @throws org.company.cardstat.DatabaseHandlerException
     */
    public long updateBankMessage(BankMessage _message) throws DatabaseHandlerException {

        return updateBankMessage(_message.getId(), _message.getContent(), _message.getSender(),
                _message.getParsed());
    }

    /**
     * Удаляет сообщение от банка из бызы данных.
     * @param _id ID сообщения.
     * @throws org.company.cardstat.DatabaseHandlerException ошибка
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
     * @throws org.company.cardstat.DatabaseHandlerException
     */
    public void deleteBankMessage(BankMessage _message) throws DatabaseHandlerException {

        deleteBankMessage(_message.getId());
    }

    /**
     * Добавляет транзакцию
     * @param _sum сумма
     * @param _type_id ID типа
     * @param _card_id ID карты
     * @return ID транзакции
     * @throws org.company.cardstat.DatabaseHandlerException
     */
    public long addBankTransaction(float _sum, long _type_id, long _card_id)
            throws DatabaseHandlerException {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BankTransaction.KEY_SUM, _sum);
        values.put(BankTransaction.KEY_TYPE_ID, _type_id);
        values.put(BankTransaction.KEY_CARD_ID, _card_id);

        long id = database.insert(BankTransaction.TABLE_NAME, null, values);
        if (id < 1) {
            throw new DatabaseHandlerException();
        }

        database.close();
        return id;
    }

    /**
     * Добавляет транзакцию
     * @param _transaction транзакция
     * @return ID
     * @throws org.company.cardstat.DatabaseHandlerException
     */
    public long addBankTransaction(BankTransaction _transaction) throws DatabaseHandlerException {

        return addBankTransaction(_transaction.getSum(), _transaction.getTypeId(),
                _transaction.getCardId());
    }

    /**
     * Возвращает транзакцию по ID
     * @param _id ID
     * @return транзакция
     * @throws org.company.cardstat.DatabaseHandlerException
     */
    public BankTransaction getBankTransaction(long _id) throws DatabaseHandlerException {

        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.query(BankTransaction.TABLE_NAME,
                new String[] { BankTransaction.KEY_ID, BankTransaction.KEY_SUM,
                BankTransaction.KEY_TYPE_ID, BankTransaction.KEY_CARD_ID}, String.format("%s = ?",
                BankTransaction.KEY_ID), new String[] { String.valueOf(_id) },
                null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        } else {
            throw  new DatabaseHandlerException();
        }

        database.close();
        return  getBankTransaction(cursor);
    }

    /**
     * Возвращает все транзакции
     * @return список транзакция
     * @throws org.company.cardstat.DatabaseHandlerException
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
     * Возвращает транзакцию из результата запроса
     * @param cursor результат запроса
     * @return транзакция
     * @throws org.company.cardstat.DatabaseHandlerException
     */
    private BankTransaction getBankTransaction(Cursor cursor) throws DatabaseHandlerException {

        int id = cursor.getColumnIndex(BankTransaction.KEY_ID);
        int sum = cursor.getColumnIndex(BankTransaction.KEY_SUM);
        int typeId = cursor.getColumnIndex(BankTransaction.KEY_TYPE_ID);
        int bankId = cursor.getColumnIndex(BankTransaction.KEY_CARD_ID);

        BankTransaction transaction = new BankTransaction();
        transaction.setId(cursor.getLong(id));
        transaction.setSum(cursor.getFloat(sum));
        transaction.setTypeId(cursor.getLong(typeId));
        transaction.setCardId(cursor.getLong(bankId));

        return transaction;
    }

    /**
     * Обновляет транзакцию
     * @param _id ID транзакции
     * @param _sum сумма
     * @param _type_id ID типа транзакции
     * @param _card_id ID банка
     * @return  ID транзакции
     * @throws org.company.cardstat.DatabaseHandlerException
     */
    public long updateBankTransaction(long _id, float _sum, long _type_id, long _card_id)
            throws DatabaseHandlerException {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BankTransaction.KEY_SUM, _sum);
        values.put(BankTransaction.KEY_TYPE_ID, _type_id);
        values.put(BankTransaction.KEY_CARD_ID, _card_id);

        long id = database.update(BankTransaction.TABLE_NAME, values, String.format("%s = ?",
                BankTransaction.KEY_ID), new String[] { String.valueOf(_id) });

        database.close();
        return id;
    }

    /**
     * Обновляет транзакцию
     * @param _transaction транзакция
     * @return ID транзакции
     * @throws org.company.cardstat.DatabaseHandlerException
     */
    public long updateBankTransaction(BankTransaction _transaction)
            throws DatabaseHandlerException {

        return updateBankTransaction(_transaction.getId(), _transaction.getSum(),
                _transaction.getTypeId(), _transaction.getCardId());
    }

    /**
     * Удаляет транзакцию
     * @param _id ID транзакции
     * @throws org.company.cardstat.DatabaseHandlerException
     */
    public void deleteBankTransaction(long _id) throws DatabaseHandlerException {

        SQLiteDatabase database = this.getWritableDatabase();

        database.delete(BankTransaction.TABLE_NAME, String.format("%s = ?", BankTransaction.KEY_ID),
                new String[] { String.valueOf(_id) });

        database.close();
    }

    /**
     * Удаляет транзакцию
     * @param _transaction транзакция
     * @throws org.company.cardstat.DatabaseHandlerException
     */
    public void deleteBankTransaction(BankTransaction _transaction)
            throws DatabaseHandlerException {

        deleteBankTransaction(_transaction.getId());
    }

    /**
     * Удаляет из базы все транзакии
     * @throws org.company.cardstat.DatabaseHandlerException
     */
    public void removeAllTransactions() throws DatabaseHandlerException {

        // TODO:
    }

    /**
     * Удаляет из базы все банки
     * @throws org.company.cardstat.DatabaseHandlerException
     */
    public void removeAllBanks() throws DatabaseHandlerException {

        SQLiteDatabase database = this.getWritableDatabase();
        // TODO: database.delete()
        database.close();
    }

    /**
     * Добавляет тип транзакции
     * @param _name имя типа
     * @return ID типа
     * @throws org.company.cardstat.DatabaseHandlerException
     */
    public long addBankTransactionType(String _name) throws DatabaseHandlerException {

        SQLiteDatabase database = this.getWritableDatabase();


        ContentValues values = new ContentValues();
        values.put(TransactionType.KEY_NAME, _name);

        long id = database.insert(TransactionType.TABLE_NAME, null, values);
        if (id < 1) {
            throw new DatabaseHandlerException();
        }

        database.close();
        return id;
    }

    /**
     * Добавляет тип транзакции
     * @param _transactionType тип транзакции
     * @return ID
     * @throws org.company.cardstat.DatabaseHandlerException
     */
    public long addBankTransactionType(TransactionType _transactionType)
            throws DatabaseHandlerException {

        return addBankTransactionType(_transactionType.getName());
    }

    /**
     * Возвращает тип транзакции по ID
     * @param _id ID
     * @return тип транзакции
     * @throws org.company.cardstat.DatabaseHandlerException
     */
    public TransactionType getTransactionType(long _id) throws DatabaseHandlerException {

        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.query(TransactionType.TABLE_NAME,
                new String[] { TransactionType.KEY_ID, TransactionType.KEY_NAME },
                String.format("%s = ?", TransactionType.KEY_ID),
                new String[] { String.valueOf(_id) }, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        } else {
            throw  new DatabaseHandlerException();
        }

        database.close();
        return getBankTransactionType(cursor);
    }

    /**
     * Возвращает тип транзакции по ее названию
     * @param name название типа транзакции
     * @return объект типа транзакции; null, если не найден
     * @throws DatabaseHandlerException
     */
    public TransactionType getTransactionType(String name)  {
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.query(TransactionType.TABLE_NAME,
                new String[] {TransactionType.KEY_ID, TransactionType.KEY_NAME},
                String.format("%s = ?", TransactionType.KEY_NAME),
                new String[] { String.valueOf(name)}, null, null, null, null);

        if (cursor == null)
            return null;

        cursor.moveToFirst();
        database.close();

        try {
            return getBankTransactionType(cursor);
        }
        catch (DatabaseHandlerException e)
        {
            Log.e("CardStat", "Ошибка в методе getTransactionType. " + e.getMessage());
            return null;
        }
    }

    /**
     * Возвращает все типы транзакций
     * @return список типов транзакций
     * @throws org.company.cardstat.DatabaseHandlerException
     */
    public List<TransactionType> getAllBankTransactionTypes() throws DatabaseHandlerException {

        List<TransactionType> types = new ArrayList<TransactionType>();

        SQLiteDatabase database = this.getReadableDatabase();
        String query = String.format("SELECT * FROM %s", TransactionType.TABLE_NAME);
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {

            do {
                TransactionType type = getBankTransactionType(cursor);
                types.add(type);

            } while (cursor.moveToNext());
        }

        database.close();
        return types;
    }

    /**
     * Возвращает тип транзакции из результата запроса
     * @param cursor результат запроса
     * @return тип транзакции
     * @throws org.company.cardstat.DatabaseHandlerException
     */
    private TransactionType getBankTransactionType(Cursor cursor)
            throws DatabaseHandlerException {

        int id = cursor.getColumnIndex(TransactionType.KEY_ID);
        int name = cursor.getColumnIndex(TransactionType.KEY_NAME);

        TransactionType type = new TransactionType();
        type.setId(cursor.getLong(id));
        type.setName(cursor.getString(name));

        return type;
    }

    /**
     * Обновляет тип транзакции
     * @param _id ID транзакции
     * @param _name имя
     * @return ID транзакции
     * @throws org.company.cardstat.DatabaseHandlerException
     */
    public long updateBankTransactionType(long _id, String _name)
            throws DatabaseHandlerException {

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TransactionType.KEY_NAME, _name);


        long id = database.update(TransactionType.TABLE_NAME, values, String.format("%s = ?",
                TransactionType.KEY_ID), new String[] { String.valueOf(_id) });

        database.close();
        return id;
    }

    /**
     * Обновляет тип транзакции
     * @param _transactionType тип транзакции
     * @return ID
     * @throws org.company.cardstat.DatabaseHandlerException
     */
    public long updateBankTransactionType(TransactionType _transactionType)
            throws DatabaseHandlerException {

        return updateBankTransactionType(_transactionType.getId(), _transactionType.getName());
    }

    /**
     * Удаляет тип транзакции по ID
     * @param _id ID транзакции
     * @throws org.company.cardstat.DatabaseHandlerException
     */
    public void deleteBankTransactionType(long _id) throws DatabaseHandlerException {

        SQLiteDatabase database = this.getWritableDatabase();

        database.delete(TransactionType.TABLE_NAME,
                String.format("%s = ?", TransactionType.KEY_ID),
                new String[] { String.valueOf(_id) });

        database.close();
    }

    /**
     * Удаляет тип транзакции
     * @param _TransactionType тип транзакции
     * @throws org.company.cardstat.DatabaseHandlerException
     */
    public void deleteBankTransactionType(TransactionType _TransactionType)
            throws DatabaseHandlerException {

        deleteBank(_TransactionType.getId());
    }

    /**
     * Добавляет ключевое слово типа транзакции
     * @param _keyword ключевое слово
     * @return ID
     * @throws org.company.cardstat.DatabaseHandlerException
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
     * Добавляет ключевое слово типа транзакции
     * @param _keyword ключевое слово типа транзакции
     * @return ID
     * @throws org.company.cardstat.DatabaseHandlerException
     */
    public long addTransactionTypeKeyword(BankTransactionTypeKeyword _keyword)
            throws DatabaseHandlerException {

        return addTransactionTypeKeyword(_keyword.getWord(), _keyword.getTransactionTypeId());
    }

    /**
     * Возвращает ключевое слово типа транзакции
     * @param _id ID
     * @return ключевое слово типа транзакции
     * @throws org.company.cardstat.DatabaseHandlerException
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

        database.close();
        return getBankTransactionTypeKeyword(cursor);
    }

    /**
     * Возвращает список всех ключевых слов типов ранзакций
     * @return список всех ключевых слов типов ранзакций
     * @throws org.company.cardstat.DatabaseHandlerException
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
     * Возвращает ключевое слово типа транзакции из результата запроса
     * @param cursor результат запроса
     * @return ключевое слово
     * @throws org.company.cardstat.DatabaseHandlerException
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
     * Обновляет ключевое слово типа транзакции
     * @param _id ID
     * @param _word ключевое слово
     * @return ID ключевого слова
     * @throws org.company.cardstat.DatabaseHandlerException
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
     * Обновляет ключевое слово типа транзакции
     * @param _keyword ключевое слово типа транзакции
     * @return ID типа
     * @throws org.company.cardstat.DatabaseHandlerException
     */
    public long updateTransactionTypeKeyword(BankTransactionTypeKeyword _keyword)
            throws DatabaseHandlerException {

        return updateTransactionTypeKeyword(_keyword.getId(), _keyword.getWord());
    }

    /**
     * Удаляет ключевое слово типа транзакции
     * @param _id ID
     * @throws org.company.cardstat.DatabaseHandlerException
     */
    public void deleteTransactionTypeKeyword(long _id) throws DatabaseHandlerException {

        SQLiteDatabase database = this.getWritableDatabase();

        database.delete(BankTransactionTypeKeyword.TABLE_NAME,
                String.format("%s = ?", BankTransactionTypeKeyword.KEY_ID),
                new String[] { String.valueOf(_id) });

        database.close();
    }

    /**
     * Удаляет ключевое слово типа транзакции
     * @param _keyword ключевое слово типа транзакции
     * @throws org.company.cardstat.DatabaseHandlerException
     */
    public void deleteTransactionTypeKeyword(BankTransactionTypeKeyword _keyword)
            throws DatabaseHandlerException {

        deleteTransactionTypeKeyword(_keyword.getId());
    }
}

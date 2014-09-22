package org.company.cardstat.domain;

/**
 * Created by klayman9 on 08.07.14.
 */

/**
 *
 */
public class BankTransactionTypeKeyword extends DatabaseObject {

    /** */
    public static final String TABLE_NAME = "t_key_word",

    /** */
    KEY_WORD = "word",

    /** */
    KEY_TYPE_ID = "transaction_type_id";

    /** */
    private String m_word;

    /** */
    private long m_transaction_type_id;

    /**
     *
     */
    public BankTransactionTypeKeyword() {

        super();
        m_word = "";
        m_transaction_type_id = -1;
    }

    /**
     *
     * @param _word
     */
    public BankTransactionTypeKeyword(long _id, String _word, long _transaction_type_id) {

        super(_id);
        m_word = _word;
        m_transaction_type_id = _transaction_type_id;
    }

    /**
     *
     * @return
     */
    public String getWord() {

        return m_word;
    }

    /**
     *
     * @param _word
     */
    public void setWord(String _word) {

        m_word = _word;
    }

    /**
     *
     * @return
     */
    public long getTransactionTypeId() {

        return m_transaction_type_id;
    }

    /**
     *
     * @param _transaction_type_id
     */
    public void setTransactionTypeId(long _transaction_type_id) {

        m_transaction_type_id = _transaction_type_id;
    }
}

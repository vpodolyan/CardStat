package org.company.cardstat;

/**
 * Created by klayman9 on 08.07.14.
 */
public class BankTransactionTypeKeyword extends DatabaseObject {

    /** */
    public static final String TABLE_NAME = "t_key_word",

    /** */
    KEY_WORD = "word";

    /** */
    private String m_word;

    /**
     *
     */
    public BankTransactionTypeKeyword() {

        super();
    }

    /**
     *
     * @param _word
     */
    public BankTransactionTypeKeyword(long _id, String _word) {

        super(_id);
        m_word = _word;
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
}

package org.company.cardstat;

/**
 * Created by klayman9 on 22.07.14.
 */
public class BankCard extends DatabaseObject {

    /** */
    public static final String TABLE_NAME = "t_card",

    /** Имя банка */
    KEY_NAME = "name",

    /** Номер */
    KEY_NUMBER = "number";

    /** */
    private long m_number;

    /** */
    private long m_transactionId;

    /**
     *
     */
    public BankCard() {

        super();
    }

    /**
     *
     * @return
     */
    public long getNumber() {

        return m_number;
    }

    /**
     *
     * @param _number
     */
    public void setNumber(long _number) {

        m_number = _number;
    }

    /**
     *
     * @return
     */
    public long getTransactionId() {

        return m_transactionId;
    }

    /**
     * 
     * @param _transactionId
     */
    public void setTransactionId(long _transactionId) {

        m_transactionId = _transactionId;
    }
}

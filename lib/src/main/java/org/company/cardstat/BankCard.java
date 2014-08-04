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
    KEY_NUMBER = "number",

    /** */
    KEY_TRANSACTION_ID = "transaction_id";

    /** */
    private String m_name;

    /** Primary Account Number - номер банковской карты */
    private long m_number;

    public BankCard() {

        super();
    }

    public String getName() {

        return m_name;
    }

    /**
     *
     * @param _name
     */
    public void setName(String _name) {

        m_name = _name;
    }

    public long getNumber() {

        return m_number;
    }

    /**
     *
     * @param _number новый номер банковской карты
     */
    public void setNumber(long _number) {

        m_number = _number;
    }

}

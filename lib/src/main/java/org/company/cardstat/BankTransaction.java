package org.company.cardstat;

/**
 * Created by klayman9 on 07.07.14.
 */
public class BankTransaction extends DatabaseObject {

    /** */
    public static final String TABLE_NAME = "t_transaction",

    /** */
    KEY_SUM = "sum",

    /** */
    KEY_TYPE_ID = "type_id",

    /** */
    KEY_BANK_ID = "bank_id";

    /** */
    private float m_sum;

    /** */
    private long m_typeId;

    /** */
    private long m_bankId;

    /**
     *
     */
    public BankTransaction() {

        super();
        m_sum = 0.0f;
        m_typeId = -1;
        m_bankId = -1;
    }

    /**
     *
     * @param _id
     * @param _sum
     * @param _type_id
     * @param _bank_id
     */
    public BankTransaction(long _id, float _sum, int _type_id, int _bank_id) {

        super(_id);
        m_sum = _sum;
        m_typeId = _type_id;
        m_bankId = _bank_id;
    }

    /**
     *
     * @return
     */
    public long getId() {

        return m_id;
    }

    /**
     *
     * @param _id
     */
    public void setId(long _id) {

        m_id = _id;
    }

    /**
     *
     * @return
     */
    public float getSum() {

        return m_sum;
    }

    /**
     *
     * @param _sum
     */
    public void setSum(float _sum) {

        m_sum = _sum;
    }

    /**
     *
     * @return
     */
    public long getTypeId() {

        return m_typeId;
    }

    /**
     *
     * @param _typeId
     */
    public void setTypeId(long _typeId) {

        m_typeId = _typeId;
    }

    /**
     *
     * @return
     */
    public long getBankId() {

        return m_bankId;
    }

    /**
     *
     * @param _bankId
     */
    public void setBankId(long _bankId) {

        m_bankId = _bankId;
    }
}
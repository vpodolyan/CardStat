package org.company.cardstat;

/**
 * Created by klayman9 on 07.07.14.
 */

/**
 *
 */
public class BankTransaction extends DatabaseObject {

    /** */
    public static final String TABLE_NAME = "t_transaction",

    /** */
    KEY_SUM = "sum",

    /** */
    KEY_TYPE_ID = "type_id",

    /** */
    KEY_CARD_ID = "card_id",

    KEY_DESTINATION = "destination";

    /** */
    private float mSum;

    /** */
    private long mTypeId;

    private long mCardId;

    private String mDestination;

    /**
     *
     */
    public BankTransaction() {

        super();
        mSum = 0.0f;
        mTypeId = -1;
        mCardId = -1;
    }

    /**
     *
     * @param _id
     * @param _sum
     * @param _type_id
     * @param _card_id
     */
    public BankTransaction(long _id, float _sum, int _type_id, int _card_id) {

        super(_id);
        mSum = _sum;
        mTypeId = _type_id;
        mCardId = _card_id;
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

        return mSum;
    }

    /**
     *
     * @param _sum
     */
    public void setSum(float _sum) {

        mSum = _sum;
    }

    /**
     *
     * @return
     */
    public long getTypeId() {

        return mTypeId;
    }

    /**
     *
     * @param _typeId
     */
    public void setTypeId(long _typeId) {

        mTypeId = _typeId;
    }

    /**
     *
     * @return
     */
    public long getCardId() {

        return mCardId;
    }

    /**
     *
     * @param _bankId
     */
    public void setCardId(long _bankId) {

        mCardId = _bankId;
    }

    public String getDestination()
    {
        return mDestination;
    }

    public void setDestination(String _dest)
    {
        mDestination = _dest;
    }
}
package org.company.cardstat;

/**
 * Created by klayman9 on 07.07.14.
 */

/**
 *
 */
public class BankTransactionType extends DatabaseObject {

    /** */
    public static final String TABLE_NAME = "t_operation_type",

    /** */
    KEY_NAME = "name";

    /** */
    private String m_name;

    /**
     *
     */
    public BankTransactionType() {

        super();
        m_name = "Unknown";
    }

    /**
     *
     * @param _id
     * @param _name
     */
    public BankTransactionType(long _id, String _name) {

        super(_id);
        m_name = _name;
    }

    /**
     *
     * @return
     */
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
}

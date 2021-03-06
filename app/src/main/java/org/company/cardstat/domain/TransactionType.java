package org.company.cardstat.domain;

/**
 * Created by klayman9 on 07.07.14.
 */

/**
 *
 */
public class TransactionType extends DatabaseObject {

    /** */
    public static final String TABLE_NAME = "t_operation_type",

    /** */
    KEY_NAME = "name";

    /** */
    private String m_name;

    /**
     *
     */
    public TransactionType() {

        super();
        m_name = "Unknown";
    }

    /**
     *
     * @param _id
     * @param _name
     */
    public TransactionType(long _id, String _name) {

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

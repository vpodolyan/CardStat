package org.company.cardstat;

/**
 * Created by klayman9 on 07.07.14.
 */

/**
 *
 */
public abstract class DatabaseObject {

    /** */
    public static final String KEY_ID = "id";

    /** */
    protected long m_id;

    /**
     *
     */
    public DatabaseObject() {

        m_id = -1;
    }

    public DatabaseObject(long _id) {

        m_id = _id;
    }

    /**
     *
     * @return
     */
    protected long getId() {

        return m_id;
    }

    /**
     *
     * @param _id
     */
    protected void setId(long _id) {

        m_id = _id;
    }
}

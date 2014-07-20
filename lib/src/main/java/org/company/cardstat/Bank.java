package org.company.cardstat;

/**
 * Created by klayman9 on 07.07.14.
 */
public class Bank extends DatabaseObject {

    /** */
    public static final String TABLE_NAME = "t_bank",

    /** Имя банка */
    KEY_NAME = "name",

    /** Номер */
    KEY_NUMBER = "number";

    /** Имя банка */
    private String m_name;

    /** Номер с которого приходят сообщения */
    private String m_number;

    /**
     *
     */
    public Bank() {

        super();
        m_name = "Unknown";
    }

    /**
     *
     * @param _id
     * @param _name
     */
    public Bank(int _id, String _name) {

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

    /**
     *
     * @return
     */
    public String getNumber() {

        return m_number;
    }

    /**
     *
     * @param _number
     */
    public void setNumber(String _number) {

        m_number = _number;
    }

    @Override
    public String toString() {

        return String.format("Id = %d, name = %s", m_id, m_name);
    }
}
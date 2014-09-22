package org.company.cardstat.domain;

/**
 * Created by klayman9 on 07.07.14.
 */

/**
 *
 */
public class BankMessage extends DatabaseObject {

    /** */
    public static final String TABLE_NAME = "t_message",

    /** */
    KEY_CONTENT = "content",

    /** */
    KEY_SENDER = "sender",

    /** */
    KEY_PARSED = "parsed";

    /** */
    private String m_content;

    /** */
    private String m_sender;

    /** */
    private boolean m_parsed;

    /**
     *
     */
    public BankMessage() {

        super();
    }

    /**
     *
     * @param _id
     * @param _content
     * @param _sender
     * @param _parsed
     */
    public BankMessage(long _id, String _content, String _sender, boolean _parsed) {

        super(_id);
        m_content = _content;
        m_sender = _sender;
        m_parsed = _parsed;
    }

    /**
     *
     * @return
     */
    public String getContent() {

        return m_content;
    }

    /**
     *
     * @param _content
     */
    public void setContent(String _content) {

        m_content = _content;
    }

    /**
     *
     * @return
     */
    public String getSender() {

        return m_sender;
    }

    /**
     *
     * @param _sender
     */
    public void setSender(String _sender) {

        m_sender = _sender;
    }

    /**
     *
     * @return
     */
    public boolean getParsed() {

        return m_parsed;
    }

    /**
     *
     * @param _parsed
     */
    public void setParsed(boolean _parsed) {

        m_parsed = _parsed;
    }

    @Override
    public String toString() {

        return String.format("Id = %d, sender = %s", m_id, m_sender);
    }
}
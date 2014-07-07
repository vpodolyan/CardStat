package org.company.lib;

/**
 * Created by klayman9 on 07.07.14.
 */
public class DatabaseHandlerException extends Exception {

    /**
     *
     */
    public DatabaseHandlerException() {

        super();
    }

    /**
     *
     * @param _message
     * @param _cause
     */
    public DatabaseHandlerException(String _message, Throwable _cause) {

        super(_message, _cause);
    }

    /**
     *
     * @param _message
     */
    public DatabaseHandlerException(String _message) {

        super(_message);
    }

    /**
     *
     * @param _cause
     */
    public DatabaseHandlerException(Throwable _cause) {

        super(_cause);
    }
}
package org.company.cardstat.domain;

/**
 * Created by klayman9 on 07.07.14.
 */

import org.company.cardstat.DatabaseHandlerException;

/**
 *
 */
public interface IDatabaseObject {

    /**
     *
     * @param _object
     * @return
     * @throws org.company.cardstat.DatabaseHandlerException
     */
    public long add(DatabaseObject _object) throws DatabaseHandlerException;

    /**
     *
     * @param _id
     * @return
     * @throws org.company.cardstat.DatabaseHandlerException
     */
    public DatabaseObject get(long _id) throws DatabaseHandlerException;

    /**
     *
     * @param _object
     * @return
     * @throws org.company.cardstat.DatabaseHandlerException
     */
    public long update(DatabaseObject _object) throws DatabaseHandlerException;

    /**
     *
     * @param _id
     * @throws org.company.cardstat.DatabaseHandlerException
     */
    public void delete(int _id) throws DatabaseHandlerException;

    /**
     *
     * @param _object
     * @throws org.company.cardstat.DatabaseHandlerException
     */
    public void delete(DatabaseObject _object) throws DatabaseHandlerException;
}
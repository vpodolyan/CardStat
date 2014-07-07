package org.company.lib;

/**
 * Created by klayman9 on 07.07.14.
 */
public interface IDatabaseObject {

    /**
     *
     * @param _object
     * @return
     * @throws DatabaseHandlerException
     */
    public long add(DatabaseObject _object) throws DatabaseHandlerException;

    /**
     *
     * @param _id
     * @return
     * @throws DatabaseHandlerException
     */
    public DatabaseObject get(long _id) throws DatabaseHandlerException;

    /**
     *
     * @param _object
     * @return
     * @throws DatabaseHandlerException
     */
    public long update(DatabaseObject _object) throws DatabaseHandlerException;

    /**
     *
     * @param _id
     * @throws DatabaseHandlerException
     */
    public void delete(int _id) throws DatabaseHandlerException;

    /**
     *
     * @param _object
     * @throws DatabaseHandlerException
     */
    public void delete(DatabaseObject _object) throws DatabaseHandlerException;
}
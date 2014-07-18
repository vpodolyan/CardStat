package org.company.cardstat;

/**
 * Интерфейс инициализатора базы данных
 */
public interface IDbInitializer {
   /**
    * @param db Объект для работы с БД.
    * Наполняет базу заранее заданными значениями.
   */
   public void InitializeDb(DatabaseHandler db);
}

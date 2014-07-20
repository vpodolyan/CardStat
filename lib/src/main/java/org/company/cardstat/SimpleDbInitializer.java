package org.company.cardstat;

public class SimpleDbInitializer implements IDbInitializer {
    DatabaseHandler m_databaseHandler;

    @Override
    public void InitializeDb(DatabaseHandler db) {
        m_databaseHandler = db;
        createKeyWorkDictionary();
    }

    /**
     * Наполняет базу ключевыми словами.
     */
    private void createKeyWorkDictionary() {

        /** Пополнение счёта */
        String[] typeOneKeywords = { "пополнен", "пополнение" };

        /** */
        String[] typeTwoKeywords = { "расчёт", "оплата" };

        /** */
        String[] typeThreeKeywords = { "снятие", "cash" };

        try {

            long typeOneId = m_databaseHandler.addBankTransactionType("Пополнение");
            long typeTowId = m_databaseHandler.addBankTransactionType("Оплата");
            long typeThreeId = m_databaseHandler.addBankTransactionType("Наличные");

            for (int i = 0; i < typeOneKeywords.length; i++) {

                m_databaseHandler.addTransactionTypeKeyword(typeOneKeywords[i], typeOneId);
            }

            for (int i = 0; i < typeTwoKeywords.length; i++) {

                m_databaseHandler.addTransactionTypeKeyword(typeTwoKeywords[i], typeTowId);
            }

            for (int i = 0; i < typeThreeKeywords.length; i++) {

                m_databaseHandler.addTransactionTypeKeyword(typeThreeKeywords[i], typeThreeId);
            }
        } catch (DatabaseHandlerException exception) {
            exception.printStackTrace();
        }
    }
}

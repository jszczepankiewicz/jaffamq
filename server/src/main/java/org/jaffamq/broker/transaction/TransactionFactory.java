package org.jaffamq.broker.transaction;

import org.jaffamq.broker.StompMessageSender;

/**
 * Factory that creates transactions. Currently it only creates Transactions of type
 * VolatileMemoryTransaction.
 */
public class TransactionFactory {

    private TransactionFactory(){

    }

    public static Transaction createTransaction(String clientTransactionName, StompMessageSender sender){
        return new VolatileMemoryTransaction(clientTransactionName, sender);
    }
}

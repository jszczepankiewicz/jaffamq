package org.jaffamq.broker.transaction;

import org.jaffamq.Errors;
import org.jaffamq.RequestValidationFailedException;

/**
 * Verifies that transaction has expected status.
 */
public class TransactionStatusVerifier {

    public static void assertTransactionStatus(Transaction.Status target, Transaction.Status current) throws RequestValidationFailedException {
        if(target == Transaction.Status.ROLLBACKED){
            if(current == Transaction.Status.ROLLBACKED){
                throw new RequestValidationFailedException(Errors.TRANSACTION_ALREADY_ABORTED);
            }

            if(current == Transaction.Status.COMMITED){
                throw new RequestValidationFailedException(Errors.TRANSACTION_TO_ROLLBACK_ALREADY_COMMITED);
            }
        }
        else if(target == Transaction.Status.COMMITED){
            if(current == Transaction.Status.COMMITED){
                throw new RequestValidationFailedException(Errors.TRANSACTION_ALREADY_COMMITED);
            }

            if(current == Transaction.Status.ROLLBACKED){
                throw new RequestValidationFailedException(Errors.TRANSACTION_TO_COMMIT_ALREADY_ABORTED);
            }
        }
    }
}

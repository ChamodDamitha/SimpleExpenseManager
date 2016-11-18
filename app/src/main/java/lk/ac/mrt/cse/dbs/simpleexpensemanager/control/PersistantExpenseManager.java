package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistantAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistantTransactionDAO;

/**
 * Created by Chamod on 11/19/2016.
 */
public class PersistantExpenseManager extends ExpenseManager {
    private Context ctx = null;

    //Constructor
    public PersistantExpenseManager(Context ctx) {
        this.ctx = ctx;
        setup();
    }


    public void setup()  {

        //Setup AccountDAO object
        AccountDAO persistentAccountDAO = new PersistantAccountDAO(ctx);
        setAccountsDAO(persistentAccountDAO);

        //Setup TransactionDAO object
        TransactionDAO persistentTransactionDAO = new PersistantTransactionDAO(ctx);
        setTransactionsDAO(persistentTransactionDAO);

    }
}

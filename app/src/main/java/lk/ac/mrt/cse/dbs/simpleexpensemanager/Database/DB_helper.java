package lk.ac.mrt.cse.dbs.simpleexpensemanager.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by Chamod on 11/18/2016.
 */
public class DB_helper extends SQLiteOpenHelper {
    protected static final String db_name = "140542B";
    private static DB_helper db_helper = null;
    private static final int db_version = 1;

    public static final String accounts_table = "Accounts";
    public static final String account_no = "account_no";
    public static final String bank_name = "bank_name";
    public static final String account_holder_name = "account_holder_name";
    public static final String account_balance = "account_balance";

    public static final String transactions_table = "Transations";
    public static final String transaction_id = "transaction_id";
    public static final String date = "date";
    public static final String expense_type = "expense_type";
    public static final String amount = "amount";


    public static DB_helper getInstance(Context context) {
        if (db_helper == null)
            db_helper = new DB_helper(context);
        return db_helper;
    }

    private DB_helper(Context context) {
        super(context, db_name, null, db_version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String accountTable = String.format("CREATE TABLE %s(%s VARCHAR(20) NOT NULL PRIMARY KEY," +
                "%s VARCHAR(100) NULL,%s VARCHAR(100) NULL,%s DECIMAL(10,2) NULL )", accounts_table,
                account_no, bank_name, account_holder_name, account_balance);

        String transactionTable = String.format("CREATE TABLE %s(%s INTEGER PRIMARY KEY AUTOINCREMENT " +
                "NOT NULL,%s VARCHAR(100) NOT NULL,%s DATE NULL,%s DECIMAL(10,2) NULL," +
                "%s VARCHAR(100) NULL, FOREIGN KEY(%s) REFERENCES %s(%s))", "transactions",
                transaction_id, account_no, date, amount, expense_type, account_no, accountTable, account_no);

        db.execSQL(accountTable);
        db.execSQL(transactionTable);
        
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + accounts_table);
        db.execSQL("DROP TABLE IF EXISTS " + transactions_table);
        onCreate(db);
    }

}

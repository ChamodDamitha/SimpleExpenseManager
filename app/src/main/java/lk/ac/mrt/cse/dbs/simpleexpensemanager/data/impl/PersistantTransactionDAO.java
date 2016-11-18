package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.Database.DB_helper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

/**
 * Created by Chamod on 11/18/2016.
 */
public class PersistantTransactionDAO implements TransactionDAO {
    private Context ctx;

    //Constructor
    public PersistantTransactionDAO(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        DB_helper db_helper = DB_helper.getInstance(ctx);
        SQLiteDatabase db = db_helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(db_helper.account_no, accountNo);
        values.put(db_helper.date, dateToString(date));
        values.put(db_helper.amount, amount);
        values.put(db_helper.expense_type, expenseType.toString());

        db.insert(db_helper.transactions_table,null,values);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        return getPaginatedTransactionLogs(0);
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        DB_helper db_helper = DB_helper.getInstance(ctx);
        SQLiteDatabase db = db_helper.getReadableDatabase();

        //Query to get details of all the transactions
        String query = String.format("SELECT %s, %s, %s, %s FROM %s ORDER BY %s DESC",
                db_helper.account_no,db_helper.date,db_helper.expense_type,db_helper.amount,
                db_helper.transactions_table,db_helper.transaction_id);

        Cursor cursor = db.rawQuery(query, null);

        ArrayList<Transaction> transaction_logs = new ArrayList<>();

        //Add the transaction details to a list
        while (cursor.moveToNext())
        {
            try {
                ExpenseType expenseType = null;
                if (cursor.getString(cursor.getColumnIndex(db_helper.expense_type)).equals(ExpenseType.INCOME.toString())) {
                    expenseType = ExpenseType.INCOME;
                }
                else{
                    expenseType = ExpenseType.EXPENSE;
                }

                String dateString = cursor.getString(cursor.getColumnIndex(db_helper.date));
                Date date = stringToDate(dateString);

                Transaction tans = new Transaction(date,cursor.getString(cursor.getColumnIndex(db_helper.account_no)),
                        expenseType,cursor.getDouble(cursor.getColumnIndex(db_helper.amount)));

                transaction_logs.add(tans);

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        return transaction_logs;
    }

    public static String dateToString(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String dateString = dateFormat.format(date);
        return dateString;

    }
    public static Date stringToDate(String date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date strDate = dateFormat.parse(date);
        return strDate;
    }

}

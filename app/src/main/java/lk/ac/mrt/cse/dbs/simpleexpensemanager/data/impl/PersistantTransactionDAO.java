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
    private DB_helper db_helper;

    //Constructor
    public PersistantTransactionDAO(Context ctx) {
        this.ctx = ctx;
        this.db_helper=DB_helper.getInstance(ctx);
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        //set transaction details
        SQLiteDatabase db = db_helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(db_helper.account_no, accountNo);

        //convert date object to a string
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String dateString = dateFormat.format(date);

        values.put(db_helper.date, dateString);

        values.put(db_helper.amount, amount);
        values.put(db_helper.expense_type, expenseType.toString());

        db.insert(db_helper.transactions_table,null,values);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        SQLiteDatabase db = db_helper.getReadableDatabase();

        //get details of all the transactions in the descending order of id
        String query = String.format("SELECT %s, %s, %s, %s FROM %s ORDER BY %s DESC",
                db_helper.account_no,db_helper.date,db_helper.expense_type,db_helper.amount,
                db_helper.transactions_table,db_helper.transaction_id);

        Cursor cursor = db.rawQuery(query, null);

        ArrayList<Transaction> transaction_logs = new ArrayList<>();

        //Add the transaction objects to a list
        while (cursor.moveToNext())
        {
            try {
                ExpenseType expenseType = null;
                //check the transaction type
                if (cursor.getString(cursor.getColumnIndex(db_helper.expense_type)).equals(ExpenseType.INCOME.toString())) {
                    expenseType = ExpenseType.INCOME;
                }
                else{
                    expenseType = ExpenseType.EXPENSE;
                }

                //convert date string to a date object
                String dateString = cursor.getString(cursor.getColumnIndex(db_helper.date));
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Date date = dateFormat.parse(dateString);

                //create transaction object
                Transaction tans = new Transaction(date,cursor.getString(cursor.getColumnIndex(db_helper.account_no)),
                        expenseType,cursor.getDouble(cursor.getColumnIndex(db_helper.amount)));

                transaction_logs.add(tans);

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        return transaction_logs;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        SQLiteDatabase db = db_helper.getReadableDatabase();

        //Query to get details of all the transactions
        String query = String.format("SELECT %s, %s, %s, %s FROM %s ORDER BY %s DESC LIMIT %s",
                db_helper.account_no,db_helper.date,db_helper.expense_type,db_helper.amount,
                db_helper.transactions_table,db_helper.transaction_id,limit);

        Cursor cursor = db.rawQuery(query, null);

        ArrayList<Transaction> transaction_logs = new ArrayList<>();

        //Add the transaction details to a list
        while (cursor.moveToNext())
        {
            try {
                ExpenseType expenseType = null;
                //check the transaction type
                if (cursor.getString(cursor.getColumnIndex(db_helper.expense_type)).equals(ExpenseType.INCOME.toString())) {
                    expenseType = ExpenseType.INCOME;
                }
                else{
                    expenseType = ExpenseType.EXPENSE;
                }

                //convert date string to a date object
                String dateString = cursor.getString(cursor.getColumnIndex(db_helper.date));
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Date date = dateFormat.parse(dateString);
                //create transaction object
                Transaction tans = new Transaction(date,cursor.getString(cursor.getColumnIndex(db_helper.account_no)),
                        expenseType,cursor.getDouble(cursor.getColumnIndex(db_helper.amount)));

                transaction_logs.add(tans);

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        return transaction_logs;
    }
}

package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.Database.DB_helper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

/**
 * Created by Chamod on 11/18/2016.
 */
public class PersistantAccountDAO implements AccountDAO {
    private Context ctx;
    private DB_helper db_helper;

    //Constructor
    public PersistantAccountDAO(Context ctx) {
        this.ctx = ctx;
        this.db_helper=DB_helper.getInstance(ctx);
    }

    @Override
    public List<String> getAccountNumbersList() {
        SQLiteDatabase db = db_helper.getReadableDatabase();

        String query = String.format("SELECT %s FROM %s ORDER BY %s",
                db_helper.account_no,db_helper.accounts_table,db_helper.account_no);

        Cursor cursor = db.rawQuery(query, null);

        ArrayList<String> result = new ArrayList<>();
        //add account numbers to an list
        while (cursor.moveToNext())
        {
            result.add(cursor.getString(cursor.getColumnIndex(db_helper.account_no)));
        }

        cursor.close();

        return result;
    }

    @Override
    public List<Account> getAccountsList() {
        SQLiteDatabase db = db_helper.getReadableDatabase();

        String query = String.format("SELECT * FROM %s ORDER BY %s",
                db_helper.accounts_table,db_helper.account_no);

        Cursor cursor = db.rawQuery(query, null);

        ArrayList<Account> result = new ArrayList<>();

        //add account objects to a list
        while (cursor.moveToNext())
        {
            Account account=new Account(cursor.getString(cursor.getColumnIndex(db_helper.account_no)),
                    cursor.getString(cursor.getColumnIndex(db_helper.bank_name)),
                    cursor.getString(cursor.getColumnIndex(db_helper.account_holder_name)),
                    cursor.getDouble(cursor.getColumnIndex(db_helper.account_balance)));
            result.add(account);
        }

        cursor.close();

        return result;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = db_helper.getReadableDatabase();

        String query = String.format("SELECT * FROM %s WHERE %s=%s",
                db_helper.accounts_table,db_helper.account_no,accountNo);

        Cursor cursor = db.rawQuery(query, null);
        Account account = null;
        //create account object
        if (cursor.moveToFirst()) {
            account=new Account(cursor.getString(cursor.getColumnIndex(db_helper.account_no)),
                    cursor.getString(cursor.getColumnIndex(db_helper.bank_name)),
                    cursor.getString(cursor.getColumnIndex(db_helper.account_holder_name)),
                    cursor.getDouble(cursor.getColumnIndex(db_helper.account_balance)));
        }
        else {
            throw new InvalidAccountException("You have selected an invalid account number...!");
        }
        cursor.close();
        return account;
    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase db = db_helper.getWritableDatabase();

        //Save account details to the account table
        ContentValues values = new ContentValues();
        values.put(db_helper.account_no, account.getAccountNo());
        values.put(db_helper.bank_name, account.getBankName());
        values.put(db_helper.account_holder_name, account.getAccountHolderName());
        values.put(db_helper.account_balance, account.getBalance());

        db.insert(db_helper.accounts_table, null, values);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = db_helper.getWritableDatabase();
        //check for the availability of the account
        String query = String.format("SELECT * FROM %s WHERE %s = %s",
                db_helper.accounts_table,db_helper.account_no,accountNo);

        Cursor cursor = db.rawQuery(query, null);
        //delete the available account
        if (cursor.moveToFirst()) {
            db.delete(db_helper.accounts_table, db_helper.account_no + " = ?", new String[] { accountNo });
            cursor.close();

        }
        else {
            throw new InvalidAccountException("No such account found...!");
        }
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        SQLiteDatabase db = db_helper.getWritableDatabase();

        Account account = getAccount(accountNo);

        if (account!=null) {

            double new_amount=0;
            //check the account type
            if (expenseType.equals(ExpenseType.EXPENSE)) {
                new_amount = account.getBalance() - amount;
            }
            else if (expenseType.equals(ExpenseType.INCOME)) {
                new_amount = account.getBalance() + amount;
            }

            String query = String.format("UPDATE %s SET %s = %s WHERE %s = %s",db_helper.accounts_table,
                    db_helper.account_balance,new_amount,db_helper.account_no,accountNo);

            db.execSQL(query);
        }
        else {
            throw new InvalidAccountException("No such account found...!");
        }
    }
}

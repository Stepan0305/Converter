package com.example.converter.Models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Converter";

    public static final String TABLE_EXCHANGE = "Exchange";
    public static final String INPUT_CURRENCY = "CurrencyInput";
    public static final String OUTPUT_CURRENCY = "CurrencyOutput";
    public static final String SUM_INPUT = "SumInput";
    public static final String SUM_OUTPUT = "SumOutput";
    public static final String CONVERT_DATE = "ConvertDate";
    public static final String RATE_DATE = "RateDate";

    public static final String TABLE_CURRENCY = "Currency";
    public static final String KEY_ID_CURRENCY = "_id";   //1 рубль 2 доллар 3 евро 4 йен
    public static final String CURRENCY_CODE = "Code";

    public static final String TABLE_CURRENT_RATE = "CurrentRate";
    public static final String CURRENT_RATE_CURRENCY = "CurrencyKey";
    public static final String CURRENT_RATE_RUBLES_AMOUNT = "RublesAmount"; //сколько рублей за валюту

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table " + TABLE_CURRENCY + " ( " + KEY_ID_CURRENCY + " integer, " + CURRENCY_CODE +
                " varchar(3), primary key ( " + KEY_ID_CURRENCY + " ))";
        db.execSQL(sql);
        sql = "create table " + TABLE_EXCHANGE + " ( " + INPUT_CURRENCY + " integer, " + OUTPUT_CURRENCY + " integer, " +
                SUM_INPUT + " real, " + SUM_OUTPUT + " real, " + CONVERT_DATE + " integer, " + RATE_DATE + " integer, foreign key ( " +
                INPUT_CURRENCY + " ) references " + TABLE_CURRENCY + " ( " + KEY_ID_CURRENCY + " ), foreign key ( " +
                OUTPUT_CURRENCY + " ) references " + TABLE_CURRENCY + " ( " + KEY_ID_CURRENCY + " ))";
        db.execSQL(sql);
        sql = "create table " + TABLE_CURRENT_RATE + " ( " + CURRENT_RATE_CURRENCY + " integer, " + CURRENT_RATE_RUBLES_AMOUNT +
                " real, foreign key ( " + CURRENT_RATE_CURRENCY + " ) references " + TABLE_CURRENCY + " ( " +
                KEY_ID_CURRENCY + " ))";
        db.execSQL(sql);
        sql = "insert into " + TABLE_CURRENCY + " ( " + CURRENCY_CODE + " ) values ( 'RUB' )";
        db.execSQL(sql);
        sql = "insert into " + TABLE_CURRENCY + " ( " + CURRENCY_CODE + " ) values ( 'USD' )";
        db.execSQL(sql);
        sql = "insert into " + TABLE_CURRENCY + " ( " + CURRENCY_CODE + " ) values ( 'EUR' )";
        db.execSQL(sql);
        sql = "insert into " + TABLE_CURRENCY + " ( " + CURRENCY_CODE + " ) values ( 'JPY' )";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public Rate getCurrentRate() {
        SQLiteDatabase database = getReadableDatabase();
        String sql = "select * from " + TABLE_CURRENT_RATE;
        Cursor cursor = database.rawQuery(sql, null);
        Rate rate = new Rate();
        while (cursor.moveToNext()) {
            int key = cursor.getInt(cursor.getColumnIndex(CURRENT_RATE_CURRENCY));
            double sum = cursor.getDouble(cursor.getColumnIndex(CURRENT_RATE_RUBLES_AMOUNT));
            rate.setCurrency(key, sum);
        }
        return rate;
    }

    public void setCurrentRate(Rate rate) {
        SQLiteDatabase database = getWritableDatabase();
        String sql = "delete from " + TABLE_CURRENT_RATE;
        database.execSQL(sql);
        sql = "insert into " + TABLE_CURRENT_RATE + " ( " + CURRENT_RATE_CURRENCY + ", " + CURRENT_RATE_RUBLES_AMOUNT +
                " ) values ( 2, " + rate.getUSD() + " )";
        database.execSQL(sql);
        sql = "insert into " + TABLE_CURRENT_RATE + " ( " + CURRENT_RATE_CURRENCY + ", " + CURRENT_RATE_RUBLES_AMOUNT +
                " ) values ( 3, " + rate.getEUR() + " )";
        database.execSQL(sql);
        sql = "insert into " + TABLE_CURRENT_RATE + " ( " + CURRENT_RATE_CURRENCY + ", " + CURRENT_RATE_RUBLES_AMOUNT +
                " ) values ( 4, " + rate.getJPY100() + " )";
        database.execSQL(sql);
    }

    public void addToHistory(Exchange exchange) {
        SQLiteDatabase database = getWritableDatabase();
        int currencyInput = exchange.getCurrencyInput(), currencyOutput = exchange.getCurrencyOutput();
        double inputSum = exchange.getInputSum(), outputSum = exchange.getOutputSum();
        long dateCreatedByUser = exchange.getDateCreatedByUser(), dateOfRate = exchange.getDateOfRate();
       /* String sql="insert into "+TABLE_EXCHANGE+" ( "+INPUT_CURRENCY+", "+OUTPUT_CURRENCY+", "+
                SUM_INPUT+", "+SUM_OUTPUT+", "+CONVERT_DATE+", "+RATE_DATE+" ) values ( "+currencyInput+
                ", "+currencyOutput+", "+inputSum+", "+outputSum+", "+dateCreatedByUser+", "+
                dateOfRate+" )";
        database.execSQL(sql);*/
        ContentValues contentValues = new ContentValues();
        contentValues.put(INPUT_CURRENCY, currencyInput);
        contentValues.put(OUTPUT_CURRENCY, currencyOutput);
        contentValues.put(SUM_INPUT, inputSum);
        contentValues.put(SUM_OUTPUT, outputSum);
        contentValues.put(RATE_DATE, dateOfRate);
        contentValues.put(CONVERT_DATE, dateCreatedByUser);
        System.out.println(database.insert(TABLE_EXCHANGE, null, contentValues));
        String sql = "select count(*) as counter from " + TABLE_EXCHANGE;
        Cursor c = database.rawQuery(sql, null);
        if (c.moveToFirst()) {
            int count = c.getInt(c.getColumnIndex("counter"));
            if (count >= 10) {
                sql = "delete from " + TABLE_EXCHANGE + " where " + CONVERT_DATE + " = ( select min( " + CONVERT_DATE + " ) from " +
                        TABLE_EXCHANGE + ")";
                database.execSQL(sql);
            }
        } else Log.e("customError", "sql запрос ничего не вернул");
    }

    public ArrayList<Exchange> getAllHistory() {
        SQLiteDatabase database = getReadableDatabase();
        ArrayList<Exchange> exchanges = new ArrayList<>();
        String sql = "select * from " + TABLE_EXCHANGE+" order by "+CONVERT_DATE+" desc";
        Cursor c = database.rawQuery(sql, null);
        while (c.moveToNext()) {
            int currencyInput = c.getInt(c.getColumnIndex(INPUT_CURRENCY)),
                    currencyOutput = c.getInt(c.getColumnIndex(OUTPUT_CURRENCY));
            double inputSum = c.getDouble(c.getColumnIndex(SUM_INPUT)),
                    outputSum = c.getDouble(c.getColumnIndex(SUM_OUTPUT));
            long dateCreatedByUser = c.getLong(c.getColumnIndex(CONVERT_DATE));
            long dateOfRate = c.getLong(c.getColumnIndex(RATE_DATE));
            Exchange exchange = new Exchange(currencyInput, currencyOutput, inputSum, outputSum,
                    dateOfRate, dateCreatedByUser);
            exchanges.add(exchange);
        }
        return exchanges;
    }
}

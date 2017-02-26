package com.cq9191.cashbook.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 91910 on 2016/8/26.
 */
public class CashDatabaseHelper extends SQLiteOpenHelper {

    private static final String CREATE_TABLE_CASH = "create table cash (" +
            "_id integer primary key autoincrement, " +
            "person nvarchar, " +
            "things nvarchar, " +
            "money real, " +
            "time nvarchar," +
            "hour nvarchar," +
            "week nvarchar," +
            "desc nvarchar," +
            "tag int )";

    public CashDatabaseHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CASH);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }
}

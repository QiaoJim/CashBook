package com.cq9191.cashbook.MyManageUi;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cq9191.cashbook.Data.CashDatabaseHelper;
import com.cq9191.cashbook.R;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by 91910 on 2016/12/15.
 */
public class TotalDetail extends Activity {

    private TextView tv_total_in;
    private TextView tv_total_out;
    private TextView tv_total_left;
    private ImageButton ib_back;

    private double total_in;
    private double total_out;
    private double total_left;

    private CashDatabaseHelper helper;
    private SQLiteDatabase db;

    private DecimalFormat df = new DecimalFormat("#.0");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.total_detail_layout);

        helper = new CashDatabaseHelper(this, "CashDatabase.db", 1);
        db = helper.getWritableDatabase();

        getSQLData();

        findView();
    }

    private void findView() {
        tv_total_in = (TextView) findViewById(R.id.total_in);
        tv_total_out = (TextView) findViewById(R.id.total_out);
        tv_total_left = (TextView) findViewById(R.id.total_left);

        tv_total_in.setText("+"+df.format(total_in)+" 元");
        tv_total_out.setText("-"+df.format(total_out)+"元");
        if (total_left >= 0){
            tv_total_left.setText("+"+df.format(total_left)+"元");
        }
        else {
            tv_total_left.setText(df.format(total_left)+"元");
        }


        ib_back = (ImageButton) findViewById(R.id.ib_back);
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void getSQLData() {
        Cursor cursor_in = db.rawQuery("select person, tag, money ,sum(money) as total_in from cash " +
                "where person = ? and tag = ?;",
                new String[]{"我", "0"});
        while (cursor_in.moveToNext()) {
            total_in = cursor_in.getDouble(cursor_in.getColumnIndex("total_in"));
        }
        Cursor cursor_out = db.rawQuery("select person, tag, money , sum(money) as total_out from cash " +
                "where person = ? and tag = ?;",
                new String[]{"我", "1"});
        while (cursor_out.moveToNext()) {
            total_out = cursor_out.getDouble(cursor_out.getColumnIndex("total_out"));
        }
        cursor_in.close();
        cursor_out.close();
        total_left = total_in - total_out;
        //Log.d("TotalDetail", "total data :"+total_in+"\n"+total_out+"\n"+total_left);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}

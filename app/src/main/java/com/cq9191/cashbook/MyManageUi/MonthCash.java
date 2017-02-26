package com.cq9191.cashbook.MyManageUi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.cq9191.cashbook.Data.CashDatabaseHelper;
import com.cq9191.cashbook.Data.QueryRequirement;
import com.cq9191.cashbook.MyView.LineChartView;
import com.cq9191.cashbook.MyView.LineChartView2;
import com.cq9191.cashbook.MyView.YScalePicker;
import com.cq9191.cashbook.R;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MonthCash extends Activity implements View.OnClickListener {

    private ImageButton back;
    private ImageButton settings;
    private TextView top_title;

    private TableLayout mTableLayout;

    private int screen_width;
    private String person;
    private CashDatabaseHelper helper;
    private SQLiteDatabase db;

    private List<String> time_list = new ArrayList<>();
    private List<String> new_time_list = new ArrayList<>();
    private List<Float> money_list = new ArrayList<>();

    private List<String> month_list = new ArrayList<>();
    private List<Float> month_daily_list = new ArrayList<>();
    private List<Float> month_expense_list = new ArrayList<>();
    private LineChartView2 mLineChartView;
    private int YScale = 200;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private List<Float> month_expense_final;
    private List<String> month_list_final;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.month_cash);

        //创建数据库
        helper = new CashDatabaseHelper(this, "CashDatabase.db", 1);
        db = helper.getWritableDatabase();

        //获取xml存储的数据
        mPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        mEditor = mPreferences.edit();

        WindowManager wm = this.getWindowManager();
        screen_width = wm.getDefaultDisplay().getWidth();

        findView();

        getRequirement();

        doQuery();

        sortList();

        showTable();

        showChart();
    }

    private void sortList() {
        for (int i = 0; i < time_list.size(); i++) {
            new_time_list.add(time_list.get(i).substring(0, 7));
        }

        String current_month = new_time_list.get(0);
        Float current_month_expense = money_list.get(0);

        if (month_list.size() == 1) {
            month_list.add(current_month);
            month_expense_list.add(current_month_expense);
        } else {
            int i = 1;
            while (i < new_time_list.size()) {
                while (i < new_time_list.size() &&
                        new_time_list.get(i).equals(current_month)) {
                    current_month_expense += money_list.get(i);
                    i++;
                }
                if (i < new_time_list.size()) {
                    month_list.add(current_month);
                    month_expense_list.add(current_month_expense);
                    current_month = new_time_list.get(i);
                    current_month_expense = money_list.get(i);
                    i++;
                }
            }
            month_list.add(current_month);
            month_expense_list.add(current_month_expense);
        }

        for (int i = 0; i < month_list.size(); i++) {
            int day_of_month;
            String month_num = month_list.get(i).substring(5, 7);
            switch (month_num) {
                case "01":
                case "03":
                case "05":
                case "07":
                case "08":
                case "10":
                case "12":
                    day_of_month = 31;
                    break;
                case "04":
                case "06":
                case "09":
                case "11":
                    day_of_month = 30;
                    break;
                case "02":
                    day_of_month = 28;
                    break;
                default:
                    day_of_month = 30;
                    break;
            }

            float month_daily_expense = month_expense_list.get(i) / day_of_month;
            BigDecimal b = new BigDecimal(month_daily_expense);
            month_daily_list.add(b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
        }


        for (int i = 0; i < time_list.size(); i++) {
            Log.v("--- list ---", time_list.get(i) + "       " + new_time_list.get(i) + "       " + money_list.get(i));
        }
        for (int i = 0; i < month_expense_list.size(); i++) {
            Log.v("--- month expense ---", month_list.get(i) + "        " + month_expense_list.get(i) + "         " + month_daily_list.get(i));
        }
    }

    private void showTable() {
        for (int row = 0; row < month_list.size() + 1; row++) {
            TableRow tabRow = new TableRow(MonthCash.this);
            TextView tv_1 = new TextView(this);
            TextView tv_2 = new TextView(this);
            TextView tv_3 = new TextView(this);
            tv_1.setSingleLine(true);
            tv_1.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
            tv_3.setSingleLine(true);
            tv_3.setEllipsize(TextUtils.TruncateAt.valueOf("END"));

            if (row > 0) {

                tv_1.setText(month_list.get(row - 1));
                tv_2.setText(month_daily_list.get(row - 1)+"");
                tv_3.setText(month_expense_list.get(row - 1) + "元");
                tv_1.setGravity(Gravity.CENTER);
                tv_2.setGravity(Gravity.CENTER);
                tv_3.setGravity(Gravity.CENTER);
                tv_1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                tv_2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                tv_3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                tv_1.setHeight((int) (35 * this.getResources().getDisplayMetrics().density + 0.5f));
                tv_2.setHeight((int) (35 * this.getResources().getDisplayMetrics().density + 0.5f));
                tv_3.setHeight((int) (35 * this.getResources().getDisplayMetrics().density + 0.5f));
                tv_1.setWidth(screen_width / 3);
                tv_2.setWidth(screen_width / 4);
                tv_3.setWidth(screen_width / 4);
                tv_1.setTextColor(getResources().getColor(R.color.black));
                tv_3.setTextColor(getResources().getColor(R.color.red));
                tv_1.setBackgroundResource(R.drawable.blue_thin_border);
                tv_2.setBackgroundResource(R.drawable.blue_thin_border);
                tv_3.setBackgroundResource(R.drawable.blue_thin_border);

                if (month_daily_list.get(row - 1)<50f){
                    tv_2.setTextColor(getResources().getColor(R.color.deep_gray));
                }else if (month_daily_list.get(row -1)>=50f && month_daily_list.get(row -1)<100f){
                    tv_2.setTextColor(getResources().getColor(R.color.green));
                }else if (month_daily_list.get(row -1)>=100f && month_daily_list.get(row -1)<250f){
                    tv_2.setTextColor(getResources().getColor(R.color.purple));
                }else if (month_daily_list.get(row -1)>=250f && month_daily_list.get(row -1)<500f){
                    tv_2.setTextColor(getResources().getColor(R.color.orange));
                }else {
                    tv_2.setTextColor(getResources().getColor(R.color.red));
                }

                tabRow.addView(tv_1);
                tabRow.addView(tv_2);
                tabRow.addView(tv_3);

                mTableLayout.addView(tabRow);
            } else if (row == 0) {
                tv_1.setText("年-月");
                tv_2.setText("日均消费");
                tv_3.setText("月总消费");
                tv_1.setGravity(Gravity.CENTER);
                tv_2.setGravity(Gravity.CENTER);
                tv_3.setGravity(Gravity.CENTER);
                tv_1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                tv_2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                tv_3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                tv_1.setHeight((int) (35 * this.getResources().getDisplayMetrics().density + 0.5f));
                tv_2.setHeight((int) (35 * this.getResources().getDisplayMetrics().density + 0.5f));
                tv_3.setHeight((int) (35 * this.getResources().getDisplayMetrics().density + 0.5f));
                tv_1.setWidth(screen_width / 3);
                tv_2.setWidth(screen_width / 4);
                tv_3.setWidth(screen_width / 4);
                tv_1.setTextColor(getResources().getColor(R.color.blue));
                tv_2.setTextColor(getResources().getColor(R.color.blue));
                tv_3.setTextColor(getResources().getColor(R.color.blue));
                tv_1.setBackgroundResource(R.drawable.blue_thin_border);
                tv_2.setBackgroundResource(R.drawable.blue_thin_border);
                tv_3.setBackgroundResource(R.drawable.blue_thin_border);

                tabRow.addView(tv_1);
                tabRow.addView(tv_2);
                tabRow.addView(tv_3);

                mTableLayout.addView(tabRow);
            }
        }
    }

    private void showChart(){
        sortTimeAndExpense(month_list, month_expense_list);
        mLineChartView.setList(month_list_final, month_expense_final);
    }

    private void doQuery() {
        @SuppressLint("Recycle")
        Cursor cursor = db.rawQuery("select time , money from cash where person = ? order by time DESC,hour DESC",
                new String[]{person});

        while (cursor.moveToNext()) {
            time_list.add(cursor.getString(cursor.getColumnIndex("time")));
            money_list.add(cursor.getFloat(cursor.getColumnIndex("money")));
        }

    }

    private void findView() {
        back = (ImageButton) findViewById(R.id.bt_back);
        settings = (ImageButton) findViewById(R.id.bt_settings);
        back.setOnClickListener(this);
        settings.setOnClickListener(this);

        mTableLayout = (TableLayout) findViewById(R.id.mTableLayout);
        mTableLayout.setStretchAllColumns(true);
        mLineChartView = (LineChartView2) findViewById(R.id.line_chart_view);

        top_title = (TextView) findViewById(R.id.tv_title);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_back:
                onBackPressed();
                break;
            case R.id.bt_settings:

                final String[] num = {"50","100","150","200","300","500","800","1000","2000"};
                final View mView = LayoutInflater.from(MonthCash.this).inflate(R.layout.set_y_scale, null);
                YScalePicker mPicker = (YScalePicker) mView.findViewById(R.id.num_picker);
                mPicker.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
                AlertDialog.Builder mBuidler = new AlertDialog.Builder(MonthCash.this);
                mBuidler.setView(mView);
                mPicker.setDisplayedValues(num);
                mPicker.setMaxValue(num.length-1);
                mPicker.setMinValue(0);
                mPicker.setValue(3);
                YScale = Integer.parseInt(num[mPicker.getValue()]);
                mPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                        YScale = Integer.parseInt(num[i1]);
                    }
                });

                //重绘折现图
                mBuidler.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mEditor.putInt("month_y_scale", YScale);
                        mEditor.commit();
                        sortTimeAndExpense(month_list, month_expense_list);
                        mLineChartView.setList(month_list_final, month_expense_final);
                        mLineChartView.invalidate();
                        Toast.makeText(MonthCash.this, "成功修改刻度为 "+ YScale, Toast.LENGTH_SHORT).show();
                    }
                });
                mBuidler.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                mBuidler.create().show();
                break;
        }
    }

    public void getRequirement() {
        QueryRequirement requirement = (QueryRequirement) getIntent().getSerializableExtra("requirement");
        person = requirement.getPerson();

        top_title.setText(person + "的月账单");

        Log.v("--- person ---", person);
    }

    private void sortTimeAndExpense(List<String> time_record, List<Float> month_expense){

        month_list_final = new ArrayList<>();
        month_expense_final = new ArrayList<>();

        int scale = mPreferences.getInt("month_y_scale",200);
        for (int i=0;i<time_record.size();i++){
            month_list_final.add(time_record.get(i).substring(5,7));
            month_expense_final.add(Float.valueOf(month_expense.get(i))>12*scale ?
                    12.5f:Float.valueOf(month_expense.get(i))/scale);
        }

        if (month_list_final.size()>14){
            for (int i = 14;i<month_list_final.size();i++){
                month_list_final.remove(i);
                month_expense_final.remove(i);
                i--;
            }
        }
        Collections.reverse(month_list_final);
        Collections.reverse(month_expense_final);

        for (int i=0;i<month_list_final.size();i++){
            Log.v("---after reverse---",month_list_final.get(i)+"    "+month_expense_final.get(i));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

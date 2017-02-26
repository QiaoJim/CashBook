package com.cq9191.cashbook.MyManageUi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.media.Image;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.cq9191.cashbook.Data.CashDatabaseHelper;
import com.cq9191.cashbook.Data.QueryRequirement;
import com.cq9191.cashbook.MyView.LineChartView;
import com.cq9191.cashbook.MyView.RoundPerView;
import com.cq9191.cashbook.MyView.YScalePicker;
import com.cq9191.cashbook.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomCashDetail extends Activity {

    private RoundPerView mRoundView;
    private LineChartView mLineChartView;

    private ImageView iv_record_1;
    private ImageView iv_record_2;
    private ImageView iv_record_3;
    private ImageView iv_record_4;
    private ImageView iv_record_5;
    private ImageView iv_record_6;
    private ImageView iv_record_7;

    private TextView tv_title;
    private TextView tv_record_1;
    private TextView tv_record_2;
    private TextView tv_record_3;
    private TextView tv_record_4;
    private TextView tv_record_5;
    private TextView tv_record_6;
    private TextView tv_record_7;

    private List<Integer> _id_record;
    //记录在筛选条件下，原始金额数组
    private List<String> money_record;
    //原始金额数组的copy，供整理使用
    private List<String> money_record_copy;
    //筛选条件下，原始 每天/每事件的 金额数组
    private List<String> expense_daily;
    private List<Float> expense_daily_final;

    private List<String> person_record;
    private List<String> things_record;

    private List<String> time_record_final;
    private List<String> time_record;
    private List<String> hour_record;
    private List<String> week_record;

    private List<String> desc_record;
    private List<String> percent_record;
    private List<Integer> number_record;

    private String person;
    private String things;
    private String start_time;
    private String end_time;
    private boolean[] tag;

    private CashDatabaseHelper helper;
    private SQLiteDatabase db;

    private TableLayout mTableLayout;     //1
    private TableLayout mTableLayout2;    //2

    private int screen_width;
    private int screen_height;

    private ImageButton bt_back;
    private ImageButton bt_set_scale;

    private int YScale = 50;
    private final String[] num = {"5","10","15","20","25","30","35","40","45","50",
            "55","60","65","70","75","80","85","90","95","100","150","200","300","500"};

    SharedPreferences mPreferences;
    SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_cash_detail_layout);

        //创建数据库
        helper = new CashDatabaseHelper(this, "CashDatabase.db", 1);
        db = helper.getWritableDatabase();

        //获取xml存储的数据
        mPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        mEditor = mPreferences.edit();

        YScale = mPreferences.getInt("y_scale", 50);

        WindowManager wm = this.getWindowManager();
        screen_width = wm.getDefaultDisplay().getWidth();
        screen_height = wm.getDefaultDisplay().getHeight();

        _id_record = new ArrayList<>();
        money_record = new ArrayList<>();
        money_record_copy = new ArrayList<>();
        person_record = new ArrayList<>();
        things_record = new ArrayList<>();
        time_record = new ArrayList<>();
        hour_record = new ArrayList<>();
        week_record = new ArrayList<>();
        desc_record = new ArrayList<>();
        percent_record = new ArrayList<>();
        number_record = new ArrayList<>();
        expense_daily = new ArrayList<>();
        expense_daily_final = new ArrayList<>();
        time_record_final = new ArrayList<>();

        findView();

        //得到查询条件
        getRequirement();

        if (tag[0]){
            tv_title.setText(person+"的账单");
        }
        else if (tag[1]){
            tv_title.setText(things+"的账单");
        }

        //查询账单,生成圆饼图
        doQuery2();

        //查询，生成 事件 统计表格
        doQuery1();

        //生成 时间 统计表格
        doQuery3();

        //查询，生成折线图
        doQuery4();

        //填充圆形比例图
        setColorAndText();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(CustomCashDetail.this, CreateCash.class);
        startActivity(intent);
        finish();
    }

    private void setColorAndText() {

        //用来分别给不同等级的扇形做底色
        int[] color = new int[]{
                R.color.green,
                R.color.pink,
                R.color.slight_orange,
                R.color.blue,
                R.color.yellow,
                R.color.purple,
                R.color.slight_gray
        };

        ///////////////////////////////////////////////
        //生成 个人 自定义账单
        if (tag[0]) {

            List<String> new_things_record = sortThingsOrPersonRecord(things_record);
            sortPercentRecord(sortMoneyRecord(money_record_copy));
            switch (new_things_record.size()) {

                case 1:
                    iv_record_7.setBackgroundColor(getResources().getColor(color[0]));
                    tv_record_7.setText(percent_record.get(0)+ " " + new_things_record.get(0));
                    tv_record_7.setTextColor(getResources().getColor(color[0]));
                    break;
                case 2:
                    iv_record_6.setBackgroundColor(getResources().getColor(color[0]));
                    iv_record_7.setBackgroundColor(getResources().getColor(color[1]));
                    tv_record_6.setText(percent_record.get(0)+ " " + new_things_record.get(0));
                    tv_record_7.setText(percent_record.get(1)+ " " + new_things_record.get(1));
                    tv_record_6.setTextColor(getResources().getColor(color[0]));
                    tv_record_7.setTextColor(getResources().getColor(color[1]));
                    break;
                case 3:
                    iv_record_5.setBackgroundColor(getResources().getColor(color[0]));
                    iv_record_6.setBackgroundColor(getResources().getColor(color[1]));
                    iv_record_7.setBackgroundColor(getResources().getColor(color[2]));
                    tv_record_5.setText(percent_record.get(0)+ " " + new_things_record.get(0));
                    tv_record_6.setText(percent_record.get(1)+ " " + new_things_record.get(1));
                    tv_record_7.setText(percent_record.get(2)+ " " + new_things_record.get(2));
                    tv_record_5.setTextColor(getResources().getColor(color[0]));
                    tv_record_6.setTextColor(getResources().getColor(color[1]));
                    tv_record_7.setTextColor(getResources().getColor(color[2]));
                    break;
                case 4:
                    iv_record_4.setBackgroundColor(getResources().getColor(color[0]));
                    iv_record_5.setBackgroundColor(getResources().getColor(color[1]));
                    iv_record_6.setBackgroundColor(getResources().getColor(color[2]));
                    iv_record_7.setBackgroundColor(getResources().getColor(color[3]));
                    tv_record_4.setText(percent_record.get(0)+ " " + new_things_record.get(0));
                    tv_record_5.setText(percent_record.get(1)+ " " + new_things_record.get(1));
                    tv_record_6.setText(percent_record.get(2)+ " " + new_things_record.get(2));
                    tv_record_7.setText(percent_record.get(3)+ " " + new_things_record.get(3));
                    tv_record_4.setTextColor(getResources().getColor(color[0]));
                    tv_record_5.setTextColor(getResources().getColor(color[1]));
                    tv_record_6.setTextColor(getResources().getColor(color[2]));
                    tv_record_7.setTextColor(getResources().getColor(color[3]));
                    break;
                case 5:
                    iv_record_3.setBackgroundColor(getResources().getColor(color[0]));
                    iv_record_4.setBackgroundColor(getResources().getColor(color[1]));
                    iv_record_5.setBackgroundColor(getResources().getColor(color[2]));
                    iv_record_6.setBackgroundColor(getResources().getColor(color[3]));
                    iv_record_7.setBackgroundColor(getResources().getColor(color[4]));
                    tv_record_3.setText(percent_record.get(0)+ " " + new_things_record.get(0));
                    tv_record_4.setText(percent_record.get(1)+ " " + new_things_record.get(1));
                    tv_record_5.setText(percent_record.get(2)+ " " + new_things_record.get(2));
                    tv_record_6.setText(percent_record.get(3)+ " " + new_things_record.get(3));
                    tv_record_7.setText(percent_record.get(4)+ " " + new_things_record.get(4));
                    tv_record_3.setTextColor(getResources().getColor(color[0]));
                    tv_record_4.setTextColor(getResources().getColor(color[1]));
                    tv_record_5.setTextColor(getResources().getColor(color[2]));
                    tv_record_6.setTextColor(getResources().getColor(color[3]));
                    tv_record_7.setTextColor(getResources().getColor(color[4]));
                    break;
                case 6:
                    iv_record_2.setBackgroundColor(getResources().getColor(color[0]));
                    iv_record_3.setBackgroundColor(getResources().getColor(color[1]));
                    iv_record_4.setBackgroundColor(getResources().getColor(color[2]));
                    iv_record_5.setBackgroundColor(getResources().getColor(color[3]));
                    iv_record_6.setBackgroundColor(getResources().getColor(color[4]));
                    iv_record_7.setBackgroundColor(getResources().getColor(color[5]));
                    tv_record_2.setText(percent_record.get(0)+ " " + new_things_record.get(0));
                    tv_record_3.setText(percent_record.get(1)+ " " + new_things_record.get(1));
                    tv_record_4.setText(percent_record.get(2)+ " " + new_things_record.get(2));
                    tv_record_5.setText(percent_record.get(3)+ " " + new_things_record.get(3));
                    tv_record_6.setText(percent_record.get(4)+ " " + new_things_record.get(4));
                    tv_record_7.setText(percent_record.get(5)+ " " + new_things_record.get(5));
                    tv_record_2.setTextColor(getResources().getColor(color[0]));
                    tv_record_3.setTextColor(getResources().getColor(color[1]));
                    tv_record_4.setTextColor(getResources().getColor(color[2]));
                    tv_record_5.setTextColor(getResources().getColor(color[3]));
                    tv_record_6.setTextColor(getResources().getColor(color[4]));
                    tv_record_7.setTextColor(getResources().getColor(color[5]));
                    break;
                case 7:
                    iv_record_1.setBackgroundColor(getResources().getColor(color[0]));
                    iv_record_2.setBackgroundColor(getResources().getColor(color[1]));
                    iv_record_3.setBackgroundColor(getResources().getColor(color[2]));
                    iv_record_4.setBackgroundColor(getResources().getColor(color[3]));
                    iv_record_5.setBackgroundColor(getResources().getColor(color[4]));
                    iv_record_6.setBackgroundColor(getResources().getColor(color[5]));
                    iv_record_7.setBackgroundColor(getResources().getColor(color[6]));
                    tv_record_1.setText(percent_record.get(0)+ " " + new_things_record.get(0));
                    tv_record_2.setText(percent_record.get(1)+ " " + new_things_record.get(1));
                    tv_record_3.setText(percent_record.get(2)+ " " + new_things_record.get(2));
                    tv_record_4.setText(percent_record.get(3)+ " " + new_things_record.get(3));
                    tv_record_5.setText(percent_record.get(4)+ " " + new_things_record.get(4));
                    tv_record_6.setText(percent_record.get(5)+ " " + new_things_record.get(5));
                    tv_record_7.setText(percent_record.get(6)+ " " + new_things_record.get(6));
                    tv_record_1.setTextColor(getResources().getColor(color[0]));
                    tv_record_2.setTextColor(getResources().getColor(color[1]));
                    tv_record_3.setTextColor(getResources().getColor(color[2]));
                    tv_record_4.setTextColor(getResources().getColor(color[3]));
                    tv_record_5.setTextColor(getResources().getColor(color[4]));
                    tv_record_6.setTextColor(getResources().getColor(color[5]));
                    tv_record_7.setTextColor(getResources().getColor(color[6]));
                    break;
                default:
                    break;

            }
        }

        ///////////////////////////////////////////////
        //生成 事件 自定义账单
        else if (tag[1]) {

            List<String> new_person_record = sortThingsOrPersonRecord(person_record);
            sortPercentRecord(sortMoneyRecord(money_record_copy));
            switch (new_person_record.size()) {

                case 1:
                    iv_record_7.setBackgroundColor(getResources().getColor(color[0]));
                    tv_record_7.setText(percent_record.get(0)+" "+new_person_record.get(0));
                    tv_record_7.setTextColor(getResources().getColor(color[0]));
                    break;
                case 2:
                    iv_record_6.setBackgroundColor(getResources().getColor(color[0]));
                    iv_record_7.setBackgroundColor(getResources().getColor(color[1]));
                    tv_record_6.setText(percent_record.get(0)+" "+new_person_record.get(0));
                    tv_record_7.setText(percent_record.get(1)+" "+new_person_record.get(1));
                    tv_record_6.setTextColor(getResources().getColor(color[0]));
                    tv_record_7.setTextColor(getResources().getColor(color[1]));
                    break;
                case 3:
                    iv_record_5.setBackgroundColor(getResources().getColor(color[0]));
                    iv_record_6.setBackgroundColor(getResources().getColor(color[1]));
                    iv_record_7.setBackgroundColor(getResources().getColor(color[2]));
                    tv_record_5.setText(percent_record.get(0)+" "+new_person_record.get(0));
                    tv_record_6.setText(percent_record.get(1)+" "+new_person_record.get(1));
                    tv_record_7.setText(percent_record.get(2)+" "+new_person_record.get(2));
                    tv_record_5.setTextColor(getResources().getColor(color[0]));
                    tv_record_6.setTextColor(getResources().getColor(color[1]));
                    tv_record_7.setTextColor(getResources().getColor(color[2]));
                    break;
                case 4:
                    iv_record_4.setBackgroundColor(getResources().getColor(color[0]));
                    iv_record_5.setBackgroundColor(getResources().getColor(color[1]));
                    iv_record_6.setBackgroundColor(getResources().getColor(color[2]));
                    iv_record_7.setBackgroundColor(getResources().getColor(color[3]));
                    tv_record_4.setText(percent_record.get(0)+" "+new_person_record.get(0));
                    tv_record_5.setText(percent_record.get(1)+" "+new_person_record.get(1));
                    tv_record_6.setText(percent_record.get(2)+" "+new_person_record.get(2));
                    tv_record_7.setText(percent_record.get(3)+" "+new_person_record.get(3));
                    tv_record_4.setTextColor(getResources().getColor(color[0]));
                    tv_record_5.setTextColor(getResources().getColor(color[1]));
                    tv_record_6.setTextColor(getResources().getColor(color[2]));
                    tv_record_7.setTextColor(getResources().getColor(color[3]));
                    break;
                case 5:
                    iv_record_3.setBackgroundColor(getResources().getColor(color[0]));
                    iv_record_4.setBackgroundColor(getResources().getColor(color[1]));
                    iv_record_5.setBackgroundColor(getResources().getColor(color[2]));
                    iv_record_6.setBackgroundColor(getResources().getColor(color[3]));
                    iv_record_7.setBackgroundColor(getResources().getColor(color[4]));
                    tv_record_3.setText(percent_record.get(0)+" "+new_person_record.get(0));
                    tv_record_4.setText(percent_record.get(1)+" "+new_person_record.get(1));
                    tv_record_5.setText(percent_record.get(2)+" "+new_person_record.get(2));
                    tv_record_6.setText(percent_record.get(3)+" "+new_person_record.get(3));
                    tv_record_7.setText(percent_record.get(4)+" "+new_person_record.get(4));
                    tv_record_3.setTextColor(getResources().getColor(color[0]));
                    tv_record_4.setTextColor(getResources().getColor(color[1]));
                    tv_record_5.setTextColor(getResources().getColor(color[2]));
                    tv_record_6.setTextColor(getResources().getColor(color[3]));
                    tv_record_7.setTextColor(getResources().getColor(color[4]));
                    break;
                case 6:
                    iv_record_2.setBackgroundColor(getResources().getColor(color[0]));
                    iv_record_3.setBackgroundColor(getResources().getColor(color[1]));
                    iv_record_4.setBackgroundColor(getResources().getColor(color[2]));
                    iv_record_5.setBackgroundColor(getResources().getColor(color[3]));
                    iv_record_6.setBackgroundColor(getResources().getColor(color[4]));
                    iv_record_7.setBackgroundColor(getResources().getColor(color[5]));
                    tv_record_2.setText(percent_record.get(0)+" "+new_person_record.get(0));
                    tv_record_3.setText(percent_record.get(1)+" "+new_person_record.get(1));
                    tv_record_4.setText(percent_record.get(2)+" "+new_person_record.get(2));
                    tv_record_5.setText(percent_record.get(3)+" "+new_person_record.get(3));
                    tv_record_6.setText(percent_record.get(4)+" "+new_person_record.get(4));
                    tv_record_7.setText(percent_record.get(5)+" "+new_person_record.get(5));
                    tv_record_2.setTextColor(getResources().getColor(color[0]));
                    tv_record_3.setTextColor(getResources().getColor(color[1]));
                    tv_record_4.setTextColor(getResources().getColor(color[2]));
                    tv_record_5.setTextColor(getResources().getColor(color[3]));
                    tv_record_6.setTextColor(getResources().getColor(color[4]));
                    tv_record_7.setTextColor(getResources().getColor(color[5]));
                    break;
                case 7:
                    iv_record_1.setBackgroundColor(getResources().getColor(color[0]));
                    iv_record_2.setBackgroundColor(getResources().getColor(color[1]));
                    iv_record_3.setBackgroundColor(getResources().getColor(color[2]));
                    iv_record_4.setBackgroundColor(getResources().getColor(color[3]));
                    iv_record_5.setBackgroundColor(getResources().getColor(color[4]));
                    iv_record_6.setBackgroundColor(getResources().getColor(color[5]));
                    iv_record_7.setBackgroundColor(getResources().getColor(color[6]));
                    tv_record_1.setText(percent_record.get(0)+" "+new_person_record.get(0));
                    tv_record_2.setText(percent_record.get(1)+" "+new_person_record.get(1));
                    tv_record_3.setText(percent_record.get(2)+" "+new_person_record.get(2));
                    tv_record_4.setText(percent_record.get(3)+" "+new_person_record.get(3));
                    tv_record_5.setText(percent_record.get(4)+" "+new_person_record.get(4));
                    tv_record_6.setText(percent_record.get(5)+" "+new_person_record.get(5));
                    tv_record_7.setText(percent_record.get(6)+" "+new_person_record.get(6));
                    tv_record_1.setTextColor(getResources().getColor(color[0]));
                    tv_record_2.setTextColor(getResources().getColor(color[1]));
                    tv_record_3.setTextColor(getResources().getColor(color[2]));
                    tv_record_4.setTextColor(getResources().getColor(color[3]));
                    tv_record_5.setTextColor(getResources().getColor(color[4]));
                    tv_record_6.setTextColor(getResources().getColor(color[5]));
                    tv_record_7.setTextColor(getResources().getColor(color[6]));
                    break;
                default:

                    break;

            }

        }

    }

    private void findView() {
        mRoundView = (RoundPerView) findViewById(R.id.round_view);
        mLineChartView = (LineChartView) findViewById(R.id.line_chart_view);

        iv_record_1 = (ImageView) findViewById(R.id.iv_record_1);
        iv_record_2 = (ImageView) findViewById(R.id.iv_record_2);
        iv_record_3 = (ImageView) findViewById(R.id.iv_record_3);
        iv_record_4 = (ImageView) findViewById(R.id.iv_record_4);
        iv_record_5 = (ImageView) findViewById(R.id.iv_record_5);
        iv_record_6 = (ImageView) findViewById(R.id.iv_record_6);
        iv_record_7 = (ImageView) findViewById(R.id.iv_record_7);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_record_1 = (TextView) findViewById(R.id.tv_record_1);
        tv_record_2 = (TextView) findViewById(R.id.tv_record_2);
        tv_record_3 = (TextView) findViewById(R.id.tv_record_3);
        tv_record_4 = (TextView) findViewById(R.id.tv_record_4);
        tv_record_5 = (TextView) findViewById(R.id.tv_record_5);
        tv_record_6 = (TextView) findViewById(R.id.tv_record_6);
        tv_record_7 = (TextView) findViewById(R.id.tv_record_7);

        mTableLayout= (TableLayout) findViewById(R.id.mTableLayout);
        mTableLayout2= (TableLayout) findViewById(R.id.mTableLayout2);
        mTableLayout.setStretchAllColumns(true);
        mTableLayout2.setStretchAllColumns(true);

        bt_back= (ImageButton) findViewById(R.id.bt_back);
        bt_set_scale= (ImageButton) findViewById(R.id.bt_set_scale);

        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomCashDetail.this, CreateCash.class);
                startActivity(intent);
                finish();
            }
        });

        //设置Y轴刻度
        bt_set_scale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMyDialog();
            }
        });
    }

    public void getRequirement() {
        QueryRequirement requirement = (QueryRequirement) getIntent().getSerializableExtra("requirement");
        person = requirement.getPerson();
        things = requirement.getThings();
        start_time = requirement.getStart_time();
        end_time = requirement.getEnd_time();

        tag = requirement.getTag();
    }

    //第1个表格
    private void doQuery1() {

        //生成 人物 统计表格
        if (tag[0]){
            for (int row=0;row<money_record.size()+1;row++){
                TableRow tabRow = new TableRow(CustomCashDetail.this);
                TextView tv_1 = new TextView(this);
                TextView tv_2 = new TextView(this);
                TextView tv_3 = new TextView(this);
                tv_1.setSingleLine(true);
                tv_1.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
                tv_3.setSingleLine(true);
                tv_3.setEllipsize(TextUtils.TruncateAt.valueOf("END"));

                if (row>0) {

                    tv_1.setText(things_record.get(row-1));
                    tv_2.setText(number_record.get(row-1) + "");
                    tv_3.setText(money_record.get(row-1) + "元");
                    tv_1.setGravity(Gravity.CENTER);
                    tv_2.setGravity(Gravity.CENTER);
                    tv_3.setGravity(Gravity.CENTER);
                    tv_1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                    tv_2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                    tv_3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                    tv_1.setHeight((int) (35 * this.getResources().getDisplayMetrics().density + 0.5f));
                    tv_2.setHeight((int) (35 * this.getResources().getDisplayMetrics().density + 0.5f));
                    tv_3.setHeight((int) (35 * this.getResources().getDisplayMetrics().density + 0.5f));
                    tv_1.setWidth(screen_width/3);
                    tv_2.setWidth(screen_width/7);
                    tv_3.setWidth(screen_width/4);
                    tv_1.setTextColor(getResources().getColor(R.color.black));
                    tv_2.setTextColor(getResources().getColor(R.color.deep_gray));
                    tv_3.setTextColor(getResources().getColor(R.color.red));
                    tv_1.setBackgroundResource(R.drawable.blue_thin_border);
                    tv_2.setBackgroundResource(R.drawable.blue_thin_border);
                    tv_3.setBackgroundResource(R.drawable.blue_thin_border);

                    tabRow.addView(tv_1);
                    tabRow.addView(tv_2);
                    tabRow.addView(tv_3);

                    mTableLayout.addView(tabRow);
                }
                else if (row ==0){
                    tv_1.setText("事件");
                    tv_2.setText("事件数");
                    tv_3.setText("总金额");
                    tv_1.setGravity(Gravity.CENTER);
                    tv_2.setGravity(Gravity.CENTER);
                    tv_3.setGravity(Gravity.CENTER);
                    tv_1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                    tv_2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                    tv_3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                    tv_1.setHeight((int) (35 * this.getResources().getDisplayMetrics().density + 0.5f));
                    tv_2.setHeight((int) (35 * this.getResources().getDisplayMetrics().density + 0.5f));
                    tv_3.setHeight((int) (35 * this.getResources().getDisplayMetrics().density + 0.5f));
                    tv_1.setWidth(screen_width/3);
                    tv_2.setWidth(screen_width/7);
                    tv_3.setWidth(screen_width/4);
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




        //生成 事件 统计表格
        else if (tag[1]){

            for (int row=0;row<money_record.size()+1;row++){
                TableRow tabRow = new TableRow(CustomCashDetail.this);
                TextView tv_1 = new TextView(this);
                TextView tv_2 = new TextView(this);
                TextView tv_3 = new TextView(this);
                tv_1.setSingleLine(true);
                tv_1.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
                tv_3.setSingleLine(true);
                tv_3.setEllipsize(TextUtils.TruncateAt.valueOf("END"));

                if (row>0) {

                    tv_1.setText(person_record.get(row-1));
                    tv_2.setText(number_record.get(row-1) + "");
                    tv_3.setText(money_record.get(row-1) + "元");
                    tv_1.setGravity(Gravity.CENTER);
                    tv_2.setGravity(Gravity.CENTER);
                    tv_3.setGravity(Gravity.CENTER);
                    tv_1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                    tv_2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                    tv_3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                    tv_1.setHeight((int) (35 * this.getResources().getDisplayMetrics().density + 0.5f));
                    tv_2.setHeight((int) (35 * this.getResources().getDisplayMetrics().density + 0.5f));
                    tv_3.setHeight((int) (35 * this.getResources().getDisplayMetrics().density + 0.5f));
                    tv_1.setWidth(screen_width/3);
                    tv_2.setWidth(screen_width/7);
                    tv_3.setWidth(screen_width/4);
                    tv_1.setTextColor(getResources().getColor(R.color.black));
                    tv_2.setTextColor(getResources().getColor(R.color.deep_gray));
                    tv_3.setTextColor(getResources().getColor(R.color.red));
                    tv_1.setBackgroundResource(R.drawable.blue_thin_border);
                    tv_2.setBackgroundResource(R.drawable.blue_thin_border);
                    tv_3.setBackgroundResource(R.drawable.blue_thin_border);

                    tabRow.addView(tv_1);
                    tabRow.addView(tv_2);
                    tabRow.addView(tv_3);

                    mTableLayout.addView(tabRow);
                }
                else if (row ==0){
                    tv_1.setText("人物");
                    tv_2.setText("事件数");
                    tv_3.setText("总金额");
                    tv_1.setGravity(Gravity.CENTER);
                    tv_2.setGravity(Gravity.CENTER);
                    tv_3.setGravity(Gravity.CENTER);
                    tv_1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                    tv_2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                    tv_3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                    tv_1.setHeight((int) (35 * this.getResources().getDisplayMetrics().density + 0.5f));
                    tv_2.setHeight((int) (35 * this.getResources().getDisplayMetrics().density + 0.5f));
                    tv_3.setHeight((int) (35 * this.getResources().getDisplayMetrics().density + 0.5f));
                    tv_1.setWidth(screen_width/3);
                    tv_2.setWidth(screen_width/7);
                    tv_3.setWidth(screen_width/4);
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
    }

    //第2个表格
    private void doQuery2() {

        StringBuilder time_sql = new StringBuilder();
        //判断时间查询
        if (tag[2]) {
            String str = "";
            if (!start_time.equals("")) {
                str = "and time >= '" + start_time + "'";
                time_sql.append(str);
            }

        }
        if (tag[3]) {
            String str = "";
            if (!end_time.equals("")) {
                str = "and time <= '" + end_time + "'";
                time_sql.append(str);
            }
        }

        if (tag[0]) {
            Cursor cursor = db.rawQuery("select things ,count(things) as number , sum(money) as expense from cash " +
                    "where person = ? " + time_sql + "group by things order by expense DESC;", new String[]{person});
            while (cursor.moveToNext()) {
                things_record.add(cursor.getString(cursor.getColumnIndex("things")));
                number_record.add(cursor.getInt(cursor.getColumnIndex("number")));
                money_record.add(cursor.getString(cursor.getColumnIndex("expense")));
            }
        } else if (tag[1]) {
            Cursor cursor = db.rawQuery("select person ,count(things) as number , sum(money) as expense from cash " +
                    "where things = ? " + time_sql + "group by person order by expense DESC;", new String[]{things});
            while (cursor.moveToNext()) {
                person_record.add(cursor.getString(cursor.getColumnIndex("person")));
                number_record.add(cursor.getInt(cursor.getColumnIndex("number")));
                money_record.add(cursor.getString(cursor.getColumnIndex("expense")));
            }
        }


        //复制money_record数组，供整理使用
        for (int i=0;i<money_record.size();i++){
            money_record_copy.add(money_record.get(i));
        }

        if (money_record.size()>0) {
            List<String> new_money_record;
            new_money_record = sortMoneyRecord(money_record_copy);

            mRoundView.setList(new_money_record);
        }
        else {
            final android.support.v7.app.AlertDialog.Builder mDialog = new android.support.v7.app.AlertDialog.Builder(this);
            mDialog.setTitle("提示");
            mDialog.setMessage("亲，暂无相关账单数据哦!");
            mDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            mDialog.create();
            mDialog.show();
        }

    }

    //第3个表格
    private void doQuery4() {

        List<String> new_time_record = time_record;
        List<String> new_expense_daily_record = expense_daily;
        sortTimeAndExpense(new_time_record, new_expense_daily_record);
        mLineChartView.setList(time_record_final, expense_daily_final);

    }

    //第4个表格
    private void doQuery3(){

        StringBuilder time_sql = new StringBuilder();
        //判断时间查询
        if (tag[2]) {
            String str = "";
            if (!start_time.equals("")) {
                str = "and time >= '" + start_time + "'";
                time_sql.append(str);
            }

        }
        if (tag[3]) {
            String str = "";
            if (!end_time.equals("")) {
                str = "and time <= '" + end_time + "'";
                time_sql.append(str);
            }
        }

        if (tag[0]) {
            Cursor cursor = db.rawQuery("select time, money ,sum(money) as expense_daily from cash where person = ? "
                    + time_sql + " group by time order by time DESC; ", new String[]{person});
            while (cursor.moveToNext()){
                expense_daily.add(cursor.getString(cursor.getColumnIndex("expense_daily")));
                time_record.add(cursor.getString(cursor.getColumnIndex("time")));
            }
        }
        else if (tag[1]){
            Cursor cursor = db.rawQuery("select time,money ,sum(money) as expense_daily from cash where things = ? "
                    + time_sql + " group by time order by time DESC; ", new String[]{things});
            while (cursor.moveToNext()){
                expense_daily.add(cursor.getString(cursor.getColumnIndex("expense_daily")));
                time_record.add(cursor.getString(cursor.getColumnIndex("time")));
            }
        }

        for (int i=0;i<time_record.size();i++){
            Log.v("@@@@ table 3 @@@@",time_record.size()+"    "+time_record.get(i)+"    "+expense_daily.get(i));
        }

        //生成 人物 统计表格
        if (tag[0]){
            for (int row=0;row<time_record.size()+1;row++){
                TableRow tabRow = new TableRow(CustomCashDetail.this);
                TextView tv_1 = new TextView(this);
                TextView tv_2 = new TextView(this);
                TextView tv_3 = new TextView(this);

                if (row>0) {

                    tv_1.setText(time_record.get(row-1));
                    tv_3.setText(expense_daily.get(row-1) + "元");
                    if(Float.valueOf(expense_daily.get(row -1)) < 50f){
                        tv_2.setText("低");
                        tv_2.setTextColor(getResources().getColor(R.color.slight_gray));
                    }
                    else if(Float.valueOf(expense_daily.get(row -1)) >= 50f
                            && Float.valueOf(expense_daily.get(row -1))<150f){
                        tv_2.setText("较低");
                        tv_2.setTextColor(getResources().getColor(R.color.green));
                    }
                    else if(Float.valueOf(expense_daily.get(row -1)) >= 150f
                            && Float.valueOf(expense_daily.get(row -1))<550f){
                        tv_2.setText("较高");
                        tv_2.setTextColor(getResources().getColor(R.color.orange));
                    }
                    else if(Float.valueOf(expense_daily.get(row -1)) >= 550f
                            && Float.valueOf(expense_daily.get(row -1))<1200f){
                        tv_2.setText("高");
                        tv_2.setTextColor(getResources().getColor(R.color.red));
                    }
                    else {
                        tv_2.setText("超高");
                        tv_2.setTextColor(getResources().getColor(R.color.red));
                    }

                    tv_1.setGravity(Gravity.CENTER);
                    tv_2.setGravity(Gravity.CENTER);
                    tv_3.setGravity(Gravity.CENTER);
                    tv_1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                    tv_2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                    tv_3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                    tv_1.setHeight((int) (35 * this.getResources().getDisplayMetrics().density + 0.5f));
                    tv_2.setHeight((int) (35 * this.getResources().getDisplayMetrics().density + 0.5f));
                    tv_3.setHeight((int) (35 * this.getResources().getDisplayMetrics().density + 0.5f));
                    tv_1.setWidth(screen_width/3);
                    tv_2.setWidth(screen_width/5);
                    tv_3.setWidth(screen_width/4);
                    tv_1.setTextColor(getResources().getColor(R.color.black));
                    tv_3.setTextColor(getResources().getColor(R.color.red));
                    tv_1.setBackgroundResource(R.drawable.blue_thin_border);
                    tv_2.setBackgroundResource(R.drawable.blue_thin_border);
                    tv_3.setBackgroundResource(R.drawable.blue_thin_border);

                    tabRow.addView(tv_1);
                    tabRow.addView(tv_2);
                    tabRow.addView(tv_3);

                    mTableLayout2.addView(tabRow);
                }
                else if (row ==0){
                    tv_1.setText("最近消费");
                    tv_2.setText("消费等级");
                    tv_3.setText("总金额");
                    tv_1.setGravity(Gravity.CENTER);
                    tv_2.setGravity(Gravity.CENTER);
                    tv_3.setGravity(Gravity.CENTER);
                    tv_1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                    tv_2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                    tv_3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                    tv_1.setHeight((int) (35 * this.getResources().getDisplayMetrics().density + 0.5f));
                    tv_2.setHeight((int) (35 * this.getResources().getDisplayMetrics().density + 0.5f));
                    tv_3.setHeight((int) (35 * this.getResources().getDisplayMetrics().density + 0.5f));
                    tv_1.setWidth(screen_width/3);
                    tv_2.setWidth(screen_width/5);
                    tv_3.setWidth(screen_width/4);
                    tv_1.setTextColor(getResources().getColor(R.color.blue));
                    tv_2.setTextColor(getResources().getColor(R.color.blue));
                    tv_3.setTextColor(getResources().getColor(R.color.blue));
                    tv_1.setBackgroundResource(R.drawable.blue_thin_border);
                    tv_2.setBackgroundResource(R.drawable.blue_thin_border);
                    tv_3.setBackgroundResource(R.drawable.blue_thin_border);

                    tabRow.addView(tv_1);
                    tabRow.addView(tv_2);
                    tabRow.addView(tv_3);

                    mTableLayout2.addView(tabRow);
                }
            }
        }




        //生成 事件 统计表格
        else if (tag[1]){

            for (int row=0;row<time_record.size()+1;row++){
                TableRow tabRow = new TableRow(CustomCashDetail.this);
                TextView tv_1 = new TextView(this);
                TextView tv_2 = new TextView(this);
                TextView tv_3 = new TextView(this);

                if (row>0) {

                    tv_1.setText(time_record.get(row-1));
                    tv_3.setText(expense_daily.get(row-1) + "元");
                    if(Float.valueOf(expense_daily.get(row -1)) < 50f){
                        tv_2.setText("低");
                        tv_2.setTextColor(getResources().getColor(R.color.slight_gray));
                    }
                    else if(Float.valueOf(expense_daily.get(row -1)) >= 50f
                            && Float.valueOf(expense_daily.get(row -1))<150f){
                        tv_2.setText("较低");
                        tv_2.setTextColor(getResources().getColor(R.color.green));
                    }
                    else if(Float.valueOf(expense_daily.get(row -1)) >= 150f
                            && Float.valueOf(expense_daily.get(row -1))<550f){
                        tv_2.setText("较高");
                        tv_2.setTextColor(getResources().getColor(R.color.orange));
                    }
                    else if(Float.valueOf(expense_daily.get(row -1)) >= 550f
                            && Float.valueOf(expense_daily.get(row -1))<1200f){
                        tv_2.setText("高");
                        tv_2.setTextColor(getResources().getColor(R.color.red));
                    }
                    else {
                        tv_2.setText("超高");
                        tv_2.setTextColor(getResources().getColor(R.color.red));
                    }

                    tv_1.setGravity(Gravity.CENTER);
                    tv_2.setGravity(Gravity.CENTER);
                    tv_3.setGravity(Gravity.CENTER);
                    tv_1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                    tv_2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                    tv_3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                    tv_1.setHeight((int) (35 * this.getResources().getDisplayMetrics().density + 0.5f));
                    tv_2.setHeight((int) (35 * this.getResources().getDisplayMetrics().density + 0.5f));
                    tv_3.setHeight((int) (35 * this.getResources().getDisplayMetrics().density + 0.5f));
                    tv_1.setWidth(screen_width/3);
                    tv_2.setWidth(screen_width/5);
                    tv_3.setWidth(screen_width/4);
                    tv_1.setTextColor(getResources().getColor(R.color.black));
                    tv_3.setTextColor(getResources().getColor(R.color.red));
                    tv_1.setBackgroundResource(R.drawable.blue_thin_border);
                    tv_2.setBackgroundResource(R.drawable.blue_thin_border);
                    tv_3.setBackgroundResource(R.drawable.blue_thin_border);

                    tabRow.addView(tv_1);
                    tabRow.addView(tv_2);
                    tabRow.addView(tv_3);

                    mTableLayout2.addView(tabRow);
                }
                else if (row ==0){
                    tv_1.setText("时间");
                    tv_2.setText("消费等级");
                    tv_3.setText("总金额");
                    tv_1.setGravity(Gravity.CENTER);
                    tv_2.setGravity(Gravity.CENTER);
                    tv_3.setGravity(Gravity.CENTER);
                    tv_1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                    tv_2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                    tv_3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                    tv_1.setHeight((int) (35 * this.getResources().getDisplayMetrics().density + 0.5f));
                    tv_2.setHeight((int) (35 * this.getResources().getDisplayMetrics().density + 0.5f));
                    tv_3.setHeight((int) (35 * this.getResources().getDisplayMetrics().density + 0.5f));
                    tv_1.setWidth(screen_width/3);
                    tv_2.setWidth(screen_width/5);
                    tv_3.setWidth(screen_width/4);
                    tv_1.setTextColor(getResources().getColor(R.color.blue));
                    tv_2.setTextColor(getResources().getColor(R.color.blue));
                    tv_3.setTextColor(getResources().getColor(R.color.blue));
                    tv_1.setBackgroundResource(R.drawable.blue_thin_border);
                    tv_2.setBackgroundResource(R.drawable.blue_thin_border);
                    tv_3.setBackgroundResource(R.drawable.blue_thin_border);

                    tabRow.addView(tv_1);
                    tabRow.addView(tv_2);
                    tabRow.addView(tv_3);

                    mTableLayout2.addView(tabRow);
                }
            }
        }
    }

    private List<String> sortMoneyRecord(List<String> list) {
        if (list.size() > 7) {
            float sum_other = 0;
            for (int i = 6; i < list.size(); i++) {
                sum_other += Float.valueOf(list.get(i));
            }
            list.add(6, String.valueOf(sum_other));
            for (int i = 7; i < list.size(); i++) {
                list.remove(i);
                i--;
            }
        }
        sortPercentRecord(list);
        return list;


    }

    private List<String> sortThingsOrPersonRecord(List<String> list) {
        if (list.size() > 7) {
            list.add(6, "其他");
            for (int i = 7; i < list.size(); i++) {
                list.remove(i);
                i--;
            }
        }
        return list;
    }

    private void sortPercentRecord(List<String> list) {
        float sum = 0;
        //规范百分比
        DecimalFormat df = new DecimalFormat("#0.00%");
        for (int i = 0; i < list.size(); i++) {
            sum += Float.valueOf(list.get(i));
        }
        for (int i = 0; i < list.size(); i++) {
            //计算百分比
            String percent = df.format(Float.valueOf(list.get(i)) / sum);
            percent_record.add(percent);
        }
    }

    private void sortTimeAndExpense(List<String> time_record, List<String> expense_daily){

        time_record_final = null;
        expense_daily_final = null;
        time_record_final = new ArrayList<>();
        expense_daily_final = new ArrayList<>();

        for (int i=0;i<time_record.size();i++){
            String month_first_num = time_record.get(i).charAt(5)+"";
            String month_second_num = time_record.get(i).charAt(6)+"";
            String day_first_num = time_record.get(i).charAt(8)+"";
            String day_second_num = time_record.get(i).charAt(9)+"";

            String time = "";
            if (!(month_first_num.equals("0"))){
                time += month_first_num;
            }
            time += month_second_num+".";
            if (!(day_first_num.equals("0"))){
                time += day_first_num;
            }
            time +=day_second_num;
            time_record_final.add(time);

        }

        int scale = mPreferences.getInt("y_scale",50);
        for (int i=0;i<expense_daily.size();i++){
            expense_daily_final.add(Float.valueOf(expense_daily.get(i))>12*scale ?
                    12.5f:Float.valueOf(expense_daily.get(i))/scale);
        }


        if (time_record_final.size()>30){
            for (int i = 30;i<time_record_final.size();i++){
                time_record_final.remove(i);
                expense_daily_final.remove(i);
                i--;
            }
        }
        Collections.reverse(time_record_final);
        Collections.reverse(expense_daily_final);

        for (int i=0;i<time_record_final.size();i++){
            Log.v("---after reverse---",time_record_final.get(i)+"    "+expense_daily_final.get(i));
        }
    }

    private void showMyDialog(){

        final View mView = LayoutInflater.from(CustomCashDetail.this).inflate(R.layout.set_y_scale, null);
        YScalePicker mPicker = (YScalePicker) mView.findViewById(R.id.num_picker);
        mPicker.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        AlertDialog.Builder mBuidler = new AlertDialog.Builder(CustomCashDetail.this);
        mBuidler.setView(mView);
        mPicker.setDisplayedValues(num);
        mPicker.setMaxValue(num.length-1);
        mPicker.setMinValue(0);
        mPicker.setValue(9);
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
                mEditor.putInt("y_scale", YScale);
                mEditor.commit();
                sortTimeAndExpense(time_record, expense_daily);
                mLineChartView.setList(time_record_final, expense_daily_final);
                mLineChartView.requestLayout();
                Toast.makeText(CustomCashDetail.this, "成功修改刻度为 "+YScale, Toast.LENGTH_SHORT).show();
            }
        });
        mBuidler.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        mBuidler.create().show();
    }

}

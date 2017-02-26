package com.cq9191.cashbook.MyManageUi;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.cq9191.cashbook.Data.CashDatabaseHelper;
import com.cq9191.cashbook.Data.QueryRequirement;
import com.cq9191.cashbook.MyManageActivity;
import com.cq9191.cashbook.R;

import java.util.ArrayList;
import java.util.List;

public class CreateCash extends FragmentActivity implements View.OnClickListener, DropDownListAdapter.MyListItemClickListener,
        View.OnTouchListener, SetStartTimeDialog.OnTimeListener, SetEndTimeDialog.OnTimeListener {

    ImageButton bt_dropdown;
    ImageButton bt_dropdown_2;
    ImageButton ib_start_time;
    ImageButton ib_end_time;
    ImageButton ib_back;
    Button bt_month_cash;
    Button bt_create;

    EditText et_person;
    EditText et_things;
    EditText et_start_time;
    EditText et_end_time;

    CheckBox cb_person;
    CheckBox cb_things;
    CheckBox cb_start_time;
    CheckBox cb_end_time;

    List<String> all_person;
    List<String> all_things;

    DropDownList mDropDownPersonList;
    DropDownList mDropDownThingsList;

    private int num_click;
    private int num_click_2;

    private boolean tag[] = {false,false,false,false,false};

    CashDatabaseHelper helper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_cash_layout);

        helper = new CashDatabaseHelper(this, "CashDatabase.db", 1);
        db = helper.getWritableDatabase();

        findView();

        bt_dropdown.setOnClickListener(this);
        bt_dropdown_2.setOnClickListener(this);
        ib_start_time.setOnClickListener(this);
        ib_end_time.setOnClickListener(this);
        bt_month_cash.setOnClickListener(this);
        ib_back.setOnClickListener(this);
        bt_create.setOnClickListener(this);

        //获取列表数据
        initListData();

    }

    private void setTag() {
        if (cb_person.isChecked()) {
            tag[0] = true;
        }
        if (cb_things.isChecked()) {
            tag[1] = true;
        }
        if (cb_start_time.isChecked()) {
            tag[2] = true;
        }
        if (cb_end_time.isChecked()) {
            tag[3] = true;
        }
    }

    private void resetTag(){
        for (int i=0;i<5;i++){
            tag[i]=false;
        }
    }

    private void findView() {
        bt_dropdown = (ImageButton) findViewById(R.id.bt_dropdown);
        bt_dropdown_2 = (ImageButton) findViewById(R.id.bt_dropdown_2);
        ib_start_time = (ImageButton) findViewById(R.id.ib_start_time);
        ib_end_time = (ImageButton) findViewById(R.id.ib_end_time);
        ib_back = (ImageButton) findViewById(R.id.ib_back);

        bt_month_cash= (Button) findViewById(R.id.bt_month_cash);
        bt_create= (Button) findViewById(R.id.bt_create);

        et_person = (EditText) findViewById(R.id.et_person);
        et_things = (EditText) findViewById(R.id.et_things);
        et_start_time = (EditText) findViewById(R.id.start_time);
        et_end_time = (EditText) findViewById(R.id.end_time);

        cb_person= (CheckBox) findViewById(R.id.cb_person);
        cb_things= (CheckBox) findViewById(R.id.cb_things);
        cb_start_time= (CheckBox) findViewById(R.id.cb_start_time);
        cb_end_time= (CheckBox) findViewById(R.id.cb_end_time);

        cb_person.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!cb_person.isChecked()){
                    et_person.setText("");
                }
            }
        });

        cb_things.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!cb_things.isChecked()){
                    et_things.setText("");
                }
            }
        });
        cb_start_time.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!cb_start_time.isChecked()){
                    et_start_time.setText("");
                }
            }
        });
        cb_end_time.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!cb_end_time.isChecked()){
                    et_end_time.setText("");
                }
            }
        });

    }

    //读取存储的所有 人物、事件
    private void initListData() {
        all_person = new ArrayList<>();
        all_things = new ArrayList<>();

        Cursor cursor_1 = db.query(true, "cash", new String[]{"person"}, null, null, null, null, "_id ASC", null);
        while (cursor_1.moveToNext()) {
            all_person.add(cursor_1.getString(cursor_1.getColumnIndex("person")));
        }
        Cursor cursor_2 = db.query(true, "cash", new String[]{"things"}, null, null, null, null, "_id ASC", null);
        while (cursor_2.moveToNext()) {
            all_things.add(cursor_2.getString(cursor_2.getColumnIndex("things")));
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_dropdown:
                ++num_click;
                if (num_click % 2 == 1) {
                    mDropDownPersonList = new DropDownList(1, CreateCash.this, all_person, CreateCash.this);
                    mDropDownPersonList.setWidth(et_person.getWidth());
                    mDropDownPersonList.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
                    mDropDownPersonList.showAsDropDown(et_person);
                } else {
                    mDropDownPersonList.dismiss();
                }
                break;
            case R.id.bt_dropdown_2:
                ++num_click_2;
                if (num_click_2 % 2 == 1) {
                    mDropDownThingsList = new DropDownList(2, CreateCash.this, all_things, CreateCash.this);
                    mDropDownThingsList.setWidth(et_things.getWidth());
                    mDropDownThingsList.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
                    mDropDownThingsList.showAsDropDown(et_things);
                } else {
                    mDropDownThingsList.dismiss();
                }
                break;

            case R.id.ib_start_time:
                SetStartTimeDialog start_time_picker = new SetStartTimeDialog();
                start_time_picker.show(getSupportFragmentManager(), "start_time_picker");
                break;
            case R.id.ib_end_time:
                SetEndTimeDialog end_time_picker = new SetEndTimeDialog();
                end_time_picker.show(getSupportFragmentManager(), "end_time_picker");
                break;
            case R.id.ib_back:
                Intent intent2 = new Intent(CreateCash.this, MyManageActivity.class);
                startActivity(intent2);
                finish();
                break;


            //月账单
            case R.id.bt_month_cash:
                setTag();

                if (tag[0] && !tag[1] && !tag[2]&& !tag[3] && !tag[4]) {
                    Intent intent = new Intent(CreateCash.this, MonthCash.class);

                    QueryRequirement requirement = new QueryRequirement();
                    requirement.setPerson(et_person.getText().toString());
                    requirement.setThings(et_things.getText().toString());
                    requirement.setStart_time(et_start_time.getText().toString());
                    requirement.setEnd_time(et_end_time.getText().toString());
                    requirement.setTag(tag);

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("requirement", requirement);
                    intent.putExtras(bundle);

                    startActivity(intent);
                }
                else {
                    Toast.makeText(CreateCash.this, "请仅选择人物之后，生成月账单", Toast.LENGTH_SHORT).show();
                }
                resetTag();
                break;

            //自定义账单
            case R.id.bt_create:

                //判断选中哪些条件
                setTag();

                if ((tag[0] && !tag[1]) || (!tag[0] && tag[1])){
                    Intent intent1 = new Intent(CreateCash.this, CustomCashDetail.class);

                    Bundle bundle = new Bundle();

                    QueryRequirement requirement = new QueryRequirement();
                    requirement.setPerson(et_person.getText().toString());
                    requirement.setThings(et_things.getText().toString());
                    requirement.setStart_time(et_start_time.getText().toString());
                    requirement.setEnd_time(et_end_time.getText().toString());
                    requirement.setTag(tag);

                    bundle.putSerializable("requirement", requirement);
                    intent1.putExtras(bundle);
                    startActivity(intent1);

                }
                else {
                    Toast.makeText(this, "请选择人物，事件其中之一",Toast.LENGTH_SHORT).show();
                }
                resetTag();
                break;

        }
    }

    @Override
    public void onItemClick(int type, int position) {

        if (type == 1) {
            et_person.setText(all_person.get(position));
            cb_person.setChecked(true);
        } else if (type == 2) {
            et_things.setText(all_things.get(position));
            cb_things.setChecked(true);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return true;
    }

    @Override
    public void getStartTime(int year, int month, int day) {
        if (month/10 ==0 && day/10 == 0){
            et_start_time.setText(year + "-0" + month + "-0" + day);
        }
        else if (month/10 ==0 && day/10 != 0){
            et_start_time.setText(year + "-0" + month + "-" + day);
        }
        else if (month/10 !=0 && day/10 == 0){
            et_start_time.setText(year + "-" + month + "-0" + day);
        }
        else {
            et_start_time.setText(year + "-" + month + "-" + day);
        }
        cb_start_time.setChecked(true);
    }

    @Override
    public void getEndTime(int year, int month, int day) {
        if (month/10 ==0 && day/10 == 0){
            et_end_time.setText(year + "-0" + month + "-0" + day);
        }
        else if (month/10 ==0 && day/10 != 0){
            et_end_time.setText(year + "-0" + month + "-" + day);
        }
        else if (month/10 !=0 && day/10 == 0){
            et_end_time.setText(year + "-" + month + "-0" + day);
        }
        else {
            et_end_time.setText(year + "-" + month + "-" + day);
        }
        cb_end_time.setChecked(true);
    }
}

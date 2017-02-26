package com.cq9191.cashbook;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.cq9191.cashbook.Data.CashDatabaseHelper;
import com.cq9191.cashbook.Data.QueryRequirement;
import com.cq9191.cashbook.MyManageUi.CreateCash;
import com.cq9191.cashbook.MyManageUi.DropDownList;
import com.cq9191.cashbook.MyManageUi.DropDownListAdapter;
import com.cq9191.cashbook.MyManageUi.SetEndTimeDialog;
import com.cq9191.cashbook.MyManageUi.SetStartTimeDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 91910 on 2016/7/30.
 */

public class SearchActivity extends FragmentActivity implements DropDownListAdapter.MyListItemClickListener,
                            SetStartTimeDialog.OnTimeListener, SetEndTimeDialog.OnTimeListener{

    private Button bt_search;
    private Button bt_back;

    private EditText et_person;
    private EditText et_things;
    private EditText et_start_time;
    private EditText et_end_time;
    private EditText et_min_money;
    private EditText et_max_money;

    private CheckBox cb_person;
    private CheckBox cb_things;
    private CheckBox cb_start_time;
    private CheckBox cb_end_time;
    private CheckBox cb_money;

    ImageButton bt_dropdown;
    ImageButton bt_dropdown_2;
    ImageButton ib_start_time;
    ImageButton ib_end_time;

    List<String> all_person;
    List<String> all_things;

    DropDownList mDropDownPersonList;
    DropDownList mDropDownThingsList;

    private int num_click;
    private int num_click_2;
    private boolean[] tag = {false, false, false, false, false};

    CashDatabaseHelper helper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);

        helper = new CashDatabaseHelper(this, "CashDatabase.db", 1);
        db = helper.getWritableDatabase();

        findView();

        //获取列表数据
        initListData();

        //选择搜索按钮类型
        buttonClick();
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
        if (cb_money.isChecked()) {
            tag[4] = true;
        }
    }

    private void resetTag(){
        for (int i=0;i<4;i++){
            tag[i]=false;
        }
    }

    private void findView() {
        et_person = (EditText) findViewById(R.id.et_person);
        et_things = (EditText) findViewById(R.id.et_things);

        et_start_time = (EditText) findViewById(R.id.start_time);
        et_end_time = (EditText) findViewById(R.id.end_time);

        et_min_money = (EditText) findViewById(R.id.et_min_money);
        et_max_money = (EditText) findViewById(R.id.et_max_money);

        cb_person= (CheckBox) findViewById(R.id.cb_person);
        cb_things= (CheckBox) findViewById(R.id.cb_things);
        cb_start_time= (CheckBox) findViewById(R.id.cb_start_time);
        cb_end_time= (CheckBox) findViewById(R.id.cb_end_time);
        cb_money= (CheckBox) findViewById(R.id.cb_money);

        bt_dropdown = (ImageButton) findViewById(R.id.bt_dropdown);
        bt_dropdown_2 = (ImageButton) findViewById(R.id.bt_dropdown_2);
        ib_start_time = (ImageButton) findViewById(R.id.ib_start_time);
        ib_end_time = (ImageButton) findViewById(R.id.ib_end_time);

        cb_person.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b){
                    et_person.setText("");
                }
            }
        });
        cb_things.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b){
                    et_things.setText("");
                }
            }
        });
        cb_start_time.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b){
                    et_start_time.setText("");
                }
            }
        });
        cb_end_time.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b){
                    et_end_time.setText("");
                }
            }
        });
        cb_money.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b){
                    et_min_money.setText("");
                    et_max_money.setText("");
                }
            }
        });
    }

    private void buttonClick() {
        bt_search = (Button) findViewById(R.id.bt_search);
        bt_back = (Button) findViewById(R.id.bt_back);

        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setTag();

                String person = et_person.getText().toString();
                String things = et_things.getText().toString();
                String start_time = et_start_time.getText().toString();
                String end_time = et_end_time.getText().toString();
                String min_money = et_min_money.getText().toString();
                String max_money = et_max_money.getText().toString();

                if (!min_money.equals("") &&
                        !max_money.equals("") &&
                        Float.valueOf(min_money) >= Float.valueOf(max_money)){
                    Toast.makeText(SearchActivity.this, "请输入正确的金额上下限", Toast.LENGTH_LONG).show();
                    return;
                }

                Intent intent = new Intent(SearchActivity.this, QueryResultListActivity.class);

                Bundle bundle = new Bundle();

                QueryRequirement requirement = new QueryRequirement();
                requirement.setPerson(person);
                requirement.setThings(things);
                requirement.setStart_time(start_time);
                requirement.setEnd_time(end_time);
                requirement.setMin_money(min_money);
                requirement.setMax_money(max_money);
                requirement.setTag(tag);

                Log.v("--- search ---",et_min_money.getText().toString()+"   "+et_max_money.getText().toString());

                bundle.putSerializable("requirement", requirement);
                intent.putExtras(bundle);
                startActivity(intent);

                resetTag();
            }
        });

        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        bt_dropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ++num_click;
                if (num_click % 2 == 1) {
                    mDropDownPersonList = new DropDownList(1, SearchActivity.this, all_person, SearchActivity.this);
                    mDropDownPersonList.setWidth(et_person.getWidth());
                    mDropDownPersonList.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
                    mDropDownPersonList.showAsDropDown(et_person);
                } else {
                    mDropDownPersonList.dismiss();
                }
            }
        });

        bt_dropdown_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ++num_click_2;
                if (num_click_2 % 2 == 1) {
                    mDropDownThingsList = new DropDownList(2, SearchActivity.this, all_things, SearchActivity.this);
                    mDropDownThingsList.setWidth(et_things.getWidth());
                    mDropDownThingsList.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
                    mDropDownThingsList.showAsDropDown(et_things);
                } else {
                    mDropDownThingsList.dismiss();
                }
            }
        });

        ib_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetStartTimeDialog start_time_picker = new SetStartTimeDialog();
                start_time_picker.show(getSupportFragmentManager(), "start_time_picker");
            }
        });

        ib_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetEndTimeDialog end_time_picker = new SetEndTimeDialog();
                end_time_picker.show(getSupportFragmentManager(), "end_time_picker");
            }
        });

        et_person.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb_person.setChecked(true);
            }
        });

        et_person.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    cb_person.setChecked(true);
                }
            }
        });

        et_things.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb_things.setChecked(true);
            }
        });

        et_things.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    cb_things.setChecked(true);
                }
            }
        });

        et_min_money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb_money.setChecked(true);
            }
        });

        et_min_money.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    cb_money.setChecked(true);
                }
            }
        });

        et_max_money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb_money.setChecked(true);
            }
        });

        et_max_money.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    cb_money.setChecked(true);
                }
            }
        });
    }

    //读取存储的所有 人物、事件
    private void initListData() {
        all_person = new ArrayList<>();
        all_things = new ArrayList<>();

        all_person.add("所有");
        all_things.add("所有");

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

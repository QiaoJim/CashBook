package com.cq9191.cashbook.MyManageUi;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.cq9191.cashbook.MyManageActivity;
import com.cq9191.cashbook.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class AddNewPersonCards extends Activity implements View.OnClickListener{

    EditText new_person;
    EditText delete_person;

    GridView gridView;

    Button bt_add;
    Button bt_delete;
    Button bt_back;
    Button bt_finish_1;
    ImageButton bt_finish_2;

    //GridView适配器
    private GridViewAdapter gridViewAdapter;

    //人物卡片名称数组
    private List<String> names;

    SharedPreferences mPreferences;
    SharedPreferences.Editor mEditor;

    private int person_cards_num = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_person_cards_layout);

        //获取用户基本数据--人物卡
        mPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        mEditor = mPreferences.edit();
        person_cards_num = mPreferences.getInt("person_cards_num", 0);

        bt_add = (Button) findViewById(R.id.bt_add);
        bt_delete = (Button) findViewById(R.id.bt_delete);
        bt_back = (Button) findViewById(R.id.bt_back);
        bt_finish_1 = (Button) findViewById(R.id.bt_finish);
        bt_finish_2 = (ImageButton) findViewById(R.id.bt_done);

        bt_add.setOnClickListener(this);
        bt_delete.setOnClickListener(this);
        bt_back.setOnClickListener(this);
        bt_finish_1.setOnClickListener(this);
        bt_finish_2.setOnClickListener(this);

        new_person = (EditText) findViewById(R.id.et_new_person);
        delete_person = (EditText) findViewById(R.id.et_delete_person);

        //卡片效果
        gridView = (GridView) findViewById(R.id.grid_person);

        //加载人物卡数据
        initList();
        gridViewAdapter = new GridViewAdapter(AddNewPersonCards.this, names, 1);

        if (gridView != null) {

            //每个卡片的长按监听
            gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //删除对应的item
                    names.remove(i);
                    //刷新数据
                    gridViewAdapter.notifyDataSetChanged();
                    return true;
                }
            });

            //设置Adapter
            gridView.setAdapter(gridViewAdapter);
        }
    }

    //默认人物卡
    private void initList() {

        names = new ArrayList<>();
        if (person_cards_num == 0){
            names.add("我");
            names.add("好友 1");
            names.add("好友 2");
            names.add("员工 1");
            names.add("员工 2");
            names.add("同学 1");
            names.add("同学 2");
        }
        else {
            for (int k = 0; k < person_cards_num; k++) {
                names.add(mPreferences.getString("person" + k, null));
            }
        }
    }

    //添加、删除、返回、完成编辑 按钮监听
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            //添加新人物卡片
            case R.id.bt_add:
                if (!new_person.getText().toString().equals("")) {
                    if (names.size() < 24){
                        names.add(new_person.getText().toString());
                    }
                    else {
                        Toast.makeText(AddNewPersonCards.this, "已超过人物卡上限数量", Toast.LENGTH_SHORT).show();
                    }
                }
                gridViewAdapter = new GridViewAdapter(AddNewPersonCards.this, names,1);
                gridView.setAdapter(gridViewAdapter);
                gridViewAdapter.notifyDataSetChanged();

                //清空数据，关闭键盘
                new_person.setText("");
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(new_person.getWindowToken(), 0);
                break;

            //删除人物卡片
            case R.id.bt_delete:
                if (names.size() == 0){
                    Toast.makeText(AddNewPersonCards.this, "人物卡数量已为0", Toast.LENGTH_SHORT).show();
                }
                for (int k = 0; k < names.size(); k ++){
                    if (names.get(k).equals(delete_person.getText().toString())){
                        names.remove(k);
                    }
                }
                gridViewAdapter = new GridViewAdapter(AddNewPersonCards.this, names,1);
                gridView.setAdapter(gridViewAdapter);
                gridViewAdapter.notifyDataSetChanged();

                //清空数据，关闭键盘
                delete_person.setText("");
                InputMethodManager imm_2 = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm_2.hideSoftInputFromWindow(delete_person.getWindowToken(), 0);
                break;

            //返回
            case R.id.bt_back:
                Intent intent_1 = new Intent(AddNewPersonCards.this, MyManageActivity.class);
                startActivity(intent_1);
                finish();
                break;

            //完成编辑
            case R.id.bt_finish:
            case R.id.bt_done:
                for (int k = 0; k < names.size(); k++){
                    mEditor.putString("person"+k, names.get(k));
                    mEditor.commit();
                }

                //存入人物卡数组长度
                mEditor.putInt("person_cards_num", names.size());
                mEditor.commit();

                Intent intent_2 = new Intent(AddNewPersonCards.this, MyManageActivity.class);
                startActivity(intent_2);
                finish();
                break;

        }
    }

}

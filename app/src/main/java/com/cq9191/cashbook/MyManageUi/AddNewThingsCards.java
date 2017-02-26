package com.cq9191.cashbook.MyManageUi;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AddNewThingsCards extends Activity implements View.OnClickListener{

    EditText new_things;
    EditText delete_things;

    GridView gridView;

    Button bt_add;
    Button bt_delete;
    Button bt_back;
    Button bt_finish_1;
    ImageButton bt_finish_2;

    //GridView适配器
    private GridViewAdapter gridViewAdapter;

    //人物卡片名称数组
    private List<String> things;

    SharedPreferences mPreferences;
    SharedPreferences.Editor mEditor;

    private int things_cards_num = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_things_cards_layout);

        mPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        mEditor = mPreferences.edit();
        things_cards_num = mPreferences.getInt("things_cards_num", 0);


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

        new_things = (EditText) findViewById(R.id.et_new_things);
        delete_things = (EditText) findViewById(R.id.et_delete_things);

        //卡片效果
        gridView = (GridView) findViewById(R.id.grid_things);

        //加载人物卡数据
        initList();
        gridViewAdapter = new GridViewAdapter(AddNewThingsCards.this, things, 2);

        if (gridView != null) {

            //每个卡片的长按监听
            gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //删除对应的item
                    things.remove(i);
                    //刷新数据
                    gridViewAdapter.notifyDataSetChanged();
                    return true;
                }
            });

            //设置Adapter
            gridView.setAdapter(gridViewAdapter);
        }
    }

    //默认人物卡名称
    private void initList() {
        things = new ArrayList<>();
        if (things_cards_num == 0) {
            things.add("淘宝");
            things.add("早餐");
            things.add("午餐");
            things.add("晚餐");
            things.add("还钱");
            things.add("零食、小吃");
            things.add("购买图书");
            things.add("看电影");
            things.add("新衣服");
            things.add("日用品");
        }
        else {
            for (int k = 0; k < things_cards_num; k++) {
                things.add(mPreferences.getString("things" + k, null));
            }
        }
    }

    //添加、删除、返回、完成编辑 按钮监听
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            //添加新人物卡片
            case R.id.bt_add:
                if (!Objects.equals(new_things.getText().toString(), "")) {
                    if (things.size() < 16){
                        things.add(new_things.getText().toString());
                    }
                    else {
                        Toast.makeText(AddNewThingsCards.this, "已超过事件卡上限数量", Toast.LENGTH_SHORT).show();
                    }
                }
                gridViewAdapter = new GridViewAdapter(AddNewThingsCards.this, things, 2);
                gridView.setAdapter(gridViewAdapter);
                gridViewAdapter.notifyDataSetChanged();

                new_things.setText("");
                InputMethodManager imm_1 = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm_1.hideSoftInputFromWindow(new_things.getWindowToken(), 0);
                break;

            //删除人物卡片
            case R.id.bt_delete:
                if (things.size() == 0){
                    Toast.makeText(AddNewThingsCards.this, "事件卡数量已为0", Toast.LENGTH_SHORT).show();
                }
                for (int k = 0; k < things.size(); k ++){
                    if (things.get(k).equals(delete_things.getText().toString())){
                        things.remove(k);
                    }
                }
                gridViewAdapter = new GridViewAdapter(AddNewThingsCards.this, things, 2);
                gridView.setAdapter(gridViewAdapter);
                gridViewAdapter.notifyDataSetChanged();

                delete_things.setText("");
                InputMethodManager imm_2 = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm_2.hideSoftInputFromWindow(delete_things.getWindowToken(), 0);
                break;

            //返回
            case R.id.bt_back:
                Intent intent_1 = new Intent(AddNewThingsCards.this, MyManageActivity.class);
                startActivity(intent_1);
                finish();
                break;

            //完成编辑
            case R.id.bt_finish:
            case R.id.bt_done:
                for (int k = 0; k < things.size(); k++){
                    mEditor.putString("things"+k, things.get(k));
                    mEditor.commit();
                }

                //存入人物卡数组长度
                mEditor.putInt("things_cards_num", things.size());
                mEditor.commit();

                Intent intent_2 = new Intent(AddNewThingsCards.this, MyManageActivity.class);
                startActivity(intent_2);
                finish();
                break;

        }
    }

}

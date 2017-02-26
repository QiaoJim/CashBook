package com.cq9191.cashbook.ThreeMainFragments;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.cq9191.cashbook.CertainCashListActivity;
import com.cq9191.cashbook.Data.CashDatabaseHelper;
import com.cq9191.cashbook.DetailRecords;
import com.cq9191.cashbook.R;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThingsFragment extends ListFragment {

    private final int BY_THINGS = 3;

    private List<String> things_record;

    private SimpleAdapter simpleAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSQLData();

        simpleAdapter = new SimpleAdapter(getActivity(),getList(),
                R.layout.things_record_item,
                new String[]{"things"},
                new int[]{R.id.tv_things});
        setListAdapter(simpleAdapter);
    }

    private List<? extends Map<String, ?>> getList() {
        List<Map<String, Object>> list = new ArrayList<>();

        for (int i = 0;i < things_record.size(); i++){
            Map<String, Object> map = new HashMap<>();
            map.put("things",things_record.get(i));
            list.add(map);
        }
        return list;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.things_record_list, container, false);
        return view;
    }

    //列表项点击
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        //记录点击item的账单信息
        Intent intent = new Intent(getActivity(), CertainCashListActivity.class);
        intent.putExtra("fragment_from", BY_THINGS);
        intent.putExtra("certain_title", things_record.get(position));
        startActivity(intent);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void getSQLData() {

        things_record = new ArrayList<>();

        CashDatabaseHelper helper = new CashDatabaseHelper(getContext(), "CashDatabase.db", 1);
        SQLiteDatabase db = helper.getWritableDatabase();

        Cursor cursor = db.query(true,"cash", new String[]{"things"}, null, null,null,null,"_id ASC", null);
        while (cursor.moveToNext()){
            things_record.add(cursor.getString(cursor.getColumnIndex("things")));
        }
    }
}

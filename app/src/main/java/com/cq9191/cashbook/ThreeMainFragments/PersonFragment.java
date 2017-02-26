package com.cq9191.cashbook.ThreeMainFragments;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.cq9191.cashbook.CertainCashListActivity;
import com.cq9191.cashbook.Data.CashDatabaseHelper;
import com.cq9191.cashbook.DetailRecords;
import com.cq9191.cashbook.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonFragment extends ListFragment {

    private final int BY_PERSON = 2;

    private List<String> person_record;

    private SimpleAdapter simpleAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSQLData();

        simpleAdapter = new SimpleAdapter(getActivity(),getList(),
                R.layout.person_record_item,
                new String[]{"person"},
                new int[]{R.id.tv_person});
        setListAdapter(simpleAdapter);
    }

    private List<? extends Map<String, ?>> getList() {
        List<Map<String, Object>> list = new ArrayList<>();

        for (int i = 0;i < person_record.size(); i++){
            Map<String, Object> map = new HashMap<>();
            map.put("person",person_record.get(i)+ "的账单");
            list.add(map);
        }
        return list;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.person_record_list, container, false);
        return view;
    }

    //列表项点击
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        //记录点击item的账单信息
        Intent intent = new Intent(getActivity(), CertainCashListActivity.class);
        intent.putExtra("fragment_from",BY_PERSON);
        intent.putExtra("certain_title", person_record.get(position));
        startActivity(intent);
        //getActivity().finish();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void getSQLData() {

        person_record = new ArrayList<>();

        CashDatabaseHelper helper = new CashDatabaseHelper(getContext(), "CashDatabase.db", 1);
        SQLiteDatabase db = helper.getWritableDatabase();

        Cursor cursor = db.query(true,"cash", new String[]{"person"}, null, null,null,null,"_id ASC", null);
        while (cursor.moveToNext()){
            person_record.add(cursor.getString(cursor.getColumnIndex("person")));
        }
    }
}

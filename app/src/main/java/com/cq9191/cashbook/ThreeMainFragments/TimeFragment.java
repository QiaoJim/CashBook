package com.cq9191.cashbook.ThreeMainFragments;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.cq9191.cashbook.Data.CashDatabaseHelper;
import com.cq9191.cashbook.DetailRecords;
import com.cq9191.cashbook.Data.CashData;
import com.cq9191.cashbook.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeFragment extends Fragment {

    private final int BY_TIME = 1;

    private List<Integer> _id_record;
    private List<String> money_record;
    private List<String> person_record;
    private List<String> things_record;

    private List<String> time_record;
    private List<String> hour_record;
    private List<String> week_record;

    private List<String> desc_record;
    private List<Integer> tag_record;

    private MyListAdapter adapter;
    private ListView time_order_list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //从数据库取出数据，加载列表
        getSQLData();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.time_record_list, container, false);
        time_order_list = (ListView) view.findViewById(R.id.time_order_list);

        adapter = new MyListAdapter(getContext(), time_record, hour_record, week_record,
                person_record, things_record, money_record, tag_record);
        time_order_list.setAdapter(adapter);
        time_order_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //传入该改单数据，在详情页显示
                CashData cash_data = new CashData();
                cash_data.setCash_id(_id_record.get(position));

                cash_data.setTime(time_record.get(position));
                cash_data.setHour(hour_record.get(position));
                cash_data.setWeek(week_record.get(position));

                cash_data.setCash_person(person_record.get(position));
                cash_data.setCash_things(things_record.get(position));
                cash_data.setCash_money(money_record.get(position));
                cash_data.setCash_desc(desc_record.get(position));
                cash_data.setTag(tag_record.get(position));

                Intent intent = new Intent(getActivity(), DetailRecords.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("cash_data", cash_data);
                bundle.putInt("fragment_from", BY_TIME);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        return view;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    //从数据库取得数据
    public void getSQLData() {

        _id_record = new ArrayList<>();

        time_record = new ArrayList<>();
        hour_record = new ArrayList<>();
        week_record = new ArrayList<>();

        money_record = new ArrayList<>();
        person_record = new ArrayList<>();
        things_record = new ArrayList<>();
        desc_record = new ArrayList<>();
        tag_record = new ArrayList<>();

        CashDatabaseHelper helper = new CashDatabaseHelper(getContext(), "CashDatabase.db", 1);
        SQLiteDatabase db = helper.getWritableDatabase();

        Cursor cursor = db.query("cash", null, null, null, null, null, "time DESC, hour DESC");
        while (cursor.moveToNext()) {
            _id_record.add(cursor.getInt(cursor.getColumnIndex("_id")));

            time_record.add(cursor.getString(cursor.getColumnIndex("time")));
            hour_record.add(cursor.getString(cursor.getColumnIndex("hour")));
            week_record.add(cursor.getString(cursor.getColumnIndex("week")));

            money_record.add(cursor.getString(cursor.getColumnIndex("money")));
            person_record.add(cursor.getString(cursor.getColumnIndex("person")));
            things_record.add(cursor.getString(cursor.getColumnIndex("things")));
            desc_record.add(cursor.getString(cursor.getColumnIndex("desc")));
            tag_record.add(cursor.getInt(cursor.getColumnIndex("tag")));
        }
    }
}

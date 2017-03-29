package com.easyanalysis.android;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private List<CostBean> mCostBeanList;
    private DatabaseHelper mDatabaseHelper;
    private CostListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        mDatabaseHelper = new DatabaseHelper(this);
        mCostBeanList = new ArrayList<>();
        ListView costList = (ListView) findViewById(R.id.lv_main);
        initCostData();
        mAdapter = new CostListAdapter(this, mCostBeanList);
        costList.setAdapter(mAdapter);





        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflate = LayoutInflater.from(MainActivity.this);
                View viewDialog = inflate.inflate(R.layout.new_cost_data,null);
                final EditText title = (EditText) viewDialog.findViewById(R.id.et_cost_title);
                final EditText money = (EditText) viewDialog.findViewById(R.id.et_cost_money);
                final DatePicker date = (DatePicker) viewDialog.findViewById(R.id.dp_cost_date);
                builder.setView(viewDialog);
                builder.setTitle("New Cost");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CostBean costBean = new CostBean();
                        costBean.costTitle = title.getText().toString();
                        costBean.costMoney = money.getText().toString();
                        costBean.costDate = date.getYear() + "-" + (date.getMonth()+1) + "-" +date.getDayOfMonth();
                        mDatabaseHelper.insertCost(costBean);//对数据库进行的操作
                        mCostBeanList.add(costBean);//对list进行的操作
                        mAdapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("Cancel",null);
                builder.create().show();
            }
        });


        costList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            /*
            public void onItemClick(AdapterView< > arg0, View arg1, int position,long arg3)
                各项的意义：arg1是当前item的view，通过它可以获得该项中的各个组件。
                    例如arg1.textview.settext("asd");
                                    arg2是当前item的ID。这个id根据你在适配器中的写法可以自己定义。
                                    arg3是当前的item在listView中的相对位置！
             */
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long id) {
                //定义AlertDialog.Builder对象，当长按列表项的时候弹出确认删除对话框
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("确认删除");
                builder.setTitle("提示");
                //添加AlertDialog.Builder对象的setPositiveButton()方法
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mCostBeanList.remove(position)!=null) {
                            //从数据库中删除
                            CostBean costbean = (CostBean) mAdapter.getItem(position);
                            int id = costbean.costid;
                            mDatabaseHelper.deleteCost(id);

                            Log.d(TAG, "success");
                        }else {
                            Log.d(TAG, "failed");
                        }
//                        mAdapter = new CostListAdapter(MainActivity.this, mCostBeanList);
                        Toast.makeText(MainActivity.this, "删除列表项", Toast.LENGTH_SHORT).show();
                        mAdapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("取消",null);
                builder.create().show();
                return false;
            }
        });
    }

    private void initCostData() {
        Cursor cursor = mDatabaseHelper.getAllCostData();
        if (cursor  != null) {
            while(cursor.moveToNext()) {
                CostBean costBean = new CostBean();
                costBean.costTitle = cursor.getString(cursor.getColumnIndex("cost_title"));
                costBean.costDate = cursor.getString(cursor.getColumnIndex("cost_date"));
                costBean.costMoney = cursor.getString(cursor.getColumnIndex("cost_money"));
                costBean.costid = cursor.getInt(cursor.getColumnIndex("id"));
                mCostBeanList.add(costBean);
            }
            cursor.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_chart) {
            Intent intent = new Intent(MainActivity.this,ChartActivity.class);
            intent.putExtra("cost_list", (Serializable) mCostBeanList);//传递数据
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

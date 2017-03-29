package com.easyanalysis.android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by www10 on 2017/3/22.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, "easyanalysis", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists analysis_cost(" +
        "id integer primary key, " +
        "cost_title varchar, " +
        "cost_date varchar, " +
        "cost_money varchar)");
    }
    public void insertCost(CostBean costBean) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("cost_title",costBean.costTitle);
        cv.put("cost_date",costBean.costDate);
        cv.put("cost_money",costBean.costMoney);
        database.insert("analysis_cost",null,cv);
    }

    public void deleteCost(int itemId) {
        SQLiteDatabase database = getWritableDatabase();
        String[] args = {String.valueOf(itemId)};
        database.delete("analysis_cost","id=?",args);
        database.close();
    }
    public Cursor getAllCostData(){
        SQLiteDatabase database = getWritableDatabase();
        return database.query("analysis_cost",null,null,null,null,null,"cost_date " + "ASC");
    }

    public void deleteAllData() {
        SQLiteDatabase database = getWritableDatabase();
        database.delete("analysis_cost",null,null);

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}

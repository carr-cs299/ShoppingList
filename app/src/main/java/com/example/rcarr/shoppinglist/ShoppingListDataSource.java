package com.example.rcarr.shoppinglist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ShoppingListDataSource {
    private SQLiteDatabase db;
    private DbHelper dbHelper;
    private String[] allColumns = {DbHelper.COLUMN_ID, DbHelper.COLUMN_ITEM, DbHelper.COLUMN_QUANTITY};

    public ShoppingListDataSource(Context context) {
        dbHelper = new DbHelper(context);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public ShoppingListItem createShoppingListItem(String item, int quantity) {
        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_ITEM, item);
        values.put(DbHelper.COLUMN_QUANTITY, quantity);
        long insertId = db.insert(dbHelper.TABLE_SHOPPINGLIST, null,
                values);
        Cursor cursor = db.query(dbHelper.TABLE_SHOPPINGLIST,
                allColumns, dbHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        ShoppingListItem newShoppingListItem = cursorToShoppingListItem(cursor);
        cursor.close();
        return newShoppingListItem;
    }


    public List<ShoppingListItem> getAllItems() {
        List<ShoppingListItem> list = new ArrayList<ShoppingListItem>();
        Cursor cursor = db.query(dbHelper.TABLE_SHOPPINGLIST,allColumns,null,null,null,null,null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ShoppingListItem item = cursorToShoppingListItem(cursor);
            list.add(item);
            cursor.moveToNext();
        }

        cursor.close();
        return list;
    }

    public boolean deleteItem(ShoppingListItem item) {
        int success = db.delete(dbHelper.TABLE_SHOPPINGLIST,dbHelper.COLUMN_ID + " = " + item.getId(),null);
        return success > 0;
    }

    private ShoppingListItem cursorToShoppingListItem(Cursor cursor) {
        ShoppingListItem shoppingListItem = new ShoppingListItem();
        shoppingListItem.setId(cursor.getLong(0));
        shoppingListItem.setItem(cursor.getString(1));
        shoppingListItem.setQuantity(cursor.getInt(2));
        return shoppingListItem;
    }
}

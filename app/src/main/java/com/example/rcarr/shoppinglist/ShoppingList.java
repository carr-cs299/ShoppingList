package com.example.rcarr.shoppinglist;

import android.app.ListActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.sql.SQLException;
import java.util.List;


public class ShoppingList extends ListActivity {
    private ShoppingListDataSource datasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        datasource = new ShoppingListDataSource(this);
        try {
            datasource.open();
        } catch(SQLException ex) {}

        List<ShoppingListItem> items = datasource.getAllItems();

        ArrayAdapter<ShoppingListItem> adapter = new ArrayAdapter<ShoppingListItem>(this, android.R.layout.simple_list_item_1, items);
        setListAdapter(adapter);

        ListView list = (ListView) findViewById(android.R.id.list);
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ListView lv = (ListView) findViewById(android.R.id.list);
                ShoppingListItem item = (ShoppingListItem) lv.getItemAtPosition(position);
                boolean success = datasource.deleteItem(item);
                ArrayAdapter<ShoppingListItem> adapter = (ArrayAdapter) lv.getAdapter();
                if (success)
                    adapter.remove(item);
                adapter.notifyDataSetChanged();
                return success;
            }
        });
    }

    public void onClick(View view) {
        @SuppressWarnings("unchecked")
        ArrayAdapter<ShoppingListItem> adapter = (ArrayAdapter<ShoppingListItem>) getListAdapter();
        ShoppingListItem item = null;
        EditText etItem = (EditText) findViewById(R.id.etName);
        EditText etQuantity = (EditText) findViewById(R.id.etQuantity);

        switch (view.getId()) {
            case R.id.btnAdd:
                String itemName = etItem.getText().toString();
                int itemQuantity = Integer.parseInt(etQuantity.getText().toString());

                // save the new comment to the database
                item = datasource.createShoppingListItem(itemName,itemQuantity);
                adapter.add(item);
                break;
            /*case R.id.delete:
                if (getListAdapter().getCount() > 0) {
                    comment = (Comment) getListAdapter().getItem(0);
                    datasource.deleteComment(comment);
                    adapter.remove(comment);
                }
                break;*/
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        try {
            datasource.open();
        } catch (SQLException ex) {}
        super.onResume();
    }

    @Override
    protected void onPause() {
        datasource.close();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shopping_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

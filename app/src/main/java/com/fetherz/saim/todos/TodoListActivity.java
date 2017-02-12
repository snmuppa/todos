package com.fetherz.saim.todos;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.fetherz.saim.todos.data.DatabaseHelper;
import com.fetherz.saim.todos.data.TodosContract;
import com.fetherz.saim.todos.data.TodosQueryHandler;

public class TodoListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    static final int ALL_RECORDS = -1;
    private static final int URL_LOADER = 0 ;
    TodosCursorAdapter adapter;
    Cursor cursor;

    private void createCategory(){
        ContentValues values = new ContentValues();
        values.put(TodosContract.CategoryEntry.COLUMN_DESCRIPTION, "Work");

        //content resolver: class for providing the applications access to the content model
        Uri uri = getContentResolver().insert(TodosContract.CategoryEntry.CONTENT_URI, values);
        Log.d("TodoListActivity", "Inserted Category " + uri);
    }

    private void updateTodo(){
        int id = 1;
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] args = { String.valueOf(id)};
        ContentValues values = new ContentValues();
        values.put(TodosContract.TodoEntry.COLUMN_TEXT, "call james");

        int numRows = db.update(TodosContract.TodoEntry.TABLE_NAME, values, TodosContract.TodoEntry._ID + " =?", args);
        Log.d("Update rows", String.valueOf(numRows));
        db.close();
    }

    private void createTestTodos(){
        for (int i = 1; i<20; i++ ) {
            ContentValues values = new ContentValues();
            values.put(TodosContract.TodoEntry.COLUMN_TEXT, "Todo Item #" + i);
            values.put(TodosContract.TodoEntry.COLUMN_CATEGORY, 1);
            values.put(TodosContract.TodoEntry.COLUMN_CREATED, "2016-02-02");
            values.put(TodosContract.TodoEntry.COLUMN_DONE, 0);
            TodosQueryHandler handler = new TodosQueryHandler(this.getContentResolver());
            handler.startInsert(1, null, TodosContract.TodoEntry.CONTENT_URI, values);
        }
    }

    private void deleteTodo(int id)
    {
        String[] args = { String.valueOf(id) };
        if(id == ALL_RECORDS){
            args = null;
        }

        TodosQueryHandler handler = new TodosQueryHandler(this.getContentResolver());
        handler.startDelete(1, null, TodosContract.TodoEntry.CONTENT_URI, TodosContract.TodoEntry._ID + "=?", args);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getLoaderManager().initLoader(URL_LOADER, null, this);
        final ListView lv=(ListView) findViewById(R.id.lvTodos);
        adapter = new TodosCursorAdapter(this, cursor, false);
        lv.setAdapter(adapter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*//adds the click event to the listView, reading the content
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                Intent intent = new Intent(TodoListActivity.this, TodoActivity.class);
                String content= (String) lv.getItemAtPosition(pos);
                intent.putExtra("Content", content);
                startActivity(intent);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_categories:
                //get the categories cursor for the
                Intent intent = new Intent(TodoListActivity.this, CategoryActivity.class);
                startActivity(intent);
                break;
            case R.id.action_delete_all_todos:
                deleteTodo(ALL_RECORDS);
                break;
            case R.id.action_create_test_data:
                createTestTodos();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = { TodosContract.TodoEntry.COLUMN_TEXT,
                TodosContract.TodoEntry.TABLE_NAME + "." + TodosContract.TodoEntry._ID,
                TodosContract.TodoEntry.COLUMN_CREATED,
                TodosContract.TodoEntry.COLUMN_EXPIRED,
                TodosContract.TodoEntry.COLUMN_DONE,
                TodosContract.CategoryEntry.TABLE_NAME + "." + TodosContract.CategoryEntry._ID,
                TodosContract.CategoryEntry.COLUMN_DESCRIPTION };


        return new CursorLoader(this, TodosContract.TodoEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
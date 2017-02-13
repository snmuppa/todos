package com.fetherz.saim.todos;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;

import com.fetherz.saim.todos.data.TodosContract;
import com.fetherz.saim.todos.data.TodosQueryHandler;
import com.fetherz.saim.todos.model.Category;
import com.fetherz.saim.todos.model.CategoryList;
import com.fetherz.saim.todos.model.Todo;

/**
 * TodoList class
 */
public class TodoListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    static final int ALL_RECORDS = -1;
    static final int ALL_CATEGORIES = -1;
    private static final int URL_LOADER = 0 ;
    TodosCursorAdapter adapter;
    Cursor cursor;
    CategoryList categories = new CategoryList();
    Spinner spinner;
    CategoryListAdapter categoriesAdapter;

    private void setCategories() {
        final TodosQueryHandler categoriesHandler = new TodosQueryHandler(this.getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                try {
                    if (cursor != null) {
                        int index = 0;
                        categories.Items.add(index, new Category(ALL_CATEGORIES, "All Categories"));
                        index++;
                        while(cursor.moveToNext()){
                            categories.Items.add(index, new Category(cursor.getInt(0), cursor.getString(1)));
                            index++;
                        }
                    }
                } finally {
                    //do something here
                }
            }
        };
        categoriesHandler.startQuery(1, null, TodosContract.CategoryEntry.CONTENT_URI, null, null, null, TodosContract.CategoryEntry.COLUMN_DESCRIPTION);
    }

    /*
    * this is purely used for testing purposes by creating a whole bunch of TODO_items
     */
    private void createTestTodos(){
        for (int i = 1; i < 20; i++) {
            ContentValues values = new ContentValues();
            values.put(TodosContract.TodoEntry.COLUMN_TEXT, "Todo Item #" + i);
            values.put(TodosContract.TodoEntry.COLUMN_CATEGORY, 1);
            values.put(TodosContract.TodoEntry.COLUMN_CREATED, "2016-02-02");
            values.put(TodosContract.TodoEntry.COLUMN_EXPIRED, "2017-02-14");
            int done = (i%2 == 1) ? 1 : 0;
            values.put(TodosContract.TodoEntry.COLUMN_DONE, done);
            TodosQueryHandler handler = new TodosQueryHandler(this.getContentResolver());
            handler.startInsert(1, null, TodosContract.TodoEntry.CONTENT_URI, values);
        }
    }

    /**
     * delete all the todos in one go
     * @param id
     */
    private void deleteTodo(int id)
    {
        String[] args = { String.valueOf(id) };
        if(id == ALL_RECORDS){
            args = null;
        }

        TodosQueryHandler handler = new TodosQueryHandler(this.getContentResolver());
        handler.startDelete(1, null, TodosContract.TodoEntry.CONTENT_URI, TodosContract.TodoEntry._ID + "=?", args);
    }

    /**
     * handle creation
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        spinner = (Spinner)findViewById(R.id.spinCategories);

        getLoaderManager().initLoader(URL_LOADER, null, this);

        //set the categories
        setCategories();

        final ListView lv=(ListView) findViewById(R.id.lvTodos);

        adapter = new TodosCursorAdapter(this, cursor, false);
        lv.setAdapter(adapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        handleListItemClick(lv);

        handleFloatingActionButtonClick();

        handleSpinnerSelectionChange();
    }

    /**
    * event handler for spinner selection change
    * */
    private void handleSpinnerSelectionChange() {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItenView, int position, long id)
            {
                if(position >= 0)
                {
                    getLoaderManager().restartLoader(URL_LOADER, null, TodoListActivity.this); // restart the loader when something has changed
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent){

            }
        });
    }

    /**
     * event handler for todos_list item change
     * @param lv
     */
    private void handleListItemClick(ListView lv) {
        //adds the click event to the listView, reading the content
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                //moves the cursor to the selected row
                cursor = (Cursor)adapterView.getItemAtPosition(pos);

                //get the todo_object from the cursor
                int todoId = cursor.getInt(cursor.getColumnIndex(TodosContract.TodoEntry._ID));
                String todoText = cursor.getString(cursor.getColumnIndex((TodosContract.TodoEntry.COLUMN_TEXT)));
                String todoExpiredDate = cursor.getString(cursor.getColumnIndex(TodosContract.TodoEntry.COLUMN_EXPIRED));
                int todoDone = cursor.getInt(cursor.getColumnIndex(TodosContract.TodoEntry.COLUMN_DONE));
                String todoCreated = cursor.getString(cursor.getColumnIndex(TodosContract.TodoEntry.COLUMN_CREATED));
                String todoCategory = cursor.getString(cursor.getColumnIndex(TodosContract.TodoEntry.COLUMN_CATEGORY));
                boolean done = (todoDone == 1);


                Todo todo = new Todo(todoId, todoText, todoCreated, todoExpiredDate, done, todoCategory);

                // pass the Todo_Object to the todoActivity
                Intent intent = new Intent(TodoListActivity.this, TodoActivity.class);

                //extend the intent object
                intent.putExtra("todo", todo);
                intent.putExtra("categories", categories);
                startActivity(intent);
            }
        });
    }

    /*
     * event handler for float action button click
     */
    private void handleFloatingActionButtonClick() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() { //on click of a floating action button click, create new TODO_object and pass it on
            @Override
            public void onClick(View view) {
                Todo todo = new Todo(0, "", "", "",  false, "0");
                Intent intent = new Intent(TodoListActivity.this, TodoActivity.class);

                intent.putExtra("todo", todo);
                intent.putExtra("categories", categories);
                startActivity(intent);
            }
        });
    }

    /**
     * display menu options
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate the menu
        // this adds items to the action bar if menu exists
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * handling the selection of the items in action bar
     * @param item
     * @return
     */
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

    /**
     * create loader handler
     * @param id
     * @param args
     * @return
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = { TodosContract.TodoEntry.COLUMN_TEXT,
                TodosContract.TodoEntry.TABLE_NAME + "." + TodosContract.TodoEntry._ID,
                TodosContract.TodoEntry.COLUMN_CREATED,
                TodosContract.TodoEntry.COLUMN_EXPIRED,
                TodosContract.TodoEntry.COLUMN_DONE,
                TodosContract.TodoEntry.COLUMN_CATEGORY,
                TodosContract.CategoryEntry.TABLE_NAME + "." + TodosContract.CategoryEntry.COLUMN_DESCRIPTION };

        String selection;
        String[] arguments;

        if (spinner.getSelectedItemId()< 0) { //is the selection category id on the spinner < -1, pass in null arguments to display all todo_items
            selection = null;
            arguments = null;
        }
        else { //or else display the items related to the selected category, by creation the right selection and arguments are filter criteria
            selection = TodosContract.TodoEntry.COLUMN_CATEGORY + "=?";
            arguments = new String[] { String.valueOf(spinner.getSelectedItemId()) };
        }

        return new CursorLoader(this, TodosContract.TodoEntry.CONTENT_URI, projection, selection, arguments, null);
    }

    /**
     *
     * @param loader
     * @param data
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        adapter.swapCursor(data);
        if(categoriesAdapter == null){
            categoriesAdapter = new CategoryListAdapter(categories.Items);
            spinner.setAdapter(categoriesAdapter);
        }
    }

    /**
     *
     * @param loader
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
package com.fetherz.saim.todos;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Spinner;

import com.fetherz.saim.todos.data.TodosContract;
import com.fetherz.saim.todos.data.TodosQueryHandler;
import com.fetherz.saim.todos.databinding.ActivityTodoBinding;
import com.fetherz.saim.todos.model.Category;
import com.fetherz.saim.todos.model.CategoryList;
import com.fetherz.saim.todos.model.Todo;

public class TodoActivity extends AppCompatActivity {
    Todo todo;
    Spinner spinner;
    CategoryList categories;
    CategoryListAdapter categoriesAdapter;
    TodosQueryHandler queryHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        queryHandler = new TodosQueryHandler(getContentResolver());
        // when you have a layout file (activity_todo.xml) in this case, android studio
        // will generate a 'BindingClass' (ActivityTodoBinding.class) at compile time based
        // on the layout file name in pascal case
        ActivityTodoBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_todo);

        Intent intent = getIntent();
        todo = (Todo) intent.getSerializableExtra("todo");
        binding.setTodo(todo);

        categories = (CategoryList) intent.getSerializableExtra("categories");
        categoriesAdapter = new CategoryListAdapter(categories.Items);
        spinner = (Spinner) findViewById(R.id.spCategories);
        spinner.setAdapter(categoriesAdapter);

        //set the position of the spinners
        int position = 0;

        if (Integer.valueOf(todo.category.get()) == 0) { // category at 0th location is 'All categories', so skip that location and default to location 1
            position = categories.Items.size() > 1 ? 1 : 0; // defensive conditional if there are no categories (All categories is hard coded, so it always exists)
        } else {
            for (Category category : categories.Items) {
                if (Integer.valueOf(category.categoryId.get()) == Integer.valueOf(todo.category.get())) {
                    break;
                }
                position++;
            }
        }

        spinner.setSelection(position);

    }

    /**
     * display the items
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate the menu
        // this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_todo, menu);
        return true;
    }

    /*
    * handle selections on options menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete_todo) {
            //confirm?
            new AlertDialog.Builder(TodoActivity.this)
                    .setTitle(getString(R.string.delete_todo_dialog_title))
                    .setMessage(getString(R.string.delete_todo_dialog))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            //delete
                            Uri uri = Uri.withAppendedPath(TodosContract.TodoEntry.CONTENT_URI, String.valueOf(todo.todoID.get()));

                            String selection = TodosContract.TodoEntry._ID + "=?";
                            String[] arguments = new String[1];
                            arguments[0] = String.valueOf(todo.todoID.get());

                            queryHandler.startDelete(1, null, uri
                                    , selection, arguments);
                            Intent intent = new Intent(TodoActivity.this, TodoListActivity.class);
                            startActivity(intent);

                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    * save data to the SqlLite (when the user goes back the Main TodoListAcitivity, the data is saved)
     */
    @Override
    public void onPause() {
        super.onPause();

        String[] arguments;
        TodosQueryHandler todosQueryHandler = new TodosQueryHandler(getContentResolver());

        //get the category id based the selected spinner
        Category cat = (Category) spinner.getSelectedItem();
        int catId = cat.categoryId.get();

        //create a todo_object from the information in the screen
        ContentValues values = new ContentValues();
        values.put(TodosContract.TodoEntry.COLUMN_TEXT, todo.text.get());
        values.put(TodosContract.TodoEntry.COLUMN_CATEGORY, catId);
        values.put(TodosContract.TodoEntry.COLUMN_DONE, todo.done.get());
        values.put(TodosContract.TodoEntry.COLUMN_EXPIRED, todo.expired.get());
        values.put(TodosContract.TodoEntry.COLUMN_CREATED, todo.created.get());

        if (todo != null && todo.todoID.get() != 0) { //update if existing
            arguments = new String[] { String.valueOf(todo.todoID.get()) };
            todosQueryHandler.startUpdate(1, null, TodosContract.TodoEntry.CONTENT_URI, values, TodosContract.TodoEntry._ID + "=?", arguments);
        } else if (todo != null && todo.todoID.get() == 0) { //or insert if new
            todosQueryHandler.startInsert(1, null, TodosContract.TodoEntry.CONTENT_URI, values);
        }
    }
}

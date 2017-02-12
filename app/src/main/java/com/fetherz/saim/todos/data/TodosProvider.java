package com.fetherz.saim.todos.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.fetherz.saim.todos.data.TodosContract.CONTENT_AUTHORITY;
import static com.fetherz.saim.todos.data.TodosContract.PATH_CATEGORIES;
import static com.fetherz.saim.todos.data.TodosContract.PATH_TODOS;

/**
 * Created by Sai-Mac on 2/11/17.
 */

public class TodosProvider extends ContentProvider {

    //constants for the operations
    private static final int TODOS = 1;
    private static final int TODOS_ID = 2;
    private static final int CATEGORIES = 3;
    private static final int CATEGORIES_ID = 4;

    //UriMatcher
    private static final UriMatcher uriMatcher = new UriMatcher((UriMatcher.NO_MATCH));

    //static initializer for URI Matcher
    static
    {
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_TODOS, TODOS);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_TODOS + "/#", TODOS_ID); // to match an URI like 'content://com.fetherz.saim.todos.todosprovider/todos/100'

        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_CATEGORIES, CATEGORIES);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_CATEGORIES + "/#", CATEGORIES_ID); // to match an URI like 'content://com.fetherz.saim.todos.todosprovider/categories/2'
    }

    private DatabaseHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor;
        int match = uriMatcher.match(uri);

        //'todo_table' inner join 'category_table' on category = category._id
        String joinQuery = TodosContract.TodoEntry.TABLE_NAME + " inner join " + TodosContract.CategoryEntry.TABLE_NAME +
                " on " + TodosContract.TodoEntry.COLUMN_CATEGORY + " = " + TodosContract.CategoryEntry.TABLE_NAME + "." + TodosContract.CategoryEntry._ID;

        SQLiteQueryBuilder queryBuilder;

        switch (match)
        {
            case TODOS:
                queryBuilder = new SQLiteQueryBuilder();
                queryBuilder.setTables(joinQuery);
                cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case TODOS_ID:
                queryBuilder = new SQLiteQueryBuilder();
                queryBuilder.setTables(joinQuery);
                selection = TodosContract.CategoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CATEGORIES:
                cursor = db.query(TodosContract.CategoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CATEGORIES_ID:
                selection = TodosContract.CategoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(TodosContract.CategoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI");
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;

    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        int match = uriMatcher.match(uri);

        switch (match) {
            case TODOS:
                return insertRecord(uri, contentValues, TodosContract.TodoEntry.TABLE_NAME);
            case CATEGORIES:
                return insertRecord(uri, contentValues, TodosContract.CategoryEntry.TABLE_NAME);
            default:
                throw new IllegalArgumentException("Insert: Unknown URI" + uri);
        }
    }

    private Uri insertRecord(Uri uri,  ContentValues values, String table) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = db.insert(table, null, values);
        if(id == -1)
        {
            Log.e("TodosProvider", "Error insert " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int match = uriMatcher.match(uri);
        switch (match){
            case TODOS:
                return deleteRecord(uri, null, null, TodosContract.TodoEntry.TABLE_NAME);
            case TODOS_ID:
                return deleteRecord(uri, selection, selectionArgs, TodosContract.TodoEntry.TABLE_NAME);
            case CATEGORIES:
                return deleteRecord(uri, null, null, TodosContract.CategoryEntry.TABLE_NAME);
            case CATEGORIES_ID:
                return deleteRecord(uri, selection, selectionArgs, TodosContract.CategoryEntry.TABLE_NAME);
            default:
                throw new IllegalArgumentException("Delete: Unknown URI" + uri);
        }
    }

    private int deleteRecord(Uri uri, String selection, String[] selectionArgs, String table)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int id = db.delete(table, selection, selectionArgs);

        if(id == 0){
            Log.e("Error", "delete error for URI " + uri);
            return -1;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return id;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int match = uriMatcher.match(uri);
        switch (match){
            case TODOS:
                return updateRecord(uri, values, selection, selectionArgs, TodosContract.TodoEntry.TABLE_NAME);
            case TODOS_ID:
                selection = TodosContract.TodoEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf((ContentUris.parseId(uri))) };
                return updateRecord(uri, values, selection, selectionArgs, TodosContract.TodoEntry.TABLE_NAME);
            case CATEGORIES:
                return updateRecord(uri, values, selection, selectionArgs, TodosContract.CategoryEntry.TABLE_NAME);
            case CATEGORIES_ID:
                selection = TodosContract.CategoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf((ContentUris.parseId(uri))) };
                return updateRecord(uri, values, selection, selectionArgs, TodosContract.CategoryEntry.TABLE_NAME);
            default:
                throw new IllegalArgumentException("Update: Unknown URI" + uri);
        }
    }

    private int updateRecord(Uri uri, ContentValues values, String selection, String[] selectionArgs, String table)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int id = db.update(table, values, selection, selectionArgs);

        if(id == 0){
            Log.e("Error", "update error for URI " + uri);
            return -1;
        }
        return id;
    }
}

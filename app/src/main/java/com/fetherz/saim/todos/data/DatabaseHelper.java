package com.fetherz.saim.todos.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fetherz.saim.todos.data.TodosContract.CategoryEntry;
import com.fetherz.saim.todos.data.TodosContract.TodoEntry;

/**
 * Created by Sai-Mac on 2/11/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "todosapp.db";
    private static final int DATABASE_VERSION = 1;


    private static final String TABLE_CATEGORY_CREATE = "CREATE TABLE " + CategoryEntry.TABLE_NAME + " (" + CategoryEntry._ID + " INTEGER PRIMARY KEY, " + CategoryEntry.COLUMN_DESCRIPTION + " TEXT " + ") ";

    private static final String TABLE_TODO_CREATE = "CREATE TABLE " + TodoEntry.TABLE_NAME + " (" + TodoEntry._ID + " INTEGER PRIMARY KEY, " + TodoEntry.COLUMN_TEXT + " TEXT, " +
            TodoEntry.COLUMN_CREATED + " TEXT default CURRENT_TIMESTAMP, " + TodoEntry.COLUMN_EXPIRED + " TEXT, " + TodoEntry.COLUMN_DONE + " INTEGER, " + TodoEntry.COLUMN_CATEGORY + " INTEGER, " +
            " FOREIGN KEY(" + TodoEntry.COLUMN_CATEGORY + ") REFERENCES " + CategoryEntry.TABLE_NAME + "(" + CategoryEntry._ID + ") " + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CATEGORY_CREATE);
        db.execSQL(TABLE_TODO_CREATE);
        //seed(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //in this case we are basically deleting all the data during upgrade, this can be made smarter if the data has to persist between upgrades
        db.execSQL("DROP TABLE IF EXISTS " + TodoEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CategoryEntry.TABLE_NAME);
        onCreate(db);
    }
}

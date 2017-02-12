package com.fetherz.saim.todos.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Sai-Mac on 2/11/17.
 */

public final class TodosContract {
    public static final String CONTENT_AUTHORITY = "com.fetherz.saim.todos.todosprovider";
    public static final String PATH_TODOS = "todo";
    public static final String PATH_CATEGORIES = "category";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class TodoEntry implements BaseColumns {

        //full content URI that is used to identify the TODO_table
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TODOS);

        // Table name
        public static final String TABLE_NAME = "todo";
        //column (field) names
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_TEXT = "text";
        public static final String COLUMN_CREATED = "created";
        public static final String COLUMN_EXPIRED = "expired";
        public static final String COLUMN_DONE = "done";
        public static final String COLUMN_CATEGORY = "category";
    }

    public static final class CategoryEntry implements BaseColumns {

        //full content URI that is used to identify the Category table
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CATEGORIES);

        // Table name
        public static final String TABLE_NAME = "category";
        //column names
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_DESCRIPTION = "description";
    }
}

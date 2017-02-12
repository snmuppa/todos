package com.fetherz.saim.todos.data;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;

/**
 * Created by Sai-Mac on 2/12/17.
 */

public class TodosQueryHandler extends AsyncQueryHandler{
    public TodosQueryHandler(ContentResolver resolver) {
        super(resolver);
    }
}

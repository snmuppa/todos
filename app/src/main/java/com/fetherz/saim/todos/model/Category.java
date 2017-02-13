package com.fetherz.saim.todos.model;

import android.databinding.ObservableField;
import android.databinding.ObservableInt;

import java.io.Serializable;

/**
 * Created by Sai-Mac on 2/12/17.
 */

public class Category implements Serializable {
    public final ObservableInt categoryId = new ObservableInt();
    public final ObservableField<String> description = new ObservableField<String>();

    public Category(){}

    public Category(int id, String description)
    {
        this.categoryId.set(id);
        this.description.set(description);
    }
}

package com.fetherz.saim.todos.model;

import android.databinding.ObservableArrayList;

import java.io.Serializable;

/**
 * Created by Sai-Mac on 2/12/17.
 */

public class CategoryList implements Serializable {
    public final ObservableArrayList<Category> Items;

    public CategoryList(){
        this.Items = new ObservableArrayList<Category>();
    }

    public CategoryList(ObservableArrayList<Category> categories) {
        this.Items = categories;
    }
}

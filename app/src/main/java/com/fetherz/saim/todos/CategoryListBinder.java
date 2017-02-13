package com.fetherz.saim.todos;

import android.databinding.BindingAdapter;
import android.databinding.ObservableArrayList;
import android.widget.ListView;

import com.fetherz.saim.todos.model.Category;

/**
 * Created by Sai-Mac on 2/12/17.
 */

public class CategoryListBinder {

    @BindingAdapter("bind:items")
    public static void bindList(ListView view, ObservableArrayList<Category> categories){
        CategoryListAdapter adapter;
        if(categories == null){
            adapter = new CategoryListAdapter();
        }
        else {
            adapter = new CategoryListAdapter(categories);
        }

        view.setAdapter(adapter);
    }

}

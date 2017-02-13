package com.fetherz.saim.todos;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableInt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Spinner;

import com.fetherz.saim.todos.model.Category;
import com.fetherz.saim.todos.databinding.CategoryListItemBinding;

/**
 * Created by Sai-Mac on 2/12/17.
 */
public class CategoryListAdapter extends BaseAdapter{ //an adapter is a bridge b/w UI and data source, it's task is to provide data to the views
    public ObservableArrayList<Category> list;

    private ObservableInt position = new ObservableInt();
    private LayoutInflater inflater;

    public CategoryListAdapter(ObservableArrayList<Category> categories) {
        list = categories;
    }

    public CategoryListAdapter() {
        list = new ObservableArrayList<Category>();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        int id = list.get(position).categoryId.get();
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null) {
            inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        CategoryListItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.category_list_item, parent, false);
        binding.setCategory(list.get(position));
        return binding.getRoot();
    }

    //for the spinner
    public int getPosition(Spinner spinner) {
        return spinner.getSelectedItemPosition();
    }

    public int getPosition() {
        return position.get();
    }
    public void setPosition(int position) {
        this.position.set(position);
    }
}

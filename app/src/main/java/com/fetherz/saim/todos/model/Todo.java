package com.fetherz.saim.todos.model;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;

import java.io.Serializable;

/**
 * Created by Sai-Mac on 2/12/17.
 */

public class Todo implements Serializable {
    public ObservableInt todoID = new ObservableInt();
    public ObservableField<String> text = new ObservableField<String>();
    public ObservableField<String> created = new ObservableField<String>();
    public ObservableField<String> expired = new ObservableField<String>();
    public ObservableField<String> category = new ObservableField<String>();
    public ObservableBoolean done = new ObservableBoolean();

    public Todo(){}

    //todo: replace with builder pattern
    public Todo(int id, String text, String created, String expired, boolean done, String category){
        this.todoID.set(id);
        this.text.set(text);
        this.created.set(created);
        this.expired.set(expired);
        this.done.set(done);
        this.category.set(category);
    }
}

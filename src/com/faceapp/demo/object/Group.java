package com.faceapp.demo.object;

import java.util.ArrayList;

public class Group<T> extends Base<T> {
    public String group_id;

    public String tag;

    public String group_name;

    public ArrayList<T> person;
    
    @Override
    public String getID() {
        return group_id;
    }
    
    @Override
    public String getName() {
        return group_name;
    }
    
    @Override
    public ArrayList<T> getArray() {
        return person;
    }
    
}

package com.faceapp.demo.object;

import java.util.ArrayList;

public interface IBase<T> {
    
    public String getName();
    
    public String getID();
    
    public ArrayList<T> getArray();
}

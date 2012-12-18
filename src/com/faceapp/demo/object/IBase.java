package com.faceapp.demo.object;

import java.util.List;

public interface IBase<T> {
    
    public String getName();
    
    public String getID();
    
    public List<T> getArray();
}

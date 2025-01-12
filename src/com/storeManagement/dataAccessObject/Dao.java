package com.storeManagement.dataAccessObject;

import java.sql.SQLException;
import java.util.List;

public interface Dao<T> {

    int add(T obj)
            throws SQLException;
    void delete(int id)
            throws SQLException;
    T get(int id)
            throws SQLException;
    List<T> getList()
            throws SQLException;
    void update(T obj)
            throws SQLException;
}
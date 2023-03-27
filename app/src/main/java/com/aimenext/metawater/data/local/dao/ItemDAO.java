package com.aimenext.metawater.data.local.dao;


import com.aimenext.metawater.data.local.entity.Item;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface ItemDAO {

    @Query("SELECT * FROM items")
    public List<Item> getItems();

    @Query("SELECT * FROM items WHERE id = :id")
    public Item getItemById(Long id);

    @Insert()
    public void insert(List<Item> items);

    @Insert()
    public void insert(Item item);

    @Query("DELETE FROM items WHERE id = :id")
    public void deleteJob(Long id);

    @Query("DELETE FROM items")
    public void deleteAll();
}
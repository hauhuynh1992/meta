package com.aimenext.metawater.data.local.db;


import com.aimenext.metawater.data.local.dao.ItemDAO;
import com.aimenext.metawater.data.local.entity.Item;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Item.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ItemDAO getItemDAO();
}
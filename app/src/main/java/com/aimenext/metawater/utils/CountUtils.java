package com.aimenext.metawater.utils;

import com.aimenext.metawater.data.Job;
import com.aimenext.metawater.data.local.entity.Item;

import java.util.ArrayList;

public class CountUtils {
    public static ArrayList<Job> removeDuplicate(ArrayList<Job> items) {
        ArrayList<Job> filter = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            if (!isDuplicateCode(items.get(i).getCanCode(), filter)) {
                filter.add(items.get(i));
            }
        }
        return filter;
    }

    public static ArrayList<Item> removeDuplicateItem(ArrayList<Item> items) {
        ArrayList<Item> filter = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            if (!isDuplicateItemCode(items.get(i).getCode(), filter)) {
                filter.add(items.get(i));
            }
        }
        return filter;
    }

    public static int getDuplicateCode(String code, ArrayList<Job> items) {
        int num = 0;
        for (int i = 0; i < items.size(); i++) {
            if (code.equals(items.get(i).getCanCode())) {
                num++;
            }
        }
        return num;
    }

    public static Boolean isDuplicateCode(String code, ArrayList<Job> items) {
        Boolean isDuplicate = false;
        for (int i = 0; i < items.size(); i++) {
            if (code.equals(items.get(i).getCanCode())) {
                return true;
            }
        }
        return isDuplicate;
    }

    public static Boolean isDuplicateItemCode(String code, ArrayList<Item> items) {
        Boolean isDuplicate = false;
        for (int i = 0; i < items.size(); i++) {
            if (code.equals(items.get(i).getCode())) {
                return true;
            }
        }
        return isDuplicate;
    }
}

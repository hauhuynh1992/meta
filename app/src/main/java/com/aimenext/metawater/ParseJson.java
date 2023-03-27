package com.aimenext.metawater;

import android.util.Log;

import com.aimenext.metawater.data.ArMarker;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ParseJson {

    public ArMarker[] parseArMarkerFromStr(String strres) {
        //create ObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            //convert json string to object
            ArMarker[] emp = objectMapper.readValue(strres, ArMarker[].class);
            return emp;
        } catch (JsonProcessingException e) {
            Log.getStackTraceString(e);
        }
        return null;
    }
}

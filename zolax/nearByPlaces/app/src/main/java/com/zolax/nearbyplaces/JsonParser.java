package com.zolax.nearbyplaces;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonParser {
    private HashMap<String, String> parseJsonObject(JSONObject object) {
        //init hash map
        HashMap<String, String> dataList = new HashMap<>();
        try {
            //get name from object
            String name = object.getString("name");
            //get latitide
            String latitude = object.getJSONObject("geometry")
                    .getJSONObject("location").getString("lat");
            //get longitude
            String longitude = object.getJSONObject("geometry")
                    .getJSONObject("location").getString("lng");
            //Put all value in hash map
            dataList.put("name", name);
            dataList.put("lat", latitude);
            dataList.put("lng", longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //return hash map
        return dataList;
    }

    private List<HashMap<String, String>> parseJsonArray(JSONArray jsonArray) {
        //init hash map list
        List<HashMap<String, String>> dataList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            //init hash map
            try {
                HashMap<String, String> data = parseJsonObject((JSONObject)jsonArray.get(i));
                //add data in hash map list
                dataList.add(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return  dataList;
    }

    public List<HashMap<String,String >> parseResult(JSONObject object){
        //init json array
        JSONArray jsonArray = null;
        //get result array
        try {
            jsonArray = object.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parseJsonArray(jsonArray);
    }
}

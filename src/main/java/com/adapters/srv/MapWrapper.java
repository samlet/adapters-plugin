package com.adapters.srv;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapWrapper {
    private static final String MODULE = MapWrapper.class.getName();
    private final Delegator delegator;

    public MapWrapper(Delegator delegator) {
        this.delegator = delegator;
    }

    public void convertGenericValue(Map<String, Object> mapValues){
        Map<String, Object> convertedMap= new HashMap<>();
        for (Map.Entry<String,Object> entry : mapValues.entrySet()){
            // check input parameter is whether a map
            if(entry.getValue() instanceof Map){
                Map<String, Object> valMap=(Map<String, Object>)entry.getValue();
                String objType=(String)valMap.getOrDefault("type", "raw");
                if(objType.equals("entity")){
                    String entName=(String)valMap.get("entityName");
                    Map<String,Object> attrs=(Map<String,Object>)valMap.get("value");
                    Debug.logInfo("entity " + entName + " with attrs " + attrs, MODULE);
                    GenericValue value=delegator.makeValue(entName, attrs);
                    convertedMap.put(entry.getKey(), value);
                }
            }else if(entry.getValue() instanceof List){
                List<?> valueList=(List<?>)entry.getValue();
                List<Object> newList=new ArrayList<>();
                for(Object item:valueList){
                    if(item instanceof Map){
                        Object val=tryExtractValue((Map<String,Object>)item);
                        newList.add(val);
                    }else{
                        newList.add(item);  // push it back
                    }
                }
                convertedMap.put(entry.getKey(), newList);
            }
        }

        mapValues.putAll(convertedMap);
    }

    private Object tryExtractValue(Map<String, Object> item) {
        if(item.containsKey("type") && item.get("type").equals("entity")) {
            String entName=(String)item.get("entityName");
            Map<String,Object> attrs=(Map<String,Object>)item.get("value");
            Debug.logInfo("entity " + entName + " with attrs " + attrs, MODULE);
            return delegator.makeValue(entName, attrs);
        }else{
            return item;
        }
    }
}


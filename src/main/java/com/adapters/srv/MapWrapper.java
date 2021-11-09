package com.adapters.srv;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntity;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.model.ModelEntity;
import org.apache.ofbiz.entity.model.ModelField;
import org.apache.ofbiz.service.DispatchContext;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class MapWrapper {
    private static final String MODULE = MapWrapper.class.getName();
    private final Delegator delegator;

    DispatchContext dctx;
    GenericValue userLogin;
    // HttpServletRequest request;

    public MapWrapper(DispatchContext dctx, GenericValue userLogin) {
        this.dctx=dctx;
        this.userLogin=userLogin;
        this.delegator = dctx.getDelegator();
    }

    @SuppressWarnings("unchecked")
    public void convertGenericValue(Map<String, Object> mapValues){
        Map<String, Object> convertedMap= new HashMap<>();
        CcObjectFactory factories=CcObjectFactory.getInstance();
        for (Map.Entry<String,Object> entry : mapValues.entrySet()){
            // check input parameter is whether a map
            if(entry.getValue() instanceof Map){
                Map<String, Object> valMap=(Map<String, Object>)entry.getValue();
                String objType=(String)valMap.getOrDefault("type", "raw");
                if(objType.equals("entity")){
                    String entName=(String)valMap.get("entityName");
                    Map<String,Object> attrs=(Map<String,Object>)valMap.get("value");
                    Debug.logInfo("entity " + entName + " with attrs " + attrs, MODULE);
                    // GenericValue value=delegator.makeValue(entName, attrs);
                    // GenericValue value=fillEntityValue(entName, attrs);
                    GenericValue value=delegator.makeValidValue(entName, attrs);
                    convertedMap.put(entry.getKey(), value);
                }else if (objType.equals("object")){
                    String fac=(String)valMap.get("factory");
                    if(factories.hasFactory(fac)) {
                        Map<String,Object> attrs=(Map<String,Object>)valMap.getOrDefault("value", new HashMap<>());
                        Object val = factories.getObject(fac, dctx, userLogin, attrs);
                        convertedMap.put(entry.getKey(), val);
                    }
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

        // overrides
        mapValues.putAll(convertedMap);
    }

    @SuppressWarnings("unchecked")
    private Object tryExtractValue(Map<String, Object> item) {
        if(item.containsKey("type") && item.get("type").equals("entity")) {
            String entName=(String)item.get("entityName");
            Map<String,Object> attrs=(Map<String,Object>)item.get("value");
            Debug.logInfo("entity " + entName + " with attrs " + attrs, MODULE);
            // return delegator.makeValidValue(entName, attrs);
            return fillEntityValue(entName, attrs);
        }else{
            return item;
        }
    }

    private GenericValue fillEntityValue(String entityName, Map<String,Object> attrs){
        GenericValue value = delegator.makeValue(entityName);

        ModelEntity modelEntity = value.getModelEntity();

        Iterator<ModelField> modelFields = modelEntity.getFieldsIterator();

        while (modelFields.hasNext()) {
            ModelField modelField = modelFields.next();
            String name = modelField.getName();
            Object val=attrs.get(name);
            if (val!=null) {
                String attr = val.toString();
                if (UtilValidate.isNotEmpty(attr)) {
                    if (GenericEntity.NULL_FIELD.toString().equals(attr)) {
                        value.set(name, null);
                    } else {
                        value.setString(name, attr);
                    }
                }
            }
        }

        return value;
    }
}


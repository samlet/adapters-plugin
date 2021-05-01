package com.adapters.srv;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityFindOptions;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;

import java.util.List;
import java.util.Map;

public class CcDataSrvs {
    private static final String MODULE = CcDataSrvs.class.getName();
    @SuppressWarnings("unchecked")
    public static Map<String, Object> storeValues(DispatchContext dctx, Map<String, ?> context) {
        List<GenericValue> vals= (List<GenericValue>)context.get("values");
        if(vals!=null) {
            try {
                // for(GenericValue val:vals) dctx.getDelegator().createOrStore(val);
                int total=dctx.getDelegator().storeAll(vals);

                Map<String, Object> response = ServiceUtil.returnSuccess();
                response.put("resp", "number of rows effected: " + total);
                return response;
            }catch (GenericEntityException e) {
                Debug.logError(e, "Entity Error:" + e.getMessage(), MODULE);
                return ServiceUtil.returnError("Entity Error:" + e.getMessage());
            }
        }else{
            return ServiceUtil.returnError("Input entity values is empty");
        }
    }

    public static Map<String, Object> storeValue(DispatchContext dctx, Map<String, ?> context) {
        GenericValue val= (GenericValue)context.get("entity");
        if(val!=null) {
            try {
                GenericValue new_value= dctx.getDelegator().createOrStore(val);

                Map<String, Object> response = ServiceUtil.returnSuccess();
                response.put("new_value", new_value);
                return response;
            }catch (GenericEntityException e) {
                Debug.logError(e, "Entity Error:" + e.getMessage(), MODULE);
                return ServiceUtil.returnError("Entity Error:" + e.getMessage());
            }
        }else{
            return ServiceUtil.returnError("Input entity value is null");
        }
    }

    public static Map<String, Object> find(DispatchContext dctx, Map<String, Object> context) {
        Map<String, Object> response = ServiceUtil.returnSuccess();
        Integer maxRows= (Integer) context.getOrDefault("maxRows", 100);
        String entityName=(String)context.get("entityName");
        EntityFindOptions findOptions = new EntityFindOptions();
        findOptions.setMaxRows(maxRows);
        try {
            List<GenericValue> result = dctx.getDelegator().findList(entityName, null, null,
                    null, findOptions, false);
            response.put("result", result);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Entity Error:" + e.getMessage(), MODULE);
            return ServiceUtil.returnError("Entity Error:" + e.getMessage());
        }
        return response;
    }
}

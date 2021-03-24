package com.adapters.srv;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;

import java.util.List;
import java.util.Map;

public class CcDataSrvs {
    private static final String MODULE = CcDataSrvs.class.getName();
    public static Map<String, Object> storeValues(DispatchContext dctx, Map<String, ?> context) {
        List<GenericValue> vals= (List<GenericValue>)context.get("values");
        if(vals!=null) {
            try {
                for(GenericValue val:vals) {
                    dctx.getDelegator().createOrStore(val);
                }

                Map<String, Object> response = ServiceUtil.returnSuccess();
                response.put("resp", "total store values:" + vals.size());
                return response;
            }catch (GenericEntityException e) {
                Debug.logError(e, "Entity Error:" + e.getMessage(), MODULE);
                return ServiceUtil.returnError("Entity Error:" + e.getMessage());
            }
        }else{
            return ServiceUtil.returnError("Input entity values is empty");
        }
    }
}

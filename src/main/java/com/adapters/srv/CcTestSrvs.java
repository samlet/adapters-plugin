package com.adapters.srv;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilProperties;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;

import java.util.Map;

public class CcTestSrvs {
    private static final String MODULE = CcTestSrvs.class.getName();

    /**
     * Generic Test Service
     *@param dctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map<String, Object> testService(DispatchContext dctx, Map<String, ?> context) {
        Map<String, Object> response = ServiceUtil.returnSuccess();

        if (!context.isEmpty()) {
            for (Map.Entry<String, ?> entry: context.entrySet()) {
                Object cKey = entry.getKey();
                Object value = entry.getValue();

                Debug.logInfo("---- SVC-CONTEXT: " + cKey + " => " + value, MODULE);
            }
        }
        if (!context.containsKey("message")) {
            response.put("resp", "no message found");
        } else {
            Map<String, Object> mapValues=(Map<String, Object>) context.get("message");
            Debug.logInfo("-----SERVICE TEST----- : " + mapValues, MODULE);
            response.put("resp", "service done");
        }

        Debug.logInfo("----- SVC: " + dctx.getName() + " -----", MODULE);
        return response;
    }

    public static Map<String, Object> testStoreEntity(DispatchContext dctx, Map<String, ?> context) {
        GenericValue val= (GenericValue)context.get("entity");
        if(val!=null) {
            try {
                dctx.getDelegator().createOrStore(val);

                Map<String, Object> response = ServiceUtil.returnSuccess();
                response.put("resp", "store: " + context.get("entity"));
                return response;
            }catch (GenericEntityException e) {
                Debug.logError(e, "Entity Error:" + e.getMessage(), MODULE);
                return ServiceUtil.returnError("Entity Error:" + e.getMessage());
            }
        }else{
            return ServiceUtil.returnError("Input entity value is null");
        }
    }
}



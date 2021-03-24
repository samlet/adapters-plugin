package com.adapters.srv;

import org.apache.ofbiz.base.util.Debug;
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
}





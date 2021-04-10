package com.adapters.srv;

import org.apache.ofbiz.base.util.UtilGenerics;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.security.Security;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;

import java.util.Iterator;
import java.util.Map;

public class CcCommonSrvs {
    public static Map<String, Object> hasPermission(DispatchContext dctx, Map<String, Object> context) {

        String loginId=(String)context.get("userLoginId");
        String base =(String) context.get("basePermission");
        String action=(String) context.get("action");

        String permission = base.concat(action);
        String adminPermission = base.concat("_ADMIN");

        Security security=dctx.getSecurity();
        Iterator<GenericValue> iterator = security.findUserLoginSecurityGroupByUserLoginId(loginId);

        boolean result=false;
        while (iterator.hasNext()) {
            GenericValue userLoginSecurityGroup = iterator.next();
            if (security.securityGroupPermissionExists(userLoginSecurityGroup.getString("groupId"), permission)) {
                result= true;
                break;
            }
            if (security.securityGroupPermissionExists(userLoginSecurityGroup.getString("groupId"), adminPermission)) {
                result= true;
                break;
            }
        }
        Map<String, Object> response = ServiceUtil.returnSuccess();
        response.put("result", result);
        return response;
    }
}


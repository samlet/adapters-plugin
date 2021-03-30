package com.adapters.srv;

import com.google.common.base.Preconditions;
import org.apache.ofbiz.security.SecurityUtil;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ModelService;
import org.apache.ofbiz.service.ServiceUtil;

import java.util.HashMap;
import java.util.Map;

public class SrvAdapter{
    private static SrvAdapter INSTANCE;

    public static SrvAdapter getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new SrvAdapter();
        }
        return INSTANCE;
    }

    private final Map<String, SrvIntf> wrappers=new HashMap<>();
    private SrvAdapter(){
        this.wrappers.put("hasUserLoginAdminPermission", new SrvIntf(){
            @Override
            public Map<String, Object> proc(DispatchContext dctx, Map<String, Object> context) {
                boolean resp=SecurityUtil.hasUserLoginAdminPermission(dctx.getDelegator(),
                        (String)context.get("userLoginId"));
                return boolResp(resp);
            }
        });
    }

    public static Map<String, Object> boolResp(boolean val){
        Map<String, Object> result = ServiceUtil.returnMessage(ModelService.RESPOND_SUCCESS, "ok");
        result.put("result", val);
        return result;
    }

    public static Map<String, Object> invokeAction(DispatchContext dctx, Map<String, Object> context) {
        String action=(String)context.get("action");
        Preconditions.checkNotNull(action, "Cannot get action parameter");
        SrvIntf srv=SrvAdapter.getInstance().wrappers.get(action);
        Preconditions.checkNotNull(action, "Cannot find action "+action);
        return srv.proc(dctx, context);
    }
}


package com.adapters.srv;

import com.google.common.collect.Maps;
import org.apache.ofbiz.service.ModelService;
import org.apache.ofbiz.service.ServiceUtil;

import java.util.Map;

public class InfoResponse {
    public Map<String, Object> getValues() {
        return values;
    }

    public void setValues(Map<String, Object> values) {
        this.values = values;
    }

    Map<String, Object> values= Maps.newHashMap();

    public InfoResponse() {
    }

    public static InfoResponse fail(String message){
        InfoResponse r=new InfoResponse();
        r.setValues(ServiceUtil.returnFailure(message));
        return r;
    }

    public static InfoResponse unimpl(){
        InfoResponse r=new InfoResponse();
        r.setValues(ServiceUtil.returnFailure("unimpl"));
        return r;
    }

    public static InfoResponse ok(Object data){
        Map<String, Object> result = ServiceUtil.returnMessage(ModelService.RESPOND_SUCCESS, "ok");
        result.put("result", data);
        InfoResponse r=new InfoResponse();
        r.setValues(result);
        return r;
    }
}

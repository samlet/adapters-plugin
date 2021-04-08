package com.adapters.srv;

import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ModelService;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class ProcContext {
    private final ModelService service;
    private final Map<String, Object> parameters;
    private final DispatchContext dctx;
    private final GenericValue userLogin;
    private final HttpServletRequest request;
    private Map<String, Object> responseData;

    public ProcContext(ModelService service, Map<String, Object> parameters, DispatchContext dctx,
                       GenericValue userLogin, HttpServletRequest request) {
        this.service = service;
        this.parameters = parameters;
        this.dctx = dctx;
        this.userLogin = userLogin;
        this.request = request;
    }

    public Map<String, Object> getResponseData() {
        return responseData;
    }

    public void setResponseData(Map<String, Object> responseData) {
        this.responseData = responseData;
    }

    public ModelService getService() {
        return service;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public DispatchContext getDctx() {
        return dctx;
    }

    public GenericValue getUserLogin() {
        return userLogin;
    }

    public HttpServletRequest getRequest() {
        return request;
    }
}

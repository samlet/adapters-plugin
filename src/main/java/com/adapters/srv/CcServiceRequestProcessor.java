package com.adapters.srv;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.*;
import org.apache.ofbiz.ws.rs.MethodNotAllowedException;
import org.apache.ofbiz.ws.rs.util.ErrorUtil;
import org.apache.ofbiz.ws.rs.util.RestApiUtil;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class CcServiceRequestProcessor {
    private static final String MODULE = CcServiceRequestProcessor.class.getName();
    /**
     * @param requestContext
     * @return
     * @throws GenericServiceException
     */
    @SuppressWarnings("unchecked")
    public Response process(Map<String, Object> requestContext) throws GenericServiceException {
        String serviceName = (String) requestContext.get("serviceName");
        String httpVerb = (String) requestContext.get("httpVerb");

        Map<String, Object> requestMap = (Map<String, Object>) requestContext.get("requestMap");
        Debug.logInfo("input parameters: "+requestMap.keySet(), MODULE);

        LocalDispatcher dispatcher = (LocalDispatcher) requestContext.get("dispatcher");
        MapWrapper wrapper=new MapWrapper(dispatcher.getDelegator());
        wrapper.convertGenericValue(requestMap);

        HttpServletRequest request = (HttpServletRequest) requestContext.get("request");
        GenericValue userLogin = (GenericValue) request.getAttribute("userLogin");
        DispatchContext dispatchContext = dispatcher.getDispatchContext();
        ModelService service = null;
        try {
            service = dispatchContext.getModelService(serviceName);
        } catch (GenericServiceException gse) {
            throw new NotFoundException(gse.getMessage());
        }
        if (UtilValidate.isNotEmpty(service.getAction()) && !service.getAction().equalsIgnoreCase(httpVerb)) {
            throw new MethodNotAllowedException("HTTP " + httpVerb + " is not allowed on this service.");
        }
        Map<String, Object> serviceContext = dispatchContext.makeValidContext(serviceName,
                ModelService.IN_PARAM, requestMap);
        serviceContext.put("userLogin", userLogin);
        Map<String, Object> result = dispatcher.runSync(serviceName, serviceContext);
        Map<String, Object> responseData = new LinkedHashMap<>();
        if (ServiceUtil.isSuccess(result)) {
            Set<String> outParams = service.getOutParamNames();
            for (String outParamName : outParams) {
                ModelParam outParam = service.getParam(outParamName);
                if (!outParam.isInternal()) {
                    Object value = result.get(outParamName);
                    if (UtilValidate.isNotEmpty(value)) {
                        responseData.put(outParamName, value);
                    }
                }
            }
            return RestApiUtil.success((String) result.get(ModelService.SUCCESS_MESSAGE), responseData);
        } else {
            return ErrorUtil.buildErrorFromServiceResult(serviceName, result, request.getLocale());
        }
    }
}
package com.adapters.srv;

import com.bluecc.triggers.ServiceTrigger;
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
import java.util.Locale;
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
        HttpServletRequest request = (HttpServletRequest) requestContext.get("request");
        GenericValue userLogin = (GenericValue) request.getAttribute("userLogin");
        DispatchContext dispatchContext = dispatcher.getDispatchContext();

        MapWrapper wrapper=new MapWrapper(dispatchContext, request, userLogin);
        wrapper.convertGenericValue(requestMap);

        return runService(serviceName, httpVerb, requestMap, dispatcher,
                request, userLogin, dispatchContext);
    }

    private Response runService(String serviceName, String httpVerb, Map<String, Object> requestMap,
                                LocalDispatcher dispatcher, HttpServletRequest request,
                                GenericValue userLogin, DispatchContext dispatchContext) throws GenericServiceException {
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

        ProcContext triggerCtx=new ProcContext(service, serviceContext,
                dispatchContext, userLogin, request);
        ServiceTrigger.getInstance().fire(triggerCtx);

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

            triggerCtx.setResponseData(responseData);
            ServiceTrigger.getInstance().succ(triggerCtx);

            return RestApiUtil.success((String) result.get(ModelService.SUCCESS_MESSAGE), responseData);
        } else {
            Locale locale= Locale.getDefault();
            if(request!=null){
                locale=request.getLocale();
            }
            return ErrorUtil.buildErrorFromServiceResult(serviceName, result, locale);
        }
    }
}

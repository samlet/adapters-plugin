package com.adapters.srv;

import com.bluecc.triggers.ServiceTrigger;
import com.google.common.base.Preconditions;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.DelegatorFactory;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.security.Security;
import org.apache.ofbiz.security.SecurityConfigurationException;
import org.apache.ofbiz.security.SecurityFactory;
import org.apache.ofbiz.service.*;
import org.apache.ofbiz.ws.rs.MethodNotAllowedException;
import org.apache.ofbiz.ws.rs.util.ErrorUtil;
import org.apache.ofbiz.ws.rs.util.RestApiUtil;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class Requester {
    private static final String MODULE = Requester.class.getName();
    LocalDispatcher dispatcher;
    public Security getSecurity() throws SecurityConfigurationException {
        Security security = SecurityFactory.getInstance(getDelegator());
        return security;
    }

    public Delegator getDelegator(){
        Delegator delegator = DelegatorFactory.getDelegator("default");
        return delegator;
    }

    public LocalDispatcher getDispatcher() {
        if(dispatcher==null){
            Delegator delegator = getDelegator();
            this.dispatcher = ServiceContainer.getLocalDispatcher(
                    delegator.getDelegatorName(),
                    delegator);
        }
        return dispatcher;
    }

    public Map<String, Object> responseAsJson(String serviceName, Map<String, Object> serviceInParams, GenericValue userLogin) throws GenericServiceException {
        Preconditions.checkNotNull(serviceInParams, "input parameters is null");
        return process(UtilMisc.toMap("serviceName", serviceName,
                // "httpVerb", HttpMethod.POST,
                "requestMap", serviceInParams,
                "dispatcher", getDispatcher(),
                "userLogin", userLogin));
        // https://stackoverflow.com/questions/25196427/how-to-retrieve-json-response-from-a-javax-ws-rs-core-response-response
        // String responseAsString = response.readEntity(String.class);
        // return responseAsString;
    }


    @SuppressWarnings("unchecked")
    public Map<String, Object> process(Map<String, Object> requestContext) throws GenericServiceException {
        String serviceName = (String) requestContext.get("serviceName");
        // String httpVerb = (String) requestContext.get("httpVerb");

        Map<String, Object> requestMap = (Map<String, Object>) requestContext.get("requestMap");
        Debug.logInfo("input parameters: " + requestMap.keySet(), MODULE);

        LocalDispatcher dispatcher = (LocalDispatcher) requestContext.get("dispatcher");
        GenericValue userLogin =  (GenericValue) requestContext.get("userLogin");
        Preconditions.checkNotNull(userLogin, "Not assign a userLogin");

        DispatchContext dispatchContext = dispatcher.getDispatchContext();
        MapWrapper wrapper = new MapWrapper(dispatchContext, userLogin);
        wrapper.convertGenericValue(requestMap);

        return runService(serviceName, requestMap, dispatcher,
                userLogin, dispatchContext);
    }

    private Map<String, Object> runService(String serviceName, Map<String, Object> requestMap,
                                LocalDispatcher dispatcher,
                                GenericValue userLogin, DispatchContext dispatchContext) throws GenericServiceException {
        ModelService service = null;
        try {
            service = dispatchContext.getModelService(serviceName);
        } catch (GenericServiceException gse) {
            throw new NotFoundException(gse.getMessage());
        }
        // if (UtilValidate.isNotEmpty(service.getAction()) && !service.getAction().equalsIgnoreCase(httpVerb)) {
        //     throw new MethodNotAllowedException("HTTP " + httpVerb + " is not allowed on this service.");
        // }
        Map<String, Object> serviceContext = dispatchContext.makeValidContext(serviceName,
                ModelService.IN_PARAM, requestMap);

        // ProcContext triggerCtx = new ProcContext(service, serviceContext,
        //         dispatchContext, userLogin);
        // ServiceTrigger.getInstance().fire(triggerCtx);

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

            // triggerCtx.setResponseData(responseData);
            // ServiceTrigger.getInstance().succ(triggerCtx);

            // return RestApiUtil.success((String) result.get(ModelService.SUCCESS_MESSAGE), responseData);
            return responseData;
        } else {
            // Locale locale = Locale.getDefault();
            // return ErrorUtil.buildErrorFromServiceResult(serviceName, result, locale);
            return result;
        }
    }
}

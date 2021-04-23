package com.adapters.srv;

import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.DelegatorFactory;
import org.apache.ofbiz.security.Security;
import org.apache.ofbiz.security.SecurityConfigurationException;
import org.apache.ofbiz.security.SecurityFactory;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceContainer;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.Response;
import java.util.Map;

public class Requester {
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

    public String responseAsJson(String serviceName, Map<String, Object> serviceInParams) throws GenericServiceException {
        CcServiceRequestProcessor processor = new CcServiceRequestProcessor();
        Response response=processor.process(UtilMisc.toMap("serviceName", serviceName,
                "httpVerb", HttpMethod.POST, "requestMap",
                serviceInParams, "dispatcher", getDispatcher(), "request", null));
        // https://stackoverflow.com/questions/25196427/how-to-retrieve-json-response-from-a-javax-ws-rs-core-response-response
        String responseAsString = response.readEntity(String.class);
        return responseAsString;
    }
}

package com.adapters.srv;

import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.service.DispatchContext;

import java.util.Map;

public interface SrvIntf {
    Map<String, Object> proc(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException;
}

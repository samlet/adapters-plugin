package com.adapters.srv;

import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface ObjectFactoryIntf<T> {
    T create(DispatchContext dctx, HttpServletRequest request, GenericValue userLogin, Map<String, ?> context);
}


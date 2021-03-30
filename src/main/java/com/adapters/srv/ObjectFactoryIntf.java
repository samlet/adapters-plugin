package com.adapters.srv;

import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;

import java.util.Map;

public interface ObjectFactoryIntf<T> {
    T create(DispatchContext dctx, GenericValue userLogin, Map<String, ?> context);
}


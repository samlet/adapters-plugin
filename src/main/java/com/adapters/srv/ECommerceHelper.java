package com.adapters.srv;

import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.LocalDispatcher;

public class ECommerceHelper {
    LocalDispatcher dispatcher;

    public LocalDispatcher getDispatcher() {
        return dispatcher;
    }

    public Delegator getDelegator() {
        return delegator;
    }

    Delegator delegator;
    public ECommerceHelper(LocalDispatcher dispatcher, Delegator delegator){
        this.dispatcher=dispatcher;
        this.delegator=delegator;
    }

    protected EntityQuery from(String entityName) {
        return EntityQuery.use(getDelegator()).from(entityName);
    }

    public GenericValue getLatestSalesOrder() throws GenericEntityException {
        GenericValue orderHeader = from("OrderHeader").where("orderTypeId", "SALES_ORDER")
                .orderBy("-entryDate").queryFirst();
        return orderHeader;
    }
}

package com.adapters.srv;

import com.google.common.base.Preconditions;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.shipment.packing.PackingSession;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class CcObjectFactory {
    private static CcObjectFactory INSTANCE;

    public static CcObjectFactory getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new CcObjectFactory();
        }
        return INSTANCE;
    }

    private final HashMap<String, ObjectFactoryIntf<Object>> factories=new HashMap<>();
    private CcObjectFactory() {
        this.factories.put("PackingSession", new ObjectFactoryIntf<Object>() {
            @Override
            public Object create(DispatchContext dctx,
                                 GenericValue userLogin, Map<String, ?> attrs) {
                // String facilityId, String binId, String orderId, String shipGrp
                return new PackingSession(dctx.getDispatcher(), userLogin,
                        (String)attrs.get("facilityId"),
                        (String)attrs.get("binId"),
                        (String)attrs.get("orderId"),
                        (String)attrs.get("shipGrp")
                        );
            }
        });
    }

    public Object getObject(String factoryName, DispatchContext dctx,
                            GenericValue userLogin, Map<String, ?> attrs){
        ObjectFactoryIntf<Object> factory=this.factories.get(factoryName);
        Preconditions.checkNotNull(factory, "No such factory "+factoryName);
        return factory.create(dctx, userLogin, attrs);
    }

    public boolean hasFactory(String name){
        return this.factories.containsKey(name);
    }
}


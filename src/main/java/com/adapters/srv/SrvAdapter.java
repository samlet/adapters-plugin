package com.adapters.srv;

import com.adapters.objects.EntityMeta;
import com.adapters.objects.FieldMeta;
import com.google.common.base.Preconditions;
import org.apache.commons.compress.utils.Lists;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.model.ModelEntity;
import org.apache.ofbiz.entity.model.ModelField;
import org.apache.ofbiz.security.SecurityUtil;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ModelService;
import org.apache.ofbiz.service.ServiceUtil;

import java.util.*;
import java.util.stream.Collectors;

public class SrvAdapter{
    private static SrvAdapter INSTANCE;

    public static SrvAdapter getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new SrvAdapter();
        }
        return INSTANCE;
    }

    private final Map<String, SrvIntf> wrappers=new HashMap<>();
    private SrvAdapter(){
        this.wrappers.put("hasUserLoginAdminPermission", new SrvIntf(){
            @Override
            public Map<String, Object> proc(DispatchContext dctx, Map<String, Object> context) {
                boolean resp=SecurityUtil.hasUserLoginAdminPermission(dctx.getDelegator(),
                        (String)context.get("userLoginId"));
                return boolResp(resp);
            }
        });
        this.wrappers.put("hasUserLoginMorePermissionThan", new SrvIntf(){
            @Override
            public Map<String, Object> proc(DispatchContext dctx, Map<String, Object> context) {
                List<String> vals=SecurityUtil.hasUserLoginMorePermissionThan(dctx.getDelegator(),
                        (String)context.get("userLoginId"),
                        (String)context.get("toUserLoginId")
                        );
                return stringListResp(vals);
            }
        });
        this.wrappers.put("allServiceNames", new SrvIntf(){
            @Override
            public Map<String, Object> proc(DispatchContext dctx, Map<String, Object> context) {
                Collection<String> vals=dctx.getAllServiceNames();
                String filter=(String)context.get("filter");
                if(filter!=null){
                    vals=vals.stream().filter(x->x.contains(filter)).collect(Collectors.toList());
                }
                return stringListResp(vals);
            }
        });
        this.wrappers.put("entities", new SrvIntf(){
            @Override
            public Map<String, Object> proc(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException {
                Collection<String> vals=dctx.getDelegator().getModelReader().getEntityNames();
                String filter=(String)context.get("filter");
                if(filter!=null){
                    vals=vals.stream().filter(x->x.contains(filter)).collect(Collectors.toList());
                }
                List<EntityMeta> ents= Lists.newArrayList();
                for(String ent:vals){
                    ModelEntity e=dctx.getDelegator().getModelEntity(ent);
                    EntityMeta meta=new EntityMeta();
                    meta.setName(e.getEntityName());
                    meta.setDescription(e.getDescription());
                    meta.setPackageName(e.getPackageName());

                    for (Iterator<ModelField> it = e.getFieldsIterator(); it.hasNext(); ) {
                        ModelField fld = it.next();
                        FieldMeta fldMeta=new FieldMeta();
                        fldMeta.setName(fld.getName());
                        fldMeta.setType(fld.getType());
                        fldMeta.setNotNull(fld.getIsNotNull());
                        fldMeta.setPk(fld.getIsPk());
                        meta.getFields().add(fldMeta);
                    }

                    ents.add(meta);
                }
                return objectsResp(ents);
            }
        });
    }

    public void addSrv(String name, SrvIntf srvIntf){
        this.wrappers.put(name, srvIntf);
    }

    public static Map<String, Object> boolResp(boolean val){
        Map<String, Object> result = ServiceUtil.returnMessage(ModelService.RESPOND_SUCCESS, "ok");
        result.put("result", val);
        return result;
    }

    public static Map<String, Object> stringListResp(Collection<String> vals){
        Map<String, Object> result = ServiceUtil.returnMessage(ModelService.RESPOND_SUCCESS, "ok");
        result.put("result", vals);
        return result;
    }

    public static Map<String, Object> objectsResp(List<?> vals){
        Map<String, Object> result = ServiceUtil.returnMessage(ModelService.RESPOND_SUCCESS, "ok");
        result.put("result", vals);
        return result;
    }

    public static Map<String, Object> invokeAction(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException {
        String action=(String)context.get("action");
        Preconditions.checkNotNull(action, "Cannot get action parameter");
        SrvIntf srv=SrvAdapter.getInstance().wrappers.get(action);
        Preconditions.checkNotNull(action, "Cannot find action "+action);
        return srv.proc(dctx, context);
    }
}


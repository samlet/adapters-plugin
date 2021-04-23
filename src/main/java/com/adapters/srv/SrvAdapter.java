package com.adapters.srv;

import com.adapters.objects.EntityMeta;
import com.adapters.objects.FieldMeta;
import com.adapters.objects.ServiceMeta;
import com.google.common.base.Preconditions;
import org.apache.commons.compress.utils.Lists;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.model.ModelEntity;
import org.apache.ofbiz.entity.model.ModelField;
import org.apache.ofbiz.security.SecurityUtil;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.ModelService;
import org.apache.ofbiz.service.ServiceUtil;
import org.jetbrains.annotations.NotNull;

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
                    EntityMeta meta = getEntityMeta(e);

                    ents.add(meta);
                }
                return objectsResp(ents);
            }
        });
    }

    @NotNull
    public EntityMeta getEntityMeta(ModelEntity e) {
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

        e.getRelations().stream().forEach(r -> {
            EntityMeta.RelationMeta rel=new EntityMeta.RelationMeta();
            rel.setName(r.getCombinedName());
            rel.setRelEntityName(r.getRelEntityName());
            rel.setType(r.getType());
            meta.getRelations().add(rel);
        });

        return meta;
    }

    public ServiceMeta getServiceMeta(ModelService srv){
        ServiceMeta meta=new ServiceMeta();
        meta.setName(srv.getName());
        meta.setDescription(srv.getDescription());
        meta.setAction(srv.getAction());

        srv.getContextParamList().stream().forEach(p -> {
            ServiceMeta.ParameterMeta para=new ServiceMeta.ParameterMeta();
            para.setName(p.getName());
            para.setRepr(p.getShortDisplayDescription());
            para.setFormLabel(p.getFormLabel());
            para.setType(p.getType());
            para.setMode(p.getMode());
            meta.getParameters().add(para);
        });
        return meta;
    }

    public void addSrv(String name, SrvIntf srvIntf){
        this.wrappers.put(name, srvIntf);
    }

    public static Map<String, Object> boolResp(boolean val){
        Map<String, Object> result = ServiceUtil.returnMessage(ModelService.RESPOND_SUCCESS, "ok");
        result.put("result", val);
        return result;
    }

    public static Map<String, Object> response(String key, Object data){
        Map<String, Object> result = ServiceUtil.returnMessage(ModelService.RESPOND_SUCCESS, "ok");
        result.put(key, data);
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

    public static Map<String, Object> getEntityMeta(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException {
        String entityName=(String)context.get("entityName");
        ModelEntity ent=dctx.getDelegator().getModelEntity(entityName);
        EntityMeta meta= getInstance().getEntityMeta(ent);
        return response("entity", meta);
    }

    public static Map<String, Object> getServiceMeta(DispatchContext dctx, Map<String, Object> context) throws GenericServiceException {
        String serviceName=(String)context.get("serviceName");
        ModelService srv=dctx.getModelService(serviceName);
        ServiceMeta meta= getInstance().getServiceMeta(srv);
        return response("service", meta);
    }
}


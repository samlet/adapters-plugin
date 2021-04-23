package com.adapters.objects;

import java.util.ArrayList;
import java.util.List;

public class EntityMeta {
    public static class RelationMeta{
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        private String name;

        public String getRelEntityName() {
            return relEntityName;
        }

        public void setRelEntityName(String relEntityName) {
            this.relEntityName = relEntityName;
        }

        private String relEntityName;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        /** the type: either "one" or "many" or "one-nofk" */
        private String type;
    }
    private String name;
    private String description;
    private String packageName;
    private List<RelationMeta> relations=new ArrayList<>();

    public List<RelationMeta> getRelations() {
        return relations;
    }

    public void setRelations(List<RelationMeta> relations) {
        this.relations = relations;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    private List<FieldMeta> fields= new ArrayList<>();

    public List<FieldMeta> getFields() {
        return fields;
    }

    public void setFields(List<FieldMeta> fields) {
        this.fields = fields;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

package com.adapters.objects;

import java.util.ArrayList;
import java.util.List;

public class ServiceMeta {
    List<ParameterMeta> parameters=new ArrayList<>();
    private String name;
    private String description;
    private String action;
    private String invoke;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getInvoke() {
        return invoke;
    }

    public void setInvoke(String invoke) {
        this.invoke = invoke;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ParameterMeta> getParameters() {
        return parameters;
    }

    public void setParameters(List<ParameterMeta> parameters) {
        this.parameters = parameters;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static class ParameterMeta{
        private String name;
        private String type;

        public String getRepr() {
            return repr;
        }

        public void setRepr(String repr) {
            this.repr = repr;
        }

        private String repr;
        private String mode;
        private String formLabel;


        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public String getFormLabel() {
            return formLabel;
        }

        public void setFormLabel(String formLabel) {
            this.formLabel = formLabel;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}

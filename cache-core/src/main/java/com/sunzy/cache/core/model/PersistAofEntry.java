package com.sunzy.cache.core.model;

import java.util.Arrays;

public class PersistAofEntry {

    /**
     * 参数列表
     */
    private Object[] params;

    /**
     * 方法名
     */
    private String methodName;

    public PersistAofEntry() {
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public static PersistAofEntry newInstance() {
        return new PersistAofEntry();
    }

    @Override
    public String toString() {
        return "PersistAofEntry{" +
                "params=" + Arrays.toString(params) +
                ", methodName='" + methodName + '\'' +
                '}';
    }
}

package com.example.login1.domain;

public class Result {
    boolean result;
    Object user;


    public Result() {
    }

    public Result(boolean result, Object user) {
        this.result = result;
        this.user = user;
    }

    /**
     * 获取
     *
     * @return result
     */
    public boolean isResult() {
        return result;
    }

    /**
     * 设置
     *
     * @param result
     */
    public void setResult(boolean result) {
        this.result = result;
    }

    /**
     * 获取
     *
     * @return user
     */
    public Object getUser() {
        return user;
    }

    /**
     * 设置
     *
     * @param user
     */
    public void setUser(Object user) {
        this.user = user;
    }

    public String toString() {
        return "Result{result = " + result + ", user = " + user + "}";
    }
}

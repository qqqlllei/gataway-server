package com.gataway.server.exception;

/**
 * Created by 李雷 on 2018/7/13.
 */
public class BaseException extends RuntimeException {
    protected int code;

    private static final long serialVersionUID = 3160241586346324994L;

    public BaseException() {
    }


    public BaseException(Throwable cause) {
        super(cause);
    }

    public BaseException(String message) {
        super(message);
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BaseException(int code, String msgFormat, Object... args) {
        super(String.format(msgFormat, args));
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}

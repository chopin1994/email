package com.nexus.website.impl.exceptions;

import lombok.Data;

/**
 * @Description: 自定义异常类
 */
@Data
public class NexusException extends RuntimeException {
    private String defineCode;
    private String defineMsg;

    public NexusException(String  defineCode, String defineMsg){
        super();
        this.defineCode=defineCode;
        this.defineMsg=defineMsg;
    }

    public NexusException(String message) {
        super(message);
    }

    public NexusException(Throwable e) {
        super(e);
    }

    public NexusException(String message, Throwable cause) {
        super(message, cause);
    }
}

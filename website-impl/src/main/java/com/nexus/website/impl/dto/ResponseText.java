package com.nexus.website.impl.dto;

import java.io.Serializable;
import java.util.Arrays;

public class ResponseText implements Serializable{
    private static final long serialVersionUID = 1L;
    public static final String STATUS_TRUE = "true";
    public static final String STATUS_FALSE = "false";
    private String status;
    private ErrorText error;
    private Object[] content;
    
    public ResponseText() {
        ErrorText error = new ErrorText();
        this.setError(error);
    }
    
    public static class ErrorText implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private String msg;
        private String errorCode;
        
        public String getMsg() {
            return msg;
        }
        public void setMsg(String msg) {
            this.msg = msg;
        }
        public String getErrorCode() {
            return errorCode;
        }
        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }
        
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("ErrorText [msg=");
            builder.append(msg);
            builder.append(", errorCode=");
            builder.append(errorCode);
            builder.append("]");
            return builder.toString();
        }       
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ErrorText getError() {
        return error;
    }

    public void setError(ErrorText error) {
        this.error = error;
    }

    public Object[] getContent() {
        return content;
    }

    public void setContent(Object[] content) {
        this.content = content;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ResponseText [status=");
        builder.append(status);
        builder.append(", error=");
        builder.append(error);
        builder.append(", content=");
        builder.append(Arrays.toString(content));
        builder.append("]");
        return builder.toString();
    }   
    
}
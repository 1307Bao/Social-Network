package com.example.Social_Network.Exception;

public class AppRuntimeException extends Exception{

    public AppRuntimeException(ErrorCode errorCode){
        this.errorCode = errorCode;
    }

    private ErrorCode errorCode;

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}

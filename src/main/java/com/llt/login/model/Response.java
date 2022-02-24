package com.llt.login.model;

import lombok.Data;

@Data
public class Response {
    private String status;
    private String message;
    private Object data;
}

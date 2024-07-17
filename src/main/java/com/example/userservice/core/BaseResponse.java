package com.example.userservice.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class BaseResponse<T> implements Serializable {
    private int status;
    private String message;
    private Object data;

    public BaseResponse(String rp) {
        this.status = 200;
        this.message = "success";
        this.data = rp;
    }

    public BaseResponse() {
        this.status = 200;
        this.message = "success";
        this.data = "Thành công";
    }

    public BaseResponse(T data) {
        this.status = 200;
        this.message = "success";
        this.data = data;
    }

    public BaseResponse(List<T> data) {
        this.status = 200;
        this.message = "success";
        this.data = data;
    }
}

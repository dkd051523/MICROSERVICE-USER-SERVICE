package com.example.userservice.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 *  @author diemdz
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostRegistryRequestBody {
    private String userName;
}

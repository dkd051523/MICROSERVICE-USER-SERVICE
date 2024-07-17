package com.example.userservice.model.response;
/*
 *  @author diemdz
 */

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PostLoginResponseBody {
    private String userName;
    private String token;
    private String refreshToken;
}

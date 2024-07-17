package com.example.userservice.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 *  @author diemdz
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PostUserListResponseBody {
    private String userName;
}

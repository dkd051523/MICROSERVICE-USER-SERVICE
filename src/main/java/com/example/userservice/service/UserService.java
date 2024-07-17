package com.example.userservice.service;
/*
 *  @author diemdz
 */


import com.example.userservice.model.request.PostUserListRequestBody;
import com.example.userservice.model.response.PostUserListResponseBody;

import java.util.List;

public interface UserService {

    List<PostUserListResponseBody> postUserList(PostUserListRequestBody responseBody);
}

package com.example.userservice.service;
/*
 *  @author diemdz
 */


import com.example.userservice.model.request.PostUserListRequestBody;
import com.example.userservice.model.response.PostUserListResponseBody;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface UserService {

    List<PostUserListResponseBody> postUserList(PostUserListRequestBody responseBody);
    boolean insertMultiThreadProducts(int records, int threadCount);

    boolean insertBatchProducts();

    ResponseEntity<byte[]> export() throws IOException;
    ResponseEntity<byte[]> exportMultiThread(int batchSize,int threadCount) throws IOException, InterruptedException, ExecutionException;
}

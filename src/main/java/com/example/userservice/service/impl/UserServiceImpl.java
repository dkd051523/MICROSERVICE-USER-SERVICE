package com.example.userservice.service.impl;
/*
 *  @author diemdz
 */


import com.example.userservice.entity.UserEntity;
import com.example.userservice.model.request.PostUserListRequestBody;
import com.example.userservice.model.response.PostUserListResponseBody;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.service.UserService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
@Transactional(rollbackFor = Throwable.class)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    @Override
    public List<PostUserListResponseBody> postUserList(PostUserListRequestBody requestBody) {
        Specification<UserEntity> employeeInfoEntitySpec = getUser(requestBody);
        List<UserEntity> userEntityList = userRepository.findAll(employeeInfoEntitySpec);
        return userEntityList.stream().map((e)->
              modelMapper.map(e, PostUserListResponseBody.class)
        ).toList();
    }
    private Specification<UserEntity> getUser(PostUserListRequestBody requestBody) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.isNotEmpty(requestBody.getUserName())) {
                predicates.add(builder.like(root.get("userName"), "%" + requestBody.getUserName() + "%"));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }


}

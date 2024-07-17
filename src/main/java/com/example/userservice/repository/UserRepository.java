package com.example.userservice.repository;
/*
 *  @author diemdz
 */


import com.example.userservice.entity.UserEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,String> {
    List<UserEntity> findAll(Specification<UserEntity> specification);
}

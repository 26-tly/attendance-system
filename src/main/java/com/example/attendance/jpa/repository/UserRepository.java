package com.example.attendance.jpa.repository;
import com.example.attendance.jpa.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    //根据用户名查询用户
    User findByUsername(String username);
    //查询所有教师用户
    List<User> findByUserRole(String userRole);

    Optional<User> findByUserId(Integer userId);

    void deleteByUserId(Integer userId);


    boolean existsByUsername(String username);

}

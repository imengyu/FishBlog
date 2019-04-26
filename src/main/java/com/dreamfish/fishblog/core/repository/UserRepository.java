package com.dreamfish.fishblog.core.repository;

import com.dreamfish.fishblog.core.entity.UserExtened;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserExtened, Integer> {

    /**
     * 根据用户名检查用户是否存在
     * @param name 用户名
     * @return 是否存在
     */
    boolean existsByName(String name);

    /**
     * 根据邮箱检查用户是否存在
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmail(String email);
}


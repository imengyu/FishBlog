package com.dreamfish.fishblog.core.repository;

import com.dreamfish.fishblog.core.entity.UserExtened;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    /**
     * 通过最低用户等级查询用户分页
     * @param requireLevel 最低用户等级（会查询等级高的用户）
     * @param pageable 分页
     * @return 用户分页
     */
    Page<UserExtened> findByLevelLessThanEqual(Integer requireLevel, Pageable pageable);
}


package com.dreamfish.fishblog.core.repository;

import com.dreamfish.fishblog.core.entity.PostClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PostClassRepository extends JpaRepository<PostClass, Integer> {

    /**
     * 删除在 ID 集内的分类
     * @param ids ID 集
     * @return 成功数
     */
    @Transactional
    Integer deleteByIdIn(List<Integer> ids);
}

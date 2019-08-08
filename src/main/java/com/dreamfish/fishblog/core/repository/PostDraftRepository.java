package com.dreamfish.fishblog.core.repository;

import com.dreamfish.fishblog.core.entity.PostDraft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PostDraftRepository extends JpaRepository<PostDraft, Integer> {


    /**
     * 根据草稿所属文章ID查询
     * @param belongPost 草稿所属文章ID
     * @return
     */
    PostDraft findByBelongPost(Integer belongPost);

    /**
     * 删除指定文章ID的草稿
     * @param belongPost 文章ID
     */
    @Transactional
    void deleteByBelongPost(Integer belongPost);
}

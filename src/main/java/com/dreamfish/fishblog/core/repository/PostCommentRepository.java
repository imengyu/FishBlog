package com.dreamfish.fishblog.core.repository;

import com.dreamfish.fishblog.core.entity.PostComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Integer> {

    /**
     * 根据 文章ID 查找评论并
     * @param postId
     * @return
     */
    List<PostComment> findByPostId(Integer postId);

    /**
     * 根据 文章ID 查找评论并按时间倒序排序
     * @param postId
     * @return
     */
    List<PostComment> findByPostId(Integer postId, Sort sort);
    /**
     * 根据 文章ID 查找评论并分页按时间倒序排序
     * @param postId
     * @return
     */
    Page<PostComment> findByPostId(Integer postId, Pageable pageable);


    /**
     * 删除评论id在 目标id中 的评论
     * @param ids 要删除的评论id
     * @return
     */
    @Transactional
    Integer deleteByPostIdAndIdIn(Integer postId, List<Integer> ids);
}

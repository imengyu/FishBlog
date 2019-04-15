package com.dreamfish.fishblog.core.repository;

import com.dreamfish.fishblog.core.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    /**
     * 根据状态查询
     * @param status 状态 id Post.POST_STATUS_*
     * @return
     */
    List<Post> findByStatus(Integer status);

    /**
     * 根据状态查询并排序
     * @param status 状态 id Post.POST_STATUS_*
     * @param sort 排序
     * @return
     */
    List<Post> findByStatus(Integer status, Sort sort);
    /**
     * 根据状态查询并分页
     * @param status 状态 id Post.POST_STATUS_*
     * @param pageable 分页
     * @return
     */
    Page<Post> findByStatus(Integer status, Pageable pageable);


    /**
     * 根据文章url名称查找文章
     * @param urlName 文章 url 名称
     * @return
     */
    Post findByUrlName(String urlName);


}

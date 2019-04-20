package com.dreamfish.fishblog.core.repository;

import com.dreamfish.fishblog.core.entity.PostSimple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PostSimpleRepository extends JpaRepository<PostSimple, Integer> {

    /**
     * 根据状态查询
     * @param status 状态 id Post.POST_STATUS_*
     * @return
     */
    List<PostSimple> findByStatusAndShowInList(Integer status, Boolean showInList);

    /**
     * 根据状态查询并排序
     * @param status 状态 id Post.POST_STATUS_*
     * @param sort 排序
     * @return
     */
    List<PostSimple> findByStatusAndShowInList(Integer status, Boolean showInList, Sort sort);
    /**
     * 根据状态查询并分页
     * @param status 状态 id Post.POST_STATUS_*
     * @param pageable 分页
     * @return
     */
    Page<PostSimple> findByStatusAndShowInList(Integer status, Boolean showInList, Pageable pageable);
    /**
     * 根据状态查询指定标签的文章并分页
     * @param status 状态 id Post.POST_STATUS_*
     * @param tag 指定的标签ID
     * @param pageable 分页
     * @return
     */
    Page<PostSimple> findByStatusAndShowInListAndTagsLike(Integer status, Boolean showInList, String tag, Pageable pageable);
    Page<PostSimple> findByStatusAndShowInListAndPostDateLike(Integer status, Boolean showInList, String postDate, Pageable pageable);
    Page<PostSimple> findByStatusAndShowInListAndPostClassLike(Integer status, Boolean showInList, String postClass, Pageable pageable);

    Page<PostSimple> findByStatusAndAuthorId(Integer status, Integer authorId, Pageable pageable);
    Page<PostSimple> findByStatusAndAuthorIdAndTagsLike(Integer status, Integer authorId, String tag, Pageable pageable);
    Page<PostSimple> findByStatusAndAuthorIdAndPostDateLike(Integer status, Integer authorId, String postDate, Pageable pageable);
    Page<PostSimple> findByStatusAndAuthorIdAndPostClassLike(Integer status, Integer authorId, String postClass, Pageable pageable);

    /**
     * 查询指定标签的文章并分页
     * @param tag 指定的标签ID
     * @param pageable 分页
     * @return 返回目标数据
     */
    Page<PostSimple> findByTagsLikeAndShowInList(String tag, Boolean showInList, Pageable pageable);
    Page<PostSimple> findByPostDateLikeAndShowInList(String postDate, Boolean showInList, Pageable pageable);
    Page<PostSimple> findByPostClassLikeAndShowInList(String postClass, Boolean showInList, Pageable pageable);

    Page<PostSimple> findByAuthorId(Integer authorId, Pageable pageable);
    Page<PostSimple> findByAuthorIdAndTagsLike(Integer authorId, String tag, Pageable pageable);
    Page<PostSimple> findByAuthorIdAndPostDateLike(Integer authorId, String postDate, Pageable pageable);
    Page<PostSimple> findByAuthorIdAndPostClassLike(Integer authorId, String postClass, Pageable pageable);

    /**
     * 删除文章id在 目标id中 的文章
     * @param ids 要删除的文章id
     * @return
     */
    @Transactional
    Integer deleteByIdIn(List<Integer> ids);

    /**
     * 根据文章url名称查找文章
     * @param urlName 文章 url 名称
     * @return
     */
    PostSimple findByUrlName(String urlName);
}

package com.dreamfish.fishblog.core.repository;

import com.dreamfish.fishblog.core.entity.PostMedia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PostMediaRepository extends JpaRepository<PostMedia, Integer> {

    /**
     * 检查是否存在 指定文章 ID 和 HASH 的资源
     * @param postId 文章 ID
     * @param hash 资源 HASH
     * @return 返回是否存在
     */
    boolean existsByPostIdAndHash(Integer postId, String hash);

    /**
     * 统计当前表指定hash记录条数
     * @param hash 指定hash
     * @return 返回记录条数
     */
    Integer countByHash(String hash);

    /**
     * 通过指定文章 ID 和 HASH 删除资源
     * @param postId 文章 ID
     * @param hash HASH
     */
    void deleteByPostIdAndHash(Integer postId, String hash);

    /**
     * 查找指定文章的资源
     * @param postId 文章 ID
     * @param pageable 分页
     * @return 返回分页数据
     */
    Page<PostMedia> findByPostId(Integer postId, Pageable pageable);
}

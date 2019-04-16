package com.dreamfish.fishblog.core.repository;

import com.dreamfish.fishblog.core.entity.PostMedia;
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
}

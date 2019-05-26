package com.dreamfish.fishblog.core.repository;

import com.dreamfish.fishblog.core.entity.MessageItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<MessageItem, Integer> {


    Page<MessageItem> findAllByUserIdOrderByDateDesc(Integer userId, Pageable pageable);
    Page<MessageItem> findAllByOrderByDateDesc(Pageable pageable);



    @Transactional
    void deleteByIdIn(List<Integer> ids);
    @Transactional
    void deleteByUserId(Integer userId);
}

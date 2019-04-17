package com.dreamfish.fishblog.core.repository;

import com.dreamfish.fishblog.core.entity.StatPage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatDayLogRepository extends JpaRepository<StatPage, Integer> {


    Page<StatPage> findAllByOrderByDateDesc(Pageable var1);
}

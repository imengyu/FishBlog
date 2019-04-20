package com.dreamfish.fishblog.core.repository;

import com.dreamfish.fishblog.core.entity.LogItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<LogItem, Integer> {


    Page<LogItem> findAllByOrderByDatetimeDesc(Pageable pageable);
}

package com.learn.tinyurl.adapters.outbound.jpa;

import com.learn.tinyurl.adapters.outbound.persistence.TinyUrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TinyUrlRepo extends JpaRepository<TinyUrlEntity, Long>{
    Optional<TinyUrlEntity> findByShortKey(String shortKey);
}

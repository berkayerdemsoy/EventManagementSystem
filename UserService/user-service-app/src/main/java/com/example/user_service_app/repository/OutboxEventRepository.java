package com.example.user_service_app.repository;

import com.example.user_service_app.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    List<OutboxEvent> findByProcessedFalseOrderByCreatedAtAsc();
}

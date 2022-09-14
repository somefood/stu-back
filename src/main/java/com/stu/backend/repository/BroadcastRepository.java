package com.stu.backend.repository;

import com.stu.backend.domain.Broadcast;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BroadcastRepository extends JpaRepository<Broadcast, Long> {
}

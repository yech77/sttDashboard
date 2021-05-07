package com.stt.dash.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stt.dash.backend.data.entity.HistoryItem;

public interface HistoryItemRepository extends JpaRepository<HistoryItem, Long> {
}

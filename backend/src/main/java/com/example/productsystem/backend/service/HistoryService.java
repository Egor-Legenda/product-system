package com.example.productsystem.backend.service;

import com.example.productsystem.backend.entity.History;
import com.example.productsystem.backend.repository.HistoryRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class HistoryService {

    @Inject
    private HistoryRepository historyRepository;

    public void create(History history) {
        historyRepository.create(history);
    }

    public List<History> findAll() {
        return historyRepository.list();
    }

    @Transactional
    public void recordSuccess(String username, String fileName, int count) {
        History history = new History();
        history.setStatus(true);
        history.setUseName(username);
        history.setCountObjects(count);
        historyRepository.create(history);
    }

    @Transactional
    public void recordFailure(String username, String fileName, String errorMessage) {
        History history = new History();
        history.setStatus(false);
        history.setUseName(username);
        history.setCountObjects(0);
        historyRepository.create(history);
    }
}

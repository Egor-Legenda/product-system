package com.example.productsystem.backend.service;


import com.example.productsystem.backend.entity.Coordinates;
import com.example.productsystem.backend.repository.CoordinatesRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Map;

@ApplicationScoped
public class CoordinatesService {

    @Inject
    private CoordinatesRepository coordinatesRepository;

    @Transactional
    public Coordinates create(Coordinates coordinates) {
        return coordinatesRepository.create(coordinates);
    }

    public Coordinates find(Long id) {
        return coordinatesRepository.find(id);
    }

    @Transactional
    public Coordinates update(Coordinates coordinates) {
        return coordinatesRepository.update(coordinates);
    }

    @Transactional
    public void delete(Long id) {
        coordinatesRepository.delete(id);
    }

    public List<Coordinates> list(int page, int size, String sortField, boolean asc) {
        return coordinatesRepository.list(page, size, sortField, asc);
    }

    public List<Coordinates> filterByX(Integer xMin, Integer xMax, int page, int size) {
        return coordinatesRepository.filterByX(xMin, xMax, page, size);
    }

    public List<Coordinates> filterByY(Double yMin, Double yMax, int page, int size) {
        return coordinatesRepository.filterByY(yMin, yMax, page, size);
    }

    public List<Coordinates> findByXGreaterThan(Integer x) {
        return coordinatesRepository.findByXGreaterThan(x);
    }

    public List<Coordinates> findByYLessThan(Double y) {
        return coordinatesRepository.findByYLessThan(y);
    }

    public List<Integer> findUniqueXValues() {
        return coordinatesRepository.findUniqueXValues();
    }

    public Map<String, Object> getCoordinatesStats() {
        return coordinatesRepository.getCoordinatesStats();
    }
}
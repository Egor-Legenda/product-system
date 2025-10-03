package com.example.productsystem.backend.repository;


import com.example.productsystem.backend.entity.Coordinates;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class CoordinatesRepository {

    @PersistenceContext(unitName = "ProductPU")
    private EntityManager em;

    public Coordinates create(Coordinates coordinates) {
        em.persist(coordinates);
        return coordinates;
    }

    public Coordinates find(Long id) {
        return em.find(Coordinates.class, id);
    }

    public Coordinates update(Coordinates coordinates) {
        return em.merge(coordinates);
    }

    public void delete(Long id) {
        Coordinates coordinates = find(id);
        if (coordinates != null) {
            em.remove(em.contains(coordinates) ? coordinates : em.merge(coordinates));
        }
    }

    public List<Coordinates> list(int page, int size, String sortField, boolean asc) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Coordinates> cq = cb.createQuery(Coordinates.class);
        Root<Coordinates> root = cq.from(Coordinates.class);

        if (sortField != null && !sortField.isEmpty()) {
            Path<?> path = root.get(sortField);
            cq.orderBy(asc ? cb.asc(path) : cb.desc(path));
        }

        TypedQuery<Coordinates> q = em.createQuery(cq);
        q.setFirstResult(page * size);
        q.setMaxResults(size);
        return q.getResultList();
    }

    public List<Coordinates> filterByX(Integer xMin, Integer xMax, int page, int size) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Coordinates> cq = cb.createQuery(Coordinates.class);
        Root<Coordinates> root = cq.from(Coordinates.class);

        List<Predicate> predicates = new ArrayList<>();
        if (xMin != null) {
            predicates.add(cb.ge(root.get("x"), xMin));
        }
        if (xMax != null) {
            predicates.add(cb.le(root.get("x"), xMax));
        }

        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(new Predicate[0]));
        }

        TypedQuery<Coordinates> q = em.createQuery(cq);
        q.setFirstResult(page * size);
        q.setMaxResults(size);
        return q.getResultList();
    }

    public List<Coordinates> filterByY(Double yMin, Double yMax, int page, int size) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Coordinates> cq = cb.createQuery(Coordinates.class);
        Root<Coordinates> root = cq.from(Coordinates.class);

        List<Predicate> predicates = new ArrayList<>();
        if (yMin != null) {
            predicates.add(cb.ge(root.get("y"), yMin));
        }
        if (yMax != null) {
            predicates.add(cb.le(root.get("y"), yMax));
        }

        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(new Predicate[0]));
        }

        TypedQuery<Coordinates> q = em.createQuery(cq);
        q.setFirstResult(page * size);
        q.setMaxResults(size);
        return q.getResultList();
    }

    public List<Coordinates> findByXGreaterThan(Integer x) {
        return em.createQuery("SELECT c FROM Coordinates c WHERE c.x > :x", Coordinates.class)
                .setParameter("x", x)
                .getResultList();
    }

    public List<Coordinates> findByYLessThan(Double y) {
        return em.createQuery("SELECT c FROM Coordinates c WHERE c.y < :y", Coordinates.class)
                .setParameter("y", y)
                .getResultList();
    }

    public List<Integer> findUniqueXValues() {
        return em.createQuery("SELECT DISTINCT c.x FROM Coordinates c WHERE c.x IS NOT NULL ORDER BY c.x", Integer.class)
                .getResultList();
    }

    public Map<String, Object> getCoordinatesStats() {
        Double avgX = em.createQuery("SELECT AVG(c.x) FROM Coordinates c", Double.class)
                .getSingleResult();
        Double avgY = em.createQuery("SELECT AVG(c.y) FROM Coordinates c", Double.class)
                .getSingleResult();
        Long totalCount = em.createQuery("SELECT COUNT(c) FROM Coordinates c", Long.class)
                .getSingleResult();
        Integer maxX = em.createQuery("SELECT MAX(c.x) FROM Coordinates c", Integer.class)
                .getSingleResult();
        Integer minX = em.createQuery("SELECT MIN(c.x) FROM Coordinates c", Integer.class)
                .getSingleResult();

        return Map.of(
                "totalCount", totalCount,
                "averageX", avgX != null ? avgX : 0,
                "averageY", avgY != null ? avgY : 0,
                "maxX", maxX != null ? maxX : 0,
                "minX", minX != null ? minX : 0
        );
    }
}
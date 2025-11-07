package com.example.productsystem.backend.repository;

import com.example.productsystem.backend.entity.History;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

@ApplicationScoped













public class HistoryRepository {
    @PersistenceContext(unitName = "ProductPU")
    private EntityManager em;

    public void create(History history) {
        em.persist(history);
    }

    public List<History> list() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<History> cq = cb.createQuery(History.class);
        Root<History> root = cq.from(History.class);
        cq.select(root);
        return em.createQuery(cq).getResultList();
    }

}

package com.example.productsystem.backend.repository;

import com.example.productsystem.backend.entity.Address;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class AddressRepository {

    @PersistenceContext(unitName = "ProductPU")
    private EntityManager em;

    public Address create(Address address) {
        em.persist(address);
        return address;
    }

    public Address find(Long id) {
        return em.find(Address.class, id);
    }

    public Address update(Address address) {
        return em.merge(address);
    }

    public void delete(Long id) {
        Address address = find(id);
        if (address != null) {
            em.remove(em.contains(address) ? address : em.merge(address));
        }
    }

    public List<Address> list(int page, int size, String sortField, boolean asc) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Address> cq = cb.createQuery(Address.class);
        Root<Address> root = cq.from(Address.class);

        // Join with town to allow sorting by town fields
        Join<Object, Object> townJoin = root.join("town", JoinType.LEFT);

        if (sortField != null && !sortField.isEmpty()) {
            Path<?> path;
            // Handle sorting by town fields
            if (sortField.startsWith("town.")) {
                String townField = sortField.substring(5); // remove "town." prefix
                path = townJoin.get(townField);
            } else {
                path = root.get(sortField);
            }
            cq.orderBy(asc ? cb.asc(path) : cb.desc(path));
        }

        TypedQuery<Address> q = em.createQuery(cq);
        q.setFirstResult(page * size);
        q.setMaxResults(size);
        return q.getResultList();
    }

    public List<Address> findByZipCode(String zipCode) {
        return em.createQuery("SELECT a FROM Address a WHERE a.zipCode LIKE :zipCode", Address.class)
                .setParameter("zipCode", "%" + zipCode + "%")
                .getResultList();
    }

    public List<Address> findByTownName(String townName, int page, int size) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Address> cq = cb.createQuery(Address.class);
        Root<Address> root = cq.from(Address.class);

        // Join with town to search by town name
        Join<Object, Object> townJoin = root.join("town", JoinType.INNER);

        Predicate namePredicate = cb.like(cb.lower(townJoin.get("name")),
                "%" + townName.toLowerCase() + "%");
        cq.where(namePredicate);

        TypedQuery<Address> q = em.createQuery(cq);
        q.setFirstResult(page * size);
        q.setMaxResults(size);
        return q.getResultList();
    }

    public List<String> findUniqueZipCodes() {
        return em.createQuery("SELECT DISTINCT a.zipCode FROM Address a WHERE a.zipCode IS NOT NULL ORDER BY a.zipCode", String.class)
                .getResultList();
    }

    public List<Map<String, Object>> getAddressCountByTown() {
        List<Object[]> results = em.createQuery(
                "SELECT a.town.name, COUNT(a) FROM Address a GROUP BY a.town.name ORDER BY COUNT(a) DESC",
                Object[].class
        ).getResultList();

        return results.stream()
                .map(result -> Map.of(
                        "townName", result[0] != null ? result[0] : "Unknown",
                        "addressCount", result[1]
                ))
                .collect(Collectors.toList());
    }

    // Дополнительные методы для сложных запросов

    public List<Address> findByTownCoordinatesRange(Double minX, Double maxX, Double minY, Double maxY) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Address> cq = cb.createQuery(Address.class);
        Root<Address> root = cq.from(Address.class);
        Join<Object, Object> townJoin = root.join("town", JoinType.INNER);

        List<Predicate> predicates = new ArrayList<>();
        if (minX != null) predicates.add(cb.ge(townJoin.get("x"), minX));
        if (maxX != null) predicates.add(cb.le(townJoin.get("x"), maxX));
        if (minY != null) predicates.add(cb.ge(townJoin.get("y"), minY));
        if (maxY != null) predicates.add(cb.le(townJoin.get("y"), maxY));

        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(new Predicate[0]));
        }

        return em.createQuery(cq).getResultList();
    }
}
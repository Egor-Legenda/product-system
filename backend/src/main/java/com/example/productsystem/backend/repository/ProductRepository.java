package com.example.productsystem.backend.repository;



import com.example.productsystem.backend.entity.Product;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import java.util.*;

/*
 * Репозиторий для управления сущностями Product.
 * Предоставляет методы для создания, поиска, обновления, удаления и сложных запросов.
 */
@ApplicationScoped
public class ProductRepository {
    @PersistenceContext(unitName = "ProductPU")
    private EntityManager em;

    public Product create(Product p) {
        em.persist(p);
        return p;
    }

    public void saveAll(List<Product> products) {
        for (Product product : products) {
            em.persist(product);
        }
    }

    public Product find(Long id) {
        return em.find(Product.class, id);
    }

    public Product update(Product p) {
        return em.merge(p);
    }

    public void delete(Product p) {
        if (p != null) em.remove(em.contains(p) ? p : em.merge(p));
    }

    public Optional<Product> findByPartNumber(String partNumber) {
        try {
            List<Product> products = em.createQuery("SELECT p FROM Product p WHERE p.partNumber = :pn", Product.class)
                    .setParameter("pn", partNumber)
                    .getResultList();
            
            return products.isEmpty() ? Optional.empty() : Optional.of(products.get(0));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    public List<Product> list(int page, int size, String sortField, boolean asc) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> root = cq.from(Product.class);

        if (sortField != null) {
            Path<?> path = getSortPathWithReflection(root, sortField);
            cq.orderBy(asc ? cb.asc(path) : cb.desc(path));
        }

        TypedQuery<Product> q = em.createQuery(cq);
        q.setFirstResult(page * size);
        q.setMaxResults(size);
        return q.getResultList();
    }


    public List<Product> filter(String name, Float priceMin, Float priceMax, int page, int size) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> root = cq.from(Product.class);
        List<Predicate> preds = new ArrayList<>();
        if (name != null) preds.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        if (priceMin != null) preds.add(cb.ge(root.get("price"), priceMin));
        if (priceMax != null) preds.add(cb.le(root.get("price"), priceMax));
        cq.where(preds.toArray(new Predicate[0]));
        TypedQuery<Product> q = em.createQuery(cq);
        q.setFirstResult(page * size);
        q.setMaxResults(size);
        return q.getResultList();
    }

    public List<String> uniquePartNumbers() {
        return em.createQuery("SELECT DISTINCT p.partNumber FROM Product p WHERE p.partNumber IS NOT NULL", String.class)
                .getResultList();
    }

    public List<Product> ratingGreaterThan(int rating) {
        return em.createQuery("SELECT p FROM Product p WHERE p.rating > :r", Product.class)
                .setParameter("r", rating)
                .getResultList();
    }

    public List<Product> priceRange(Float min, Float max) {
        return em.createQuery("SELECT p FROM Product p WHERE p.price BETWEEN :min AND :max", Product.class)
                .setParameter("min", min)
                .setParameter("max", max)
                .getResultList();
    }

    public int increasePricePercent(int percent) {
        int updated = em.createQuery("UPDATE Product p SET p.price = p.price * :factor")
                .setParameter("factor", 1 + (percent / 100.0f))
                .executeUpdate();
        return updated;
    }

    public List<Product> filterByField(String field, String value, int page, int size, String sortField, boolean asc) {
        if (field == null || value == null || value.trim().isEmpty()) {
            return list(page, size, sortField, asc);
        }

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> root = cq.from(Product.class);

        Predicate filterPredicate = createFilterPredicate(cb, root, field, value);
        cq.where(filterPredicate);

        if (sortField != null) {
            Path<?> path = getSortPathWithReflection(root, sortField);
            cq.orderBy(asc ? cb.asc(path) : cb.desc(path));
        }

        TypedQuery<Product> q = em.createQuery(cq);
        q.setFirstResult(page * size);
        q.setMaxResults(size);
        return q.getResultList();
    }

    private Predicate createFilterPredicate(CriteriaBuilder cb, Root<Product> root, String field, String value) {
        String searchPattern = "%" + value.toLowerCase() + "%";

        switch (field) {
            case "name":
                return cb.like(cb.lower(root.get("name")), searchPattern);
            case "partNumber":
                return cb.like(cb.lower(root.get("partNumber")), searchPattern);
            case "unitOfMeasure":
                return cb.like(cb.lower(root.get("unitOfMeasure")), searchPattern);
            case "manufacturer.name":
                return cb.like(cb.lower(root.join("manufacturer").get("name")), searchPattern);
            case "owner.name":
                return cb.like(cb.lower(root.join("owner").get("name")), searchPattern);
            case "owner.passportID":
                return cb.like(cb.lower(root.join("owner").get("passportID")), searchPattern);
            case "manufacturer.type":
                return cb.like(cb.lower(root.join("manufacturer").get("type")), searchPattern);
            default:
                throw new IllegalArgumentException("Unknown filter field: " + field);
        }
    }

    private Path<?> getSortPathWithReflection(Root<Product> root, String sortField) {
        String[] fieldParts = sortField.split("\\.");

        Path<?> path = root;
        for (String part : fieldParts) {
            path = path.get(part);
        }

        return path;
    }
}

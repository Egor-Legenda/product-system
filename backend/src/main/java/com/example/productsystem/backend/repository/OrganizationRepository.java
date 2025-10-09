package com.example.productsystem.backend.repository;

import com.example.productsystem.backend.entity.Address;
import com.example.productsystem.backend.entity.Organization;
import com.example.productsystem.common.OrganizationType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

import java.util.List;

/*
 * Репозиторий для управления сущностями Organization.
 * Предоставляет методы для создания, поиска, обновления, удаления и сложных запросов.
 */
@ApplicationScoped
public class OrganizationRepository {

    @PersistenceContext(unitName = "ProductPU")
    private EntityManager em;

    public Organization create(Organization org) {
        em.persist(org);
        return org;
    }

    public Organization find(Integer id) {
        return em.find(Organization.class, id);
    }

    public Organization update(Organization org) {
        return em.merge(org);
    }

    public void delete(Organization org) {
        if (org != null) em.remove(em.contains(org) ? org : em.merge(org));
    }

    public List<Organization> findByName(String name) {
        return em.createQuery("SELECT o FROM Organization o WHERE LOWER(o.name) LIKE LOWER(:name)", Organization.class)
                .setParameter("name", "%" + name + "%")
                .getResultList();
    }

    public List<Organization> findByType(OrganizationType type) {
        return em.createQuery("SELECT o FROM Organization o WHERE o.type = :type", Organization.class)
                .setParameter("type", type)
                .getResultList();
    }

    public List<Organization> list(int page, int size, String sortField, boolean asc) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Organization> cq = cb.createQuery(Organization.class);
        Root<Organization> root = cq.from(Organization.class);

        if (sortField != null) {
            Path<?> path = root.get(sortField);
            cq.orderBy(asc ? cb.asc(path) : cb.desc(path));
        }

        TypedQuery<Organization> q = em.createQuery(cq);
        q.setFirstResult(page * size);
        q.setMaxResults(size);
        return q.getResultList();
    }

    public List<Organization> sortByTurnover(int page, int size, boolean ascending) {
        String query = "SELECT o FROM Organization o ORDER BY o.annualTurnover " + (ascending ? "ASC" : "DESC");
        return em.createQuery(query, Organization.class)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public Address findAddressByOrganizationId(Integer id) {
        Organization org = find(id);
        return org != null ? org.getOfficialAddress() : null;
    }

    @Transactional
    public void updateAddress(Integer organizationId, Address newAddress) {
        Organization org = find(organizationId);
        if (org != null) {
            if (org.getOfficialAddress() != null) {
                org.getOfficialAddress().setTown(newAddress.getTown());
                org.getOfficialAddress().setZipCode(newAddress.getZipCode());
            } else {
                org.setOfficialAddress(newAddress);
            }
            em.merge(org);
        }
    }

    public List<Organization> filterByEmployeeRange(int minEmployees, int maxEmployees, int page, int size) {
        return em.createQuery("SELECT o FROM Organization o WHERE o.employeesCount BETWEEN :min AND :max", Organization.class)
                .setParameter("min", minEmployees)
                .setParameter("max", maxEmployees)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public List<Organization> findByRatingGreaterThan(int rating) {
        return em.createQuery("SELECT o FROM Organization o WHERE o.rating > :rating", Organization.class)
                .setParameter("rating", rating)
                .getResultList();
    }

    public Long countByType(OrganizationType type) {
        return em.createQuery("SELECT COUNT(o) FROM Organization o WHERE o.type = :type", Long.class)
                .setParameter("type", type)
                .getSingleResult();
    }
}
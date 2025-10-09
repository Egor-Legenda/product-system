package com.example.productsystem.backend.repository;

import com.example.productsystem.backend.entity.Location;
import com.example.productsystem.backend.entity.Person;
import com.example.productsystem.common.Color;
import com.example.productsystem.common.Country;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PersonRepository {

    @PersistenceContext(unitName = "ProductPU")
    private EntityManager em;

    public Person create(Person person) {
        em.persist(person);
        return person;
    }

    public Person find(Long id) {
        return em.find(Person.class, id);
    }

    public Person update(Person person) {
        return em.merge(person);
    }

    public void delete(Person person) {
        if (person != null) em.remove(em.contains(person) ? person : em.merge(person));
    }

    public List<Person> list(int page, int size, String sortField, boolean asc) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Person> cq = cb.createQuery(Person.class);
        Root<Person> root = cq.from(Person.class);

        if (sortField != null) {
            Path<?> path = root.get(sortField);
            cq.orderBy(asc ? cb.asc(path) : cb.desc(path));
        }

        TypedQuery<Person> q = em.createQuery(cq);
        q.setFirstResult(page * size);
        q.setMaxResults(size);
        return q.getResultList();
    }

    public Optional<Person> findByPassportID(String passportID) {
        try {
            Person person = em.createQuery("SELECT p FROM Person p WHERE p.passportID = :passport", Person.class)
                    .setParameter("passport", passportID)
                    .getSingleResult();
            return Optional.of(person);
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public List<Person> findByNationality(Country nationality) {
        return em.createQuery("SELECT p FROM Person p WHERE p.nationality = :nationality", Person.class)
                .setParameter("nationality", nationality)
                .getResultList();
    }

    public List<Person> findByEyeColor(Color eyeColor) {
        return em.createQuery("SELECT p FROM Person p WHERE p.eyeColor = :eyeColor", Person.class)
                .setParameter("eyeColor", eyeColor)
                .getResultList();
    }

    public List<Person> findByHairColor(Color hairColor) {
        return em.createQuery("SELECT p FROM Person p WHERE p.hairColor = :hairColor", Person.class)
                .setParameter("hairColor", hairColor)
                .getResultList();
    }

    public List<Person> findByName(String name) {
        return em.createQuery("SELECT p FROM Person p WHERE LOWER(p.name) LIKE LOWER(:name)", Person.class)
                .setParameter("name", "%" + name + "%")
                .getResultList();
    }

    public Location findLocationByPersonId(Long id) {
        Person person = find(id);
        return person != null ? person.getLocation() : null;
    }

    @Transactional
    public void updateLocation(Long personId, Location newLocation) {
        Person person = find(personId);
        if (person != null) {
            if (person.getLocation() != null) {
                person.getLocation().setX(newLocation.getX());
                person.getLocation().setY(newLocation.getY());
                person.getLocation().setZ(newLocation.getZ());
                person.getLocation().setName(newLocation.getName());
            } else {
                person.setLocation(newLocation);
            }
            em.merge(person);
        }
    }

    public List<Person> filterByMultipleCriteria(Color eyeColor, Color hairColor, Country nationality, int page, int size) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Person> cq = cb.createQuery(Person.class);
        Root<Person> root = cq.from(Person.class);

        List<Predicate> predicates = new ArrayList<>();

        if (eyeColor != null) {
            predicates.add(cb.equal(root.get("eyeColor"), eyeColor));
        }
        if (hairColor != null) {
            predicates.add(cb.equal(root.get("hairColor"), hairColor));
        }
        if (nationality != null) {
            predicates.add(cb.equal(root.get("nationality"), nationality));
        }

        cq.where(predicates.toArray(new Predicate[0]));

        TypedQuery<Person> q = em.createQuery(cq);
        q.setFirstResult(page * size);
        q.setMaxResults(size);
        return q.getResultList();
    }

    public Long countByNationality(Country nationality) {
        return em.createQuery("SELECT COUNT(p) FROM Person p WHERE p.nationality = :nationality", Long.class)
                .setParameter("nationality", nationality)
                .getSingleResult();
    }
}
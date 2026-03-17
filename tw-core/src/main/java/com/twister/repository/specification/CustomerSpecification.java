package com.twister.repository.specification;

import com.twister.domain.Customer;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class CustomerSpecification {

    public static Specification<Customer> search(CustomerFilter filter) {
        return ((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            Optional.ofNullable(filter.surname()).ifPresent(surname -> predicates.add(
                cb.like(cb.lower(root.get("surname")), "%" + surname.toLowerCase() + "%")
            ));

            Optional.ofNullable(filter.name()).ifPresent(name -> predicates.add(
                cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%")
            ));

            Optional.ofNullable(filter.patronymic()).ifPresent(patronymic -> predicates.add(
                cb.like(root.get("patronymic"), "%" + patronymic.toLowerCase() + "%")
            ));

            Optional.ofNullable(filter.phone()).ifPresent(phone -> predicates.add(
                cb.like(root.get("phone"), "%" + phone + "%")
            ));

            return cb.and(predicates.toArray(new Predicate[0]));
        });
    }
}

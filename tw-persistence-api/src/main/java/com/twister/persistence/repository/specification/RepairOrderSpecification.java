package com.twister.persistence.repository.specification;

import com.twister.domain.Customer;
import com.twister.domain.reference.Transport;
import com.twister.domain.repair.RepairOrder;
import com.twister.repository.specification.RepairOrderFilter;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class RepairOrderSpecification {

    public static Specification<RepairOrder> search(RepairOrderFilter filter) {
        return ((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            Optional.ofNullable(filter.description()).ifPresent(name -> predicates.add(
                cb.like(cb.lower(root.get("description")), "%" + name.toLowerCase() + "%")
            ));

            Optional.ofNullable(filter.customerId()).ifPresent(customerId -> {
                Join<RepairOrder, Customer> customerJoin = root.join("customer");
                predicates.add(cb.equal(customerJoin.get("id"), customerId));
            });

            Optional.ofNullable(filter.transportId()).ifPresent(transportId -> {
                Join<RepairOrder, Transport> transportJoin = root.join("transport");
                predicates.add(cb.equal(transportJoin.get("id"), transportId));
            });

            return cb.and(predicates.toArray(new Predicate[0]));
        });
    }
}

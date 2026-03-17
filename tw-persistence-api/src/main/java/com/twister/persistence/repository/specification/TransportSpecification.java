package com.twister.persistence.repository.specification;

import com.twister.domain.reference.Country;
import com.twister.domain.reference.Transport;
import com.twister.domain.reference.TransportBrand;
import com.twister.domain.reference.TransportType;
import com.twister.repository.specification.TransportFilter;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class TransportSpecification {

    public static Specification<Transport> search(TransportFilter filter) {
        return ((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            Optional.ofNullable(filter.name()).ifPresent(name -> predicates.add(
                cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%")
            ));

            Optional.ofNullable(filter.model()).ifPresent(model -> predicates.add(
                cb.like(cb.lower(root.get("model")), "%" + model.toLowerCase() + "%")
            ));

            Optional.ofNullable(filter.brandId()).ifPresent(brandId -> {
                Join<Transport, TransportBrand> brandJoin = root.join("brand");
                predicates.add(cb.equal(brandJoin.get("id"), brandId));
            });

            Optional.ofNullable(filter.countryId()).ifPresent(countryId -> {
                Join<Transport, Country> countryJoin = root.join("country");
                predicates.add(cb.equal(countryJoin.get("id"), countryId));
            });

            Optional.ofNullable(filter.transportTypeId()).ifPresent(transportTypeId -> {
                Join<Transport, TransportType> typeJoin = root.join("type");
                predicates.add(cb.equal(typeJoin.get("id"), transportTypeId));
            });

            Optional.ofNullable(filter.issueYear()).ifPresent(issueYear -> predicates.add(
                cb.equal(root.get("issueYear"), issueYear)
            ));


            return cb.and(predicates.toArray(new Predicate[0]));
        });
    }
}

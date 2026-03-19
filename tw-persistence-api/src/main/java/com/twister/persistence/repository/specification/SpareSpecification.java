package com.twister.persistence.repository.specification;

import com.twister.persistence.entity.reference.CountryEntity;
import com.twister.persistence.entity.reference.SpareBrandEntity;
import com.twister.persistence.entity.reference.SpareEntity;
import com.twister.persistence.entity.reference.SpareTypeEntity;
import com.twister.persistence.entity.reference.TransportTypeEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class SpareSpecification {


    public static Specification<SpareEntity> search(SpareFilter filter) {
        return ((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            Optional.ofNullable(filter.name()).ifPresent(name -> predicates.add(
                cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%")
            ));

            Optional.ofNullable(filter.model()).ifPresent(model -> predicates.add(
                cb.like(cb.lower(root.get("model")), "%" + model.toLowerCase() + "%")
            ));

            Optional.ofNullable(filter.material()).ifPresent(model -> predicates.add(
                cb.like(cb.lower(root.get("material")), "%" + model.toLowerCase() + "%")
            ));

            Optional.ofNullable(filter.brandId()).ifPresent(brandId -> {
                Join<SpareEntity, SpareBrandEntity> brandJoin = root.join("brand");
                predicates.add(cb.equal(brandJoin.get("id"), brandId));
            });

            Optional.ofNullable(filter.countryId()).ifPresent(countryId -> {
                Join<SpareEntity, CountryEntity> countryJoin = root.join("country");
                predicates.add(cb.equal(countryJoin.get("id"), countryId));
            });

            Optional.ofNullable(filter.spareTypeId()).ifPresent(spareTypeId -> {
                Join<SpareEntity, SpareTypeEntity> spareTypeJoin = root.join("type");
                predicates.add(cb.equal(spareTypeJoin.get("id"), spareTypeId));
            });

            Optional.ofNullable(filter.transportTypeId()).ifPresent(transportTypeId -> {
                Join<SpareEntity, TransportTypeEntity> transportTypeJoin = root.join("transportType");
                predicates.add(cb.equal(transportTypeJoin.get("id"), transportTypeId));
            });

            Optional.ofNullable(filter.issueYear()).ifPresent(issueYear -> predicates.add(
                cb.equal(root.get("issueYear"), issueYear)
            ));


            return cb.and(predicates.toArray(new Predicate[0]));
        });
    }
}

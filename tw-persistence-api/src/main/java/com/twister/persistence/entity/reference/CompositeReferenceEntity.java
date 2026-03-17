package com.twister.persistence.entity.reference;

import com.twister.domain.reference.Country;
import com.twister.domain.reference.ReferenceEntity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
public abstract class CompositeReferenceEntity<TYPE, BRAND> extends ReferenceEntity {

    private String model;

    private Integer issueYear;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private TYPE type;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private BRAND brand;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    private String sku;

    private String url;
}

package com.twister.domain.reference;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public abstract class CompositeReference<TYPE, BRAND> extends Reference {

    private String model;
    private Integer issueYear;
    private TYPE type;
    private BRAND brand;
    private Country country;
    private String sku;
    private String url;
}

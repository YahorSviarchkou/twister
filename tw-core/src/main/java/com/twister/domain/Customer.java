package com.twister.domain;

import com.twister.domain.reference.Transport;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Customer extends AuditableModel {

    Long id;
    String surname;
    String name;
    String patronymic;
    String phone;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<Transport> transportList;
}

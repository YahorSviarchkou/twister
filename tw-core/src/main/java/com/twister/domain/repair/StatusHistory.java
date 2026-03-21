package com.twister.domain.repair;

import com.twister.domain.AuditableModel;
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
public abstract class StatusHistory<S> extends AuditableModel {

    private Long id;
    private Long workflowItemId;
    private S status;
}

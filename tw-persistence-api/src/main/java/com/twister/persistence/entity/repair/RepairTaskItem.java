package com.twister.persistence.entity.repair;

import com.twister.domain.AuditableEntity;
import com.twister.domain.reference.ServiceType;
import com.twister.domain.reference.Spare;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Table(name = "repair_task_items")
@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class RepairTaskItem extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private RepairTask task;

    @ManyToOne
    @JoinColumn(name = "service_type_id", nullable = false)
    private ServiceType serviceType;

    @ManyToMany
    @JoinTable(
        name = "task_item_spares",
        joinColumns = {@JoinColumn(name = "item_id")},
        inverseJoinColumns = {@JoinColumn(name = "spare_id")}
    )
    private List<Spare> spares;

    @OneToMany(mappedBy = "taskItem")
    private List<RepairTaskItemStatusHistory> statusHistory;
}

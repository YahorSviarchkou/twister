package com.twister.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Entity
@Table(name = "work_tasks")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WorkTasks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

//    @ManyToOne
//    @JoinColumn(name = "work_id")
//    Work work;
//
//    @ManyToOne
//    @JoinColumn(name = "task_id")
//    Task task;
//
//    @Enumerated(EnumType.STRING)
//    TaskStatus type;
}

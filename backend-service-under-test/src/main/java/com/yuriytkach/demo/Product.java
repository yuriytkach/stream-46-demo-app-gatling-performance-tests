package com.yuriytkach.demo;

import java.math.BigDecimal;
import java.time.Instant;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;


@Entity
public class Product extends PanacheEntity {
    @Column(nullable = false)
    public String name;

    @Column(length = 1000)
    public String description;

    @Column(nullable = false)
    public BigDecimal price;

    @Column(nullable = false)
    public int stock;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    public Instant createdDate;
}

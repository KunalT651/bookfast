package com.bookfast.backend.common.model;

import jakarta.persistence.*;

@Entity
public class ServiceCategory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    // getters/setters
}
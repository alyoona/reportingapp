package com.stroganova.reportingapp.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "genre")
public class Genre {
    @Id
    private long id;
    private String name;
}

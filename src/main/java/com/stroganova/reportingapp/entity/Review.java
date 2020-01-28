package com.stroganova.reportingapp.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "review")
public class Review {
    @Id
    private long id;

}

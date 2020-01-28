package com.stroganova.reportingapp.entity;


import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
@Data
public class Movie {
    @Id
    private long id;
    @Column(name = "name_native")
    private String title;

    private LocalDate year;
    private double price;
    private double rating;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "movie_genre",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private List<Genre> genres;


    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "movie_id")
    private Set<Review> reviews;




}

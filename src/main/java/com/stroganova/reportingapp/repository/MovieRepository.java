package com.stroganova.reportingapp.repository;

import com.stroganova.reportingapp.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    List<Movie> findAllByYearBetween(LocalDate fromYear, LocalDate toYear);
}

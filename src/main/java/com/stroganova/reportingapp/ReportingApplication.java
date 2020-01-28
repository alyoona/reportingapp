package com.stroganova.reportingapp;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.BrokenBarrierException;

@SpringBootApplication
@Slf4j

public class ReportingApplication {

    public static void main(String[] args) throws BrokenBarrierException, InterruptedException {
        SpringApplication.run(ReportingApplication.class, args);


    }
}

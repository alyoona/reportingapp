package com.stroganova.reportingapp.service.reportwriter.impl;

import com.stroganova.reportingapp.entity.Movie;
import com.stroganova.reportingapp.enums.ReportFormat;
import com.stroganova.reportingapp.enums.ReportRequestType;
import com.stroganova.reportingapp.enums.ReportType;
import com.stroganova.reportingapp.reports.ReportRequestInfo;
import com.stroganova.reportingapp.reports.ReportTypeAndFormat;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Test;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertTrue;

class MovieXlsxReportWriterServiceTest {

    @Test
    void writeIntoExcel() throws NoSuchFieldException {

        DefaultReportStoreService storeService = new DefaultReportStoreService();
        Field field = DefaultReportStoreService.class.getDeclaredField("rootPath");
        field.setAccessible(true);
        String root = Paths.get(".\\reportStorage").toAbsolutePath().toString();
        ReflectionUtils.setField(field, storeService, root);
        field.setAccessible(false);

        ReportTypeAndFormat typeAndFormat = new ReportTypeAndFormat(ReportType.ALL_MOVIES, ReportFormat.XLSX);
        ReportRequestInfo reportRequestInfo = new ReportRequestInfo(ReportRequestType.REPORT_ADD, typeAndFormat);
        String uuid = "da9b821f-e3e0-4c0e-b6dc-79b38952596d";

        try (OutputStream outputStream = storeService.getOutputStream(reportRequestInfo, uuid);
             Workbook workbook = new HSSFWorkbook()) {

            MovieXlsxReportWriterService writerService = new MovieXlsxReportWriterService();

            Movie movie = new Movie();
            movie.setId(123L);
            movie.setTitle("Title");
            movie.setYear(LocalDate.now());
            movie.setPrice(147D);
            movie.setRating(852D);
            movie.setGenres(Collections.EMPTY_LIST);
            movie.setReviews(Collections.EMPTY_SET);

            List<Movie> movies = Arrays.asList(movie);

            writerService.writeIntoExcel(movies, outputStream, workbook);

            Path path = Paths.get(storeService.getReportLink(reportRequestInfo, uuid));

            assertTrue(Files.exists(path));

            Files.deleteIfExists(path);

        } catch (IOException e) {

            throw new RuntimeException("Report generation error", e);
        }


    }

}
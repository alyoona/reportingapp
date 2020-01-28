package com.stroganova.reportingapp.service.reportwriter.impl;

import com.stroganova.reportingapp.entity.Movie;
import com.stroganova.reportingapp.service.reportwriter.XlsxReportWriterService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Service
public class MovieXlsxReportWriterService implements XlsxReportWriterService<Movie> {

    private int tableRowNum;
    private final List<String> headers = Arrays.asList("Movie id", "Title", "Year", "Price", "Rating", "Genres", "Review Count");

    @Override
    public void writeIntoExcel(List<Movie> movies, OutputStream outputStream, Workbook workbook) throws IOException {

        Sheet sheet = workbook.createSheet();

        addTableHeader(sheet);

        movies.forEach(movie -> addReportRow(sheet, movie));

        workbook.write(outputStream);
    }

    private void addReportRow(Sheet sheet, Movie movie) {
        Row row = sheet.createRow(tableRowNum++);

        row.createCell(0).setCellValue(movie.getId());
        row.createCell(1).setCellValue(movie.getTitle());
        row.createCell(2).setCellValue(movie.getYear());
        row.createCell(3).setCellValue(movie.getPrice());
        row.createCell(4).setCellValue(movie.getRating());

        List<String> genreNames = new ArrayList<>();
        movie.getGenres().forEach(genre -> genreNames.add(genre.getName()));

        row.createCell(5).setCellValue(String.join(",", genreNames));
        row.createCell(6).setCellValue(movie.getReviews().size());

    }


    private void addTableHeader(Sheet sheet) {
        tableRowNum = 0;
        Row headerRow = sheet.createRow(tableRowNum++);
        for (int i = 0; i < headers.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers.get(i));
        }
    }


}

package dev.aj.sdj_hibernate;

import com.opencsv.ICSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;


public class CSVWriter<T> {

    public void writeCsvData(List<T> data, String fileName) {


        String testDataDirectoryPath = "src/test/resources/test_data/";

        File testDataDirectory = new File(testDataDirectoryPath);

        if (!testDataDirectory.exists()) {
            boolean isDirectoryCreated = testDataDirectory.mkdirs();
            if (!isDirectoryCreated) {
                throw new RuntimeException("Failed to create test data directory");
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(testDataDirectoryPath + fileName))) {
            StatefulBeanToCsv<T> beanToCsvConverter = new StatefulBeanToCsvBuilder<T>(writer)
                    .withSeparator(ICSVWriter.DEFAULT_SEPARATOR)
                    .withOrderedResults(true)
                    .build();

            data.forEach(d -> {
                try {
                    beanToCsvConverter.write(d);
                } catch (CsvRequiredFieldEmptyException | CsvDataTypeMismatchException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

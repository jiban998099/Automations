package com.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class CompareTwoTextAndFindUniqueNumber {
    public static void main(String[] args) {
        String text1File = "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\Total_Values.txt";
        String text2File = "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\Filtered_Values.txt";
        String outputFile = "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\Missing_Values.txt";

        Set<String> text1Records = readFile(text1File);
        Set<String> text2Records = readFile(text2File);

        Set<String> missingRecords = findMissingRecords(text1Records, text2Records);

        writeToFile(outputFile, missingRecords);

        System.out.println("Found " + missingRecords.size() + " missing records and saved them to 'Missing_Values.txt'.");
    }

    private static Set<String> readFile(String filename) {
        Set<String> records = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                records.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
    }

    private static Set<String> findMissingRecords(Set<String> text1Records, Set<String> text2Records) {
        Set<String> missingRecords = new HashSet<>();
        for (String record : text1Records) {
            if (!text2Records.contains(record)) {
                missingRecords.add(record);
            }
        }
        return missingRecords;
    }

    private static void writeToFile(String filename, Set<String> records) {
        try (FileWriter writer = new FileWriter(filename)) {
            for (String record : records) {
                writer.write(record + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


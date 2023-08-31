package org.example;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.example.Const.*;

/**
 * author: Alesia Skarakhod
 * date: 2023-08-29
 * This class contains utility methods.
 */
@Slf4j
public class Utils {

    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    public static File[] checkArgs(String[] args) {
        if (args.length == 0) {
            var errorMessage = "Please provide the path to the directory with files.";
            LOGGER.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        var directoryPath = args[0];
        var directory = new File(directoryPath);

        if (!directory.isDirectory()) {
            var errorMessage = String.format("Provided path: %s is not a directory.", directoryPath);
            LOGGER.info(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        return retrieveFiles(directory);
    }

    public static File[] retrieveFiles(File directory) {
        File[] files = directory.listFiles();
        if (files == null || files.length == 0) {
            var errorMessage = String.format("No files found in the directory %s.", directory.getAbsolutePath());
            LOGGER.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
        LOGGER.info("Number of files: {}", files.length);
        return files;
    }

    public static Path getFilePath(String folderName, String fileName) {
        return Path.of(ROOT_PATH.concat(folderName).concat(fileName));
    }

    public static List<String> readWordsFromFile(File file) throws IOException {
        List<String> words = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] lineWords = line.split("\\s+");
                for (String word : lineWords) {
                    word = word.replaceAll("[.,!?;:\"'()\\[\\]{}]", "");
                    words.add(word.toLowerCase());
                }
            }
        }
        return words;
    }

    public static Map<String, Integer> countWords(List<String> words) {
        var start = Instant.now();
        Map<String, Integer> wordCount = new HashMap<>();
        for (String word : words) {
            wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
        }
        var end = Instant.now();
        printDuration(start, end, COUNTING_WORDS);
        return wordCount;
    }

    public static String getDurationInMillis(Duration duration) {
        long millis = duration.toMillis();
        long micros = duration.toNanos() / 1000;

        if (millis < 1) {
            return String.format("%d.%d", micros / 1000, micros % 1000);
        } else {
            return String.valueOf(millis);
        }
    }


    public static void printDuration(Instant start, Instant end, String logMessage){
        var durationInMillis = getDurationInMillis(Duration.between(start, end));
        MDC.put(DURATION, durationInMillis);
        log.info("{} - Duration time: {} ms", logMessage, durationInMillis);
        MDC.remove(DURATION);
    }
}

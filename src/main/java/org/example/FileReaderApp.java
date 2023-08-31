package org.example;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.example.AlphabetFileWriter.writeWordsToAlphabetFiles;
import static org.example.Const.*;
import static org.example.Exclusion.loadExclusionList;
import static org.example.Exclusion.saveExcludedWordCount;
import static org.example.Utils.*;

/**
 * author: Alesia Skarakhod
 * date: 2023-08-29
 * This class reads words from multiple files, counts the number of occurrences for each word,
 * excludes words from the list, counts the number of excluded words and saves the result in a file,
 * writes the given words to files with the alphabet in the title.
 */
public class FileReaderApp {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileReaderApp.class);

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        var start = Instant.now();
        var files = checkArgs(args);

        // Step 1: Read all words from all files
        List<String> allWords = getAllWords(files);

        // Step 2: Count the number of occurrences for each word
        var wordCount = countWords(allWords);

        // Step 3: Exclude words from the list
        var exclusionSet = loadExclusionList(getFilePath(INPUT_DIRECTORY, EXCLUDE_FILE));

        // Step 4: Count excluded words
        var excludedResultFilePath = getFilePath(OUTPUT_DIRECTORY, EXCLUDED_RESULT_FILE);
        saveExcludedWordCount(wordCount, exclusionSet, excludedResultFilePath);

        // Step 5: Write to alphabet files
        writeWordsToAlphabetFiles(wordCount, ROOT_PATH + OUTPUT_DIRECTORY);

        var endLast = Instant.now();
        printDuration(start, endLast, "Total time");
    }

    static List<String> getAllWords(File[] files) throws InterruptedException, ExecutionException {
        var start = Instant.now();
        int numThreads = Math.min(Runtime.getRuntime().availableProcessors(), files.length);
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<List<String>>> futures = new ArrayList<>();

        for (File file : files) {
            Callable<List<String>> task = () -> {
                Instant threadStart = Instant.now();
                List<String> result = readWordsFromFile(file);
                Instant threadEnd = Instant.now();
                printDuration(threadStart, threadEnd, "Thread name: " + Thread.currentThread().getName());
                return result;
            };
            futures.add(executor.submit(task));
            LOGGER.info("File {} is read.", file.getName());
        }

        List<String> allWords = new ArrayList<>();
        for (Future<List<String>> future : futures) {
            allWords.addAll(future.get());
        }
        executor.shutdown();
        var end = Instant.now();
        printDuration(start, end, READING_FILES);
        return allWords;
    }

}

package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.example.Const.LOADING_EXCLUSION_LIST;
import static org.example.Const.SAVING_EXCLUDED_WORDS;
import static org.example.Utils.printDuration;
import static org.example.Utils.readWordsFromFile;

/**
 * author: Alesia Skarakhod
 * date: 2023-08-29
 * This class excludes any words from a list that is in exclude.txt file,
 * Count the number of excluded words and save the result in a file.
 */
public class Exclusion {

    private static final Logger LOGGER = LoggerFactory.getLogger(Exclusion.class);

    public static Set<String> loadExclusionList(Path filePath) {
        var start = Instant.now();
        Set<String> exclusionSet = new HashSet<>();
        List<String> lines;
        try {
            lines = readWordsFromFile(filePath.toFile());
        } catch (IOException e) {
            LOGGER.error("An error occurred while loading the exclusion list: {}", e.getMessage());
            throw new RuntimeException("Failed to load the exclusion list.", e);
        }
        for (String line : lines) {
            exclusionSet.add(line.toLowerCase());
        }
        var end = Instant.now();
        printDuration(start, end, LOADING_EXCLUSION_LIST);
        return exclusionSet;
    }

    public static void saveExcludedWordCount(Map<String, Integer> wordCount, Set<String> exclusionSet, Path outputPath) {
        var start = Instant.now();
        int excludedCount = 0;
        StringBuilder content = new StringBuilder();

        for (String excludedWord : exclusionSet) {
            if (wordCount.containsKey(excludedWord)) {
                excludedCount += wordCount.get(excludedWord);
                content.append(excludedWord).append(" ").append(wordCount.get(excludedWord)).append("\n");
            }
        }

        var resultContent = "Excluded word count: " + excludedCount + "\n" + content;
        try {
            Files.writeString(outputPath, resultContent);
        } catch (IOException e) {
            var errorMessage = String.format("An error occurred while writing to path %s: %s", outputPath, e.getMessage());
            LOGGER.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }
        var end = Instant.now();
        printDuration(start, end, SAVING_EXCLUDED_WORDS);
    }
}

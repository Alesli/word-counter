package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Map;

import static org.example.Const.TXT_EXTENSION;
import static org.example.Const.WRITING_FILES;
import static org.example.Utils.printDuration;

/**
 * author: Alesia Skarakhod
 * date: 2023-08-29
 * This class writes the given words to files with the alphabet in the title.
 */
public class AlphabetFileWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlphabetFileWriter.class);

    /**
     * Writes the given words to files in the given output directory.
     * The words are written to files named FILE_A.txt, FILE_B.txt, and so on.
     * Each file contains words starting with the corresponding letter.
     * The words are written in the format: word count
     * For example, the file FILE_A.txt contains the words starting with 'a' in the format: word count
     *
     * @param wordCount       the word count
     * @param outputDirectory the output directory
     */
    public static void writeWordsToAlphabetFiles(Map<String, Integer> wordCount, String outputDirectory) {
        var start = Instant.now();
        for (char c = 'a'; c <= 'z'; c++) {
            var outputFile = outputDirectory + "FILE_" + Character.toUpperCase(c) + TXT_EXTENSION;
            StringBuilder content = new StringBuilder();

            for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
                if (entry.getKey().startsWith(String.valueOf(c))) {
                    content.append(entry.getKey()).append(" ").append(entry.getValue()).append("\n");
                }
            }

            if (content.length() > 0) {
                try {
                    Files.writeString(Path.of(outputFile), content.toString());
                } catch (IOException e) {
                    var errorMessage = String.format("An error occurred while writing to file %s: %s", outputFile, e.getMessage());
                    LOGGER.error(errorMessage);
                    throw new RuntimeException(errorMessage);
                }
            }
        }
        var end = Instant.now();
        printDuration(start, end, WRITING_FILES);
    }
}

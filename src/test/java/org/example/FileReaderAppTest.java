package org.example;


import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static org.example.AlphabetFileWriter.writeWordsToAlphabetFiles;
import static org.example.Exclusion.loadExclusionList;
import static org.example.Exclusion.saveExcludedWordCount;
import static org.example.FileReaderApp.getAllWords;
import static org.example.Utils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test for simple FileReaderApp.
 */
public class FileReaderAppTest {

    private Map<String, Integer> wordCount = new HashMap<>();
    Set<String> exclusionSet = new HashSet<>();
    File testFile1;
    File testFile2;
    File excludeFile;

    @SneakyThrows
    @BeforeEach
    void setUp() {
        URL resourceURL1 = getClass().getResource("/test_file_1.txt");
        URL resourceURL2 = getClass().getResource("/test_file_2.txt");
        URL excludeURL = getClass().getResource("/test_exclude.txt");
        testFile1 = new File(resourceURL1.getFile());
        testFile2 = new File(resourceURL2.getFile());
        excludeFile = new File(excludeURL.getFile());

        exclusionSet = new HashSet<>(Arrays.asList("amet", "purus", "neque"));

        wordCount = new HashMap<>();
        wordCount.put("amet", 1);
        wordCount.put("etiam", 1);
        wordCount.put("purus", 1);
        wordCount.put("nequ", 1);
    }

    @Test
    void testReadWordsFromFile() throws IOException {
        List<String> expectedWords = Arrays.asList("etiam", "sit", "amet", "neque", "non", "purus");
        List<String> actualWords = readWordsFromFile(testFile1);
        assertEquals(expectedWords, actualWords);
        assertEquals(6, actualWords.size());
    }

    @Test
    void testCountWords() {
        List<String> words = Arrays.asList("amet", "etiam", "purus", "nequ");
        Map<String, Integer> actualWordCount = countWords(words);

        assertEquals(wordCount, actualWordCount);
        assertEquals(1, actualWordCount.get("amet"));
        assertEquals(1, actualWordCount.get("etiam"));
    }

    @Test
    void testLoadExclusionList() {
        Set<String> actualExclusion = loadExclusionList(excludeFile.toPath());
        assertEquals(exclusionSet, actualExclusion);
    }

    @Test
    void testSaveExcludedWordCount() throws IOException {
        String outputFile = "excluded_result.txt";
        Path outputPath = Path.of(outputFile);
        saveExcludedWordCount(wordCount, exclusionSet, outputPath);

        List<String> lines = Files.readAllLines(outputPath);
        assertEquals("Excluded word count: 2", lines.get(0));
        assertEquals("amet 1", lines.get(1));
        Files.walk(outputPath).forEach(path -> path.toFile().deleteOnExit());
    }

    @Test
    void testWriteWordsToAlphabetFiles(@TempDir File tempDir) throws IOException {
        writeWordsToAlphabetFiles(wordCount, tempDir.toString() + File.separator);

        for (char c = 'a'; c <= 'z'; c++) {
            final char currentChar = c;

            String fileName = "FILE_" + Character.toUpperCase(currentChar) + ".txt";
            Path filePath = tempDir.toPath().resolve(fileName);

            if (wordCount.entrySet().stream().anyMatch(entry -> entry.getKey().startsWith(String.valueOf(currentChar)))) {
                assertTrue(filePath.toFile().exists());
                assertTrue(filePath.toFile().length() > 0);

                // Read the content of the file and validate it
                String fileContent = Files.readString(filePath);
                String[] lines = fileContent.split("\n");
                boolean contentValid = false;
                for (String line : lines) {
                    String[] parts = line.split(" ");
                    if (parts.length == 2) {
                        String word = parts[0];
                        int count = Integer.parseInt(parts[1]);
                        if (word.startsWith(String.valueOf(currentChar))) {
                            assertTrue(wordCount.containsKey(word));
                            assertEquals(wordCount.get(word), count);
                            contentValid = true;
                        }
                    }
                }
                assertTrue(contentValid, "File content validation failed");

            } else {
                assertFalse(filePath.toFile().exists());
            }
        }
    }

    @Test
    public void testGetAllWords() throws InterruptedException, ExecutionException {
        File[] files = new File[]{testFile1, testFile2};
        List<String> expectedWords = Arrays.asList("etiam", "sit", "amet", "neque", "non", "purus", "integer", "sit", "etiam");
        List<String> actualWords = getAllWords(files);
        assertEquals(expectedWords, actualWords);
    }

    @Test
    void testRetrieveFiles_FilesFound() {
        File[] mockFiles = {testFile1, testFile2};
        File directoryMock = mock(File.class);
        when(directoryMock.listFiles()).thenReturn(mockFiles);

        File[] retrievedFiles = retrieveFiles(directoryMock);

        assertNotNull(retrievedFiles);
        assertEquals(2, retrievedFiles.length);
        assertArrayEquals(mockFiles, retrievedFiles);
    }

    @Test
    void testRetrieveFiles_NoFilesFound() {
        File directoryMock = mock(File.class);
        when(directoryMock.listFiles()).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> retrieveFiles(directoryMock));
        assertTrue(exception.getMessage().contains("No files found in the directory"));
    }

    @Test
    public void testCheckArgs_EmptyArgs() {
        String[] args = new String[0];
        assertThrows(IllegalArgumentException.class, () -> checkArgs(args));
    }

    @Test
    public void testCheckArgs_NonDirectoryPath() {
        String[] args = {"nonexistent_directory"};
        assertThrows(IllegalArgumentException.class, () -> checkArgs(args));
    }

    @Test
    void testLoadExclusionList_IOException() {
        assertThrows(RuntimeException.class, () -> loadExclusionList(Path.of("someFilePath")));
    }

    @Test
    void testWriteWordsToAlphabetFiles_ExceptionHandling() {
        Map<String, Integer> wordCount = new HashMap<>();
        wordCount.put("apple", 3);
        String outputDirectory = "/invalid/directory";

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> writeWordsToAlphabetFiles(wordCount, outputDirectory));
        assertTrue(exception.getMessage().contains("An error occurred while writing to file"));
    }

}

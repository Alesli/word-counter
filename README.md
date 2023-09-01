# word-counter
Read data from multiple files, count, exclude and create a file on disk for each letter in the alphabet.

### Requirements:
1. Read data from multiple files in a directory on disk.
Path to the directory path is input to command line.
Create up to 4 files with text of min 200 words – example form
https://www.lipsum.com - as input files.
---
2. Count the number of occurrences for each word from all files.
*Words are case insensitive.*
---
3. Exclude any words from a list that is in exclude.txt file.
Create such a file and add 10 words that should be excluded from the output.
---
4. Count the number of excluded words encountered and save the result in a file.
---
5. Create a file on disk for each letter in the alphabet and write the words and their count of
occurrencesto them.
Each file should contain the words that start with the letter of the file.
- ### Sample output file:

For letter L -> `FILE_L.txt` with content:
`LORUM 10
LIKE 3`

For letter I -> `FILE_I.txt` content:
`IPSUM 20`

For letter C - > `FILE_C.txt` content:
`Content 7`
---
6. Write tests that show the code is working as intended.

   `mvn clean test`
   `mvn jacoco:report`
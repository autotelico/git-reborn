import java.io.*;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;


public class Main {
    public static String readBlob(Path file) throws IOException, DataFormatException {
        // Decompress file
        // Get byte array from file
        byte[] fileBytes = Files.readAllBytes(file);
        Inflater decompresser = new Inflater();
        decompresser.setInput(fileBytes, 0, fileBytes.length);
        int resultLength = decompresser.inflate(fileBytes);
        decompresser.end();

        // Read file
        String decompressedContent = new String(fileBytes, 0, resultLength);
        System.out.printf("Output string: %s\n", decompressedContent);
        String contentType = Files.probeContentType(file);
        long size = Files.size(file);
        System.out.printf("""
                Size: %d bytes
                Type: %s
                """, size, contentType);
        return decompressedContent;
    }

    public static void main(String[] args) throws DataFormatException {
        final String command = args[0];

        switch (command) {
            case "init" -> {
                final File root = new File(".git");
                new File(root, "objects").mkdirs();
                new File(root, "refs").mkdirs();
                final File head = new File(root, "HEAD");

                try {
                    head.createNewFile();
                    Files.write(head.toPath(), "ref: refs/heads/main\n".getBytes());
                    System.out.println("Initialized git directory");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            case "add" -> {
                File file = new File(args[1]);
                System.out.printf("the file is %s\n", file);
                try (FileInputStream fileInputStream = new FileInputStream(file);
                     ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

                    byte[] byteArray = Files.readAllBytes(Path.of(file.getPath()));
                    String encodedFile = SHA1Hasher.encode(byteArray);
                    System.out.printf("Encoded file: %s\n", encodedFile);
                    Path newFilePath = Path.of("../../../.git/objects/".concat(encodedFile.substring(0, 2)));
                    if (!Files.exists(newFilePath)) {
                        Path newDir = Files.createDirectory(newFilePath);
                        Files.createFile(Path.of(newDir + "/" + encodedFile.substring(2)));
                    }


                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            case "cat-file" -> {
                try {
                    String fileName = args[1];
                    Path filePath = Path.of(System.getProperty("user.dir") + "\\" + fileName);
                    System.out.printf("file path: %s\n", filePath);
                    String content = readBlob(filePath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            default -> System.out.println("Unknown command: " + command);
        }
    }
}

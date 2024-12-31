import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.nio.file.Files;


public class Main {
    public static void main(String[] args) {
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

            case "cat-file" -> {
                File file = new File(args[1]);
                System.out.printf("the file is %s\n", file);
                try (FileInputStream fileInputStream = new FileInputStream(file);
                     ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

                    byte[] buffer = new byte[1024];

                    for (int len; (len = fileInputStream.read(buffer)) != -1; ) {
                        byteArrayOutputStream.write(buffer, 0, len);
                    }
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    String encodedFile = SHA256Hasher.encode(byteArray);
                    System.out.printf("Encoded file: %s", encodedFile);
                    Path newDir = Files.createDirectory(Path.of("../../../.git/objects/".concat(encodedFile.substring(0, 2))));
                    System.out.println(newDir);
                    Files.createFile(Path.of(newDir + "/" + encodedFile.substring(2)));

                }  catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            default -> System.out.println("Unknown command: " + command);
        }
    }
}

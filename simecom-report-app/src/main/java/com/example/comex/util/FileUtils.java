package com.example.comex.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FileUtils {
    private FileUtils() {
    }

    public static Path ensureDirectory(String directory) throws IOException {
        Path path = Path.of(directory);
        Files.createDirectories(path);
        return path;
    }
}

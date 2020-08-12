package utils.files;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class ResourceSynchronizer extends SimpleFileVisitor<Path> implements Runnable {
    private static final Logger logger = LogManager.getLogger(ResourceSynchronizer.class);
    private static ResourceSynchronizer instance;
    private Path sourceFolder;
    private Path targetFolder;

    private ResourceSynchronizer() {
    }

    public static void synchronize(Path sourceFolder, Path targetFolder) {
        if (instance == null) {
            instance = new ResourceSynchronizer();
        }

        instance.sourceFolder = sourceFolder;
        instance.targetFolder = targetFolder;
        Thread thread = new Thread(instance);
        thread.setName("File Synchronizer Thread");
        thread.setDaemon(false);
        thread.start();
    }

    @Override
    public void run() {
        try {
            logger.trace("Запущена синхронизация файлов {} -> {}", sourceFolder, targetFolder);
            Files.walkFileTree(sourceFolder, this);
            logger.trace("Завершена синхронизация файлов {} -> {}", sourceFolder, targetFolder);
        } catch (IOException e) {
            logger.error("Ошибка {} синхронизации файлов {} -> {}", e.getMessage(), sourceFolder, targetFolder, e);
        }
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        Path targetFolderPath = targetFolder.resolve(dir.relativize(sourceFolder));
        if (!Files.exists(targetFolderPath)) {
            Files.createDirectory(targetFolderPath);
            logger.trace("Создана папка {}", targetFolderPath);
        }

        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        Path targetFilePath = targetFolder.resolve(sourceFolder.relativize(file));
        if (!Files.exists(targetFilePath) || attrs.size() != targetFilePath.toFile().length()) {
            Files.copy(file, targetFilePath, StandardCopyOption.REPLACE_EXISTING);
            logger.trace("Скопирован файл {} -> {}", file, targetFilePath);
        }

        return FileVisitResult.CONTINUE;
    }
}

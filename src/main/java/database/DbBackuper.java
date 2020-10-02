package database;

import core.App;
import files.Folders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ui_windows.options_window.user_editor.Users;
import utils.Archiver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class DbBackuper extends DbRequest {
    private static final int BACKUP_COPIES = 30;
    private static final Logger logger = LogManager.getLogger(DbBackuper.class);

    public DbBackuper() throws Exception {
    }

    public static void run() throws Exception {
        final Path cashedDbFile = Folders.getInstance().getCalcedCashedDbFileName();
        logger.debug("start copy db to {}", cashedDbFile);
        try {
            Files.copy(Folders.getInstance().getMainDbFile(), cashedDbFile, REPLACE_EXISTING);
        } catch (IOException e) {
            logger.error("db backup copy process error: {}", e.getMessage(), e);
            throw new RuntimeException(String.format("ошибка копирования файла DB %s -> %s",
                    Folders.getInstance().getMainDbFile(), cashedDbFile));
        }
        logger.debug("db copy process is finished");

        Thread backupThread = new Thread(() -> {
            try {
                String currDateTime = new SimpleDateFormat("yyyy.MM.dd_HH-mm-ss").format(new Date());
                Path localDbZipFile = Folders.getInstance().getTempFolder().resolve(
                        App.getProperties().getDbFileName() + "_backup_" + currDateTime + "_" +
                                Users.getInstance().getCurrentUser().getSurname() + ".zip");

                Path remoteDbZipFile = Folders.getInstance().getDbBackupFolder().resolve(localDbZipFile.getFileName());

                Archiver.addToArchive(Collections.singletonList(cashedDbFile.toFile()), localDbZipFile.toFile());
                logger.debug("db file {} was added to archive {}", cashedDbFile, localDbZipFile);
                if (Files.exists(remoteDbZipFile.getParent())) {
                    Files.copy(localDbZipFile, remoteDbZipFile);
                    logger.debug("local db backup archive {} was copied to {}", localDbZipFile, remoteDbZipFile);

                    Files.walk(Folders.getInstance().getDbBackupFolder(), 1)
                            .filter(path -> path.toString().endsWith(".zip"))
                            .sorted((p1, p2) -> Long.compare(p2.toFile().lastModified(), p1.toFile().lastModified()))
//                        .peek(path -> logger.trace("сортировка бэкапов {}", path))
                            .skip(BACKUP_COPIES)
                            .peek(path -> logger.trace("удаление старого файла бэкапа {}", path))
                            .forEach(path -> {
                                try {
                                    Files.deleteIfExists(path);
                                } catch (IOException e) {
                                    logger.warn("ошибка удаления {} {}", path, e.getMessage());
                                }
                            });
                }
            } catch (Exception e) {
                logger.error("error db backup {}", e.getMessage(), e);
            }
        });
        backupThread.setName("DB Backup Thread");
        backupThread.start();
    }
}

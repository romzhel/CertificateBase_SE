package database;

import core.App;
import files.Folders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ui_windows.options_window.user_editor.Users;
import utils.Archiver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TreeSet;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class DbBackuper extends DbRequest {
    private static final int BACKUP_COPIES = 30;
    private static final Logger logger = LogManager.getLogger(DbBackuper.class);

    public DbBackuper() {
        try {
//            Statement backupStatement = connection.createStatement();
            final File cashedDbFile = Folders.getInstance().getCashedDbFile();

            if (!Folders.getInstance().getTempFolder().exists()) {
                Folders.getInstance().getTempFolder().mkdir();
            }

            if (!Folders.getInstance().getDbBackupFolder().exists()) {
                Folders.getInstance().getDbBackupFolder().mkdir();
            }

            /*logger.debug("start backup db to {}", cashedDbFile);
            backupStatement.executeUpdate("backup to '" + cashedDbFile + "';");
            logger.debug("backup process is finished");*/

            logger.debug("start copy db to {}", cashedDbFile);
            try {
                Files.copy(DataBase.getInstance().getDbFile().toPath(), cashedDbFile.toPath(), REPLACE_EXISTING);
            } catch (IOException e) {
                logger.error("db backup copy process error: {}", e.getMessage());
            }
            logger.debug("copy process is finished");

            new Thread(() -> {
                try {
                    String currDateTime = new SimpleDateFormat("yyyy.MM.dd_HH-mm-ss").format(new Date());
                    File localDbZipFile = new File(Folders.getInstance().getTempFolder().getPath() + "\\" +
                            App.getProperties().getDbFileName() + "_backup_" + currDateTime + "_" +
                            Users.getInstance().getCurrentUser().getSurname() + ".zip");

                    File remoteDbZipFile = new File(Folders.getInstance().getDbBackupFolder() + "\\" + localDbZipFile.getName());

                    Archiver.addToArchive(cashedDbFile, localDbZipFile);
                    logger.debug("db file {} was added to archive {}", cashedDbFile, localDbZipFile);
                    Files.copy(localDbZipFile.toPath(), remoteDbZipFile.toPath());
                    logger.debug("local db backup archive {} was copied to {}", localDbZipFile, remoteDbZipFile);

                    File[] filesList = remoteDbZipFile.getParentFile().listFiles(pathname -> pathname.getName().endsWith(".zip"));
                    if (filesList.length > BACKUP_COPIES) {

                        TreeSet<File> files = new TreeSet<>((o1, o2) -> (int) (o1.lastModified() - o2.lastModified()));
                        files.addAll(Arrays.asList(filesList));

                        File forDeleteFile = files.first();
                        logger.debug("old backup " + forDeleteFile.getPath() + " will be deleted");
                        forDeleteFile.delete();
                    }
                } catch (Exception ee) {
                    logger.warn("error db backuping {}", ee.getMessage());
                }
            }).start();

        } catch (Exception e) {
            logger.warn("backup error: {}", e.getMessage(), e);
        }
    }
}

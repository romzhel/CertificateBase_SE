package database;

import core.CoreModule;
import utils.Archiver;
import utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class DbBackuper extends DbRequest {
    private static final int BACKUP_COPIES = 30;

    public DbBackuper() {
        try {
            Statement backupStatement = connection.createStatement();
            final File dbLocalBackupFile = new File(CoreModule.getFolders().getTempFolder().getPath() + "\\certificateDB_backup.db");

            if (!CoreModule.getFolders().getTempFolder().exists()) {
                CoreModule.getFolders().getTempFolder().mkdir();
            }

            if (!CoreModule.getFolders().getDbBackupFolder().exists()) {
                CoreModule.getFolders().getDbBackupFolder().mkdir();
            }
            backupStatement.executeUpdate("backup to '" + dbLocalBackupFile.getPath() + "'");

            new Thread(() -> {
                try {
//                    Files.copy(CoreModule.getDataBase().getDataBaseFile().toPath(), dbLocalBackupFile.toPath(), REPLACE_EXISTING);

                    String currDateTime = new SimpleDateFormat("dd.MM.yyyy_HH.mm.ss").format(new Date());
                    File localDbZipFile = new File(dbLocalBackupFile.getParent() + "\\certificateDB_backup_" +
                            currDateTime + "_" + CoreModule.getUsers().getCurrentUser().getSurname() + ".zip");

                    File remoteDbZipFile = new File(CoreModule.getFolders().getDbBackupFolder() + "\\" + localDbZipFile.getName());

                    new Archiver().addToArchive(dbLocalBackupFile, localDbZipFile);
                    Files.copy(localDbZipFile.toPath(), remoteDbZipFile.toPath());

                    File[] filesList = remoteDbZipFile.getParentFile().listFiles(pathname -> pathname.getName().endsWith(".zip"));
                    if (filesList.length > BACKUP_COPIES) {
                        System.out.println("backup " + filesList[0].getPath() + " will be deleted");
                        filesList[0].delete();
                    }
                } catch (Exception ee) {
                    System.out.println(ee.getMessage());
                }
            }).start();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}

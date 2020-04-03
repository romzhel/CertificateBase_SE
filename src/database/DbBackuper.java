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
            System.out.printf("backuper %s create %s", this, Utils.printTime());
            Statement backupStatement = connection.createStatement();
            final File dbLocalBackupFile = new File(CoreModule.getFolders().getTempFolder().getPath() + "\\certificateDB_backup.db");
//            final File dbZipRemoteBackupFile = new File(CoreModule.getFolders().getDbBackupFolder().getPath() + "\\certificateDB_backup.db");

            if (!CoreModule.getFolders().getTempFolder().exists()) {
                CoreModule.getFolders().getTempFolder().mkdir();
            }

            if (!CoreModule.getFolders().getDbBackupFolder().exists()) {
                CoreModule.getFolders().getDbBackupFolder().mkdir();
            }
            System.out.printf("backuper %s before backup to local %s", this, Utils.printTime());
            backupStatement.executeUpdate("backup to '" + dbLocalBackupFile.getPath() + "'");
            System.out.printf("backuper %s after backup to local %s", this, Utils.printTime());

            new Thread(() -> {
                try {
//                    Files.copy(CoreModule.getDataBase().getDataBaseFile().toPath(), dbLocalBackupFile.toPath(), REPLACE_EXISTING);

                    String currDateTime = new SimpleDateFormat("dd.MM.yyyy_HH.mm.ss").format(new Date());
                    File localDbZipFile = new File(dbLocalBackupFile.getParent() + "\\certificateDB_backup_" +
                            currDateTime + "_" + CoreModule.getUsers().getCurrentUser().getSurname() + ".zip");

                    File remoteDbZipFile = new File(CoreModule.getFolders().getDbBackupFolder() + "\\" + localDbZipFile.getName());

                    System.out.printf("backuper %s before local archiving %s", this, Utils.printTime());
                    new Archiver().addToArchive(dbLocalBackupFile, localDbZipFile);
                    System.out.printf("backuper %s after local archiving %s", this, Utils.printTime());

//                    CountDownLatch latcher = new CountDownLatch(1);
//                    latcher.await(2, TimeUnit.SECONDS);

                    System.out.printf("backuper %s before copy archive to remote %s", this, Utils.printTime());
                    Files.copy(localDbZipFile.toPath(), remoteDbZipFile.toPath());
                    System.out.printf("backuper %s after copy archive to remote %s", this, Utils.printTime());

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

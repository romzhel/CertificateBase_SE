package database;

import core.CoreModule;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class DbBackuper extends DbRequest {
    private static final int BACKUP_COPIES = 30;

    public DbBackuper() {
        try {
            Statement backupStatement = connection.createStatement();
            final File dbBackupFile = new File(CoreModule.getFolders().getDbBackupFolder().getPath() + "\\certificateDB_backup.db");
            if (!dbBackupFile.getParentFile().exists()) {
                dbBackupFile.getParentFile().mkdir();
            }
            backupStatement.executeUpdate("backup to '" + dbBackupFile.getPath() + "'");

            new Thread(() -> {
                FileOutputStream fos = null;
                ZipOutputStream zipOs = null;
                FileInputStream fis = null;
                try {
                    String currDateTime = new SimpleDateFormat("dd.MM.yyyy_HH.mm.ss").format(new Date());
                    File targetZipFile = new File(dbBackupFile.getParent() + "\\certificateDB_backup_" +
                            currDateTime + "_" + CoreModule.getUsers().getCurrentUser().getSurname() + ".zip");

                    byte[] buffer = new byte[1024];
                    fos = new FileOutputStream(targetZipFile);
                    zipOs = new ZipOutputStream(fos);
                    fis = new FileInputStream(dbBackupFile);

                    ZipEntry ze = new ZipEntry("certificateDB.db");
                    zipOs.putNextEntry(ze);

                    int len;
                    while ((len = fis.read(buffer)) > 0) {
                        zipOs.write(buffer, 0, len);
                    }

                    File[] filesList = dbBackupFile.getParentFile().listFiles(pathname -> pathname.getName().endsWith(".zip"));
                    if (filesList.length > BACKUP_COPIES) {
                        System.out.println("backup " + filesList[0].getPath() + " will be deleted");
                        filesList[0].delete();
                    }
                } catch (IOException ee) {
                    System.out.println(ee.getMessage());
                } finally {
                    try {
                        fis.close();
                        zipOs.close();
                        fos.close();
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }).start();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}

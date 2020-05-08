package database;

import files.Folders;
import ui_windows.options_window.user_editor.Users;
import utils.Archiver;

import java.io.File;
import java.nio.file.Files;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DbBackuper extends DbRequest {
    private static final int BACKUP_COPIES = 30;

    public DbBackuper() {
        try {
            Statement backupStatement = connection.createStatement();
            final File dbLocalBackupFile = new File(Folders.getInstance().getTempFolder().getPath() + "\\certificateDB_backup.db");

            if (!Folders.getInstance().getTempFolder().exists()) {
                Folders.getInstance().getTempFolder().mkdir();
            }

            if (!Folders.getInstance().getDbBackupFolder().exists()) {
                Folders.getInstance().getDbBackupFolder().mkdir();
            }
            backupStatement.executeUpdate("backup to '" + dbLocalBackupFile.getPath() + "'");

            new Thread(() -> {
                try {
//                    Files.copy(CoreModule.getDataBase().getDataBaseFile().toPath(), dbLocalBackupFile.toPath(), REPLACE_EXISTING);

                    String currDateTime = new SimpleDateFormat("dd.MM.yyyy_HH.mm.ss").format(new Date());
                    File localDbZipFile = new File(dbLocalBackupFile.getParent() + "\\certificateDB_backup_" +
                            currDateTime + "_" + Users.getInstance().getCurrentUser().getSurname() + ".zip");

                    File remoteDbZipFile = new File(Folders.getInstance().getDbBackupFolder() + "\\" + localDbZipFile.getName());

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

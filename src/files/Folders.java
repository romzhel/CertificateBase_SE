package files;

import core.Dialogs;
import ui_windows.main_window.MainWindow;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class Folders {
    private File dbFile;
    private File certFolder;
    private File manualsFolder;
    private File tempFolder;
    private PathFile pathFile;
    private String appFolder;
    private File dbBackupFolder;

    public Folders() {
        appFolder = System.getProperty("user.dir");
        File remoteBaseFile = new File("S:\\SBT\\FS\\Base\\certificateDB.db");
        File localBaseFile = new File(appFolder + "\\certificateDB.db");

        if (remoteBaseFile.exists()) {
            dbFile = remoteBaseFile;
        } else if (!remoteBaseFile.exists() && localBaseFile.exists()) {
            dbFile = localBaseFile;

            Dialogs.showMessage("Подключение к базе данных", "Был найден локальный файл базы данных. " +
                    "Обратите внимание, что все изменения будут сохраняться в этом файле.");
        } else if (!remoteBaseFile.exists() && !localBaseFile.exists()) {
            //check path file
            pathFile = new PathFile();
            dbFile = pathFile.getDBFile();

            if (dbFile == null) {
                Dialogs.showMessage("Подключение к базе данных", "Файл базы данных не был найден. Как это " +
                        "сообщение будет закрыто откроется диалог открытия файла. Местонахождение файла:\n\n" +
                        "SBT.RU:\\SBT\\FS\\Base\\");
                dbFile = Dialogs.selectDBFile(MainWindow.getMainStage());

                //create new path file
                if (dbFile != null && dbFile.exists()) {//save
                    pathFile.createDBFile(dbFile);
                }
            }
        }

        if (dbFile != null && dbFile.exists()) {
            certFolder = new File(dbFile.getParent() + "\\_certs");
            manualsFolder = new File(dbFile.getParent() + "\\_manuals");
            dbBackupFolder = new File(dbFile.getParent() + "\\_db_backups");
//            tempFolder = new File(System.getProperty("user.dir") + "\\_temp");

            try {
                File tempFile = File.createTempFile("temp-file", ".tmp");
                tempFolder = new File(tempFile.getParent() + "\\" + "CertificateBase");
                tempFile.delete();
                tempFolder.mkdir();
            } catch (IOException ioe) {
                System.out.println("error temp folder creating " + ioe.getMessage());
            }
        }
    }

    public File getDbFile() {
        return dbFile;
    }

    public File getCertFolder() {
        return certFolder;
    }

    public File getManualsFolder() {
        return manualsFolder;
    }

    public File getTempFolder() {
        return tempFolder;
    }

    public String getAppFolder() {
        return appFolder;
    }

    public File getDbBackupFolder() {
        return dbBackupFolder;
    }
}

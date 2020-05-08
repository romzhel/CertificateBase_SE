package files;

import core.Dialogs;
import ui_windows.main_window.MainWindow;

import java.io.File;
import java.io.IOException;

public class Folders {
    private static Folders instance;
    private final String dbFolder = "\\\\rumowmc0022dat\\SBT.RU\\SBT\\FS\\Base";
    private File dbFile;
    private File certFolder;
    private File manualsFolder;
    private File tempFolder;
    private File templatesFolder;
    private PathFile pathFile;
    private String appFolder;
    private File dbBackupFolder;

    private Folders() {
    }

    public static Folders getInstance() {
        if (instance == null) {
            instance = new Folders();
        }
        return instance;
    }

    public void init() throws RuntimeException {
        appFolder = System.getProperty("user.dir");
        File remoteBaseFile = new File(dbFolder + "\\certificateDB.db");
        File localBaseFile = new File(appFolder + "\\certificateDB.db");

        if (remoteBaseFile.exists()) {
            dbFile = remoteBaseFile;
        } else if (localBaseFile.exists()) {
            dbFile = localBaseFile;

            Dialogs.showMessage("Подключение к базе данных", "Был найден локальный файл базы данных. " +
                    "Обратите внимание, что все изменения будут сохраняться в этом файле.");
        } else if (!localBaseFile.exists()) {
            //check path file
            pathFile = new PathFile();
            dbFile = pathFile.getDBFile();

            if (dbFile == null) {
                Dialogs.showMessage("Подключение к базе данных", "Файл базы данных не был найден. Как это " +
                        "сообщение будет закрыто откроется диалог открытия файла. Местонахождение файла:\n\n" + dbFolder);
                dbFile = Dialogs.selectDBFile(MainWindow.getMainStage());

                //create new path file
                if (dbFile != null && dbFile.exists()) {//save
                    pathFile.createDBFile(dbFile);
                } else {
                    throw new RuntimeException("Файл базы данных не был найден.");
                }
            }
        }

        if (dbFile != null && dbFile.exists()) {
            certFolder = new File(dbFile.getParent() + "\\_certs");
            manualsFolder = new File(dbFile.getParent() + "\\_manuals");
            dbBackupFolder = new File(dbFile.getParent() + "\\_db_backups");
            templatesFolder = new File(dbFile.getParent() + "\\_templates");
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

    public File getTemplatesFolder() {
        return templatesFolder;
    }
}

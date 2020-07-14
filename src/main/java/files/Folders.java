package files;

import core.App;
import core.Dialogs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ui_windows.main_window.MainWindow;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TreeSet;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class Folders {
    private static final Logger logger = LogManager.getLogger(Folders.class);
    private static Folders instance;
    public static final String APP_FOLDER = System.getProperty("user.home") + "\\AppData\\Roaming\\CertificateBase\\";
    private String currentFolder;
    private File tempFolder;
    private File mainDbFile;
    private File cashedDbFile;
    private File certFolder;
    private File manualsFolder;
    private File templatesFolder;
    private File dbBackupFolder;
    private File appLogsFolder;
    private File remoteLogsFolder;

    private Folders() {
    }

    public static Folders getInstance() {
        if (instance == null) {
            instance = new Folders();
        }
        return instance;
    }

    public void init() throws Exception {
        File oldDbFile = new File(APP_FOLDER + App.getProperties().getDbFileName());
        if (oldDbFile.exists()) {
            oldDbFile.delete();
        }

        cashedDbFile = new File(APP_FOLDER + new SimpleDateFormat("yyyy.MM.dd_HH-mm-ss-SSS_")
                .format(new Date()) + App.getProperties().getDbFileName());
        if (!cashedDbFile.getParentFile().exists()) {
            cashedDbFile.getParentFile().mkdir();
        }

        File remoteDbFile = new File(App.getProperties().getRemoteDbFolder() + App.getProperties().getDbFileName());
        if (remoteDbFile.exists()) {
            logger.debug("Remote db file {} is found", remoteDbFile);
            mainDbFile = remoteDbFile;

            copyCashDbFile();
            initFolders();
            return;
        }

        File currentFolderDbFile = new File(System.getProperty("user.dir") + "\\" + App.getProperties().getDbFileName());
        if (currentFolderDbFile.exists()) {
            logger.debug("Current folder db file {} is found", currentFolderDbFile);
            mainDbFile = currentFolderDbFile;
            copyCashDbFile();
            initFolders();

            Dialogs.showMessageTS("Подключение к базе данных", "Был найден локальный файл базы данных.\n\n" +
                    "Обратите внимание, что все изменения будут сохраняться только в этом файле и никто больше их не увидит.\n\n");
            return;
        }

        File[] dBfilesList = new File(Folders.APP_FOLDER).listFiles(pathname -> pathname.getName().endsWith(".db"));
        TreeSet<File> files = new TreeSet<>((o1, o2) -> o2.getName().compareTo(o1.getName()));
        files.addAll(Arrays.asList(dBfilesList));

        if (files.size() > 0 && files.first().exists()) {
            cashedDbFile = mainDbFile = files.first();
            Dialogs.showMessageTS("Подключение к базе данных", "Была найдена локальная копия файла базы данных " +
                    "с предыдующих сессий.\n\n" +
                    "Обратите внимание, такой режим не поддерживает изменение данных.");
            return;
        }

        mainDbFile = Dialogs.selectDBFile(MainWindow.getMainStage());
        if (mainDbFile != null) {
            logger.debug("Custom db file {} was selected", mainDbFile);
            copyCashDbFile();
            initFolders();
            return;
        }

        logger.fatal("No db file was found");
        throw new RuntimeException("Файл базы данных не был найден.");
    }

    private void copyCashDbFile() throws Exception {
        try {
            logger.debug("Start copy cashing file {} to {}", mainDbFile, cashedDbFile);
            Files.copy(mainDbFile.toPath(), cashedDbFile.toPath(), REPLACE_EXISTING);
            logger.debug("Copy process finished");
        } catch (Exception e) {
            logger.error("Cashed db file copying error: {}", e.getMessage());
            if (!cashedDbFile.exists()) {
                throw new RuntimeException("Cashed db file is absent or corrupted");
            }
        }
    }

    private void initFolders() throws Exception {
        logger.debug("Initializing folders");
        certFolder = new File(mainDbFile.getParent() + "\\_certs");
        manualsFolder = new File(mainDbFile.getParent() + "\\_manuals");
        dbBackupFolder = new File(mainDbFile.getParent() + "\\_db_backups");
        templatesFolder = new File(mainDbFile.getParent() + "\\_templates");
        appLogsFolder = new File(APP_FOLDER + "logs");
        remoteLogsFolder = new File(App.getProperties().getRemoteDbFolder() + "_logs");
//        remoteLogsFolder = new File(mainDbFile.getParent()  + "\\_logs");
        if (!appLogsFolder.exists()) {
            appLogsFolder.mkdir();
        }
        if (!remoteLogsFolder.exists()) {
            remoteLogsFolder.mkdir();
        }

        try {
            File tempFile = File.createTempFile("temp-file", ".tmp");
            tempFolder = new File(tempFile.getParent() + "\\CertificateBase");
            tempFile.delete();
            tempFolder.mkdir();
        } catch (IOException ioe) {
            logger.error("Can't create temp folder: {}", ioe.getMessage());
        }
    }

    public File getMainDbFile() {
        return mainDbFile;
    }

    public File getCashedDbFile() {
        return cashedDbFile;
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

    public File getDbBackupFolder() {
        return dbBackupFolder;
    }

    public File getTemplatesFolder() {
        return templatesFolder;
    }

    public File getAppLogsFolder() {
        return appLogsFolder;
    }

    public File getRemoteLogsFolder() {
        return remoteLogsFolder;
    }
}

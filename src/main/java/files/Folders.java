package files;

import core.App;
import core.Initializable;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ui.Dialogs;
import utils.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Data
public class Folders implements Initializable {
    public static final Path APP_FOLDER = Paths.get(System.getProperty("user.home"), "AppData", "Roaming", "CertificateBase");
    private static final Logger logger = LogManager.getLogger(Folders.class);
    private static Folders instance;
    private final Path cashedCertFolder = APP_FOLDER.resolve("_certs");
    private Path tempFolder;
    private Path mainDbFile;
    private Path cashedDbFile;
    private Path certFolder;
    private Path manualsFolder;
    private Path templatesFolder;
    private Path dbBackupFolder;
    private Path appLogsFolder;
    private Path remoteLogsFolder;

    private Folders() {
    }

    public static Folders getInstance() {
        if (instance == null) {
            instance = new Folders();
        }
        return instance;
    }

    @Override
    public void init() throws Exception {
        if (Files.notExists(APP_FOLDER)) {
            Files.createDirectory(APP_FOLDER);
        }

        Path oldDbFile = APP_FOLDER.resolve(App.getProperties().getDbFileName());
        Files.deleteIfExists(oldDbFile);

        cashedDbFile = getCalcedCashedDbFileName();

        Path remoteDbFile = Paths.get(App.getProperties().getRemoteDbFolder(), App.getProperties().getDbFileName());
        logger.debug("remote db file name = {}", remoteDbFile);
        mainDbFile = remoteDbFile;

        if (Files.exists(remoteDbFile)) {
            logger.debug("Remote db file {} is found", remoteDbFile);

            copyCashDbFile();
            initFolders();
            return;
        }

        Path currentFolderDbFile = Paths.get(System.getProperty("user.dir"), App.getProperties().getDbFileName());
        if (Files.exists(currentFolderDbFile)) {
            logger.debug("Current folder db file {} is found", currentFolderDbFile);
            mainDbFile = currentFolderDbFile;
            copyCashDbFile();
            initFolders();

            Dialogs.showMessageTS("Подключение к базе данных", "Был найден локальный файл базы данных.\n\n" +
                    "Обратите внимание, что все изменения будут сохраняться только в этом файле и никто больше их не увидит.\n\n");
            return;
        }

        List<Path> cashedDbFiles = Files.walk(APP_FOLDER, 1)
                .filter(path -> Files.isRegularFile(path) && path.getFileName().toString().endsWith(".db"))
                .peek(path -> logger.debug("кэшированная ДБ {}", path))
                .sorted((o1, o2) -> o2.getFileName().toString().compareTo(o1.getFileName().toString()))
                .collect(Collectors.toList());

        if (cashedDbFiles.size() > 0) {
            mainDbFile = cashedDbFile = cashedDbFiles.get(0);
            Dialogs.showMessageTS("Подключение к базе данных", "Не удаётся подключиться к базе данных в " +
                    "сети. Но была найдена кэшированная локальная копия файла базы данных от предыдующей сессии.\n\n" +
                    "Обратите внимание, такой режим подключения к базе поддерживает только просмотр данных.\n\n");
            initFolders();
            return;
        }

        /*mainDbFile = Dialogs.selectDBFile(MainWindow.getMainStage());
        if (mainDbFile != null) {
            logger.debug("Custom db file {} was selected", mainDbFile);
            copyCashDbFile();
            initFolders();
            return;
        }*/

        logger.fatal("No db file was found");
        throw new RuntimeException("Файл базы данных не был найден.");
    }

    public Path getCalcedCashedDbFileName() throws Exception {
        return APP_FOLDER.resolve(new SimpleDateFormat("yyyy.MM.dd_HH-mm-ss-SSS_")
                .format(new Date()) + App.getProperties().getDbFileName());
    }

    private void copyCashDbFile() throws Exception {
        try {
            logger.debug("Start copy cashing DB file {} to {}", mainDbFile, cashedDbFile);
            Files.copy(mainDbFile, cashedDbFile, REPLACE_EXISTING);
            logger.debug("Copy process finished");
        } catch (Exception e) {
            logger.error("Cashed db file copying error: {}", e.getMessage());
            if (Files.notExists(cashedDbFile)) {
                throw new RuntimeException("Cashed db file is absent or corrupted");
            }
        }
    }

    private void initFolders() throws Exception {
        logger.debug("Initializing folders");
        certFolder = mainDbFile.getParent().resolve("_certs");
        logger.debug("certFolder = {}", certFolder);
        manualsFolder = Paths.get(App.getProperties().getRemoteDbFolder(), "_manuals");
        logger.debug("manualsFolder = {}", manualsFolder);
        dbBackupFolder = Paths.get(App.getProperties().getRemoteDbFolder(), "_db_backups");
        logger.debug("dbBackupFolder = {}", dbBackupFolder);
        templatesFolder = Paths.get(App.getProperties().getRemoteDbFolder(), "_templates");
        logger.debug("templatesFolder = {}", templatesFolder);
        remoteLogsFolder = Paths.get(App.getProperties().getRemoteDbFolder(), "_logs");
        logger.debug("remoteLogsFolder = {}", remoteLogsFolder);

        initLocalFolders();

        //TODO может быть ошибка при отсутствии доступа к диску
        Arrays.stream(new Path[]{certFolder, manualsFolder, dbBackupFolder, templatesFolder, remoteLogsFolder,
                        appLogsFolder, tempFolder})
                .filter(Files::notExists)
                .peek(path -> logger.debug("папка {} отсутствует", path))
                .forEach(path -> {
                    try {
                        Files.createDirectory(path);
                        logger.trace("папка {} была создана", path);
                    } catch (IOException e) {
                        logger.error("ошибка создания папки {} - {}", path, e.getMessage());
                    }
                });
    }

    private void initLocalFolders() {
        appLogsFolder = APP_FOLDER.resolve("logs");
        logger.debug("appLogsFolder = {}", appLogsFolder);

        try {
            File tempFile = File.createTempFile("temp-file", ".tmp");
            tempFolder = tempFile.toPath().getParent().resolve("CertificateBase");
            logger.debug("tempFolder = {}", tempFolder);
            tempFile.delete();
        } catch (IOException ioe) {
            logger.error("Can't calc temp folder: {}", ioe.getMessage());
        }
    }

    public Path getCalcCertFile(String fileName) throws Exception {
        return Utils.getFileFromMultiLocation(Paths.get(fileName), cashedCertFolder, certFolder);
    }

    public Path getCalcCertFile(Path fileName) throws Exception {
        return Utils.getFileFromMultiLocation(fileName, cashedCertFolder, certFolder);
    }
}

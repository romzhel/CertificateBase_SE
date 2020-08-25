package core.logger;

import files.Folders;
import utils.Archiver;

import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

public class LogsBackuper {
    private static LogsBackuper instance = null;

    private LogsBackuper() {
    }

    public static LogsBackuper getInstance() {
        if (instance == null) {
            instance = new LogsBackuper();
        }
        return instance;
    }

    public void backup() {
        String currDateTime;
        File localLogZipFile;

        File[] logsList = Folders.getInstance().getAppLogsFolder().toFile().listFiles(file -> file.getName().endsWith(".log"));
        for (File logFile : logsList) {
            try {
                currDateTime = new SimpleDateFormat("yyyy.MM.dd_HH-mm-ss").format(logFile.lastModified());
                localLogZipFile = new File(String.format("%s\\%s_%s.zip", Folders.getInstance().getAppLogsFolder(),
                        System.getProperty("user.name"), currDateTime));

                Archiver.addToArchive(Collections.singletonList(logFile), localLogZipFile);
                if (localLogZipFile.exists()) {
                    Files.deleteIfExists(logFile.toPath());
                }

            } catch (Exception e) {
                System.out.printf("%s %s", new SimpleDateFormat("HH-mm-ss.SSS").format(new Date()), e.getMessage());
            }
        }

        File[] archivesList = Folders.getInstance().getAppLogsFolder().toFile().listFiles(file -> file.getName().endsWith(".zip"));
        for (File archiveFile : archivesList) {
            try {
                File remoteLogZipFile = new File(Folders.getInstance().getRemoteLogsFolder() + "\\" + archiveFile.getName());
                Files.copy(archiveFile.toPath(), remoteLogZipFile.toPath());
                if (remoteLogZipFile.exists()) {
                    Files.deleteIfExists(archiveFile.toPath());
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}

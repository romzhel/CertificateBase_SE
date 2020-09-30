package core.logger;

import files.Folders;
import utils.Archiver;

import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        Pattern datePattern = Pattern.compile("\\d.*\\d");
        Matcher matcher;

        File[] logsList = Folders.getInstance().getAppLogsFolder().toFile().listFiles(file -> file.getName().endsWith(".log"));
        for (File logFile : logsList) {
            try {
                matcher = datePattern.matcher(logFile.getName());
                currDateTime = matcher.find() ?
                        logFile.getName().substring(matcher.start(), matcher.end()) :
                        new SimpleDateFormat("yyyy.MM.dd_HH-mm_ss").format(logFile.lastModified());

                localLogZipFile = new File(String.format("%s\\%s_%s%s.zip",
                        Folders.getInstance().getAppLogsFolder(),
                        System.getProperty("user.name"),
                        currDateTime,
                        logFile.getName().contains("error") ? "_error" : ""));

                Archiver.addToArchive(Collections.singletonList(logFile), localLogZipFile);
                if (localLogZipFile.exists()) {
                    Files.deleteIfExists(logFile.toPath());
                }

            } catch (Exception e) {
                System.out.printf("%s %s", new SimpleDateFormat("HH-mm-ss.SSS").format(new Date()), e.getMessage());
            }
        }

        if (Files.exists(Folders.getInstance().getRemoteLogsFolder())) {
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
}

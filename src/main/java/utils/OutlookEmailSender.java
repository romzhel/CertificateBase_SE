package utils;

import java.io.File;
import java.io.IOException;

public class OutlookEmailSender {
    final String OUTLOOK_PATH = "C:\\Program Files (x86)\\Microsoft Office\\root\\Office16\\OUTLOOK.exe";
    final String[] NEW_EMAIL = {"/c", "ipm.note"};
    final String RECIPIENTS = "/m";
    final String SUBJECT = "&subject=";
    final String MESSAGE = "&body=";
    final String ATTACH_FILE = "/a";
    private String recipients;
    private String subject;
    private String message;
    private File attachedFile;

    public OutlookEmailSender addRecipients(String recipients) {
        return this;
    }

    public void attachFile(File file) {
        this.attachedFile = file;
    }

    public void send() {
        try {
            new ProcessBuilder(OUTLOOK_PATH,
                    NEW_EMAIL[0], NEW_EMAIL[1],
                    RECIPIENTS, "romzhel@mail.ru;romzhel2@mail.ru" +
                    SUBJECT + "Тема нового сообщения".replaceAll("\\s", "%20") +
                    MESSAGE + "Уважаемые коллеги".replaceAll("\\s", "%20")
            ).start();


//            new ProcessBuilder(OUTLOOK_PATH, ADD_FILE, "C:\\Recovery.txt").start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

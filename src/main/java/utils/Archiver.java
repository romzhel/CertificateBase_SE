package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Archiver {
//    private static final Logger logger = LogManager.getLogger(Archiver.class);

    public static File addToArchive(List<File> sourceFiles, File archiveFile) throws Exception {
//        logger.trace("архивирование файлов ({}) в {}", sourceFiles.size(), archiveFile);
        FileOutputStream fos = null;
        ZipOutputStream zipOs = null;
        FileInputStream fis = null;

        try {
            byte[] buffer = new byte[1024];
            fos = new FileOutputStream(archiveFile);
            zipOs = new ZipOutputStream(fos);

            for (File insertingFile : sourceFiles) {
                fis = new FileInputStream(insertingFile);
                ZipEntry ze = new ZipEntry(insertingFile.getName());
                zipOs.putNextEntry(ze);

                int len;
                while ((len = fis.read(buffer)) > 0) {
                    zipOs.write(buffer, 0, len);
                }

                try {
                    fis.close();
                } catch (Exception e) {
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Ошибка архивирования " + e.getMessage(), e);
        } finally {
            try {
                zipOs.close();
                fos.close();
            } catch (Exception e) {
            }
        }

        return archiveFile;
    }
}

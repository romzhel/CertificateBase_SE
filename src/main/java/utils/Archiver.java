package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Archiver {

    public static boolean addToArchive(File sourceFile, File archiveFile) {
        FileOutputStream fos = null;
        ZipOutputStream zipOs = null;
        FileInputStream fis = null;

        try {
            byte[] buffer = new byte[1024];
            fis = new FileInputStream(sourceFile);
            fos = new FileOutputStream(archiveFile);
            zipOs = new ZipOutputStream(fos);

            ZipEntry ze = new ZipEntry(sourceFile.getName());
            zipOs.putNextEntry(ze);
            int len;
            while ((len = fis.read(buffer)) > 0) {
                zipOs.write(buffer, 0, len);
            }
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        } finally {
            try {
                fis.close();
                zipOs.close();
                fos.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}

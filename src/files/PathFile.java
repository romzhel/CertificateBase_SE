package files;

import java.io.*;
import java.util.Scanner;

public class PathFile {
    private File settingsFile;
    private File dbpath;

    public PathFile() {
        settingsFile = new File(System.getProperty("user.dir") + "\\DBpath.cnf");

    }

    public boolean exists() {
        return settingsFile.exists();
    }

    public File getDBFile() {
        if (!settingsFile.exists()) return null;

        BufferedReader in = null;
        try {
            in = new BufferedReader((new FileReader(settingsFile)));
            dbpath = new File(in.readLine());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                in.close();
            } catch (IOException e2) {
                System.out.println(e2.getMessage());
            }
        }

        if (dbpath.exists()) return dbpath;

        return null;
    }

    public boolean createDBFile(File dbFile) {
        BufferedWriter out = null;
        boolean result = false;

        try {
            out = new BufferedWriter(new FileWriter(settingsFile));
            out.write(dbFile.getAbsolutePath());
            result = true;
        }catch (IOException e){
            System.out.println(e.getMessage());
        }finally {
            try {
                out.close();
            } catch (IOException e2) {
                System.out.println(e2.getMessage());
            }
        }

        return result;
    }
}

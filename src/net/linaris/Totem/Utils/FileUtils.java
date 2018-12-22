
package net.linaris.Totem.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public abstract class FileUtils {
    public static void deleteDirectory(File path) {
        if (path.exists()) {
            File[] arrfile = path.listFiles();
            int n = arrfile.length;
            int n2 = 0;
            while (n2 < n) {
                File file = arrfile[n2];
                if (file.isDirectory()) {
                    FileUtils.deleteDirectory(file);
                } else {
                    file.delete();
                }
                ++n2;
            }
            path.delete();
        }
    }

    public static void copyDirectory(File source, File dest) {
        try {
            if (!source.isDirectory()) {
                return;
            }
            if (!dest.exists()) {
                dest.mkdirs();
            }
            File[] arrfile = source.listFiles();
            int n = arrfile.length;
            int n2 = 0;
            while (n2 < n) {
                File file = arrfile[n2];
                if (file.isDirectory()) {
                    FileUtils.copyDirectory(file, new File(String.valueOf(dest.getCanonicalPath()) + "/" + file.getName()));
                } else {
                    FileUtils.copyFile(file, new File(String.valueOf(dest.getCanonicalPath()) + "/" + file.getName()));
                }
                ++n2;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void copyFile(File source, File dest) {
        FileInputStream sourceFile = null;
        try {
            int nbLecture;
            sourceFile = new FileInputStream(source);
            FileOutputStream destinationFile = null;
            if (!dest.exists()) {
                dest.createNewFile();
            }
            destinationFile = new FileOutputStream(dest);
            byte[] buffer = new byte[524288];
            while ((nbLecture = sourceFile.read(buffer)) != -1) {
                destinationFile.write(buffer, 0, nbLecture);
            }
            destinationFile.close();
            sourceFile.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}


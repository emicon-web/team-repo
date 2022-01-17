package com.bezkoder.springjwt.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ChecksumUtility {

    /**
     * Method should only be used to generate checksum outside the application, when running as jar/utility
     *
     * @param args absolute file path including file name
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        if (args != null && args.length > 0) {
            String filePath = args[0];
            if (!filePath.toLowerCase().endsWith(".csv")) {
                System.out.println("Only CSV file can be used to generate checksum");
                return;
            }
            System.out.println("CSV file path: " + filePath);

            File file = new File(filePath);
            if (file != null) {
                System.out.println("Checksum: " + checksum(file));
            } else {
                throw new RuntimeException("Error while generating checksum");
            }
        } else {
            String currentDirectory = System.getProperty("user.dir");
            System.out.println("Current Directory: " + currentDirectory);

            final File currentFolder = new File(currentDirectory);
            String csvFileName = getCsvFile(currentFolder);
            if (csvFileName == null || csvFileName.length() == 0) {
                System.out.println("No CSV file found");
                return;
            }
            System.out.println("CSV file to be processed: " + csvFileName);

            File file = new File(csvFileName);
            if (file != null) {
                System.out.println("Checksum: " + checksum(file));
            } else {
                throw new RuntimeException("Error while generating checksum");
            }
        }
    }

    public static String checksum(MessageDigest digest, File file)
            throws IOException {
        FileInputStream fis = new FileInputStream(file);

        byte[] byteArray = new byte[1024];
        int bytesCount;

        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        }
        fis.close();
        byte[] bytes = digest.digest();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    public static String checksum(File file) throws NoSuchAlgorithmException, IOException {
        return checksum(MessageDigest.getInstance("MD5"), file);
    }

    private static String getCsvFile(final File folder) {
        for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.isDirectory() && fileEntry.getName().toLowerCase().endsWith(".csv")) {
                return fileEntry.getName();
            }
        }
        return null;
    }
}

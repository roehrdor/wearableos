/*
 * This file is part of the Garment OS Project. For any details concerning use
 * of this project in source or binary form please refer to the provided license
 * file.
 *
 * (c) 2014-2015 Garment OS
 */
package de.unistuttgart.vis.wearable.os.cloud;

import android.util.Log;

import java.io.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import java.security.GeneralSecurityException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import de.unistuttgart.vis.wearable.os.privacy.PrivacyManager;
import de.unistuttgart.vis.wearable.os.privacy.UserApp;
import de.unistuttgart.vis.wearable.os.properties.Properties;
import de.unistuttgart.vis.wearable.os.sensors.Sensor;
import de.unistuttgart.vis.wearable.os.sensors.SensorManager;
import de.unistuttgart.vis.wearable.os.storage.SettingsStorage;
import de.unistuttgart.vis.wearable.os.utils.Utils;

/**
 * This class is used to create an archive file containing selected sensor data files or
 * the storage file which itself contains information about the sensors or the privacy settings
 * and user applications
 *
 * @author roehrdor
 */
public class Archiver {

    /**
     * Private constructor, shall never be used
     */
    private Archiver() {
        throw new IllegalAccessError("This constructor shall not be used");
    }

    /**
     * Return all files in the given directory and in sub directory
     *
     * @param dir the root directory where to start searching
     * @return a list of files contained in the directory or sub directory
     */
    protected static List<File> getFilesInDirectory(File dir) {
        List<File> files = new ArrayList<File>();
        List<File> queue = new ArrayList<File>();
        queue.add(dir);
        while(!queue.isEmpty()) {
            File current = queue.remove(0);
            File[] filesInCurrent = current.listFiles();
            for(File f : filesInCurrent) {
                if(f.isDirectory())
                    queue.add(f);
                else
                    files.add(f);
            }
        }
        return files;
    }


    /**
     * Unpack the encrypted archive
     *
     * @param key       the key to decrypt the archive file
     * @param inputFile the inputfile to be decrypted first and then unpacked
     */
    public static void unpackEncryptedFile(String key, File inputFile) {
        decryptFile(inputFile, key);
        unpackArchiveFile(inputFile);
    }

    /**
     * Merge the two given files
     *
     * @param existent    the already existent base file, the result file will have the exact same name
     * @param dataToMerge the new data file to be merged
     * @return true if the merge process has been successfully done, false otherwise
     */
    protected static boolean mergeSensorDataFiles(File existent, File dataToMerge) {
        RandomAccessFile existentFileRAF = null;
        RandomAccessFile dataToMergeFileRAF = null;
        RandomAccessFile destinationRAF = null;
        File destination;

        //
        // Local attributes used to store chunk sizes, dates and data
        //
        long existentFileSize;
        long mergeFileSize;
        int latestMergeValueTimeStamp;
        int latestExistentValueTimeStamp;
        int dimension;
        int dataChunkSize;
        int currentMergeTime;
        int currentExistentTime;
        byte[] buffer;
        boolean returnValue = true;

        //
        // Make sure the given parameters are valid and the file do really exist
        //
        if(existent == null || dataToMerge == null || !existent.exists() || !dataToMerge.exists()) {
            return false;
        }

        try {
            //
            // Create a new File containing data from both the files, therefore move the
            // existent file so we can write to the destination file
            //
            destination = existent;
            existent = new File(destination + ".old");
            if (!destination.renameTo(existent)) {
                return false;
            }

            //
            // Random access file handles for all our three files
            //
            destinationRAF = new RandomAccessFile(destination, "rw");
            existentFileRAF = new RandomAccessFile(existent, "r");
            dataToMergeFileRAF = new RandomAccessFile(dataToMerge, "r");
            existentFileSize = existentFileRAF.length();
            mergeFileSize = dataToMergeFileRAF.length();

            //
            // Read the the time stamps from the file that needs to be merged
            //
            latestMergeValueTimeStamp = dataToMergeFileRAF.readInt();
            dimension = dataToMergeFileRAF.readInt();
            dataChunkSize = dimension * 4;
            buffer = new byte[dataChunkSize];

            //
            // Read the time stamps from the existent file we want the other one to merge to
            //
            latestExistentValueTimeStamp = existentFileRAF.readInt();
            if(dimension != existentFileRAF.readInt()) {
                // Files can not be merged since the dimension does not equal
                return false;
            }

            //
            // Write the file header consisting of the latest modification date
            // and the dimension of the sensor
            //
            destinationRAF.writeInt(latestExistentValueTimeStamp > latestMergeValueTimeStamp ?
                    latestExistentValueTimeStamp : latestMergeValueTimeStamp);
            destinationRAF.writeInt(dimension);

            //
            // Seek to the correct positions in two existing files
            //
            existentFileRAF.seek(8);
            dataToMergeFileRAF.seek(8);

            // ----------------------------------------------------------------------

            //
            // Step 1
            // Merge the data fields that are contained in both files
            // This steps inserts all the data that has been recorded before and and in
            // the time the data has been recorded to the base file
            //
            while(existentFileRAF.getFilePointer() < existentFileSize) {
                // read the current time from the base file
                currentExistentTime = existentFileRAF.readInt();

                // check whether we can read a time stamp from the new merge file
                if(dataToMergeFileRAF.getFilePointer() < mergeFileSize) {
                    //
                    // Check for all the older data fields
                    //
                    while((currentMergeTime = dataToMergeFileRAF.readInt()) < currentExistentTime) {
                        dataToMergeFileRAF.read(buffer);

                        // now that we have the time, before the initial date we can write the time and data
                        // to the new destination file
                        destinationRAF.writeInt(currentMergeTime);
                        destinationRAF.write(buffer);
                    }

                    // set the file pointer 4 bytes back to allow reading the date again
                    dataToMergeFileRAF.seek(dataToMergeFileRAF.getFilePointer() - 4);

                    //
                    // Advance so we do not duplicate data
                    //
                    while(dataToMergeFileRAF.readInt() <= currentExistentTime) {
                        dataToMergeFileRAF.seek(dataToMergeFileRAF.getFilePointer() + dataChunkSize);
                    }

                    // set the file pointer 4 bytes back to allow reading the date again
                    dataToMergeFileRAF.seek(dataToMergeFileRAF.getFilePointer() - 4);
                }

                // read the data from the base file
                existentFileRAF.read(buffer);

                // write the date and the data to the destination file
                destinationRAF.writeInt(currentExistentTime);
                destinationRAF.write(buffer);
            }

            // ----------------------------------------------------------------------

            //
            // Step 2
            // So we still need to add all the data that has been recorded after all the records from
            // the base file
            //
            while(dataToMergeFileRAF.getFilePointer() < mergeFileSize) {
                currentMergeTime = dataToMergeFileRAF.readInt();
                dataToMergeFileRAF.read(buffer);

                // now that we have the time, before the initial date we can write the time and data
                // to the new destination file
                destinationRAF.writeInt(currentMergeTime);
                destinationRAF.write(buffer);
            }

            //
            // Close all the file handles
            //
            dataToMergeFileRAF.close();
            existentFileRAF.close();
            destinationRAF.close();

            if(!existent.delete()) {
                return false;
            }

            returnValue = true;
        } catch(IOException ioe) {
            returnValue = false;
        } finally {
            //
            // Make sure we close the file handles
            //
            if(existentFileRAF != null)
                try {existentFileRAF.close();} catch(IOException ioe) {Log.i("GarmentOS", "Archiver:mergeSensorDataFiles() - IOE:efRAF");}
            if(dataToMergeFileRAF != null)
                try {dataToMergeFileRAF.close();} catch(IOException ioe) {Log.i("GarmentOS", "Archiver:mergeSensorDataFiles() - IOE:dmRAF");}
            if(destinationRAF != null)
                try {destinationRAF.close();} catch(IOException ioe) {Log.i("GarmentOS", "Archiver:mergeSensorDataFiles() - IOE:deRAF");}

            //
            // Try to delete the base file
            //
            if(!existent.delete()) {
                returnValue = false;
            }
        }

        //
        // Finally return with the stored result
        //
        return returnValue;
    }

    /**
     * Unpack the packed archive file and merge the files as far as possible
     *
     * @param inputFile the input file to unpack
     */
    public static void unpackArchiveFile(File inputFile) {
        Properties.FILE_STATUS_FIELDS_LOCK.lock();
        Properties.FILE_ARCHIVING.set(true);
        while(Properties.FILES_IN_USE.get() != 0) {
            Utils.sleepUninterrupted(200);
        }
        Properties.FILE_STATUS_FIELDS_LOCK.unlock();

        final int BUFFER = 2048;

        try {
            BufferedOutputStream outputStream;
            FileInputStream fileInputStream = new FileInputStream(inputFile);
            ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(fileInputStream));
            ZipEntry zipEntry;

            //
            // Iterate through the zip input file stream and extract all these files
            //
            while((zipEntry = zipInputStream.getNextEntry()) != null) {
                byte data[] = new byte[BUFFER];
                String name = zipEntry.getName();

                File outputFile = new File(name);
                FileOutputStream fileOutputStream;
                boolean merge;

                //
                // In case the file does not already exist we can easily extract it
                //
                if((merge = outputFile.exists())) {
                    fileOutputStream = new FileOutputStream(outputFile);
                }

                //
                // Otherwise we need to merge the extracted with the already existent file
                //
                else {
                    outputFile = new File(name + ".me");
                    fileOutputStream = new FileOutputStream(outputFile);
                }

                outputStream = new BufferedOutputStream(fileOutputStream, BUFFER);
                while(zipInputStream.read(data, 0, BUFFER) != -1)
                    outputStream.write(data, 0, BUFFER);
                outputStream.flush();
                outputStream.close();

                //
                // If we need to merge these files do it now
                //
                if(merge) {
                    if(name.equals(SettingsStorage.FILE_NAME_APPS)) {
                        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(outputFile));
                        Map<String, UserApp> apps = (Map<String, UserApp>)ois.readObject();
                        if(apps != null) {
                            //
                            // Insert all apps that are not yet registered
                            //
                            for(String s : apps.keySet()) {
                                if(PrivacyManager.instance.getApp(s) == null)
                                    PrivacyManager.instance.addApp(apps.get(s));
                            }
                        }
                        ois.close();
                    } else if(name.equals(SettingsStorage.FILE_NAME_SENSOR)) {
                        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(outputFile));
                        Map<Integer, Sensor> sensors = (Map<Integer, Sensor>)ois.readObject();
                        if(sensors != null) {
                            //
                            // Add all sensors that are yet unknown to the system
                            //
                            for(int id : sensors.keySet()) {
                                if(SensorManager.getSensorByID(id) == null)
                                    SensorManager.addNewSensor(sensors.get(id));
                            }
                        }
                        ois.close();
                    } else {
                        mergeSensorDataFiles(new File(zipEntry.getName()), outputFile);
                    }

                    // finally delete the file
                    if(!outputFile.delete()) {
                        Log.w("GarmentOS", "Archiver:unpackArchiveFile() - File can not be deleted");
                    }
                }

                zipInputStream.close();
            }
        } catch(IOException ioe) {
            Log.i("GarmentOS", "Archiver:unpackArchiveFile() - IOE");
        } catch(ClassNotFoundException cnfe) {
            Log.i("GarmentOS", "Archiver:unpackArchiveFile() - CNFE");
        }

        Properties.FILE_ARCHIVING.set(false);
    }


    /**
     * Create a compressed archive file containing all sensor data and the storage file containing
     * the privacy information and sensor settings.

     * @param outputFile the output file to store the files in
     */
    public static void createArchiveFile(File outputFile) {
        //
        // Wait until no file in the directory is in use and lock these files
        //
        Properties.FILE_STATUS_FIELDS_LOCK.lock();
        Properties.FILE_ARCHIVING.set(true);
        while(Properties.FILES_IN_USE.get() != 0) {
            Utils.sleepUninterrupted(200);
        }
        Properties.FILE_STATUS_FIELDS_LOCK.unlock();

        try {
            // Get all files in the directory and in its sub directories
            List<File> files = getFilesInDirectory(Properties.storageDirectory);

            final int BUFFER_SIZE = 1024;
            byte[] bytes = new byte[BUFFER_SIZE];
            int length;

            // Create a new file output stream and zip output stream to save the files to a zip file
            FileOutputStream fos = new FileOutputStream(outputFile);
            FileInputStream fis;
            ZipOutputStream zos = new ZipOutputStream(fos);

            // For each file we got in the file list
            for (File f : files) {

                // Check whether the file is a directory
                if (!f.isDirectory()) {
                    // and only if not add the file to the zip file
                    // Therefore open the file
                    fis = new FileInputStream(f);

                    // Make the path to the file relative
                    String zippedName = f.getCanonicalPath().substring(
                            Properties.storageDirectory.getCanonicalPath().length() + 1,
                            f.getCanonicalPath().length());

                    // Create a new zip entry with the relative name
                    ZipEntry zipEntry = new ZipEntry(zippedName);

                    // and put it as next entry to the zip file
                    zos.putNextEntry(zipEntry);

                    // copy the file to the zip output stream
                    while((length = fis.read(bytes, 0, BUFFER_SIZE)) >= 0)
                        zos.write(bytes, 0, length);

                    // close the current entry and the now added file
                    zos.closeEntry();
                    fis.close();
                }
            }

            // In the end close the zip file and the according output stream
            zos.finish();
            zos.close();
            fos.close();
        } catch (IOException ioe) {
            Log.e("GarmentOS", "Could not create compressed archive");
            Log.e("orDEBUG", ioe.getMessage());
            Log.e("orDEBUG", ioe.getLocalizedMessage());
        }

        //
        // Unlock the files in the directory again
        //
        Properties.FILE_ARCHIVING.set(false);
    }


    /**
     * Create a compressed and encrypted archive file containing all sensor data and the storage
     * file containing the privacy information and sensor settings. This function call is actually
     * the same as first calling {@link de.unistuttgart.vis.wearable.os.cloud.Archiver#createArchiveFile(java.io.File)}
     * and afterwards calling the {@link de.unistuttgart.vis.wearable.os.cloud.Archiver#encryptFile(java.io.File, String)}
     * method.
     *
     * @param key        the key to encrypt the file with
     * @param outputFile the output file to store the files in
     */
    public static void createEncryptedArchiveFile(String key, File outputFile) {
        createArchiveFile(outputFile);
        encryptFile(outputFile, key);
    }

    //
    // Arbitrarily selected 8-byte salt sequence:
    //
    private static final byte[] salt = { (byte) 0x43, (byte) 0x76, (byte) 0x95,
            (byte) 0xc7, (byte) 0x5b, (byte) 0xd7, (byte) 0x45, (byte) 0x17 };

    /**
     * Create a cipher object based on the given password and the decryption
     * mode
     *
     * @param pass
     *            the password to use for de/encryption
     * @param decryptMode
     *            the decrypt mode
     * @return the cipher object
     */
    private static Cipher makeCipher(String pass, boolean decryptMode) {
        Cipher cipher = null;
        try {
            // Use a KeyFactory to derive the corresponding key from the passphrase:
            PBEKeySpec keySpec = new PBEKeySpec(pass.toCharArray());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            SecretKey key = keyFactory.generateSecret(keySpec);

            // Create parameters from the salt and an arbitrary number of
            // iterations:
            PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, 42);

            // Set up the cipher:
            cipher = Cipher.getInstance("PBEWithMD5AndDES");

            // Set the cipher mode to decryption or encryption:
            if (decryptMode) {
                cipher.init(Cipher.ENCRYPT_MODE, key, pbeParamSpec);
            } else {
                cipher.init(Cipher.DECRYPT_MODE, key, pbeParamSpec);
            }
        } catch(GeneralSecurityException gse) {
            Log.i("GarmentOS", "Archiver:makeCipher() - GSE");
        }
        return cipher;
    }

    /**
     * Encrypt the given file with the given password
     *
     * @param fileName
     *            the file to encrypt
     * @param pass
     *            the password to use for encryption
     */
    public static void encryptFile(File fileName, String pass) {
        byte[] decData;
        byte[] encData;
        try {
            // Generate the cipher using pass:
            Cipher cipher;
            cipher = makeCipher(pass, true);

            // Read in the file:
            FileInputStream inStream = new FileInputStream(fileName);

            int blockSize = 8;
            // Figure out how many bytes are padded
            int paddedCount = blockSize - ((int) fileName.length() % blockSize);

            // Figure out full size including padding
            int padded = (int) fileName.length() + paddedCount;

            decData = new byte[padded];

            inStream.read(decData);

            inStream.close();

            // Write out padding bytes as per PKCS5 algorithm
            for (int i = (int) fileName.length(); i < padded; ++i) {
                decData[i] = (byte) paddedCount;
            }

            // Encrypt the file data:
            encData = cipher.doFinal(decData);

            // Write the encrypted data to a new file:
            FileOutputStream outStream = new FileOutputStream(fileName);
            outStream.write(encData);
            outStream.close();
        } catch (GeneralSecurityException gse) {
            Log.i("GarmentOS", "Archiver:encryptFile() - GSE");
        } catch (IOException IOE) {
            Log.i("GarmentOS", "Archiver:encryptFile() - IOE");
        }
    }

    /**
     * Decrypt the given file with the given password
     *
     * @param fileName
     *            the file to be decrypted
     * @param pass
     *            the password to use for decryption
     */
    public static void decryptFile(File fileName, String pass) {
        byte[] encData;
        byte[] decData;
        try {
            // Generate the cipher using pass:
            Cipher cipher = makeCipher(pass, false);

            // Read in the file:
            FileInputStream inStream = new FileInputStream(fileName);
            encData = new byte[(int) fileName.length()];
            inStream.read(encData);
            inStream.close();
            // Decrypt the file data:
            decData = cipher.doFinal(encData);

            // Figure out how much padding to remove

            int padCount = (int) decData[decData.length - 1];

            // Naive check, will fail if plaintext file actually contained
            // this at the end
            // For robust check, check that padCount bytes at the end have same
            // value
            if (padCount >= 1 && padCount <= 8) {
                decData = Arrays.copyOfRange(decData, 0, decData.length - padCount);
            }

            // Write the decrypted data to a new file:
            FileOutputStream target = new FileOutputStream(fileName);
            target.write(decData);
            target.close();
        } catch (GeneralSecurityException gse) {
            Log.i("GarmentOS", "Archiver:decryptFile() - GSE");
        } catch (IOException IOE) {
            Log.i("GarmentOS", "Archiver:decryptFile() - IOE");
        }
    }
}

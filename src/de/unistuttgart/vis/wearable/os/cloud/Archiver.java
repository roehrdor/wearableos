/*
 * This file is part of the Garment OS Project. For any details concerning use
 * of this project in source or binary form please refer to the provided license
 * file.
 *
 * (c) 2014-2015 Garment OS
 */
package de.unistuttgart.vis.wearable.os.cloud;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import de.unistuttgart.vis.wearable.os.utils.Constants;

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
     * Do the encryption / decryption process for the given file and key. The mode defines whether
     * the given file needs to be decrypted or encrypted.
     *
     * @param mode   the mode whether the file needs to be decrypted or encrypted
     * @param key    the key to decrypt / encrypt the file with
     * @param input  the input file to be decrypted / encrypted
     * @param output the resulting output file
     */
    private static void crypt(int mode, String key, File input, File output) {
        try {
            byte[] inputBytes, outputBytes;
            FileInputStream fis;
            FileOutputStream fos;
            Key kKey;
            Cipher cipher;

            //
            // Create a key for the decryption / encryption process, create the Cipher object and
            // initialize it
            //
            kKey = new SecretKeySpec(key.getBytes(), Constants.CRYPTO_ALGORITHM);
            cipher = Cipher.getInstance(Constants.CRYPTO_TRANSFORM);
            cipher.init(mode, kKey);

            //
            // Read the input file to a byte array
            //
            fis = new FileInputStream(input);
            inputBytes = new byte[(int)input.length()];
            fis.read(inputBytes);
            fis.close();

            //
            // Let the cipher to its work with encrypting / decrypting the file and save the
            // resulting bytes in the output file.
            //
            fos = new FileOutputStream(output);
            outputBytes = cipher.doFinal(inputBytes);
            fos.write(outputBytes);
            fos.close();

        } catch(Exception e) {
        }
    }

    /**
     * Create a compressed archive file containing all sensor data and the storage file containing
     * the privacy information and sensor settings.

     * @param outputFile the output file to store the files in
     */
    public static void createArchiveFile(File outputFile) {}


    /**
     * Create a compressed and encrypted archive file containing all sensor data and the storage
     * file containing the privacy information and sensor settings. This function call is actually
     * the same as first calling {@link de.unistuttgart.vis.wearable.os.cloud.Archiver#createArchiveFile(java.io.File)}
     * and afterwards calling the {@link de.unistuttgart.vis.wearable.os.cloud.Archiver#encryptFile(String, java.io.File, java.io.File)}
     * method.
     *
     * @param key        the key to encrypt the file with
     * @param outputFile the output file to store the files in
     */
    public static void createEncryptedArchiveFile(String key, File outputFile) {
        createArchiveFile(outputFile);
        encryptFile(key, outputFile, outputFile);
    }

    /**
     * Encrypt the given file using the given password and save the encrypted file to the given
     * output file.
     *
     * @param key        the key to encrypt the file with
     * @param inputFile  the input file to encrypt
     * @param outputFile the encrypted output file
     */
    public static void encryptFile(String key, File inputFile, File outputFile) {
        crypt(Cipher.ENCRYPT_MODE, key, inputFile, outputFile);
    }

    /**
     * Decrypt the given file using the given password and save the decrypted file to the given
     * output file.
     *
     * @param key        the key to decrypt the file with
     * @param inputFile  the input file to decrypt
     * @param outputFile the decrypted output file
     */
    public static void decryptFile(String key, java.io.File inputFile, java.io.File outputFile) {
        crypt(Cipher.DECRYPT_MODE, key, inputFile, outputFile);
    }


}

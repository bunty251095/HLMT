package com.caressa.common.utils

import android.util.Base64
import android.util.Log
import java.io.UnsupportedEncodingException
import java.security.GeneralSecurityException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.and


/**
 * Encrypt and decrypt messages using AES 256 bit encryption that are compatible with AESCrypt-ObjC and AESCrypt Ruby.
 *
 *
 * Created by scottab on 04/10/2014.
 */
class EncryptionUtility {
    companion object {

        private val TAG = "AESCrypt"

        //AESCrypt-ObjC uses CBC and PKCS7Padding
        private val AES_MODE = "AES/CBC/ISO10126Padding"
        private val CHARSET: String = "UTF-8"

        //AESCrypt-ObjC uses SHA-256 (and so a 256-bit key)
        private val HASH_ALGORITHM = "SHA-256"

        //AESCrypt-ObjC uses blank IV (not the best security, but the aim here is compatibility)


        //private static final byte[] ivBytes = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

        //togglable log option (please turn off in live!)
        var DEBUG_LOG_ENABLED = false

        /**
         * Generates SHA256 hash of the password which is used as key
         *
         * @return SHA256 of the password
         */
        @Throws(NoSuchAlgorithmException::class, UnsupportedEncodingException::class)
        private fun generateKey(keyValue: String): SecretKeySpec {
            val digest = MessageDigest.getInstance(HASH_ALGORITHM)
            val bytes = keyValue.toByteArray(charset("UTF-8"))
            digest.update(bytes, 0, bytes.size)
            val key = digest.digest()
            return SecretKeySpec(key, "AES")
        }

        /**
         * Encrypt and encode message using 256-bit AES with key generated from password.
         *
         * @param key     : secrete key
         * @param message the thing you want to encrypt assumed String UTF-8
         * @return Base64 encoded CipherText
         * @throws GeneralSecurityException if problems occur during encryption
         */
        @Throws(GeneralSecurityException::class)
        fun encrypt(key: String, message: String, IV: String): String {
            try {

                val ivBytes = IV.toByteArray()
                val keyBytes = key.toByteArray(charset("UTF-8"))

                val secreteKey = SecretKeySpec(keyBytes, "AES")
                val cipherText = encrypt(
                    secreteKey,
                    ivBytes,
                    message.toByteArray(charset(CHARSET))
                )

                return Base64.encodeToString(cipherText, Base64.NO_WRAP)
            } catch (e: UnsupportedEncodingException) {
                if (DEBUG_LOG_ENABLED)
                    Log.e(TAG, "UnsupportedEncodingException ", e)
                throw GeneralSecurityException(e)
            } catch (e: Exception) {
                e.printStackTrace()
                throw RuntimeException(e)
            }

        }

        /**
         * More flexible AES encrypt that doesn't encode
         *
         * @param key     AES key typically 128, 192 or 256 bit
         * @param iv      Initiation Vector
         * @param message in bytes (assumed it's already been decoded)
         * @return Encrypted cipher text (not encoded)
         * @throws GeneralSecurityException if something goes wrong during encryption
         */
        @Throws(GeneralSecurityException::class)
        fun encrypt(key: SecretKeySpec, iv: ByteArray, message: ByteArray): ByteArray {
            var cipherText = ByteArray(0)
            try {
                val cipher = Cipher.getInstance(AES_MODE)
                val ivSpec = IvParameterSpec(iv)
                cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec)
                cipherText = cipher.doFinal(message)
            } catch (e: Exception) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
                throw RuntimeException(e)
            }

            return cipherText
        }

        /**
         * Decrypt and decode ciphertext using 256-bit AES with key generated from password
         *
         * @param key
         * @param base64EncodedCipherText the encrpyted message encoded with base64
         * @return message in Plain text (String UTF-8)
         * @throws GeneralSecurityException if there's an issue decrypting
         */
        @Throws(GeneralSecurityException::class)
        fun decrypt(key: String, base64EncodedCipherText: String, IV: String): String {

            try {
                val ivBytes = IV.toByteArray()
                val keyBytes = key.toByteArray(charset("UTF-8"))

                val secretKey = SecretKeySpec(keyBytes, "AES")
                val decodedCipherText = Base64.decode(base64EncodedCipherText, Base64.NO_WRAP)
                val decryptedBytes =
                    decrypt(secretKey, ivBytes, decodedCipherText)

                return String(decryptedBytes, charset(CHARSET))
            } catch (e: UnsupportedEncodingException) {
                if (DEBUG_LOG_ENABLED)
                    Log.e(TAG, "UnsupportedEncodingException ", e)

                throw GeneralSecurityException(e)
            } catch (e: Exception) {
                e.printStackTrace()
                throw RuntimeException(e)
            }

        }

        /**
         * More flexible AES decrypt that doesn't encode
         *
         * @param key               AES key typically 128, 192 or 256 bit
         * @param iv                Initiation Vector
         * @param decodedCipherText in bytes (assumed it's already been decoded)
         * @return Decrypted message cipher text (not encoded)
         * @throws GeneralSecurityException if something goes wrong during encryption
         */
        @Throws(GeneralSecurityException::class)
        fun decrypt(key: SecretKeySpec, iv: ByteArray, decodedCipherText: ByteArray): ByteArray {
            val cipher = Cipher.getInstance(AES_MODE)
            val ivSpec = IvParameterSpec(iv)
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec)
            val decryptedBytes = cipher.doFinal(decodedCipherText)

            log("decryptedBytes", decryptedBytes)

            return decryptedBytes
        }

        /*private static void log(String what, String value)
		    {
		        if (DEBUG_LOG_ENABLED)
		            Log.ic_pdf(TAG, what + "[" + value.length() + "] [" + value + "]");
		    }*/

        private fun log(what: String, bytes: ByteArray) {
            if (DEBUG_LOG_ENABLED)
                Log.d(
                    TAG, what + "[" + bytes.size + "] [" + bytesToHex(
                        bytes
                    ) + "]"
                )
        }

        /**
         * Converts byte array to hexidecimal useful for logging and fault finding
         *
         * @param bytes
         * @return
         */
        private fun bytesToHex(bytes: ByteArray): String {
            val hexArray = charArrayOf(
                '0',
                '1',
                '2',
                '3',
                '4',
                '5',
                '6',
                '7',
                '8',
                '9',
                'A',
                'B',
                'C',
                'D',
                'E',
                'F'
            )
            val hexChars = CharArray(bytes.size * 2)
            var v: Int
            for (j in bytes.indices) {
                v = (bytes[j] and 0xFF.toByte()).toInt()
                hexChars[j * 2] = hexArray[v.ushr(4)]
                hexChars[j * 2 + 1] = hexArray[v and 0x0F]
            }
            return String(hexChars)
        }
    }

}


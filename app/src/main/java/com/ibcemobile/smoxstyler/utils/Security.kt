package com.ibcemobile.smoxstyler.utils

/**
 * This class is an addendum. It shouldn't really be here: it should be on the secure server. But if
 * a secure server does not exist, it's still good to check the signature of the purchases coming
 * from Google Play. At the very least, it will combat man-in-the-middle attacks. Putting it
 * on the server would provide the additional protection against hackers who may
 * decompile/rebuild the app.
 *
 * Sigh... All sorts of attacks can befall your app, website, or platform. So when it comes to
 * implementing security measures, you have to be realistic and judicious so that user experience
 * does not suffer needlessly. And you should analyze that the money you will save (minus cost of labor)
 * by implementing security measure X is greater than the money you would lose if you don't
 * implement X. Talk to a UX designer if you find yourself obsessing over security.
 *
 * The good news is, in implementing [BillingRepository], a number of measures is taken to help
 * prevent fraudulent activities in your app. We don't just focus on tech savvy hackers, but also
 * on fraudulent users who may want to exploit loopholes. Just to name an obvious case:
 * triangulation using Google Play, your secure server, and a local cache helps against non-techie
 * frauds.
 */
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import java.io.IOException
import java.security.InvalidKeyException
import java.security.KeyFactory
import java.security.NoSuchAlgorithmException
import java.security.PublicKey
import java.security.Signature
import java.security.SignatureException
import java.security.spec.InvalidKeySpecException
import java.security.spec.X509EncodedKeySpec

/**
 * Security-related methods. For a secure implementation, all of this code should be implemented on
 * a server that communicates with the application on the device.
 */
object Security {
    private const val TAG = "IABUtil/Security"
    private const val KEY_FACTORY_ALGORITHM = "RSA"
    private const val SIGNATURE_ALGORITHM = "SHA1withRSA"

    /**
     * BASE_64_ENCODED_PUBLIC_KEY should be YOUR APPLICATION'S PUBLIC KEY
     * (that you got from the Google Play developer console, usually under Services & APIs tab).
     * This is not your developer public key, it's the *app-specific* public key.
     *
     * Just like everything else in this class, this public key should be kept on your server.
     * But if you don't have a server, then you should obfuscate your app so that hackers cannot
     * get it. If you cannot afford a sophisticated obfuscator, instead of just storing the entire
     * literal string here embedded in the program,  construct the key at runtime from pieces or
     * use bit manipulation (for example, XOR with some other string) to hide
     * the actual key.  The key itself is not secret information, but we don't
     * want to make it easy for an attacker to replace the public key with one
     * of their own and then fake messages from the server.
     */

    /*val BASE_64_ENCODED_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArEaiCq7os9cmF" +
            "+i564+pIOiSOVZa/LRzu0K79Dg6wKWjnJ1PkHAa4ZOJ81KrxyFk3q3UiJ3lNsTCdW216+KKdKp+YCOFLs" +
            "sN+4FKjFBqY9lJbm6uuxZ9cPugMOTVFrVlmreYyhIY4jysfo4+LeyEmB7D20X7M+7diCRBEIsOY9lA2ne" +
            "OtD6j0BR4rhLGR3xjN0LGrhCCdbw42+eIkc/awbY7FypLMJjbAmEnNBe1tlOxxX6ZgspwAlY8XjnX832l" +
            "xxHdnuJKSPGtYCQLSt/LYc/go90/kc/U+oPtQy/KgCiQEcKeIL1a6AB294JDogkHuqRIeXIu1n4sAfzG" +
            "cshrJQIDAQAB"*/

    val BASE_64_ENCODED_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAr8EEy9EINOpXyFu2B5Wyvg7EwFFztvacTFjvG+3vBl7Ijcb7DunSM49iMgVtyyJfsMOBtRK49jS6h4WoickXrJFW+xHE2SyBfhB6Y9jVOuD2dEQs/DpZ8/7/+oOEdmpan1XgJGnwRBZoK5KWI5hujVQXQ4qUvvyuSj8a2SmA9JZJA2OBvUSSMjsYpYF8iOKOTRbHmKNOXybpPDJXShdiaS3fDtjzEy+A7nUuhiu4wUD2RXt1K7iVlARH+cqZWkQoJfhQ9EAK3dhq0ggtrxQmfHnQB9qxc3qhysQB8pYSFihTiR46K8nrWn+YIZUUwy6tAQbL4SnvhPmt97SfflMP6QIDAQAB"
    //val BASE_64_ENCODED_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAppuzSD74rqWwZZm7DpQmN9ym2N2BtXgq6utBxFXxPyC60FpVf6OGx8pZ01TwPVs4lksnKxzrXsVmW7g0PWbWZPYI1Nsjbw9UN9G1+drpWJs2VYamSWYEFhXtcWuF8oUFTP+1SuPeoeIBh6x5+eEM10I/W8DNmE3Jhb22O9ciFykBtBawNFq79M3XSUiqAPdacrvSAESfMeULD9I2JBpfNeoO+E0pIO7I7dWXN2vZNUH6IZHWwq0el1UZY9RUhPo5X6CLWz0GSBJ9QS8OOei4NtWyRGr2LesRwxNJykuX9G8K8PnNgChnhG5zsoFG8/eAlLX6vHBjUIHquTwnexTrgwIDAQAB"

    /**
     * Verifies that the data was signed with the given signature
     *
     * @param base64PublicKey the base64-encoded public key to use for verifying.
     * @param signedData the signed JSON string (signed, not encrypted)
     * @param signature the signature for the data, signed with the private key
     * @throws IOException if encoding algorithm is not supported or key specification
     * is invalid
     */
    @Throws(IOException::class)
    fun verifyPurchase(base64PublicKey: String, signedData: String, signature: String): Boolean {
        if ((TextUtils.isEmpty(signedData) || TextUtils.isEmpty(base64PublicKey)
                        || TextUtils.isEmpty(signature))
        ) {
            Log.w(TAG, "Purchase verification failed: missing data.")
            return false
        }
        val key = generatePublicKey(base64PublicKey)
        return verify(key, signedData, signature)
    }

    /**
     * Generates a PublicKey instance from a string containing the Base64-encoded public key.
     *
     * @param encodedPublicKey Base64-encoded public key
     * @throws IOException if encoding algorithm is not supported or key specification
     * is invalid
     */
    @Throws(IOException::class)
    private fun generatePublicKey(encodedPublicKey: String): PublicKey {
        try {
            val decodedKey = Base64.decode(encodedPublicKey, Base64.DEFAULT)
            val keyFactory = KeyFactory.getInstance(KEY_FACTORY_ALGORITHM)
            return keyFactory.generatePublic(X509EncodedKeySpec(decodedKey))
        } catch (e: NoSuchAlgorithmException) {
            // "RSA" is guaranteed to be available.
            throw RuntimeException(e)
        } catch (e: InvalidKeySpecException) {
            val msg = "Invalid key specification: $e"
            Log.w(TAG, msg)
            throw IOException(msg)
        }
    }

    /**
     * Verifies that the signature from the server matches the computed signature on the data.
     * Returns true if the data is correctly signed.
     *
     * @param publicKey public key associated with the developer account
     * @param signedData signed data from server
     * @param signature server signature
     * @return true if the data and signature match
     */
    private fun verify(publicKey: PublicKey, signedData: String, signature: String): Boolean {
        val signatureBytes: ByteArray
        try {
            signatureBytes = Base64.decode(signature, Base64.DEFAULT)
        } catch (e: IllegalArgumentException) {
            Log.w(TAG, "Base64 decoding failed.")
            return false
        }
        try {
            val signatureAlgorithm = Signature.getInstance(SIGNATURE_ALGORITHM)
            signatureAlgorithm.initVerify(publicKey)
            signatureAlgorithm.update(signedData.toByteArray())
            if (!signatureAlgorithm.verify(signatureBytes)) {
                Log.w(TAG, "Signature verification failed...")
                return false
            }
            return true
        } catch (e: NoSuchAlgorithmException) {
            // "RSA" is guaranteed to be available.
            throw RuntimeException(e)
        } catch (e: InvalidKeyException) {
            Log.w(TAG, "Invalid key specification.")
        } catch (e: SignatureException) {
            Log.w(TAG, "Signature exception.")
        }
        return false
    }
}
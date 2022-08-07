package cn.devmeteor.devicemanagement

import org.apache.commons.codec.binary.Base64
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.interfaces.RSAPublicKey
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher


object RSAUtil {

    private const val PUBLIC_KEY = BuildConfig.RSA_PUBLIC_KEY

    fun encrypt(str: String): String {
        val decoded = Base64.decodeBase64(PUBLIC_KEY)
        val pubKey = KeyFactory.getInstance("RSA")
            .generatePublic(X509EncodedKeySpec(decoded)) as RSAPublicKey
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, pubKey)
        return Base64.encodeBase64String(cipher.doFinal(str.toByteArray(StandardCharsets.UTF_8)))
    }

}

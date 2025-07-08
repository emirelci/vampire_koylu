package com.ee.vampirkoylu.util

import android.util.Base64
import com.android.billingclient.api.Purchase
import java.security.KeyFactory
import java.security.Signature
import java.security.spec.X509EncodedKeySpec

object PurchaseValidator {
    private const val BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArKCHhuf3b3tqx8WZSwh7SvYap3yxyUbBYljSKjw97YfPaGbVzO58BerDZ1OmErc15wRGvKdvQwlEvE/EAjzJTaOndVkOZcAqoG2hNAhOQXrmJcaGgkY7H/j6sOeyNxXDhFh7sx5JswpDD3N3h3mYORjb7huE8q6oFq7shilAoZy3lZAAv0OKG7uQZp5EtYoT0GJ14x2QKO5FGb4n+o7Y953xLIAnOm5zx4Sm25PRvjBlzbBdXcyQsECSV02J47efJU3GA8ht/K3R3vxDXwxYr6W+Fq84CksHArqoYNqo+o5x69VpOwuowQOYA5tsv44zRa61/3pvpOrEh7Q5R9ZDhwIDAQAB"

    fun verifyPurchase(purchase: Purchase): Boolean {
        return try {
            val keyFactory = KeyFactory.getInstance("RSA")
            val decodedKey = Base64.decode(BASE64_PUBLIC_KEY, Base64.DEFAULT)
            val keySpec = X509EncodedKeySpec(decodedKey)
            val publicKey = keyFactory.generatePublic(keySpec)
            val signature = Signature.getInstance("SHA1withRSA")
            signature.initVerify(publicKey)
            signature.update(purchase.originalJson.toByteArray(Charsets.UTF_8))
            val isValid = signature.verify(Base64.decode(purchase.signature, Base64.DEFAULT))
            isValid
        } catch (e: Exception) {
            false
        }
    }
}

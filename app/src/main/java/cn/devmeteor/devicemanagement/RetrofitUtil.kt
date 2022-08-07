package cn.devmeteor.devicemanagement

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager

object RetrofitUtil {

    private val okHttpClientBuilder = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request()
                .newBuilder()
                .addHeader("CipherText", RSAUtil.encrypt(System.currentTimeMillis().toString()))
                .build()
            chain.proceed(request)
        }.apply {
            if (BuildConfig.DEBUG) {
                hostnameVerifier { _, _ -> true }
                sslSocketFactory(SSLSocketClient.SSLSocketFactory, object : X509TrustManager {
                    override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {

                    }

                    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {

                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return arrayOf()
                    }
                })
            }
        }

    private val retrofit = Retrofit.Builder()
//        .baseUrl("https://192.168.26.97:8080/")
        .baseUrl("https://devmeteor.cn:8080/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClientBuilder.build())
        .build()

    val deviceApi: DeviceApi = retrofit.create(DeviceApi::class.java)

}
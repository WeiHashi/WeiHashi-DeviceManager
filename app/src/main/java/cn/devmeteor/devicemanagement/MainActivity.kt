package cn.devmeteor.devicemanagement

import android.content.Context
import android.content.DialogInterface
import android.hardware.biometrics.BiometricManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CancellationSignal
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.devmeteor.devicemanagement.RetrofitUtil.deviceApi
import com.blankj.utilcode.util.DeviceUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.DataOutputStream
import java.lang.Exception
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.Executor
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var requestAuth: TextView
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.device_list)
        requestAuth = findViewById(R.id.request_auth)
        requestAuth.setOnClickListener {
            fingerprintAuth()
        }
        fingerprintAuth()
    }

    private fun renderList() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        deviceApi.get()
            .enqueue(object : Callback<ArrayList<Device>> {
                override fun onFailure(call: Call<ArrayList<Device>>, t: Throwable) {
                    Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_LONG).show()
                    t.printStackTrace()
                }

                override fun onResponse(
                    call: Call<ArrayList<Device>>,
                    response: Response<ArrayList<Device>>
                ) {
                    val res = response.body()
                    if (res == null) {
                        Toast.makeText(this@MainActivity, "请求失败", Toast.LENGTH_SHORT).show()
                        return
                    }
                    println(res)
                    recyclerView.adapter = DeviceAdapter(this@MainActivity, res)
                }
            })
    }

    private fun fingerprintAuth() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            Toast.makeText(this, "无法进行指纹认证", Toast.LENGTH_LONG).show()
            return
        }
        val biometricManager = getSystemService(Context.BIOMETRIC_SERVICE) as BiometricManager
        if (biometricManager.canAuthenticate() != BiometricManager.BIOMETRIC_SUCCESS) {
            Toast.makeText(this, "无法进行指纹认证", Toast.LENGTH_LONG).show()
            return
        }
        BiometricPrompt.Builder(this)
            .setTitle("指纹认证")
            .setNegativeButton("退出", {
                finish()
            }, { _, _ -> })
            .setDescription("指纹认证通过后才能使用本软件")
            .build()
            .authenticate(
                CancellationSignal(),
                mainExecutor,
                @RequiresApi(Build.VERSION_CODES.P)
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(
                        errorCode: Int,
                        errString: CharSequence?
                    ) {
                        super.onAuthenticationError(errorCode, errString)
                        Toast.makeText(this@MainActivity, errString, Toast.LENGTH_LONG).show()
                    }

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                        super.onAuthenticationSucceeded(result)
                        Toast.makeText(this@MainActivity, "认证成功", Toast.LENGTH_LONG).show()
                        requestAuth.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                        renderList()
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        Toast.makeText(this@MainActivity, "指纹认证失败", Toast.LENGTH_LONG).show()
                    }

                    override fun onAuthenticationHelp(
                        helpCode: Int,
                        helpString: CharSequence?
                    ) {
                        super.onAuthenticationHelp(helpCode, helpString)
                        Toast.makeText(this@MainActivity, helpString, Toast.LENGTH_LONG).show()
                    }
                })
    }
}

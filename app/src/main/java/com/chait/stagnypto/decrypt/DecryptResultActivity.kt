package com.chait.stagnypto.decrypt

import android.content.Context
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.widget.Toolbar
import android.text.ClipboardManager
import android.view.View
import android.widget.Toast
import com.chait.stagnypto.R
import com.chait.stagnypto.algorithms.EncryptionDecryptionAES
import com.chait.stagnypto.algorithms.RSAEncryptDecrypt
import com.chait.stagnypto.utils.Constants
import kotlinx.android.synthetic.main.activity_decrypt_result.*
import kotlinx.android.synthetic.main.activity_decrypt_result.radioGroup
import kotlinx.android.synthetic.main.activity_decrypt_result.rbAES
import kotlinx.android.synthetic.main.activity_decrypt_result.rbRSA
import java.lang.Exception
import java.security.Key
import java.util.*

class DecryptResultActivity : AppCompatActivity() {

    //private var secretImagePath: String? = ""
    private var secretMessage: String? = ""
    private var decryptionType = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_decrypt_result)
        initToolbar()
        val intent = intent
        if (intent != null) {
            val bundle = intent.extras
            secretMessage = bundle!!.getString(Constants.EXTRA_SECRET_TEXT_RESULT)
            //secretImagePath = bundle.getString(Constants.EXTRA_SECRET_IMAGE_RESULT)
        }

        if (secretMessage != null) {
            isecretMessage.text = secretMessage
        } //else if (secretImagePath != null) {
            //isecretImage.visibility = View.VISIBLE
            //setSecretImage(secretImagePath!!)
        //}
        radioGroup.setOnCheckedChangeListener { _, _ ->
            if (rbAES.isChecked) {
                decryptRSA.visibility = View.GONE
                privateKey.visibility = View.GONE
                decryptAES.visibility = View.VISIBLE
                secretKey.visibility = View.VISIBLE
                decryptionType = 0
            } else if (rbRSA.isChecked) {
                decryptRSA.visibility = View.VISIBLE
                privateKey.visibility = View.VISIBLE
                decryptAES.visibility = View.GONE
                secretKey.visibility = View.GONE
                decryptionType = 1
            }
        }

        decryptAES.setOnClickListener {
            val cipherText = isecretMessage.text.toString()
            val secretKey = secretKey.text.toString()
            if(cipherText!="" && secretKey!=""){
                val aes = EncryptionDecryptionAES()
                var originalMessage = ""
                try {
                    originalMessage = aes.decrypt(cipherText,secretKey)
                    originalMessageTv.text = originalMessage
                }
                catch (e:Exception){
                    Toast.makeText(this, "Invalid Secret Key", Toast.LENGTH_SHORT).show()
                }
            }
            else if(cipherText==""){
                Toast.makeText(this, "No CipherText Available", Toast.LENGTH_SHORT).show()
            }
            else if(secretKey==""){
                Toast.makeText(this, "No Secret Key Available", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this, "Error in Decryption : Data Missing", Toast.LENGTH_SHORT).show()
            }
        }

        decryptRSA.setOnClickListener {
            val cipherText = isecretMessage.text.toString()
            val secretKey = privateKey.text.toString()
            if(cipherText!="" && secretKey!=""){
                var priv:Key? = null
                try {
                    priv = RSAEncryptDecrypt.loadPrivateKey(secretKey)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (priv != null) {
                    val originalMessage = RSAEncryptDecrypt.decrypt(Base64.getDecoder().decode(cipherText), priv)
                    originalMessageTv.text = originalMessage
                }
                else{
                    Toast.makeText(this, "Error Converting Private Key", Toast.LENGTH_SHORT).show()
                }
            }
            else if(cipherText==""){
                Toast.makeText(this, "No CipherText Available", Toast.LENGTH_SHORT).show()
            }
            else if(secretKey==""){
                Toast.makeText(this, "No Private Key Available", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this, "Error in Decryption : Data Missing", Toast.LENGTH_SHORT).show()
            }
        }

        originalMessageTv.setOnClickListener {
            val cm = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            cm.text = originalMessageTv.text
            Toast.makeText(this, "Original Message Copied to clipboard", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = "Decryption"
        }
    }

//    private fun setSecretImage(path: String) {
//        Picasso.with(this)
//                .load(File(path))
//                .fit()
//                .placeholder(R.drawable.logo_back)
//                .into(isecretImage)
//    }
}

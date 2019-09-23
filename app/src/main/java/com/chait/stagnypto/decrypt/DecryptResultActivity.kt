package com.chait.stagnypto.decrypt

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import com.chait.stagnypto.R
import com.chait.stagnypto.utils.Constants
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_decrypt_result.*
import java.io.File

class DecryptResultActivity : AppCompatActivity() {

    //private var secretImagePath: String? = ""
    private var secretMessage: String? = ""

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

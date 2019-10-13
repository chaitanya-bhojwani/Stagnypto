package com.chait.stagnypto.stego

import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import com.chait.stagnypto.R
import com.chait.stagnypto.utils.Constants
import com.chait.stagnypto.utils.StandardMethods
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_stego.*
import java.io.File
import com.chait.stagnypto.BuildConfig
import android.support.v4.content.FileProvider
import android.view.View
import android.widget.Toast
import android.content.Context
import android.text.ClipboardManager


class StegoActivity : AppCompatActivity(),StegoView {

    private var isSaved = false
    private lateinit var progressDialog: ProgressDialog
    private lateinit var mPresenter: StegoPresenter

    private var stegoImagePath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stego)
        initToolbar()
        if(Constants.encryptionAlgorithm==0){
            aesTV.visibility = View.VISIBLE
            aesSecretKey.visibility = View.VISIBLE
            aesSecretKey.text = Constants.sharedKeyAES
        }
        mPresenter = StegoPresenterImpl(this)
        progressDialog = ProgressDialog(this@StegoActivity)
        progressDialog.setMessage("Please wait...")
        val intent = intent
        if (intent != null) {
            val bundle = intent.extras
            stegoImagePath = bundle!!.getString(Constants.EXTRA_STEGO_IMAGE_PATH)
        }
        setStegoImage(stegoImagePath)
        save.setOnClickListener {
            if (!isSaved) {
                isSaved = mPresenter.saveStegoImage(stegoImagePath)
            } else {
                showToast(R.string.image_is_saved)
            }
        }
        share.setOnClickListener {
            shareStegoImage(stegoImagePath)
        }
        aesSecretKey.setOnClickListener {
            val cm = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            cm.text = aesSecretKey.text
            Toast.makeText(this, "Secret Key Copied to clipboard", Toast.LENGTH_SHORT).show()
        }
    }

    override fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = "Stego Image"
        }
    }

    override fun showToast(message: Int) {
        StandardMethods.showToast(this, message)
    }

    override fun showProgressDialog() {
        if (progressDialog != null && !progressDialog.isShowing) {
            progressDialog.show()
        }
    }

    override fun stopProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

    override fun setStegoImage(path: String) {
        showProgressDialog()
        Picasso.with(this)
                .load(File(path))
                .fit()
                .placeholder(R.drawable.logo_back)
                .into(stegoImage)
        stopProgressDialog()
    }

    override fun saveToMedia(intent: Intent) {
        sendBroadcast(intent)
    }

    override fun shareStegoImage(path: String) {
        if (stegoImagePath != "") {
            val share = Intent(Intent.ACTION_SEND)
            share.type = "image/jpeg"
            val imageURI = FileProvider.getUriForFile(this@StegoActivity, BuildConfig.APPLICATION_ID + ".provider", File(path))
            share.putExtra(Intent.EXTRA_STREAM, imageURI)
            startActivity(Intent.createChooser(share, "Share using...").addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!isSaved) {
            if (stegoImagePath != null) {
                File(stegoImagePath).delete()
            }
        }
    }
}

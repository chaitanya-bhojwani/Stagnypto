package com.chait.stagnypto.encrypt

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.widget.Toolbar
import android.view.View
import com.chait.stagnypto.BuildConfig
import com.chait.stagnypto.R
import com.chait.stagnypto.stego.StegoActivity
import com.chait.stagnypto.utils.Constants
import com.chait.stagnypto.utils.StandardMethods
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_encrypt.*
import java.io.File

class EncryptActivity : AppCompatActivity(), EncryptView {

    private var secretMessageType = Constants.TYPE_TEXT
    private lateinit var progressDialog: ProgressDialog
    private lateinit var mPresenter: EncryptPresenter
    private var whichImage = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_encrypt)
        initToolbar()
        progressDialog = ProgressDialog(this@EncryptActivity)
        progressDialog.setMessage("Please wait...")
        mPresenter = EncryptPresenterImpl(this)
        radioGroup.setOnCheckedChangeListener { _, _ ->
            if (rbAES.isChecked) {
                encryptRSA.visibility = View.GONE
                publicKey.visibility = View.GONE
                encryptAES.visibility = View.VISIBLE
            } else if (rbRSA.isChecked) {
                encryptRSA.visibility = View.VISIBLE
                publicKey.visibility = View.VISIBLE
                encryptAES.visibility = View.GONE
            }
        }
        stegoImage.setOnClickListener {
            val items = arrayOf<CharSequence>(getString(R.string.take_image_dialog), getString(R.string.select_image_dialog))
            val builder = AlertDialog.Builder(this@EncryptActivity)
            builder.setTitle(getString(R.string.select_image_title))
            builder.setCancelable(false)
            builder.setItems(items) { _, item ->
                if (items[item] == getString(R.string.take_image_dialog)) {

                    if (ContextCompat.checkSelfPermission(applicationContext,
                                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(applicationContext,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(this@EncryptActivity,
                                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                Constants.PERMISSIONS_CAMERA)

                    } else {
                        openCamera()
                    }
                } else if (items[item] == getString(R.string.select_image_dialog)) {

                    if (ContextCompat.checkSelfPermission(applicationContext,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(this@EncryptActivity,
                                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                Constants.PERMISSIONS_EXTERNAL_STORAGE)

                    } else {
                        chooseImage()
                    }
                }
            }

            builder.setNegativeButton(getString(R.string.cancel)) { dialogInterface, i -> dialogInterface.dismiss() }

            whichImage = Constants.COVER_IMAGE

            builder.show()
        }

        encryptAES.setOnClickListener {
            val text = getSecretMessage()
            if (text.isNotEmpty()) {
                mPresenter.encryptText()
            } else {
                showToast(R.string.secret_text_empty)
            }
        }

        encryptRSA.setOnClickListener {
            val text = getSecretMessage()
            if (text.isNotEmpty()) {
                mPresenter.encryptText()
            } else {
                showToast(R.string.secret_text_empty)
            }
        }
    }

    private fun getPath(uri: Uri, activity: Activity): String {
        val projection = arrayOf(MediaStore.MediaColumns.DATA)
        val cursor = activity.managedQuery(uri, projection, null, null, null)
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }

    override fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = "Encryption"
        }
    }

     override fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val file = File(Environment
                .getExternalStorageDirectory(), "temp.png")
        val imageUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, Constants.REQUEST_CAMERA)
    }

    override fun chooseImage() {
        val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(
                Intent.createChooser(intent, getString(R.string.choose_image)),
                Constants.SELECT_FILE)
    }

    override fun getCoverImage(): Bitmap {
        return (stegoImage.drawable as BitmapDrawable).bitmap
    }

    override fun getSecretImage(): Bitmap {
        return (stegoImage.drawable as BitmapDrawable).bitmap
    }

    override fun getSharedPrefs(): SharedPreferences {
        return getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
    }

    override fun setSecretMessage(secretMessage: String) {
        secretText.setText(secretMessage)
    }

    override fun setCoverImage(file: File) {
        showProgressDialog()
        Picasso.with(this)
                .load(file)
                .fit()
                .placeholder(R.drawable.logo_back)
                .into(stegoImage)
        stopProgressDialog()
        whichImage = -1
    }

    override fun setSecretImage(file: File) {
        showProgressDialog()
        Picasso.with(this)
                .load(file)
                .fit()
                .placeholder(R.drawable.logo_back)
                .into(stegoImage)
        stopProgressDialog()
        whichImage = -1
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

    override fun startStegoActivity(filePath: String) {
        val intent = Intent(this@EncryptActivity, StegoActivity::class.java)
        intent.putExtra(Constants.EXTRA_STEGO_IMAGE_PATH, filePath)
        startActivity(intent)
    }

    override fun getSecretMessage(): String {
        return secretText.text.toString().trim()
    }

    override fun showToast(message: Int) {
        StandardMethods.showToast(this, message)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            Constants.PERMISSIONS_CAMERA -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            }
            Constants.PERMISSIONS_EXTERNAL_STORAGE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                chooseImage()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.REQUEST_CAMERA) {
                mPresenter.selectImageCamera(whichImage)
            } else if (requestCode == Constants.SELECT_FILE) {
                val selectedImageUri = data!!.data
                val tempPath = getPath(selectedImageUri, this@EncryptActivity)
                mPresenter.selectImage(whichImage, tempPath)
            }
        }
    }
}

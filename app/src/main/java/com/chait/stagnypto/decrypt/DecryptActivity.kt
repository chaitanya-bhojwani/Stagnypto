package com.chait.stagnypto.decrypt

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import com.chait.stagnypto.R
import com.chait.stagnypto.utils.Constants
import com.chait.stagnypto.utils.StandardMethods
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_decrypt.*
import java.io.File

class DecryptActivity : AppCompatActivity(), DecryptView {

    private lateinit var progressDialog: ProgressDialog
    private var isSISelected = false
    private lateinit var mPresenter: DecryptPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_decrypt)
        mPresenter = DecryptPresenterImpl(this)
        initToolbar()
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Please wait...")
        stegoImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(applicationContext,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this@DecryptActivity,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        Constants.PERMISSIONS_EXTERNAL_STORAGE)

            } else {
                chooseImage()
            }
        }
        decrypt.setOnClickListener{
            if (isSISelected) {
                mPresenter.decryptMessage()
            } else {
                showToast(R.string.stego_image_not_selected)
            }
        }
    }

    override fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = "Decryption"
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            Constants.PERMISSIONS_EXTERNAL_STORAGE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                chooseImage()
            }
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.SELECT_FILE) {
                val selectedImageUri = data!!.data
                val tempPath = getPath(selectedImageUri!!, this@DecryptActivity)
                if (tempPath != null) {
                    mPresenter.selectImage(tempPath)
                }
            }
        }
    }

    override fun startDecryptResultActivity(secretMessage: String?, secretImagePath: String?) {
        val intent = Intent(this@DecryptActivity, DecryptResultActivity::class.java)

        if (secretMessage != null) {
            intent.putExtra(Constants.EXTRA_SECRET_TEXT_RESULT, secretMessage)
        }

        if (secretImagePath != null) {
            intent.putExtra(Constants.EXTRA_SECRET_IMAGE_RESULT, secretImagePath)
        }
        startActivity(intent)
    }

    fun getPath(uri: Uri, activity: Activity): String {
        val projection = arrayOf(MediaStore.MediaColumns.DATA)
        val cursor = activity.managedQuery(uri, projection, null, null, null)
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }

    override fun getStegoImage(): Bitmap {
        return (stegoImage.drawable as BitmapDrawable).bitmap
    }

    override fun setStegoImage(file: File) {
        showProgressDialog()
        Picasso.with(this)
                .load(file)
                .fit()
                .placeholder(R.drawable.logo_back)
                .into(stegoImage)
        stopProgressDialog()
        isSISelected = true
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
}

package com.chait.stagnypto.decrypt

import android.graphics.Bitmap
import android.os.Environment
import com.chait.stagnypto.R
import com.chait.stagnypto.utils.StandardMethods
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

internal class DecryptPresenterImpl(private val mView: DecryptView) : DecryptPresenter, DecryptInteractorImpl.DecryptInteractorListener {
    private val mInteractor: DecryptInteractor
    private var stegoImagePath = ""

    init {
        this.mInteractor = DecryptInteractorImpl(this)
    }

    override fun selectImage(path: String) {
        mView.showProgressDialog()

        val stegoFile = File(path)

        stegoImagePath = path

        mView.setStegoImage(stegoFile)
    }

    override fun decryptMessage() {
        if (stegoImagePath.isEmpty()) {
            mView.showToast(R.string.stego_image_not_selected)
        } else {
            mView.showProgressDialog()
            mInteractor.performDecryption(stegoImagePath)
        }
    }

    override fun onPerformDecryptionSuccessText(text: String) {
        mView.stopProgressDialog()
        mView.showToast(R.string.decrypt_success)
        mView.startDecryptResultActivity(text, null)
    }

    override fun onPerformDecryptionSuccessImage(secretImage: Bitmap) {
        mView.stopProgressDialog()
        mView.showToast(R.string.decrypt_success)
        val filePath = storeSecretImage(secretImage)
        mView.startDecryptResultActivity(null, filePath)
    }

    override fun onPerformDecryptionFailure(message: Int) {
        mView.stopProgressDialog()
        mView.showToast(message)
    }

    private fun storeSecretImage(secretImage: Bitmap): String {
        val path = Environment.getExternalStorageDirectory().toString() + File.separator + "CryptoMessenger"

        val folder = File(path)
        var file: File? = null
        var filePath = ""

        if (!folder.exists()) {
            if (folder.mkdirs()) {
                file = File(path, "SI_" + System.currentTimeMillis() + ".png")
            } else {
                mView.stopProgressDialog()
                mView.showToast(R.string.compress_error)
            }
        } else {
            file = File(path, "SI_" + System.currentTimeMillis() + ".png")
        }

        if (file != null) {
            try {
                val fos = FileOutputStream(file)
                secretImage.compress(Bitmap.CompressFormat.PNG, 100, fos)

                fos.flush()
                fos.close()

                filePath = file.absolutePath

            } catch (e1: FileNotFoundException) {
                StandardMethods.showLog("EPI/Error", e1.message)
            } catch (e2: IOException) {
                StandardMethods.showLog("EPI/Error", e2.message)
            }

        }

        return filePath
    }
}

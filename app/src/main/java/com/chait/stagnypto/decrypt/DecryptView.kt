package com.chait.stagnypto.decrypt

import android.graphics.Bitmap
import java.io.File

internal interface DecryptView {

    /**
     *
     * @return Bitmap which is stored in stegoimage imageView
     */
    fun getStegoImage(): Bitmap

    /**
     * Initializes settings for toolbar. (title, onBackArrow, etc.)
     */
    fun initToolbar()

    /**
     * Sets Stego Image to stegoImage imageView
     * @param file image itself, which is got from the path
     */
    fun setStegoImage(file: File)

    /**
     * Shows the toast message
     * @param message is stored in @string resources
     */
    fun showToast(message: Int)

    /**
     * Opens media chooser
     */
    fun chooseImage()

    /**
     * Shows progress dialog
     */
    fun showProgressDialog()

    /**
     * Dismisses progress dialog
     */
    fun stopProgressDialog()

    /**
     * Starts Decrypt Result Activity
     * @param secretMessage Result secret message decoded from stream of bits. Null if doesn't exist
     * @param secretImagePath Result secret image decoded from stream of bits. Null if doesn't exist
     */
    fun startDecryptResultActivity(secretMessage: String?, secretImagePath: String?)
}

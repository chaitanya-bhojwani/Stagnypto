package com.chait.stagnypto.encrypt

import android.content.SharedPreferences
import android.graphics.Bitmap
import java.io.File

interface EncryptView {
    /**
     *
     * @return String secretMessage which is stored inside the editTextView
     */
    fun getSecretMessage(): String

    /**
     *
     * @return Bitmap coverImage which is stored inside the imageView
     */
    fun getCoverImage(): Bitmap

    /**
     *
     * @return Bitmap secretImage which is stored inside the imageView
     */
    fun getSecretImage(): Bitmap

    /**
     *
     * @return SharedPreferences
     */
    fun getSharedPrefs(): SharedPreferences

    /**
     * Sets up the toolbar. Title, onBackButton
     */
    fun initToolbar()

    /**
     * Sets the secret message editTextView
     * @param secretMessage
     */
    fun setSecretMessage(secretMessage: String)

    /**
     * Sets the cover image imageView
     * @param file
     */
    fun setCoverImage(file: File)

    /**
     * Sets the secret image imageview
     * @param file
     */
    fun setSecretImage(file: File)

    /**
     * Shows a toast
     * @param message which is stored in the @string resources
     */
    fun showToast(message: Int)

    /**
     * Shows progress dialog
     */
    fun showProgressDialog()

    /**
     * Dismisses progress dialog
     */
    fun stopProgressDialog()

    /**
     * Opens camera activity
     */
    fun openCamera()

    /**
     * Opens media chooser
     */
    fun chooseImage()

    /**
     * Starts Stego Activity with image path
     * @param filePath which is the temp stego image path after successfullu
     * performed steganography
     */
    fun startStegoActivity(filePath: String)
}
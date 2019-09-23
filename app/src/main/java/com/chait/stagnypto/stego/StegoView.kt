package com.chait.stagnypto.stego

import android.content.Intent

interface StegoView {
    /**
     * Shows toast message
     * @param message is id of a message which is stored in @string resources
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
     * Initializes toolbar. (Title, onBackArrow, etc.)
     */
    fun initToolbar()

    /**
     * Sets stego image to stegoImage imageView
     * @param path to image in Internal Memory
     */
    fun setStegoImage(path: String)

    /**
     * Saves Stego Image to media so that it can easily accessed in media chooser
     * @param intent
     */
    fun saveToMedia(intent: Intent)

    /**
     * Create sharer to external applications (Messenger, WhatsApp, Skype, etc.)
     * @param path
     */
    fun shareStegoImage(path: String)
}
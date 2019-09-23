package com.chait.stagnypto.encrypt

interface EncryptPresenter {
    /**
     * Selects Image from Internal Storage
     *
     * @param type which is either COVER or SECRET
     * @param path indicates path to selected image
     */
    fun selectImage(type: Int, path: String)

    /**
     * Selects Image from Camera Activity
     *
     * @param type which is either COVER or SECRET
     */
    fun selectImageCamera(type: Int)

    /**
     * Encrypts text and uses listeners to perform actions
     */
    fun encryptText()

    /**
     * Encrypts image and uses listeners to perform actions
     */
    fun encryptImage()
}
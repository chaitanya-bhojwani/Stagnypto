package com.chait.stagnypto.decrypt

interface DecryptPresenter {
    /**
     * Opens a media chooser to select an image from Internal Storage
     * @param path to selected image
     */
    fun selectImage(path: String)

    /**
     * Interacts with interactor to start decryption process
     */
    fun decryptMessage()
}
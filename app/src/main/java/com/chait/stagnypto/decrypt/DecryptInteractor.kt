package com.chait.stagnypto.decrypt

interface DecryptInteractor {
    /**
     * Starts performing decryption of image selected from Internal Memory
     * @param path to selected image
     *
     */
    fun performDecryption(path: String)
}
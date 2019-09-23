package com.chait.stagnypto.stego

interface StegoPresenter {
    /**
     * Saves stego image in background by sending broadCast
     * @param stegoPath is path to stego image which should be saved
     * @return true if image is successfully saved.
     */
    fun saveStegoImage(stegoPath: String): Boolean
}
package com.chait.stagnypto.encrypt;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.chait.stagnypto.algorithms.Embedding;
import com.chait.stagnypto.algorithms.EncryptionDecryptionAES;
import com.chait.stagnypto.algorithms.RSAEncryptDecrypt;
import com.chait.stagnypto.utils.Constants;

import java.security.Key;

class EncryptInteractorImpl implements EncryptInteractor {

    private EncryptInteractorListener listener;

    EncryptInteractorImpl(EncryptInteractorListener listener) {
        this.listener = listener;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void performSteganography(String message, Bitmap coverImage, Bitmap secretImage) {
        if (secretImage == null && message != null) {
            if(Constants.encryptionAlgorithm==0){
                String[] encryptedResult = new String[2];
                try {
                    encryptedResult = generateAESMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new EmbedSecretMessage(encryptedResult[0], coverImage, null).execute();
                Constants.sharedKeyAES = encryptedResult[1];
            }
            else if(Constants.encryptionAlgorithm==1){
                Key pub = null;
                try {
                    pub = RSAEncryptDecrypt.loadPublicKey(Constants.publicKeyRSA);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(pub!=null) {
                    String encryptedMessage = RSAEncryptDecrypt.encrypt(message,pub);
                    new EmbedSecretMessage(encryptedMessage, coverImage, null).execute();
                }
                else{
                    new EmbedSecretMessage(message, coverImage, null).execute();
                }
            }
            //new EmbedSecretMessage(message, coverImage, null).execute();
        } else if(secretImage != null){
            new EmbedSecretMessage(null, coverImage, secretImage).execute();
        } else {
            listener.onPerformSteganographyFailure();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String[] generateAESMessage(String message) throws Exception{
        EncryptionDecryptionAES aes = new EncryptionDecryptionAES();
        String[] encryptionResult = aes.encrypt(message);
        return encryptionResult;
    }

    /**
     * AsyncTask which embeds secret message in the background not to block main frame
     */
    private class EmbedSecretMessage extends AsyncTask<Void, Void, Bitmap> {
        String message;
        Bitmap coverImage, secretImage;

        EmbedSecretMessage(String message, Bitmap coverImage, Bitmap secretImage) {
            this.message = message;
            this.coverImage = coverImage;
            this.secretImage = secretImage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            Bitmap stegoImage = null;

            if (message != null && secretImage == null && coverImage != null) {
                stegoImage = Embedding.embedSecretText(this.message, this.coverImage);
            } else if (message == null && secretImage != null && coverImage != null) {
                stegoImage = Embedding.embedSecretImage(this.coverImage, this.secretImage);
            }

            return stegoImage;
        }

        @Override
        protected void onPostExecute(Bitmap stegoImage) {
            if (stegoImage != null) {
                listener.onPerformSteganographySuccessful(stegoImage);
            } else {
                listener.onPerformSteganographyFailure();
            }
        }
    }

    interface EncryptInteractorListener {

        /**
         * Listener which is invoked after successfully performed steganography
         * @param stegoImage Bitmap which is the result image of steganography
         */
        void onPerformSteganographySuccessful(Bitmap stegoImage);

        /**
         * Listener which is invoked after failure during steganography
         */
        void onPerformSteganographyFailure();
    }
}

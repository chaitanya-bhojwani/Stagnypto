package com.chait.stagnypto

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.chait.stagnypto.decrypt.DecryptActivity
import com.chait.stagnypto.encrypt.EncryptActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        encrypt.setOnClickListener {
            val intent = Intent(this, EncryptActivity::class.java)
            // start your next activity
            startActivity(intent)
        }
        decrypt.setOnClickListener {
            val intent = Intent(this, DecryptActivity::class.java)
            // start your next activity
            startActivity(intent)
        }
    }
}

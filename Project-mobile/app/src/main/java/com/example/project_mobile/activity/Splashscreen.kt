package com.example.project_mobile.activity

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.example.project_mobile.R
import com.example.project_mobile.data.Attributes
import com.example.project_mobile.utility.Utility

class Splashscreen : AppCompatActivity() {
    private lateinit var attributesList: MutableList<Attributes>
    var splashProgress: ProgressBar? = null
    var SPLASH_TIME = 3500
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        attributesList = Utility().getAttributesList()
        setContentView(R.layout.activity_splashscreen)
        //This is additional feature, used to run a progress bar
        splashProgress = findViewById(R.id.splashProgress)
        playProgress()
        //Code to start timer and take action after the timer ends
        Handler().postDelayed({ //Do any action here. Now we are moving to next page
            val mySuperIntent = Intent(this@Splashscreen, MainActivity::class.java)
            mySuperIntent.action = Intent.ACTION_SEND
            mySuperIntent.putExtra("dataList", attributesList as java.io.Serializable)
            startActivity(mySuperIntent)
            //This 'finish()' is for exiting the app when back button pressed from Home page which is ActivityHome
            finish()
        }, SPLASH_TIME.toLong())
    }

    //Method to run progress bar for 5 seconds
    private fun playProgress() {
        ObjectAnimator.ofInt(splashProgress, "progress", 100)
                .setDuration(2000)
                .start()
    }
}
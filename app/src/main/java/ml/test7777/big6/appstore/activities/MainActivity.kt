package ml.test7777.big6.appstore.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ml.test7777.big6.appstore.R
import ml.test7777.big6.appstore.custom.Apps

private lateinit var appsList: ArrayList<Apps>

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
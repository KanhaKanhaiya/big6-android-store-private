package ml.test7777.big6.appstore.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ml.test7777.big6.appstore.databinding.ActivityAppDetailsBinding

private lateinit var binding: ActivityAppDetailsBinding

class AppDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}
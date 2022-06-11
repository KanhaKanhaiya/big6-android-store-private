package ml.test7777.big6.appstore.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import ml.test7777.big6.appstore.adapters.AppListAdapter
import ml.test7777.big6.appstore.custom.App
import ml.test7777.big6.appstore.databinding.ActivityMainBinding

private lateinit var  binding: ActivityMainBinding
@SuppressLint("StaticFieldLeak")
private val cloudFirestore = Firebase.firestore
private lateinit var appsList: MutableList<App>

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        getData()
    }

    private fun getData() {
        val collectionRef = cloudFirestore.collection("AppStore")
        collectionRef.get()
            .addOnSuccessListener { result ->

                appsList = ArrayList()

                for (document in result) {
                    appsList.add(document.toObject())
                }

                val adapter = AppListAdapter(appsList, this, binding.appsListRecyclerView)

                binding.appsListRecyclerView.adapter = adapter
                binding.appsListRecyclerView.layoutManager = LinearLayoutManager(this)
                binding.appsListRecyclerView.setHasFixedSize(true)
            }
    }
}

package ml.test7777.big6.appstore.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ml.test7777.big6.appstore.adapters.AppListAdapter
import ml.test7777.big6.appstore.custom.App
import ml.test7777.big6.appstore.databinding.ActivityMainBinding

private lateinit var  binding: ActivityMainBinding
@SuppressLint("StaticFieldLeak")
val cloudFirestore = Firebase.firestore

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        getData()
    }

    private fun getData() {
        val collectionRef = cloudFirestore.collection("AppStore")
        collectionRef.get()
            .addOnSuccessListener { result ->
                val appsList: List<App>
                for (document in result) {
                    TODO("Add App List Handler")
                }

                val adapter = AppListAdapter(appsList)
                binding.appsListRecyclerView.adapter = adapter
                binding.appsListRecyclerView.layoutManager = LinearLayoutManager(this)
                binding.appsListRecyclerView.setHasFixedSize(true)
            }
    }
}

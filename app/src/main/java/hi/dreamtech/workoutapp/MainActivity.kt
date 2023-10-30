package hi.dreamtech.workoutapp

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import hi.dreamtech.workoutapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private val onBackPressedCallBack = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
           exitAppDialog()
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.flStart?.setOnClickListener {
            val intent = Intent(this, ExerciseActivity::class.java)
            startActivity(intent)
        }
        binding?.flBmi?.setOnClickListener {
            val intent = Intent(this, BmiActivity::class.java)
            startActivity(intent)
        }
        binding?.flHistory?.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallBack)
    }
    private fun exitAppDialog(){
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Exit App")
        builder.setMessage("Do you want to exit the Workout App?")

        builder.setPositiveButton("Yes"){dialogInterface,_->
            this@MainActivity.finish()
            dialogInterface.dismiss()
        }
        builder.setNegativeButton("No"){dialogInterface,_->
            dialogInterface.dismiss()
        }
        val alertDialog:AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()

    }
}
package hi.dreamtech.workoutapp

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import hi.dreamtech.workoutapp.databinding.ActivityHistoryBinding
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {
    private var binding : ActivityHistoryBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarHistoryActivity)
        if(supportActionBar!= null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title ="WorkOut History"
        }
        binding?.toolbarHistoryActivity?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        val dao = (application as WorkOutApp).db.historyDao()
        getAllCompletedDates(dao)

        binding?.ivDelete?.setOnClickListener {
              clearHistory(dao)
        }
    }

    private fun clearHistory(historyDao: HistoryDao){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Clear History")
        builder.setMessage("This Action Clears Workout History. Do you want to proceed?")

        builder.setPositiveButton("yes"){dialogInterface,_->
            lifecycleScope.launch {
               historyDao.fetchAllDates().collect{
                   if(it!= null){
                       for(entry in it){
                           val date : String = entry.date
                           historyDao.delete(HistoryEntity(date))
                       }
                   }

               }
            }
        }
        builder.setNegativeButton("No"){dialogInterface,_->
            dialogInterface.dismiss()

        }
        val alertDialog : AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun getAllCompletedDates(dao : HistoryDao){
        lifecycleScope.launch {
            dao.fetchAllDates().collect{allCompletedDatesList->
                if(allCompletedDatesList.isNotEmpty()){
                    binding?.tvHistory?.visibility = View.VISIBLE
                    binding?.rvHistory?.visibility = View.VISIBLE
                    binding?.tvNoDataAvailable?.visibility = View.GONE

                    binding?.rvHistory?.layoutManager = LinearLayoutManager(this@HistoryActivity)

                    val dates = ArrayList<String>()
                    for(date in allCompletedDatesList){
                        dates.add(date.date)
                    }
                    val historyAdapter = HistoryAdapter(ArrayList(dates))
                    binding?.rvHistory?.adapter = historyAdapter
                }else {
                    binding?.tvHistory?.visibility = View.GONE
                    binding?.rvHistory?.visibility = View.GONE
                    binding?.tvNoDataAvailable?.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}

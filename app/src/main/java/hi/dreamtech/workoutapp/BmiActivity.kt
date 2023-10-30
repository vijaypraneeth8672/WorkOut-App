package hi.dreamtech.workoutapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import hi.dreamtech.workoutapp.databinding.ActivityBmiBinding
import java.math.BigDecimal
import java.math.RoundingMode

class BmiActivity : AppCompatActivity() {
    companion object{
        private const val METRIC_UNITS_VIEW = "METRIC_UNIT_VIEW"
        private const val US_UNITS_VIEW = "US_UNIT_VIEW"
    }

    private var currentVisibleView: String = METRIC_UNITS_VIEW

    private var binding : ActivityBmiBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBmiBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolBarBmi)

        if(supportActionBar!= null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "CALCULATE BMI"
        }
        binding?.toolBarBmi?.setNavigationOnClickListener(){
            onBackPressedDispatcher.onBackPressed()
        }

        binding?.rgUnits?.setOnCheckedChangeListener { _, checkedId :Int ->
            if(checkedId == R.id.rbMetricUnits){
                makeVisibleMetricUnitsView()
            }else{
                makeVisibleUsUnitsView()
            }
        }



        binding?.btnCalculateUnits?.setOnClickListener {
            calculateUnits()
        }


    }

    private fun makeVisibleMetricUnitsView() {
        currentVisibleView = METRIC_UNITS_VIEW
        binding?.tilMetricUnitWeight?.visibility = View.VISIBLE
        binding?.tilMetricUnitHeight?.visibility = View.VISIBLE
        binding?.tilUsMetricUnitWeight?.visibility = View.GONE
        binding?.tilMetricUsUnitHeightFeet?.visibility = View.GONE
        binding?.tilMetricUsUnitHeightInch?.visibility = View.GONE

        binding?.etMetricWeight?.text!!.clear()
        binding?.etMetricHeight?.text!!.clear()

        binding?.llDisplayBMIResult?.visibility = View.INVISIBLE
    }

    private fun makeVisibleUsUnitsView(){
        currentVisibleView = METRIC_UNITS_VIEW
        binding?.tilMetricUnitWeight?.visibility = View.INVISIBLE
        binding?.tilMetricUnitHeight?.visibility = View.INVISIBLE
        binding?.tilUsMetricUnitWeight?.visibility = View.VISIBLE
        binding?.tilMetricUsUnitHeightFeet?.visibility = View.VISIBLE
        binding?.tilMetricUsUnitHeightInch?.visibility = View.VISIBLE

        binding?.etUsMetricUnitWeight?.text!!.clear()
        binding?.etUsMetricUnitHeightFeet?.text!!.clear()
        binding?.etUsMetricUnitHeightInch?.text!!.clear()

        binding?.llDisplayBMIResult?.visibility = View.INVISIBLE
    }

    private fun calculateUnits(){
        if(currentVisibleView == METRIC_UNITS_VIEW){
            if(checkingMetricInputs()){
                val weight : Float = binding?.etMetricWeight?.text.toString().toFloat()
                val height :Float = binding?.etMetricHeight?.text.toString().toFloat()/100

                val bmi = weight/(height*height)
                displayBMIResult(bmi)

            }else{
                Toast.makeText(this@BmiActivity,"Please Enter valid Information", Toast.LENGTH_SHORT).show()
            }
        }else{
            if(checkUSInputs()){
                val usWeight :Float = binding?.etUsMetricUnitWeight?.text.toString().toFloat()
                val usFeet : Float = binding?.etUsMetricUnitHeightFeet?.text.toString().toFloat()
                val usInch : Float = binding?.etUsMetricUnitHeightInch?.text.toString().toFloat()

                val usHeight = usInch +usFeet*12
                val bmi = 703*(usWeight/(usHeight*usHeight))
                displayBMIResult(bmi)
            }else{
                Toast.makeText(this@BmiActivity,"Please Enter valid Information", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun checkingMetricInputs() :Boolean {
        var isValid = true
        if(binding?.etMetricWeight?.text.toString().isEmpty()){
            isValid=false
        }else if(binding?.etMetricHeight?.text.toString().isEmpty()){
            isValid = false
        }
        return isValid
    }
    private fun checkUSInputs() : Boolean {
        var isValid = true
        when{
            binding?.etUsMetricUnitWeight?.text.toString().isEmpty() ->{
                isValid = false
            }
            binding?.etUsMetricUnitHeightFeet?.text.toString().isEmpty() ->{
                isValid = false
            }
            binding?.etUsMetricUnitHeightInch?.text.toString().isEmpty() -> {
                isValid = false
            }
        }
        return isValid
    }

    private fun displayBMIResult(bmi: Float){
        val bmiLabel: String
        val bmiDescription: String

        if (bmi.compareTo(15f) <= 0) {
            bmiLabel = "Very severely underweight"
            bmiDescription = "Oops! You really need to take better care of yourself! Eat more!"
        } else if (bmi.compareTo(15f) > 0 && bmi.compareTo(16f) <= 0
        ) {
            bmiLabel = "Severely underweight"
            bmiDescription = "Oops!You really need to take better care of yourself! Eat more!"
        } else if (bmi.compareTo(16f) > 0 && bmi.compareTo(18.5f) <= 0
        ) {
            bmiLabel = "Underweight"
            bmiDescription = "Oops! You really need to take better care of yourself! Eat more!"
        } else if (bmi.compareTo(18.5f) > 0 && bmi.compareTo(25f) <= 0
        ) {
            bmiLabel = "Normal"
            bmiDescription = "Congratulations! You are in a good shape!"
        } else if (java.lang.Float.compare(bmi, 25f) > 0 && java.lang.Float.compare(
                bmi,
                30f
            ) <= 0
        ) {
            bmiLabel = "Overweight"
            bmiDescription = "Oops! You really need to take care of your yourself! Workout maybe!"
        } else if (bmi.compareTo(30f) > 0 && bmi.compareTo(35f) <= 0
        ) {
            bmiLabel = "Obese Class | (Moderately obese)"
            bmiDescription = "Oops! You really need to take care of your yourself! Workout maybe!"
        } else if (bmi.compareTo(35f) > 0 && bmi.compareTo(40f) <= 0
        ) {
            bmiLabel = "Obese Class || (Severely obese)"
            bmiDescription = "OMG! You are in a very dangerous condition! Act now!"
        } else {
            bmiLabel = "Obese Class ||| (Very Severely obese)"
            bmiDescription = "OMG! You are in a very dangerous condition! Act now!"
        }

        binding?.llDisplayBMIResult?.visibility = View.VISIBLE

        val bmiValue = BigDecimal(bmi.toDouble()).setScale(2, RoundingMode.HALF_EVEN).toString()

        binding?.tvBMIValue?.text = bmiValue
        binding?.tvBMIType?.text = bmiLabel
        binding?.tvBMIDescription?.text = bmiDescription
    }
}
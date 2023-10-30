package hi.dreamtech.workoutapp

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import hi.dreamtech.workoutapp.databinding.ActivityExerciseBinding
import hi.dreamtech.workoutapp.databinding.DialogCustomBackConfirmationBinding
import java.util.Locale

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private var binding: ActivityExerciseBinding? = null
    private var restTimer: CountDownTimer? = null;
    private var restProgress = 0

    private var exerciseTimer: CountDownTimer? = null;
    private var exProgress = 0

    private var exerciseList: ArrayList<ExerciseModel>? = null
    private var currentExercisePosition = -1

    private var tts: TextToSpeech? = null

    private var player: MediaPlayer? = null

    private var exerciseAdapter: ExerciseStatusAdapter? = null

    private val onBackPressedCallBack = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            customDialogForBackButton()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolBarExercise)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.toolBarExercise?.setNavigationOnClickListener {
            customDialogForBackButton()// since onBackPressed method is deprecated
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallBack)

        tts = TextToSpeech(this, this)

        exerciseList = Constants.defaultExerciseList()
        setRestView()
        setUpExerciseStatusRecyclerView()

    }


    private fun setUpExerciseStatusRecyclerView() {
        binding?.rvExStatus?.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        exerciseAdapter = ExerciseStatusAdapter(exerciseList!!)
        binding?.rvExStatus?.adapter = exerciseAdapter
    }


    private fun setRestView() {

        try {
            val soundURI = Uri.parse(
                "android.resource://hi.dreamtech.workoutapp/"
                        + R.raw.press_start
            )
            player = MediaPlayer.create(applicationContext, soundURI)
            player?.isLooping = false
            player?.start()

        } catch (e: Exception) {
            e.printStackTrace()
        }






        binding?.flProgressBar?.visibility = View.VISIBLE
        binding?.tvTitle?.visibility = View.VISIBLE
        binding?.tvUpExName?.visibility = View.VISIBLE
        binding?.upcomingLabel?.visibility = View.VISIBLE
        binding?.tvExName?.visibility = View.INVISIBLE
        binding?.flProgressBarEx?.visibility = View.INVISIBLE
        binding?.ivImage?.visibility = View.INVISIBLE
        if (restTimer != null) {
            restTimer!!.cancel()
            restProgress = 0
        }
        binding?.tvUpExName?.text = exerciseList!![currentExercisePosition + 1].getName()

        setRestProgressBar()
    }

    private fun setRestProgressBar() {
        binding?.progressBar?.progress = restProgress

        restTimer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                restProgress++
                binding?.progressBar?.progress = 10 - restProgress
                binding?.tvTimer?.text = (10 - restProgress).toString()

            }

            override fun onFinish() {
                currentExercisePosition++

                exerciseList!![currentExercisePosition].setIsSelected(true)
                exerciseAdapter!!.notifyDataSetChanged()

                setExerciseView()
            }

        }.start()
    }

    private fun setExerciseView() {
        binding?.flProgressBar?.visibility = View.INVISIBLE
        binding?.tvTitle?.visibility = View.INVISIBLE
        binding?.tvUpExName?.visibility = View.INVISIBLE
        binding?.upcomingLabel?.visibility = View.INVISIBLE
        binding?.tvExName?.visibility = View.VISIBLE
        binding?.flProgressBarEx?.visibility = View.VISIBLE
        binding?.ivImage?.visibility = View.VISIBLE
        if (exerciseTimer != null) {
            exerciseTimer?.cancel()
            exProgress = 0
        }
        speakOut(exerciseList!![currentExercisePosition].getName())
        binding?.ivImage?.setImageResource(exerciseList!![currentExercisePosition].getImage())
        binding?.tvExName?.text = exerciseList!![currentExercisePosition].getName()

        setExerciseProgressBar()
    }

    private fun setExerciseProgressBar() {
        binding?.progressBarEx?.progress = exProgress

        exerciseTimer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                exProgress++
                binding?.progressBarEx?.progress = 30 - exProgress
                binding?.tvTimerEx?.text = (30 - exProgress).toString()

            }

            override fun onFinish() {
                if (currentExercisePosition < exerciseList?.size!! - 1) {
                    exerciseList!![currentExercisePosition].setIsSelected(false)
                    exerciseList!![currentExercisePosition].setIsCompleted(true)

                    exerciseAdapter!!.notifyDataSetChanged()
                    setRestView()
                } else {

                    finish()
                    val intent = Intent(this@ExerciseActivity, FinishActivity::class.java)
                    startActivity(intent)

                }
            }

        }.start()
    }


    public override fun onDestroy() {
        super.onDestroy()
        if (restTimer != null) {
            restTimer?.cancel()
            restProgress = 0
        }
        if (exerciseTimer != null) {
            exerciseTimer?.cancel()
            exProgress = 0
        }
        if (tts != null) {
            tts?.stop()
            tts?.shutdown()
        }
        if (player != null) {
            player?.stop()
        }

        binding = null

    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                result == TextToSpeech.LANG_NOT_SUPPORTED
            ) {
                Log.e("TTS", "The language specified is not supported!")
            }
        } else {
            Log.e("TTS", "Initialization Failed!")
        }

    }

    private fun speakOut(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    private fun customDialogForBackButton() {
        val customDialog = Dialog(this)
        val dialogBinding = DialogCustomBackConfirmationBinding.inflate(layoutInflater)

        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)

        dialogBinding.btnYes.setOnClickListener {
            this@ExerciseActivity.finish()
            customDialog.dismiss()
        }
        dialogBinding.btnNo.setOnClickListener {
            customDialog.dismiss()
        }
        customDialog.show()
    }
}
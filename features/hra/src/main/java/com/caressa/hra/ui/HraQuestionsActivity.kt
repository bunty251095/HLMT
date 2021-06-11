package com.caressa.hra.ui

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.caressa.common.constants.Constants
import com.caressa.common.constants.NavigationConstants
import com.caressa.common.utils.AppColorHelper
import com.caressa.hra.R
import com.caressa.hra.adapter.HraQuestionsPagerAdapter
import com.caressa.hra.common.HraDataSingleton
import com.caressa.model.hra.Option
import com.caressa.model.hra.Question
import com.caressa.hra.viewmodel.HraViewModel
import com.caressa.model.AppConfigurationSingleton
import kotlinx.android.synthetic.main.activity_hra_questions.*
import kotlinx.android.synthetic.main.toolbar_hra_summary.*
import kotlinx.android.synthetic.main.toolbar_layout_new.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*

class HraQuestionsActivity : AppCompatActivity()  {

    private val appConfigurationSingleton: AppConfigurationSingleton by inject()
    private var viewPagerAdapter : HraQuestionsPagerAdapter? = null
    private val viewModel: HraViewModel by viewModel()

    private val appColorHelper = AppColorHelper.instance!!

    var personId = ""
    var personName = ""
    var hraTemplateId = ""
    private var isMale = false
    private  val hraDataSingleton = HraDataSingleton.getInstance()!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hra_questions)
        try {
            val i = intent
            if ( i.hasExtra(Constants.PERSON_ID) ) {
                personId = i.getStringExtra(Constants.PERSON_ID)!!
            }
            if ( i.hasExtra(Constants.FIRST_NAME) ) {
                personName = i.getStringExtra(Constants.FIRST_NAME)!!
            }
            if ( i.hasExtra(Constants.HRA_TEMPLATE_ID) ) {
                hraTemplateId = i.getStringExtra(Constants.HRA_TEMPLATE_ID)!!
            }
            Timber.e("PersonId,PersonName,TemplateId--->$personId,$personName,$hraTemplateId")
            initLayout()
            setupToolbar()
        } catch ( e : Exception ) {
            e.printStackTrace()
        }
    }

    @SuppressLint("BinaryOperationInTimber")
    private fun initLayout() {
        isMale = viewModel.gender.equals("1",ignoreCase = true)
        val totalQuestions = if ( isMale ) { 26 } else { 27 }
        viewPagerAdapter = HraQuestionsPagerAdapter(supportFragmentManager,isMale,totalQuestions)
        viewPagerQuestions.adapter = viewPagerAdapter
        viewPagerQuestions.setScrollDuration(600)
        Timber.i("HRAHistory--->" + appConfigurationSingleton.hraHistory)
    }

    fun setCurrentScreen(i: Int) {
        viewPagerQuestions.setCurrentItem(i,true)
        //viewPagerQuestions.currentItem = i
    }

    fun getCurrentScreen(): Int {
        return viewPagerQuestions.currentItem
    }

    override fun onBackPressed() {
        val currentItem = viewPagerQuestions.currentItem
        if ( currentItem == 0 ) {
            viewModel.clearSavedQuestionsData()
            super.onBackPressed()
            //navigateToHomeScreen()
        } else {

            if ( isMale ) {
                when( viewPagerQuestions.currentItem ) {
                    // Condition for MultiSelection Fragments
                    2, 4, 6, 7, 8, 21, 23 , 24 -> {
                        onMultiSelectionBackPressed( viewPagerQuestions.currentItem )
                    }

                    else -> {
                        //viewPagerQuestions.setCurrentItem(currentItem - 1, true)
                        setCurrentScreen( currentItem -1 )
                    }
                }
            } else {
                when( viewPagerQuestions.currentItem ) {
                    // Condition for MultiSelection Fragments
                    2, 4, 6, 7, 8, 9, 22 , 24 ,25 -> {
                        onMultiSelectionBackPressed( viewPagerQuestions.currentItem )
                    }

                    else -> {
                        //viewPagerQuestions.setCurrentItem(currentItem - 1, true)
                        setCurrentScreen( currentItem -1 )
                    }
                }
            }
        }
    }

    @SuppressLint("BinaryOperationInTimber")
    private fun onMultiSelectionBackPressed(currentScreen : Int ) {
        Timber.e("*****MultipleSelectionFragment onBackPressed*****")
        val selectedOptions =  hraDataSingleton.selectedOptionList.filter { it.isSelected }.toMutableList()
        val ques = hraDataSingleton.question
        Timber.e("qCode----->"+ques.qCode)
        Timber.e("Saved_Options-----> $selectedOptions")

        //viewModel.saveNewMultipleOptionResponses(ques.qCode, selectedOptions, ques.category, ques.tabName, "")

        var prevList = hraDataSingleton.getPrevAnsList(currentScreen-1)
        if ( prevList.isEmpty() ) {
            prevList = hraDataSingleton.getPrevAnsList(currentScreen-2)
        }
        Timber.e("prevAnsList---> $prevList")
        saveResponseForNextScreen(selectedOptions,currentScreen)

        when(ques.qCode)  {

            "KNWDIANUM" -> {
                Timber.i("Inside--->"+ques.qCode)
                if ( selectedOptions.any { it.description.equals(resources.getString(R.string.NONE),ignoreCase = true) } ) {
                    viewModel.saveResponse("KNWDIANUM","85_NO",resources.getString(R.string.NO),ques.category,ques.tabName,"")
                    viewModel.clearHRALabValuesBasedOnType("SUGAR")
                }
                setCurrentScreen( currentScreen -1 )
            }

            "KNWLIPNUM" -> {
                Timber.i("Inside--->"+ques.qCode)
                if ( selectedOptions.any { it.description.equals(resources.getString(R.string.NONE),ignoreCase = true) } ) {
                    viewModel.saveResponse("KNWLIPNUM","84_NO",resources.getString(R.string.NO),ques.category,ques.tabName,"")
                    viewModel.clearHRALabValuesBasedOnType("LIPID")
                }
                if ( prevList.any { it.description.equals(resources.getString(R.string.NONE),ignoreCase = true) } ) {
                    viewModel.saveResponse("KNWLIPNUM","84_NO",resources.getString(R.string.NO),ques.category,ques.tabName,"")
                    setCurrentScreen( currentScreen -2 )
                }
                else {
                    setCurrentScreen( currentScreen -1 )
                }
            }

            "HHILL" -> {
                Timber.i("Inside--->"+ques.qCode)
                saveMultipleResponseInDb(selectedOptions,ques)
                if ( prevList.any { it.description.equals(resources.getString(R.string.NONE),ignoreCase = true) } ) {
                    setCurrentScreen( currentScreen -2 )
                } else {
                    setCurrentScreen( currentScreen -1 )
                }
            }

            "EDS" -> {
                Timber.i("Inside--->"+ques.qCode)
                if ( selectedOptions.any { it.description.equals(resources.getString(R.string.NONE),ignoreCase = true) } ) {
                    viewModel.saveResponse(ques.qCode,"DONT","None", ques.category, ques.tabName, "")
                } else {
                    for ( option in selectedOptions ) {
                        val data = option.answerCode.split(",")
                        viewModel.saveResponse(data[1],data[2],option.description,data[0],ques.tabName,"")
                    }
                }
                setCurrentScreen(currentScreen - 1)
            }

            "EXPOSE","CHECKUP" -> {
                Timber.i("Inside--->"+ques.qCode)
                val totalList = ques.optionList.filter { it.answerCode.contains(",",ignoreCase = true) }
                if ( selectedOptions.any { it.description.equals(resources.getString(R.string.NONE),ignoreCase = true) } ) {
                    for ( option in totalList ) {
                        val data = option.answerCode.split(",")
                        viewModel.saveResponseOther(data[0],data[2],option.description,ques.category,ques.tabName,"",ques.qCode,Constants.FALSE)
                    }
                } else {
                    val list = hraDataSingleton.selectedOptionList.filter { !it.description.equals("None",ignoreCase = true) }.toMutableList()
                    for ( option in list ) {
                        val data = option.answerCode.split(",")
                        if ( option.isSelected ) {
                            viewModel.saveResponseOther(data[0],data[1],option.description,ques.category,ques.tabName,"",ques.qCode,Constants.TRUE)
                        } else {
                            viewModel.saveResponseOther(data[0],data[2],option.description,ques.category,ques.tabName,"",ques.qCode,Constants.FALSE)
                        }
                    }
                }
                setCurrentScreen(currentScreen - 1)
            }

            else -> {
                Timber.i("Inside--->"+ques.qCode)
                saveMultipleResponseInDb(selectedOptions,ques)
                setCurrentScreen( currentScreen -1 )
            }
        }
        hraDataSingleton.selectedOptionList.clear()
    }

    private fun setupToolbar() {
        setSupportActionBar(toolBar_hra)
        toolbar_title.text = resources.getString(R.string.TITLE_HRA)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_arrow)
        toolBar_hra.navigationIcon?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
            appColorHelper.primaryColor(), BlendModeCompat.SRC_ATOP)

        toolBar_hra.setNavigationOnClickListener {
            viewModel.clearSavedQuestionsData()
            navigateToHomeScreen()
/*            if (appConfigurationSingleton.hraHistory.submittedWellnessScore.isEmpty()){
                super.onBackPressed()
            } else {
                super.onBackPressed()
                val intentToPass = Intent()
                intentToPass.component = ComponentName(NavigationConstants.APPID, NavigationConstants.HOME)
                intentToPass.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intentToPass)
            }*/
        }
    }

    private fun saveResponseForNextScreen(selectedOptions: MutableList<Option>, currentScreen:Int ) {
        hraDataSingleton.previousAnsList[currentScreen] = selectedOptions
    }

    private fun saveMultipleResponseInDb(selectedOptions: MutableList<Option>, ques : Question) {
        viewModel.saveMultipleOptionResponses(ques.qCode, selectedOptions,ques.category,ques.tabName,"")
    }

    fun backToSelectFamilyMember() {
        viewModel.clearSavedQuestionsData()
        super.onBackPressed()
    }

    private fun navigateToHomeScreen() {
        val intentToPass = Intent()
        intentToPass.component = ComponentName(NavigationConstants.APPID, NavigationConstants.HOME)
        intentToPass.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intentToPass)
    }

/*    override fun onBackPressed() {
        val currentItem = viewPagerQuestions.currentItem
        if (currentItem != 0) {
            viewPagerQuestions.setCurrentItem(viewPagerQuestions.currentItem - 1, true)
        } else {
            finish()
            viewModel.clearHraQuestionsTable()
        }
    }*/

}
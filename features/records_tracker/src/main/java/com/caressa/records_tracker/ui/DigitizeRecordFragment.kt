package com.caressa.records_tracker.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.core.net.toUri
import androidx.navigation.findNavController
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.constants.FirebaseConstants
import com.caressa.common.utils.AppColorHelper
import com.caressa.common.utils.DateHelper
import com.caressa.common.utils.FirebaseHelper
import com.caressa.model.entity.HealthDocument
import com.caressa.model.shr.HealthDataParameter
import com.caressa.records_tracker.R
import com.caressa.records_tracker.adapter.DigitizeRecordParametersAdapter
import com.caressa.records_tracker.common.RecordSingleton
import com.caressa.records_tracker.databinding.FragmentDigitizeRecordBinding
import com.caressa.records_tracker.viewmodel.HealthRecordsViewModel
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*

class DigitizeRecordFragment : BaseFragment() , com.github.barteksc.pdfviewer.listener.OnPageChangeListener ,
    DatePickerDialog.OnDateSetListener {

    private val viewModel : HealthRecordsViewModel by viewModel()
    private lateinit var binding : FragmentDigitizeRecordBinding

    private val appColorHelper = AppColorHelper.instance!!

    private var from = ""
    private var code = ""
    private var uri = ""
    private var recordPath = ""
    private var recordName = ""
    private var recordType = ""
    private var personId = ""
    private var digitizedParamList : List<HealthDataParameter> = ArrayList()

    private var pageNumber: Int = 0
    private val cal = Calendar.getInstance()
    private var digitizeRecordParametersAdapter : DigitizeRecordParametersAdapter? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val record = RecordSingleton.getInstance()!!.getHealthRecord()
            from = it.getString(Constants.FROM)!!
            code = it.getString(Constants.CODE)!!
            uri = it.getString(Constants.URI)!!
            recordPath = record.Path!!
            recordName = record.Name!!
            recordType = record.Type!!
            personId = record.PersonId!!.toString()
            digitizedParamList = RecordSingleton.getInstance()!!.getDigitizedParamList()
            Timber.e("From,code,uri,Path,Name,Type,PersonId----->$from,$code,$uri,$recordPath,$recordName,$recordType,$personId")
            Timber.e("DigitizedParamList----->$digitizedParamList")
        }

        // callback to Handle back button event
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                binding.btnCancelDigitize.performClick()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDigitizeRecordBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        FirebaseHelper.logScreenEvent(FirebaseConstants.HEALTH_RECORDS_DIGITIZE_SCREEN)
        initialise()
        registerObservers()
        setClickable()
        return binding.root
    }

    private fun initialise() {
        setTodayCheckupDate()
        loadDocument()
        digitizeRecordParametersAdapter = DigitizeRecordParametersAdapter(digitizedParamList,viewModel ,requireContext(),this)
        binding.rvDigitizedRecords.setExpanded(true)
        binding.rvDigitizedRecords.adapter = digitizeRecordParametersAdapter
    }

    private fun setTodayCheckupDate() {
        binding.edtCheckupDate.setText(DateHelper.currentDateAsStringddMMMyyyyNew)
    }

    private fun registerObservers() {
        viewModel.ocrUnitExist.observe( viewLifecycleOwner , {
            if ( it != null ) {
                Timber.i("OcrUnitExist----->${it.isExist}")
                digitizeRecordParametersAdapter!!.itemViewRefresh(it.isExist)
            }
        })
        viewModel.ocrSaveDocument.observe( viewLifecycleOwner , {
            if ( it != null ) {
                Timber.i("OcrSaveDocument----->${it.isSaved}")
            }
        })
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadDocument() {
        val webView = binding.docWebview
        webView.webViewClient = WebViewClient()
        val webSettings = binding.docWebview.settings
        webSettings.javaScriptEnabled = true
        webView.settings.setSupportZoom(true)
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true
        webSettings.builtInZoomControls = true

        if ( recordType.equals("PDF",ignoreCase = true) ) {
            binding.pdfView.visibility = View.VISIBLE
            binding.docWebview.visibility = View.GONE
            displayPdf()
        } else if (recordType.equals("IMAGE",ignoreCase = true)) {
            binding.docWebview.visibility = View.VISIBLE
            binding.pdfView.visibility = View.GONE
            //val base = recordPath
            //val imagePath = "file://$base/$recordName"
            val imagePath = uri
            val html = "<html><body><img src=\"$imagePath\" width=\"100%\" height=\"100%\"\"/></body></html>"
            binding.docWebview.loadDataWithBaseURL("", html, "text/html", "utf-8", "")
        }
    }

    private fun setClickable() {

        binding.layoutCalender.setOnClickListener {
            showDatePicker()
        }

        binding.edtCheckupDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnSubmitDigitize.setOnClickListener { view ->
            val data = digitizeRecordParametersAdapter!!.healthParameterList
            val recordedDate = binding.edtCheckupDate.text.toString()
            if (digitizeRecordParametersAdapter!!.validateData()){
                Timber.i("Data=> $data")
                viewModel.callOcrSaveDocumentApi(view,this,data,recordedDate,personId)
            }
        }

        binding.btnCancelDigitize.setOnClickListener {
            performBackClick(it)
        }

    }

    fun performBackClick( view: View ) {
        Timber.e("from,code----->$from,$code")
        // Set Record in Singleton class to Empty
        RecordSingleton.getInstance()!!.setHealthRecord( HealthDocument() )
        val bundle = Bundle()
        bundle.putString(Constants.CODE,code)
        if ( from.equals("View",ignoreCase = true) ) {
            bundle.putString(Constants.FROM,"View")
            view.findNavController().navigate(R.id.action_fragmentDigitize_to_viewRecordsFragment,bundle)
        }
        if ( from.equals("Digitize",ignoreCase = true) ) {
            bundle.putString(Constants.FROM,"Digitize")
            view.findNavController().navigate(R.id.action_fragmentDigitize_to_digitizedRecordsListFragment,bundle)
        }
    }

    private fun displayPdf() {
        //pdfFileName = recordName;
        //val basePath = recordPath
        binding.pdfView.fromUri(uri.toUri())
            .defaultPage(pageNumber)
            .enableSwipe(true)
            .swipeHorizontal(false)
            .onPageChange(this)
            .enableAnnotationRendering(true)
            .scrollHandle( DefaultScrollHandle(context))
            .load()
    }

    override fun onPageChanged(page: Int, pageCount: Int) {
        pageNumber = page
        //binding.txtView.text = pageNumber.toString() + " , " + pageCount
    }

    private fun showDatePicker() {
        val dpd = DatePickerDialog.newInstance(
            this@DigitizeRecordFragment,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH))
        dpd.accentColor = appColorHelper.primaryColor()
        dpd.setTitle(resources.getString(R.string.DATE_OF_CHECKUP))
        dpd.maxDate = cal
        dpd.isThemeDark = false
        dpd.vibrate(false)
        dpd.dismissOnPause(false)
        dpd.showYearPickerFirst(false)
        dpd.show(this.requireActivity().fragmentManager, "CheckupDate")
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val date = year.toString() + "-" + (monthOfYear + 1).toString() + "-" + dayOfMonth.toString()
        val formattedDate = DateHelper.formatDateValue(DateHelper.DATEFORMAT_DDMMMYYYY_NEW, date)
        binding.edtCheckupDate.setText(formattedDate)
    }


}

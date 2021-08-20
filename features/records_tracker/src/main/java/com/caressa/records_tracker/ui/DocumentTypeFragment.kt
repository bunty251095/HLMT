package com.caressa.records_tracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.constants.FirebaseConstants
import com.caressa.common.utils.FirebaseHelper
import com.caressa.common.utils.Utilities
import com.caressa.records_tracker.R
import com.caressa.records_tracker.adapter.DocumentTypeAdapter
import com.caressa.records_tracker.databinding.FragmentDocumentTypeBinding
import com.caressa.records_tracker.model.DocumentType
import com.caressa.records_tracker.viewmodel.HealthRecordsViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*

class DocumentTypeFragment : BaseFragment() {

    private val viewModel : HealthRecordsViewModel by viewModel()
    private lateinit var binding : FragmentDocumentTypeBinding

    private var docTypeCode = ""
    private var from = ""
    private var documentTypeAdapter: DocumentTypeAdapter? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            from = it.getString(Constants.FROM,"")!!
            docTypeCode = it.getString(Constants.CODE,"ALL")!!
            Timber.e("from,DocTypeCode----->$from,$docTypeCode")
        }

        // Callback to Handle back button event
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                binding.btnBackDocType.performClick()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDocumentTypeBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        FirebaseHelper.logScreenEvent(FirebaseConstants.HEALTH_RECORDS_DOCUMENT_TYPE_SCREEN)
        initialise()
        setClickable()
        return binding.root
    }

    private fun initialise() {
        documentTypeAdapter = DocumentTypeAdapter(this, requireContext(), getDocumentTypeList())
        binding.rvDocType.layoutManager = GridLayoutManager(context, 3)
        binding.rvDocType.adapter = documentTypeAdapter
    }

    private fun setClickable() {

        binding.btnBackDocType.setOnClickListener {
            viewModel.deleteRecordsInSessionTable()
            Timber.e("DeletedRecordsInSession.....")
            when( from ) {

                Constants.VIEW -> {
                    val bundle = Bundle()
                    bundle.putString(Constants.CODE,docTypeCode)
                    it.findNavController().navigate(R.id.action_documentTypeFragment_to_viewRecordsFragment)
                }

                Constants.MEDICATION ,Constants.TRACK_PARAMETER -> {
                    requireActivity().finish()
                }

                else -> {
                    it.findNavController().navigate(R.id.action_documentTypeFragment_to_healthRecordsDashboardFragment)
                }
            }
        }

        binding.btnNextDocType.setOnClickListener {
            if ( docTypeCode.equals("ALL",ignoreCase = true) ) {
                Utilities.toastMessageShort(context, "Please Select Document Type.")
            } else {
                performNextBtnClick(it)
            }
        }

    }

    fun performNextBtnClick( view: View ) {
        val bundle = Bundle()
        bundle.putString(Constants.CODE,docTypeCode)
        view.findNavController().navigate(R.id.action_documentTypeFragment_to_uploadRecordFragment , bundle)
        docTypeCode = ""
    }

    private fun getDocumentTypeList(): ArrayList<DocumentType> {
        val list: ArrayList<DocumentType> = ArrayList()
        list.add(DocumentType(resources.getString(R.string.TYPE_PATHOLOGY_LAB_REPORT), "LAB", R.drawable.img_pathology))
        list.add(DocumentType(resources.getString(R.string.TYPE_HOSPITAL_REPORT), "HOS", R.drawable.img_hospital_report))
        list.add(DocumentType(resources.getString(R.string.TYPE_DOCTOR_PRESCRIPTION), "PRE", R.drawable.img_prescription))
        list.add(DocumentType(resources.getString(R.string.TYPE_DIET_PLAN), "DIET_PLAN", R.drawable.img_diet_plan))
        list.add(DocumentType(resources.getString(R.string.TYPE_FITNESS_PLAN), "FIT_PLAN", R.drawable.img_fitness_plan))
        list.add(DocumentType(resources.getString(R.string.TYPE_OTHER_DOCUMENT), "OTR", R.drawable.img_other))
        return list
    }

    fun setDocTypeCode(code: String) {
        docTypeCode = code
        //Timber.i("Code=>"+DocTypeCode);
    }
}
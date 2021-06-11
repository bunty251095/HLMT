package com.caressa.records_tracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.utils.Utilities
import com.caressa.model.entity.RecordInSession
import com.caressa.records_tracker.R
import com.caressa.records_tracker.adapter.SelectRelationshipAdapter
import com.caressa.records_tracker.adapter.SelectedRecordToUploadAdapter
import com.caressa.records_tracker.databinding.FragmentSelectRelationBinding
import com.caressa.records_tracker.viewmodel.HealthRecordsViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class SelectRelationFragment : BaseFragment() {

    private val viewModel : HealthRecordsViewModel by viewModel()
    private lateinit var binding : FragmentSelectRelationBinding

    private var code = ""
    private var notes = ""
    private var personId = ""
    private var relativeId = ""
    private var personRel = ""
    private var personName = ""
    private var selectedRecordToUploadAdapter : SelectedRecordToUploadAdapter? = null
    private var selectRelationshipAdapter : SelectRelationshipAdapter? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            code = it.getString(Constants.CODE)!!
            Timber.e("code----->$code")
        }

        // Callback to Handle back button event
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                binding.btnBackSelectRelation.performClick()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSelectRelationBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        initialise()
        registerObservers()
        setClickable()
        return binding.root
    }

    private fun initialise() {
        viewModel.getRecordsInSession()
        viewModel.getUserRelatives()

        selectedRecordToUploadAdapter = SelectedRecordToUploadAdapter(this, requireContext())
        binding.rvSelectedRecords.adapter = selectedRecordToUploadAdapter

        selectRelationshipAdapter = SelectRelationshipAdapter( this,viewModel , requireContext() )
        binding.rvRelativesList.adapter = selectRelationshipAdapter
    }

    private fun registerObservers() {
        viewModel.saveDocument.observe( viewLifecycleOwner , {})
    }

    private fun setClickable() {

        binding.btnAddMore.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("code",code)
            it.findNavController().navigate(R.id.action_selectRelationFragment_to_uploadRecordFragment , bundle)
        }

        binding.btnBackSelectRelation.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(Constants.CODE,code)
            it.findNavController().navigate(R.id.action_selectRelationFragment_to_uploadRecordFragment , bundle)
        }

        binding.btnSaveSelectRelation.setOnClickListener { view ->
            if ( selectedRecordToUploadAdapter!!.documentList.isNotEmpty() ) {
                notes = binding.edtNotes.text.toString()
                viewModel.getRecordToUploadList( code ,notes,relativeId,personRel,personName )
                viewModel.recordToUploadRequestList.observe( viewLifecycleOwner , Observer {
                    if ( it != null ) {
                        val recordToUploadRequestList = it
                        if ( recordToUploadRequestList.isNotEmpty() ) {
                            viewModel.callSaveDocumentApi("Save",code,recordToUploadRequestList,personName,personRel,view)
                        }
                    }
                })
            } else {
                Utilities.toastMessageShort(context,resources.getString(R.string.ERROR_SELECT_RECORD_TO_PROCEED))
            }
        }

    }

    fun setNoDataView(toShow: Boolean) {
        if (toShow) {
            binding.rvSelectedRecords.visibility = View.GONE
            binding.txtNoDocuments.visibility = View.VISIBLE
        } else {
            binding.rvSelectedRecords.visibility = View.VISIBLE
            binding.txtNoDocuments.visibility = View.GONE
        }
    }

    fun deleteRecordInSession( record : RecordInSession) {
        viewModel.deleteRecordInSession(record)
    }

    fun setPersonID(value: String) {
        personId = value
        Timber.i("PersonId----->$personId")
    }

    fun setRelativeID(value: String) {
        relativeId = value
        Timber.i("RelativeId----->$relativeId")
    }

    fun setPersonRel(value: String) {
        personRel = value
        Timber.i("PersonRelation----->$personRel")
    }

    fun setPersonName(value: String) {
        personName = value
        Timber.i("PersonName----->$personName")
    }

}
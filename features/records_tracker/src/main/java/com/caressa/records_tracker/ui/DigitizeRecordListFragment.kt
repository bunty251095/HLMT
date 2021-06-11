package com.caressa.records_tracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.lifecycle.Observer
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.utils.Utilities
import com.caressa.model.entity.HealthDocument
import com.caressa.records_tracker.R
import com.caressa.records_tracker.adapter.HealthRecordsAdapter
import com.caressa.records_tracker.common.DataHandler
import com.caressa.records_tracker.common.RecordSingleton
import com.caressa.records_tracker.databinding.FragmentDigitizeRecordListBinding
import com.caressa.records_tracker.viewmodel.HealthRecordsViewModel
import com.caressa.repository.utils.Resource
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class DigitizeRecordListFragment : BaseFragment() {

    private val viewModel : HealthRecordsViewModel by viewModel()
    private lateinit var binding : FragmentDigitizeRecordListBinding

    private var code = ""
    private var isDataChanged = false
    private var healthRecordsAdapter : HealthRecordsAdapter? = null
    private val recordsList: MutableList<HealthDocument> =  mutableListOf()
    private var animation : LayoutAnimationController? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDigitizeRecordListBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        arguments?.let {
            code = it.getString(Constants.CODE)!!
            Timber.e("code----->$code")
        }
        initialise()
        registerObservers()
        return binding.root
    }

    private fun initialise() {
        animation = AnimationUtils.loadLayoutAnimation(context,R.anim.layout_animation_slide_from_bottom)
        viewModel.getHealthDocumentsWhereCode(code)

        healthRecordsAdapter = HealthRecordsAdapter(requireContext(),viewModel,requireActivity(),"Digitize")
        binding.rvDigitizeRecords.layoutAnimation = animation
        binding.rvDigitizeRecords.adapter = healthRecordsAdapter
    }

    private fun registerObservers() {

        viewModel.listDocuments.observe( viewLifecycleOwner , Observer {
            if ( it != null) {
                if ( it.status == Resource.Status.SUCCESS) {
                    var list =  it.data!!.documents
                    val documentsList: MutableList<HealthDocument> =  mutableListOf()
                    if ( list != null ) {
                        list = list.filter { it.Code == "LAB" }
                        documentsList.clear()
                        documentsList.addAll(list)
                        if ( documentsList.size > 0) {
                            binding.rvDigitizeRecords.visibility = View.VISIBLE
                            binding.layoutNoLabReports.visibility = View.GONE
                            binding.rvDigitizeRecords.adapter = healthRecordsAdapter
                            healthRecordsAdapter!!.updateList(documentsList,code)
                        } else {
                            binding.rvDigitizeRecords.visibility = View.GONE
                            binding.layoutNoLabReports.visibility = View.VISIBLE
                        }
                        stopShimmer()
                    }
                }
            }
        })

        viewModel.healthDocumentsList.observe( viewLifecycleOwner , Observer {
            if ( it != null ) {
                val labRecords = it
                recordsList.clear()
                recordsList.addAll(labRecords)
                //recordsList.addAll(allRecords.filter { it.Code == "LAB" })
                if ( recordsList.size > 0 ) {
                    Timber.i("RecordCount----->${recordsList.size}")
                    binding.rvDigitizeRecords.visibility = View.VISIBLE
                    binding.layoutNoLabReports.visibility = View.GONE
                    binding.rvDigitizeRecords.layoutAnimation = animation
                    healthRecordsAdapter!!.updateList(recordsList,code)
                    binding.rvDigitizeRecords.scheduleLayoutAnimation()
                } else {
                    startShimmer()
                    viewModel.callListDocumentsApi(true)
                }
            }
        })

        viewModel.postDownload.observe( viewLifecycleOwner , Observer {
            if ( it != null) {
                Timber.e("postDownload----->$it")
                val record = RecordSingleton.getInstance()!!.getHealthRecord()
                if ( !Utilities.isNullOrEmptyOrZero(record.Id.toString()) ) {
                    when {
                        it.equals(Constants.VIEW,ignoreCase = true) -> {
                            DataHandler(requireContext()).viewRecord(record)
                        }
                        it.equals(Constants.SHARE,ignoreCase = true) -> {
                            DataHandler(requireContext()).shareDataWithAppSingle(record,viewModel)
                        }
                        it.equals(Constants.DIGITIZE,ignoreCase = true) -> {
                            viewModel.callDigitizeDocumentApi(Constants.DIGITIZE,code,record)
                        }
                    }
                }
            }
        })

        viewModel.documentStatus.observe( viewLifecycleOwner , {
            if ( it != null) {
                if ( it == Resource.Status.SUCCESS) {
                    isDataChanged = true
                    viewModel.getHealthDocumentsWhereCode(code)
                }
            }
        })

        viewModel.downloadDoc.observe( viewLifecycleOwner , {})
        viewModel.ocrDigitizeDocument.observe( viewLifecycleOwner , {})
        viewModel.deleteDocument.observe( viewLifecycleOwner , {})
    }

    private fun startShimmer() {
        binding.layoutDigitizedRecordsShimmer.startShimmer()
        binding.layoutDigitizedRecordsShimmer.visibility = View.VISIBLE
    }

    private fun stopShimmer() {
        binding.layoutDigitizedRecordsShimmer.stopShimmer()
        binding.layoutDigitizedRecordsShimmer.visibility = View.GONE
    }

}
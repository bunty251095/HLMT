package com.caressa.records_tracker.ui

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.constants.FirebaseConstants
import com.caressa.common.utils.AppColorHelper
import com.caressa.common.utils.FirebaseHelper
import com.caressa.common.utils.Utilities
import com.caressa.common.view.SpinnerAdapter
import com.caressa.common.view.SpinnerModel
import com.caressa.model.entity.HealthDocument
import com.caressa.records_tracker.R
import com.caressa.records_tracker.adapter.HealthRecordsAdapter
import com.caressa.records_tracker.common.DataHandler
import com.caressa.records_tracker.common.RecordSingleton
import com.caressa.records_tracker.databinding.FragmentViewRecordsBinding
import com.caressa.records_tracker.viewmodel.HealthRecordsViewModel
import com.caressa.repository.utils.Resource
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*

class ViewRecordsFragment : BaseFragment()  {

    private val viewModel : HealthRecordsViewModel by viewModel()
    private lateinit var binding : FragmentViewRecordsBinding

    private val appColorHelper = AppColorHelper.instance!!

    private var from = ""
    private var code = ""
    private var sync = false
    private var isDataChanged = false
    private var healthRecordsAdapter : HealthRecordsAdapter? = null
    private val recordsList: MutableList<HealthDocument> =  mutableListOf()
    private var spinnerAdapter: SpinnerAdapter? = null
    private var categoryList : ArrayList<SpinnerModel>? = null
    private var animation : LayoutAnimationController? = null
    val record: HealthDocument? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentViewRecordsBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        FirebaseHelper.logScreenEvent(FirebaseConstants.HEALTH_RECORDS_VIEW_SCREEN)
        arguments?.let {
            from = it.getString(Constants.FROM)!!
            code = it.getString(Constants.CODE)!!
            Timber.e("from,code----->$from,$code")
        }
        initialise()
        registerObservers()
        setClickable()
        return binding.root
    }

    private fun initialise() {
        animation = AnimationUtils.loadLayoutAnimation(context,R.anim.layout_animation_slide_from_bottom)
        categoryList = DataHandler(requireContext()).getCategoryList(requireContext())
        spinnerAdapter = SpinnerAdapter(requireContext(), categoryList!!)
        binding.docCategorySpinner.adapter = spinnerAdapter

        binding.layoutSpinner.setBackgroundColor( appColorHelper.primaryColor() )

        healthRecordsAdapter = HealthRecordsAdapter(requireContext(),viewModel,requireActivity(),"View")
        binding.rvRecords.layoutAnimation = animation
        binding.rvRecords.adapter = healthRecordsAdapter

        if ( from.equals(Constants.DASHBOARD,ignoreCase = true) ) {
            startShimmer()
            viewModel.callListDocumentsApi(true)
        } else {
            viewModel.getHealthDocumentsWhereCode(code)
            sync = true
            binding.docCategorySpinner.setSelection(DataHandler(requireContext()).getCategoryPositionByCode(code))
        }

        binding.docCategorySpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                spinnerAdapter!!.selectedPos = position
                val name: String = categoryList!![position].name
                val categoryCodeValue: String = categoryList!![position].code
                binding.txtModelSpinner.text = name
                Timber.i("Selected Item:: $position,$name,$categoryCodeValue")
                code = categoryCodeValue
                if (sync) {
                    viewModel.getHealthDocumentsWhereCode(categoryCodeValue)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

    }

    private fun registerObservers() {

        viewModel.listDocuments.observe( viewLifecycleOwner) {
            if (it != null) {
                if (it.status == Resource.Status.SUCCESS) {
                    try {
                        val list = it.data!!.documents
                        val documentsList: MutableList<HealthDocument> = mutableListOf()
                        if (!list.isNullOrEmpty()) {
                            documentsList.clear()
                            documentsList.addAll(list)
                            if (documentsList.size > 0) {
                                binding.rvRecords.visibility = View.VISIBLE
                                binding.layoutNoRecords.visibility = View.GONE
                                binding.rvRecords.adapter = healthRecordsAdapter
                                healthRecordsAdapter!!.updateList(documentsList, code)
                            } else {
                                binding.rvRecords.visibility = View.GONE
                                binding.layoutNoRecords.visibility = View.VISIBLE
                            }
                            sync = true
                        } else {
                            binding.rvRecords.visibility = View.GONE
                            binding.layoutNoRecords.visibility = View.VISIBLE
                        }
                        stopShimmer()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        viewModel.healthDocumentsList.observe( viewLifecycleOwner) {
            if (it != null) {
                val labRecords = it
                recordsList.clear()
                recordsList.addAll(labRecords)
                //recordsList.addAll(allRecords.filter { it.Code == "LAB" })
                if (recordsList.size > 0) {
                    Timber.e("RecordCount--->{recordsList.size}",)
                    binding.rvRecords.visibility = View.VISIBLE
                    binding.layoutNoRecords.visibility = View.GONE
                    binding.rvRecords.layoutAnimation = animation
                    healthRecordsAdapter!!.updateList(recordsList, code)
                    binding.rvRecords.scheduleLayoutAnimation()
                } else {
                    binding.rvRecords.visibility = View.GONE
                    binding.layoutNoRecords.visibility = View.VISIBLE
                }
            }
        }

        viewModel.postDownload.observe( viewLifecycleOwner , Observer {
            if ( it != null) {
                Timber.e("postDownload----->$it")
                val record = RecordSingleton.getInstance()!!.getHealthRecord()
                if ( !Utilities.isNullOrEmptyOrZero(record.Id.toString()) ) {
                    when {
                        it.equals(Constants.VIEW,ignoreCase = true) -> {
                            DataHandler(requireContext()).viewDocument(record)
                        }
                        it.equals(Constants.SHARE,ignoreCase = true) -> {
                            DataHandler(requireContext()).shareDataWithAppSingle(record,viewModel)
                        }
                        it.equals(Constants.DIGITIZE,ignoreCase = true) -> {
                            viewModel.callDigitizeDocumentApi(Constants.VIEW,code,record)
                        }
                    }
                }
            }
        })

        viewModel.documentStatus.observe( viewLifecycleOwner , Observer {
            if ( it != null) {
                if ( it == Resource.Status.SUCCESS) {
                    isDataChanged = true
                    viewModel.getHealthDocumentsWhereCode(code)
                }
            }
        })

        viewModel.downloadDoc.observe( viewLifecycleOwner , {})
        viewModel.deleteDocument.observe( viewLifecycleOwner , {})
        viewModel.ocrDigitizeDocument.observe( viewLifecycleOwner , {})
    }

    private fun setClickable() {

        binding.txtModelSpinner.setOnClickListener {
            binding.docCategorySpinner.performClick()
        }

        binding.btnUpload.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(Constants.CODE,code)
            bundle.putString(Constants.FROM,Constants.VIEW)
            it.findNavController().navigate(R.id.action_viewRecordsFragment_to_documentTypeFragment,bundle)
        }

    }

    private fun startShimmer() {
        binding.layoutRecordsShimmer.startShimmer()
        binding.layoutRecordsShimmer.visibility = View.VISIBLE
    }

    private fun stopShimmer() {
        binding.layoutRecordsShimmer.stopShimmer()
        binding.layoutRecordsShimmer.visibility = View.GONE
    }

}
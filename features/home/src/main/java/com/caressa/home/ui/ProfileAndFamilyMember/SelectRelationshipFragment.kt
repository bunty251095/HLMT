package com.caressa.home.ui.ProfileAndFamilyMember

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.utils.AppColorHelper
import com.caressa.common.utils.Utilities

import com.caressa.home.R
import com.caressa.home.adapter.FamilyRelationshipAdapter
import com.caressa.home.databinding.FragmentSelectRelationshipBinding
import com.caressa.home.viewmodel.ProfileFamilyMemberViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class SelectRelationshipFragment : BaseFragment() {

    private val viewModel : ProfileFamilyMemberViewModel by viewModel()
    private lateinit var binding : FragmentSelectRelationshipBinding

    private val appColorHelper = AppColorHelper.instance!!

    private var from = ""
    private var relationShipCode = ""
    private var relation = ""
    private var gender = ""
    private var familyRelationshipAdapter: FamilyRelationshipAdapter? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            from = it.getString(Constants.FROM,"")!!
            Timber.e("from----->$from")
        }

        // Callback to Handle back button event
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                binding.btnBackRelation.performClick()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSelectRelationshipBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        initialise()
        setClickable()
        return binding.root
    }

    private fun initialise() {
       // binding.btnNext.setEnabled(false)
        viewModel.getFamilyRelationshipList()
        familyRelationshipAdapter = FamilyRelationshipAdapter(this,viewModel , requireContext())
        binding.rvFamilyRelation.adapter = familyRelationshipAdapter
    }

    private fun setClickable() {

        binding.btnAddRelation.setOnClickListener {
            if ( !Utilities.isNullOrEmpty(relationShipCode) && !Utilities.isNullOrEmpty(relation) && !Utilities.isNullOrEmpty(gender) ) {
                if ( (relationShipCode.equals(Constants.FATHER_RELATIONSHIP_CODE,ignoreCase = true) && isMemberAlreadyExist()) ||
                     (relationShipCode.equals(Constants.MOTHER_RELATIONSHIP_CODE,ignoreCase = true) && isMemberAlreadyExist()) ) {
                    val msg = resources.getString(R.string.MSG_ALREADY_ADDED)+" " + relation
                    Utilities.toastMessageShort(context,msg)
                } else {
                    navigateToAddRelativeWithBundle(it,from)
                }
            } else {
                Utilities.toastMessageShort(context,resources.getString(R.string.ERROR_SELECT_RELATION_FIRST))
            }
        }

/*        binding.btnBackRelation.setOnClickListener {
            it.findNavController().navigate(R.id.action_selectRelationshipFragment2_to_familyMembersListFragment2)
        }*/

        binding.btnBackRelation.setOnClickListener {
            when {
                from.equals(Constants.HRA,ignoreCase = true) -> {
                    requireActivity().finish()
                }
                else -> {
                    it.findNavController().navigate(R.id.action_selectRelationshipFragment2_to_familyMembersListFragment2)
                }
            }
        }
    }

    private fun isMemberAlreadyExist( ) : Boolean {
        var isExist = false
        viewModel.alreadyExistRelatives.observe( viewLifecycleOwner , Observer {
            if ( it != null ) {
                isExist = it.isNotEmpty()
            }
        })
        return isExist
    }

    private fun navigateToAddRelativeWithBundle(view: View,from:String) {
        val bundle = Bundle()
        bundle.putString(Constants.RELATION_CODE,relationShipCode)
        bundle.putString(Constants.RELATION,relation)
        bundle.putString(Constants.GENDER,gender)
        bundle.putString(Constants.FROM,from)
        relationShipCode = ""
        relation = ""
        gender = ""
        view.findNavController().navigate(R.id.action_selectRelationshipFragment2_to_addFamilyMemberFragment2,bundle)
    }

    fun setRelationShipCode(relationShipCode: String) {
        this.relationShipCode = relationShipCode
    }

    fun setRelation(relation: String) {
        this.relation = relation
    }

    fun setGender(gender: String) {
        this.gender = gender
    }

}

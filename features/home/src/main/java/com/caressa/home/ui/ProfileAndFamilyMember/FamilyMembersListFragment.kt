package com.caressa.home.ui.ProfileAndFamilyMember

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.constants.FirebaseConstants
import com.caressa.common.utils.AppColorHelper
import com.caressa.common.utils.FirebaseHelper
import com.caressa.home.R
import com.caressa.home.adapter.FamilyMembersAdapter
import com.caressa.home.databinding.FragmentFamilyMembersListBinding
import com.caressa.home.viewmodel.ProfileFamilyMemberViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class FamilyMembersListFragment : BaseFragment() {

    private val viewModel : ProfileFamilyMemberViewModel by viewModel()
    private lateinit var binding : FragmentFamilyMembersListBinding

    private var from = ""
    private var familyMembersAdapter: FamilyMembersAdapter? = null
    private val appColorHelper = AppColorHelper.instance!!

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            from = it.getString(Constants.FROM,"")!!
            Timber.e("from--->$from")
        }
        if ( from.equals(Constants.HRA,ignoreCase = true) ) {
            val bundle = Bundle()
            bundle.putString(Constants.FROM, Constants.HRA)
            //bundle.putString(Constants.CODE, "ALL")
            findNavController().navigate(R.id.action_familyMembersListFragment2_to_selectRelationshipFragment2,bundle)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFamilyMembersListBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        FirebaseHelper.logScreenEvent(FirebaseConstants.FAMILY_MEMBER_LIST_SCREEN)
        initialise()
        setClickable()
        return binding.root
    }

    private fun initialise() {
       // viewModel.getUserRelativesList()
        startShimmer()
        viewModel.getLoggedInPersonDetails()
        viewModel.callListRelativesApi(true,this)
        familyMembersAdapter = FamilyMembersAdapter( this , viewModel , requireContext() )
        binding.rvFamilyMembers.adapter = familyMembersAdapter
        viewModel.listRelatives.observe(viewLifecycleOwner) { }
    }

    private fun setClickable() {

        binding.btnAddMember.setOnClickListener {
            it.findNavController().navigate(R.id.action_familyMembersListFragment2_to_selectRelationshipFragment2)
        }

        binding.imgBack.setOnClickListener {
            //it.findNavController().navigate(R.id.action_familyMembersListFragment2_to_selectRelationshipFragment2)
            requireActivity().onBackPressed()
        }
    }

    private fun startShimmer() {
        binding.rvFamilyMembersShimmer.startShimmer()
        binding.rvFamilyMembersShimmer.visibility = View.VISIBLE
    }

    fun stopShimmer() {
        binding.rvFamilyMembersShimmer.stopShimmer()
        binding.rvFamilyMembersShimmer.visibility = View.GONE
    }

    fun noDataView() {
        binding.layoutRelatives.visibility = View.GONE
        binding.txtNoRelatives.visibility = View.VISIBLE
    }

}

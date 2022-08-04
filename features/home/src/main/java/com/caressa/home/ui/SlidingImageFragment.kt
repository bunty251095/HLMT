package com.caressa.home.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.caressa.common.utils.Utilities
import com.caressa.home.R
import com.caressa.home.databinding.FragmentSlidingImageBinding
import com.caressa.home.views.HomeBinding.loadImageNew

class SlidingImageFragment : Fragment(R.layout.fragment_sliding_image) {

    private lateinit var binding: FragmentSlidingImageBinding

    private var landingImagesArray: Array<String> = arrayOf(
        "https://picsum.photos/id/0/5616/3744.jpg",
        "https://picsum.photos/id/2/5616/3744.jpg",
        "https://picsum.photos/id/1/5616/3744.jpg",
        "https://picsum.photos/id/3/5616/3744.jpg",
        "https://picsum.photos/id/4/5616/3744.jpg")

    private var landingImagesArray1: Array<String> = arrayOf(
        "https://cdn.pixabay.com/photo/2020/02/13/10/29/bees-4845211__340.jpg",
        "https://cdn.pixabay.com/photo/2020/04/24/08/57/street-5085971__340.jpg",
        "https://cdn.pixabay.com/photo/2020/03/11/01/53/landscape-4920705__340.jpg",
        "https://cdn.pixabay.com/photo/2020/02/11/12/07/portofino-4839356__340.jpg")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSlidingImageBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        initialise()
        return binding.root
    }

    fun initialise() {
        val position = requireArguments().getInt(ARG_POSITION)
        binding.slidingImage.loadImageNew(landingImagesArray[position])

        binding.slidingImage.setOnClickListener {
            Utilities.toastMessageShort(requireContext(),"$position Clicked")
        }
    }

    companion object {
        const val ARG_POSITION = "position"
        fun getInstance(position: Int): Fragment {
            val fragment = SlidingImageFragment()
            val bundle = Bundle()
            bundle.putInt(ARG_POSITION, position)
            fragment.arguments = bundle
            return fragment
        }
    }

}

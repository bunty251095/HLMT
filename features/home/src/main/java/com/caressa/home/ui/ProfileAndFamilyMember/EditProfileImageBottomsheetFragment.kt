package com.caressa.home.ui.ProfileAndFamilyMember

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caressa.home.R
import com.caressa.home.common.DataHandler
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import timber.log.Timber

class EditProfileImageBottomsheetFragment : BottomSheetDialogFragment() {

    var hasProfileImage = false
    var items : List<DataHandler.Option> = listOf()
    private var mListener: EditProfileImageItemClickListener? = null

    companion object {

        fun newInstance(itemCount: Boolean,listener : EditProfileImageItemClickListener): EditProfileImageBottomsheetFragment =
            EditProfileImageBottomsheetFragment().apply {
                hasProfileImage = itemCount
                mListener = listener
                arguments = Bundle().apply {
                    //putBoolean(hasProfileImage, itemCount)
                }
            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //dialog!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        return inflater.inflate(R.layout.fragment_edit_profile_image_bottomsheet_list_dialog, container, false)
    }

    override fun getTheme(): Int {
        //return super.getTheme();
        return R.style.BottomSheetDialog
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //return super.onCreateDialog(savedInstanceState);
        return BottomSheetDialog(requireContext(), theme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.e("hasProfileImage----->$hasProfileImage")
        val list = view.findViewById<RecyclerView>(R.id.list)
        items = if ( hasProfileImage ) {
            DataHandler(requireContext()).getHasProfileImageOptionList()
        } else {
            DataHandler(requireContext()).getNotProfileImageOptionList()
        }
        val adapter = ItemAdapter(items)
        list?.layoutManager = LinearLayoutManager(context)
        list?.adapter = adapter
        adapter.setOnItemClickListenerListener( object  : ItemClickListener {
            override fun onItemClick(position: Int,code:String) {
               dismiss()
                mListener!!.onEditProfileImageItemClick(position,code)
                Timber.e("position,code----->$position,$code")
            }

        })
    }

    interface EditProfileImageItemClickListener {
        fun onEditProfileImageItemClick(position: Int,code:String)
    }

    // ItemAdapter
    private inner class ItemAdapter constructor( val items : List<DataHandler.Option>) :
        RecyclerView.Adapter<ViewHolder>() {

        private var mListener: ItemClickListener? = null

        fun setOnItemClickListenerListener(listener: ItemClickListener) {
            this.mListener = listener
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context), parent)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.img.setImageResource(item.imageId)
            holder.text.text = item.title
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.img.backgroundTintList = ContextCompat.getColorStateList(requireContext(),item.color)
            }
            holder.itemView.setOnClickListener {
                mListener!!.onItemClick(position,item.code)
            }
        }

        override fun getItemCount(): Int {
            return items.size
        }

    }

    private inner class ViewHolder constructor(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.fragment_edit_profile_image_bottomsheet_list_dialog_item, parent, false)) {
        //val layoutTtem: ConstraintLayout = itemView.findViewById(R.id.layout_item)
        val img: ImageView = itemView.findViewById(R.id.img_item)
        val text: TextView = itemView.findViewById(R.id.txt_item)
    }

    interface ItemClickListener {
        fun onItemClick(position: Int,code:String)
    }

}
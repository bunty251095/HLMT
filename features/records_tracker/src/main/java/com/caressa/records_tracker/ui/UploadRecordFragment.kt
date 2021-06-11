package com.caressa.records_tracker.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.navigation.findNavController
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Configuration
import com.caressa.common.constants.Constants
import com.caressa.common.utils.AppColorHelper
import com.caressa.common.utils.PermissionUtil
import com.caressa.common.utils.RealPathUtil
import com.caressa.common.utils.Utilities
import com.caressa.model.entity.RecordInSession
import com.caressa.records_tracker.R
import com.caressa.records_tracker.adapter.UploadRecordAdapter
import com.caressa.records_tracker.common.DataHandler
import com.caressa.records_tracker.databinding.FragmentUploadRecordBinding
import com.caressa.records_tracker.viewmodel.HealthRecordsViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.io.IOException

class UploadRecordFragment : BaseFragment()  {

    private val viewModel : HealthRecordsViewModel by viewModel()
    private lateinit var binding : FragmentUploadRecordBinding

    private var code = ""
    private val cameraSelectCode = 0
    private val fileSelectCode = 1
    private val gallerySelectCode = 2
    private var uploadRecordAdapter : UploadRecordAdapter? = null
    private val appColorHelper = AppColorHelper.instance!!

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Callback to Handle back button event
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    binding.btnBackUploadRecord.performClick()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentUploadRecordBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.fragment = this
        binding.lifecycleOwner = viewLifecycleOwner
        requireArguments().let {
            code = it.getString(Constants.CODE,"")!!
            Timber.e("code----->$code")
        }
        initialise()
        setClickable()
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initialise() {
        binding.txtDocType.text = DataHandler(requireContext()).getCategorByCode(code)
        uploadRecordAdapter = UploadRecordAdapter( this,requireContext(),viewModel)
        binding.rvUploadRecords.adapter = uploadRecordAdapter

        viewModel.getRecordsInSession()
        //populateList()

        binding.layoutCamera.setOnTouchListener { _: View?, event: MotionEvent ->
            if (event.action == MotionEvent.ACTION_DOWN ||
                event.action == MotionEvent.ACTION_MOVE) {
                binding.layoutCamera.setCardBackgroundColor(appColorHelper.primaryColor())
                ImageViewCompat.setImageTintList(binding.imgCamera, ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.white)))
                binding.lblCamera.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
            }
            if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                binding.layoutCamera.setCardBackgroundColor(ContextCompat.getColor(requireContext(),R.color.white))
                ImageViewCompat.setImageTintList(binding.imgCamera, ColorStateList.valueOf(appColorHelper.primaryColor()))
                binding.lblCamera.setTextColor(appColorHelper.textColor)
            }
            false
        }

        binding.layoutFile.setOnTouchListener { _: View?, event: MotionEvent ->
            if (event.action == MotionEvent.ACTION_DOWN ||
                event.action == MotionEvent.ACTION_MOVE) {
                binding.layoutFile.setCardBackgroundColor(appColorHelper.primaryColor())
                ImageViewCompat.setImageTintList(binding.imgFile, ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.white)))
                binding.lblFile.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
            }
            if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                binding.layoutFile.setCardBackgroundColor(ContextCompat.getColor(requireContext(),R.color.white))
                ImageViewCompat.setImageTintList(binding.imgFile, ColorStateList.valueOf(appColorHelper.primaryColor()))
                binding.lblFile.setTextColor(appColorHelper.textColor)
            }
            false
        }

        binding.layoutGallery.setOnTouchListener { _: View?, event: MotionEvent ->
            if (event.action == MotionEvent.ACTION_DOWN ||
                event.action == MotionEvent.ACTION_MOVE) {
                binding.layoutGallery.setCardBackgroundColor(appColorHelper.primaryColor())
                ImageViewCompat.setImageTintList(binding.imgGallery, ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.white)))
                binding.lblGallery.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
            }
            if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                binding.layoutGallery.setCardBackgroundColor(ContextCompat.getColor(requireContext(),R.color.white))
                ImageViewCompat.setImageTintList(binding.imgGallery, ColorStateList.valueOf(appColorHelper.primaryColor()))
                binding.lblGallery.setTextColor(appColorHelper.textColor)
            }
            false
        }

    }

    private fun setClickable() {

        binding.layoutCamera.setOnClickListener {
            proceedWithCameraPermission()
        }

        binding.layoutFile.setOnClickListener {
            showFileChooserWithGrantedPermission(fileSelectCode)
        }

        binding.layoutGallery.setOnClickListener {
            showFileChooserWithGrantedPermission(gallerySelectCode)
        }

        binding.btnBackUploadRecord.setOnClickListener {
            viewModel.deleteRecordsInSessionTable()
            it.findNavController().navigate(R.id.action_uploadRecordFragment_to_documentTypeFragment)
        }

        binding.btnNextUploadRecord.setOnClickListener {
            val finalUploadList = uploadRecordAdapter!!.uploadRecordList
            Utilities.printData("finalUploadList",finalUploadList)
            if ( finalUploadList.size > 0) {
                val bundle = Bundle()
                bundle.putString("code",code)
                it.findNavController().navigate(R.id.action_uploadRecordFragment_to_selectRelationFragment , bundle)
            } else {
                Utilities.toastMessageShort(requireContext(),resources.getString(R.string.SELECT_RECORD_TO_UPLOAD))
            }
        }

    }

    private fun addRecordToList(item: RecordInSession ) {
        val list = uploadRecordAdapter!!.uploadRecordList
        uploadRecordAdapter!!.insertItem(item,list.size)
        viewModel.saveRecordsInSession(item)
        setListVisibility(true)
    }

    fun populateList( list: MutableList<RecordInSession> ) {
        if ( list.size > 0) {
            uploadRecordAdapter!!.updateList(list)
            setListVisibility(true)
        } else {
            setListVisibility(false)
        }
    }

    fun setListVisibility(needToShow: Boolean) {
        if (needToShow) {
            binding.rvUploadRecords.visibility = View.VISIBLE
            binding.layoutNoData.visibility = View.GONE
            // textViewNoUploadURSHP.setVisibility(View.GONE);

        } else {
            binding.rvUploadRecords.visibility = View.GONE
            binding.layoutNoData.visibility = View.VISIBLE
            //textViewNoUploadURSHP.setVisibility(View.VISIBLE);
        }
    }

    private fun proceedWithCameraPermission() {
        val permissionResult : Boolean = PermissionUtil().getInstance()!!.checkCameraPermission( object : PermissionUtil.AppPermissionListener {
            override fun isPermissionGranted(isGranted: Boolean) {
                Timber.e("$isGranted")
                if ( isGranted ) {
                    dispatchTakePictureIntent()
                }
            }
        },requireContext())
        if (permissionResult) {
            dispatchTakePictureIntent()
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireContext().packageManager) != null) {
            startActivityForResult(takePictureIntent, cameraSelectCode)
        }
    }

    private fun showFileChooser(From: Int) {
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT, null)

        if (From == fileSelectCode) {
            galleryIntent.type = "*/*"
        } else {
            galleryIntent.type = "image/*"
        }
        galleryIntent.addCategory(Intent.CATEGORY_OPENABLE)
        val removeProfPic = Intent()
        val intentArray = arrayOf(galleryIntent)
        val chooser = Intent(Intent.ACTION_CHOOSER)
        chooser.putExtra(Intent.EXTRA_TITLE, resources.getString(R.string.SELECT_RECORDS))
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
        chooser.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        chooser.putExtra(Intent.EXTRA_INTENT, removeProfPic)
        startActivityForResult(Intent.createChooser(galleryIntent, resources.getString(R.string.SELECT_RECORDS)), From)
    }

    private fun showFileChooserWithGrantedPermission(from : Int) {
        val permissionResult : Boolean = PermissionUtil().getInstance()!!.checkStoragePermission( object : PermissionUtil.AppPermissionListener {
            override fun isPermissionGranted(isGranted: Boolean) {
                Timber.e("$isGranted")
                if ( isGranted ) {
                    showFileChooser(from)
                }
            }
        },requireContext())
        if (permissionResult) {
            showFileChooser(from)
        }
    }

    private fun createRecordInSession( OriginalFileName  :String ,Name: String , Path: String, ImageType: String ) :RecordInSession {
        val id = (0..100000).random().toString()
        return RecordInSession(
            Name = Name ,
            Id = id ,
            OriginalFileName = OriginalFileName ,
            Path = Path ,
            Type = ImageType ,
            Sync = "" )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            if (resultCode == Activity.RESULT_OK && data != null) {
                var documentType = ""
                when (requestCode) {
                    cameraSelectCode -> {
                        val photo = data.extras!!.get("data") as Bitmap
                        documentType = "IMAGE"

                        if (!documentType.equals("", ignoreCase = true)) {
                            val fileName = RealPathUtil.generateUniqueFileName(Configuration.strAppIdentifier + "_REC", Configuration.strAppIdentifier + ".png")
                            run {
                                val save: Boolean
                                try {
                                    save = RealPathUtil.saveBitampAsFileToExternalCard(photo,fileName,Constants.RECORD, "")
                                    val mainDirectoryPath = RealPathUtil.getRecordFolderLocation()
                                    if ( save) {
                                        val rsData = createRecordInSession(fileName,fileName,mainDirectoryPath,documentType )
                                        //listToUpload.add(rsData)
                                        //populateList(listToUpload)
                                        addRecordToList(rsData)
                                    }
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    }
                    fileSelectCode -> {
                        val fileSize: Float
                        try {
                            val uri = data.data
                            val flePathF = RealPathUtil.getPath(requireContext(), uri!!)
                            fileSize = RealPathUtil.calculateFileSize(flePathF!!)
                            println("File Size : " + RealPathUtil.calculateFileSize(flePathF))
                            val save: Boolean
                            if (fileSize <= 6) {
                                val extension = RealPathUtil.getFileExt(flePathF)
                                println("Extension : $extension")
                                // before uploading verifing it's extension
                                if (!Utilities.isNullOrEmpty(extension)) {
                                    if (extension.equals("JPEG", ignoreCase = true) ||
                                        extension.equals("jpg", ignoreCase = true) ||
                                        extension.equals("PNG", ignoreCase = true) ||
                                        extension.equals("png", ignoreCase = true)) {
                                        documentType = "IMAGE"
                                    } else if (extension.equals("PDF", ignoreCase = true) || extension.equals("pdf", ignoreCase = true)) {
                                        documentType = "PDF"
                                    } else if (extension.equals("txt", ignoreCase = true) ||
                                        extension.equals("doc", ignoreCase = true) ||
                                        extension.equals("docx", ignoreCase = true)) {
                                        documentType = "DOC"
                                    }
                                }
                                if (!documentType.equals("", ignoreCase = true)) {
                                    val fileName = RealPathUtil.generateUniqueFileName(Configuration.strAppIdentifier + "_REC", flePathF)
                                    println("File Path : $flePathF$fileName")
                                    save = RealPathUtil.saveFileToExternalCard(flePathF,fileName,Constants.RECORD)
                                    val mainDirectoryPath = RealPathUtil.getRecordFolderLocation()
                                    if (save) {
                                        val origFileName = flePathF.substring(flePathF.lastIndexOf("/") + 1)
                                        val rsData = createRecordInSession( origFileName,fileName,mainDirectoryPath,documentType )
                                        //listToUpload.add(rsData)
                                        //populateList(listToUpload)
                                        addRecordToList(rsData)
                                    }
                                } else {
                                    Utilities.toastMessageLong(context, extension + resources.getString(R.string.ERROR_FILES_NOT_ACCEPTED))
                                }
                            } else {
                                Utilities.toastMessageLong(context, resources.getString(R.string.ERROR_FILE_SIZE_LESS_THEN_5MB))
                            }
                        } catch (e: Exception) {
                            Utilities.toastMessageShort(context, resources.getString(R.string.ERROR_UNABLE_TO_READ_FILE))
                            e.printStackTrace()
                        }
                    }
                    gallerySelectCode -> try {
                        val uri1 = data.data
                        val flePathG = RealPathUtil.getPath(requireContext(), uri1!!)
                        val fileSize = RealPathUtil.calculateFileSize(flePathG!!)

                        println("File Size : $fileSize")
                        if (fileSize <= 5) {
                            val save1: Boolean
                            val extension1 = RealPathUtil.getFileExt(flePathG)
                            println("Extension : $extension1")
                            // before uploading verifing it's extension
                            if (extension1.equals("JPEG", ignoreCase = true) ||
                                extension1.equals("jpg", ignoreCase = true) ||
                                extension1.equals("PNG", ignoreCase = true) ||
                                extension1.equals("png", ignoreCase = true)) {
                                documentType = "IMAGE"
                            } else if (extension1.equals("PDF", ignoreCase = true) || extension1.equals("pdf", ignoreCase = true)) {
                                documentType = "PDF"
                            } else if (extension1.equals("txt", ignoreCase = true) ||
                                extension1.equals("doc", ignoreCase = true) ||
                                extension1.equals("docx", ignoreCase = true)) {
                                documentType = "DOC"
                            }
                            if (!documentType.equals("", ignoreCase = true)) {
                                val fileName = RealPathUtil.generateUniqueFileName(Configuration.strAppIdentifier + "_REC", flePathG)
                                println("File Path : $flePathG")
                                save1 = RealPathUtil.saveFileToExternalCard(flePathG,fileName,Constants.RECORD)
                                //save1 = DataHandler(requireContext()).writeRecordToDisk( flePathG , flePath1.toString() , FileName.toString())
                                val mainDirectoryPath = RealPathUtil.getRecordFolderLocation()
                                if (save1) {
                                    val origFileName = flePathG.substring(flePathG.lastIndexOf("/") + 1)
                                    val rsData = createRecordInSession(origFileName,fileName,mainDirectoryPath,documentType)
                                    //listToUpload.add(rsData)
                                    //populateList(listToUpload)
                                    addRecordToList(rsData)
                                }
                            } else {
                                Utilities.toastMessageLong(context, extension1 + resources.getString(R.string.ERROR_FILES_NOT_ACCEPTED))
                            }
                        } else {
                            Utilities.toastMessageLong(context, resources.getString(R.string.ERROR_FILE_SIZE_LESS_THEN_5MB))
                        }
                    } catch (e: Exception) {
                        Utilities.toastMessageShort(context, resources.getString(R.string.ERROR_UNABLE_TO_READ_FILE))
                        e.printStackTrace()
                    }
                }
            }
        } catch ( e : Exception) {
            e.printStackTrace()
        }
    }

}
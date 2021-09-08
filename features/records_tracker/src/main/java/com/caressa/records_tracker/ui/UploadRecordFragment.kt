package com.caressa.records_tracker.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.net.Uri
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
import com.caressa.common.constants.FirebaseConstants
import com.caressa.common.utils.*
import com.caressa.model.entity.RecordInSession
import com.caressa.records_tracker.R
import com.caressa.records_tracker.adapter.UploadRecordAdapter
import com.caressa.records_tracker.common.DataHandler
import com.caressa.records_tracker.databinding.FragmentUploadRecordBinding
import com.caressa.records_tracker.viewmodel.HealthRecordsViewModel
import com.caressa.common.utils.PermissionUtil
import com.caressa.common.utils.PermissionUtil.AppPermissionListener
import com.caressa.common.utils.filepicker.FilePickerBuilder
//import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import droidninja.filepicker.utils.ContentUriUtils
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.io.IOException

class UploadRecordFragment : BaseFragment()  {

    private val viewModel : HealthRecordsViewModel by viewModel()
    private lateinit var binding : FragmentUploadRecordBinding

    private var code = ""
    private var uploadRecordAdapter : UploadRecordAdapter? = null
    private val appColorHelper = AppColorHelper.instance!!

    private val permissionUtil = PermissionUtil
    private val fileUtils = FileUtils

    //var mimeTypes = arrayOf("image/*", "application/*|text/*")
    var mimeTypes = arrayOf(
        "application/pdf",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
        "application/doc",
        "text/*")

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
        FirebaseHelper.logScreenEvent(FirebaseConstants.HEALTH_RECORDS_UPLOAD_SCREEN)
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
        binding.txtDocType.text = DataHandler(requireContext()).getCategoryByCode(code)
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
            showFileChooser()
            //showFilePicker()
        }

        binding.layoutGallery.setOnClickListener {
            showImageChooser()
            //showPhotoPicker()
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
        val permissionResult : Boolean = permissionUtil.checkCameraPermission( object : AppPermissionListener {
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
            startActivityForResult(takePictureIntent,Constants.CAMERA_SELECT_CODE)
        }
    }

    private fun showFileChooser() {
        val chooserIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        chooserIntent.type = "*/*"
        chooserIntent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes)
        chooserIntent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(Intent.createChooser(chooserIntent,resources.getString(R.string.SELECT_A_FILE)),Constants.FILE_SELECT_CODE)
    }

    private fun showImageChooser() {
        val pickIntent = Intent(Intent.ACTION_PICK)
        pickIntent.type = "image/*"
        //val chooserIntent = Intent.createChooser(pickIntent,resources.getString(R.string.SELECT_PROFILE_PHOTO))
        //chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))
        startActivityForResult(pickIntent,Constants.GALLERY_SELECT_CODE)
    }

/*    private fun showPhotoPicker() {
        FilePickerBuilder.instance
            .setMaxCount(1)
            .setActivityTitle(resources.getString(R.string.SELECT_AN_IMAGE))
            .setActivityTheme(R.style.FilePickerTheme)
            .enableCameraSupport(false)
            .pickPhoto(this, FilePickerConst.REQUEST_CODE_PHOTO)
    }

    private fun showFilePicker() {
        FilePickerBuilder.instance
            .setMaxCount(1)
            .setActivityTitle(resources.getString(R.string.SELECT_A_FILE))
            .setActivityTheme(R.style.FilePickerTheme)
            //.showFolderView(false)
            .pickFile(this, FilePickerConst.REQUEST_CODE_DOC)
    }*/

    private fun createRecordInSession( OriginalFileName  :String ,Name: String , Path: String, ImageType: String,recordUri : Uri ) :RecordInSession {
        val id = (0..100000).random().toString()
        return RecordInSession(
            Name = Name ,
            Id = id ,
            OriginalFileName = OriginalFileName ,
            Path = Path ,
            Type = ImageType ,
            FileUri = recordUri.toString(),
            Sync = "" )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            Timber.e("requestCode-> $requestCode")
            Timber.e("resultCode-> $resultCode")
            Timber.e("data-> $data")
            if (resultCode == Activity.RESULT_OK && data != null) {
                val documentType: String
                when (requestCode) {

                    Constants.CAMERA_SELECT_CODE -> {
                        val photo = data.extras!!.get("data") as Bitmap
                        documentType = "IMAGE"

                        if (!documentType.equals("", ignoreCase = true)) {
                            val fileName = fileUtils.generateUniqueFileName(Configuration.strAppIdentifier + "_REC", ".png")
                            run {
                                try {
                                    val recordFile = fileUtils.saveBitmapToExternalStorage(requireContext(),photo,fileName)
                                    val mainDirectoryPath = Utilities.getAppFolderLocation(requireContext())
                                    if ( recordFile != null ) {
                                        val fileSize = fileUtils.calculateDocumentFileSize(recordFile,"MB")
                                        if (fileSize <= 5.0) {
                                            val rsData = createRecordInSession(fileName,fileName,mainDirectoryPath,documentType,recordFile.uri)
                                            addRecordToList(rsData)
                                        } else {
                                            Utilities.deleteDocumentFileFromLocalSystem(requireContext(),recordFile.uri,fileName)
                                            Utilities.toastMessageLong(requireContext(),resources.getString(R.string.ERROR_FILE_SIZE_LESS_THEN_5MB))
                                        }
                                    }
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    }

                    Constants.FILE_SELECT_CODE -> {
                        try {
                            val uriFile = data.data
                            Timber.e("uri--->$uriFile")
                            val filePath = fileUtils.getFilePath(requireContext(), uriFile!!)!!
                            val fileSize = fileUtils.calculateFileSize(filePath,"MB")
                            if (fileSize <= 5.0) {
                                val extension = fileUtils.getFileExt(filePath)
                                Timber.e("Extension : $extension")
                                if ( Utilities.isAcceptableDocumentType(extension) ) {
                                    val fileName = fileUtils.generateUniqueFileName(Configuration.strAppIdentifier + "_REC", filePath)
                                    Timber.e("File Path---> $filePath")
                                    val saveFile = fileUtils.saveRecordToExternalStorage(requireContext(),uriFile,filePath,fileName)
                                    val mainDirectoryPath = Utilities.getAppFolderLocation(requireContext())
                                    if ( saveFile != null ) {
                                        val origFileName = filePath.substring(filePath.lastIndexOf("/") + 1)
                                        val rsData = createRecordInSession( origFileName,fileName,mainDirectoryPath,Utilities.getDocumentTypeFromExt(extension),saveFile.uri)
                                        addRecordToList(rsData)
                                    }
                                } else {
                                    Utilities.toastMessageLong(requireContext(), extension + " " + resources.getString(R.string.ERROR_FILES_NOT_ACCEPTED))
                                }
                            } else {
                                Utilities.toastMessageLong(context, resources.getString(R.string.ERROR_FILE_SIZE_LESS_THEN_5MB))
                            }
                        } catch (e: Exception) {
                            Utilities.toastMessageShort(context, resources.getString(R.string.ERROR_UNABLE_TO_READ_FILE))
                            e.printStackTrace()
                        }
                    }

                    Constants.GALLERY_SELECT_CODE -> {
                        try {
                            val uriImage = data.data
                            val imagePath = fileUtils.getFilePath(requireContext(), uriImage!!)!!
                            val fileSize = fileUtils.calculateFileSize(imagePath,"MB")

                            if (fileSize <= 5.0) {
                                val extension1 = fileUtils.getFileExt(imagePath)
                                Timber.e("Extension : $extension1")
                                if ( Utilities.isAcceptableDocumentType(extension1) ) {
                                    val fileName = fileUtils.generateUniqueFileName(Configuration.strAppIdentifier + "_REC", imagePath)
                                    Timber.e("File Path---> $imagePath")
                                    val saveImage = fileUtils.saveRecordToExternalStorage(requireContext(),uriImage,imagePath,fileName)
                                    val mainDirectoryPath = Utilities.getAppFolderLocation(requireContext())
                                    if ( saveImage != null ) {
                                        val origFileName = imagePath.substring(imagePath.lastIndexOf("/") + 1)
                                        val rsData = createRecordInSession(origFileName,fileName,mainDirectoryPath,Utilities.getDocumentTypeFromExt(extension1),saveImage.uri)
                                        addRecordToList(rsData)
                                    }
                                } else {
                                    Utilities.toastMessageLong(context, extension1 + " " + resources.getString(R.string.ERROR_FILES_NOT_ACCEPTED))
                                }
                            } else {
                                Utilities.toastMessageLong(context,resources.getString(R.string.ERROR_FILE_SIZE_LESS_THEN_5MB))
                            }
                        } catch (e: Exception) {
                            Utilities.toastMessageShort(context,resources.getString(R.string.ERROR_UNABLE_TO_READ_FILE))
                            e.printStackTrace()
                        }
                    }

/*                    FilePickerConst.REQUEST_CODE_PHOTO -> {
                        try {
                            val photoUriList = data.getParcelableArrayListExtra<Uri>(FilePickerConst.KEY_SELECTED_MEDIA)!!.toMutableList()
                            val photoUri = photoUriList[0]
                            Timber.e("photoUri---> $photoUri")
                            val photoPath = ContentUriUtils.getFilePath(requireContext(),photoUri)!!
                            Timber.e("File Path---> $photoPath")
                            //val uri1 = photoUri
                            //val photoPath = realPathUtil.getPath(requireContext(), uri1!!)
                            val fileSize = realPathUtil.calculateFileSize(photoPath,"MB")

                            if (fileSize <= 5.0) {
                                val save1: Boolean
                                val extension1 = realPathUtil.getFileExt(photoPath)
                                Timber.e("Extension : $extension1")
                                // before uploading verifing it's extension
                                if ( Utilities.isAcceptableDocumentType(extension1) ) {
                                    val fileName = realPathUtil.generateUniqueFileName(Configuration.strAppIdentifier + "_REC", photoPath)
                                    save1 = realPathUtil.saveFileToExternalCard(photoPath,fileName,Constants.RECORD)
                                    //save1 = DataHandler(requireContext()).writeRecordToDisk( flePathG , flePath1.toString() , FileName.toString())
                                    val mainDirectoryPath = realPathUtil.getRecordFolderLocation()
                                    if (save1) {
                                        val origFileName = photoPath.substring(photoPath.lastIndexOf("/") + 1)
                                        val rsData = createRecordInSession(origFileName,fileName,mainDirectoryPath,Utilities.getDocumentTypeFromExt(extension1))
                                        addRecordToList(rsData)
                                    }
                                } else {
                                    Utilities.toastMessageLong(context, extension1 + " " + resources.getString(R.string.ERROR_FILES_NOT_ACCEPTED))
                                }
                            } else {
                                Utilities.toastMessageLong(context,resources.getString(R.string.ERROR_FILE_SIZE_LESS_THEN_5MB))
                            }
                        } catch (e: Exception) {
                            Utilities.toastMessageShort(context,resources.getString(R.string.ERROR_UNABLE_TO_READ_FILE))
                            e.printStackTrace()
                        }
                    }

                    FilePickerConst.REQUEST_CODE_DOC -> {
                        val filePathList = data.getParcelableArrayListExtra<Uri>(FilePickerConst.KEY_SELECTED_DOCS)!!.toMutableList()
                        val fileUri = filePathList[0]
                        Timber.e("filePathList---> $filePathList")
                        val filePath = ContentUriUtils.getFilePath(requireContext(),fileUri)!!
                        Timber.e("File Path---> $filePath")

                        try {
                            val fileSize = realPathUtil.calculateFileSize(filePath,"MB")
                            val save: Boolean
                            if (fileSize <= 5.0) {
                                val extension = realPathUtil.getFileExt(filePath)
                                Timber.e("Extension : $extension")
                                // before uploading verifing it's extension

                                if ( Utilities.isAcceptableDocumentType(extension) ) {
                                    val fileName = realPathUtil.generateUniqueFileName(Configuration.strAppIdentifier + "_REC", filePath)
                                    save = realPathUtil.saveFileToExternalCard(filePath,fileName,Constants.RECORD)
                                    val mainDirectoryPath = realPathUtil.getRecordFolderLocation()
                                    if (save) {
                                        val origFileName = filePath.substring(filePath.lastIndexOf("/") + 1)
                                        val rsData = createRecordInSession( origFileName,fileName,mainDirectoryPath,Utilities.getDocumentTypeFromExt(extension) )
                                        addRecordToList(rsData)
                                    }
                                } else {
                                    Utilities.toastMessageLong(context, extension + " " + resources.getString(R.string.ERROR_FILES_NOT_ACCEPTED))
                                }
                            } else {
                                Utilities.toastMessageLong(context, resources.getString(R.string.ERROR_FILE_SIZE_LESS_THEN_5MB))
                            }
                        } catch (e: Exception) {
                            Utilities.toastMessageShort(context, resources.getString(R.string.ERROR_UNABLE_TO_READ_FILE))
                            e.printStackTrace()
                        }
                    }*/

                }
            }
        } catch ( e : Exception) {
            e.printStackTrace()
        }
    }

}
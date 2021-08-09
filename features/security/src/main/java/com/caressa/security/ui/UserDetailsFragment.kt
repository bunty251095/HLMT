package com.caressa.security.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.caressa.security.R
import androidx.navigation.fragment.navArgs
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Configuration
import com.caressa.common.constants.Constants
import com.caressa.common.utils.*
import com.caressa.model.home.UpdateUserDetailsModel
import com.caressa.security.databinding.FragmentUserDetailsBinding
import com.caressa.security.viewmodel.HlmtLoginViewModel
import com.yalantis.ucrop.UCrop
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.io.File
import java.util.*

class UserDetailsFragment: BaseFragment(),EditProfilePhotoBottomsheetFragment.EditProfileImageItemClickListener {

    private var mCalendar: Calendar? = null
    private val viewModel : HlmtLoginViewModel by viewModel()
    private lateinit var binding: FragmentUserDetailsBinding
    private val args: UserDetailsFragmentArgs by navArgs()
    var hasProfileImage = false
    private var completeFilePath = ""
    private var needToSet = true
    private val gallerySelectCode = 1
    private val cameraSelectCode = 2
    private val appColorHelper = AppColorHelper.instance!!

    private var fName = ""
    private var fPath = ""
    private val permissionListener = object : PermissionUtil.AppPermissionListener {
        override fun isPermissionGranted(isGranted: Boolean) {
            Timber.e("$isGranted")
            if ( isGranted ) {
                showFileChooser(gallerySelectCode)
            }
        }
    }

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentUserDetailsBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        init()
        registerObserver()
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {

        if(!args.hlmtUserID.isEmpty()){
            binding.layoutMobileEdit.visibility = View.GONE
        }else{
            binding.layoutMobileEdit.visibility = View.VISIBLE
        }

        RealPathUtil.makeFilderDirectories()
        binding.edtPhoneNumber.setText(args.mobileNo)

        binding.btnDone.setOnClickListener {

            if(args.isRegister.equals("true")) {
                var gender = "M"
                if (binding.rbMale.isChecked) {
                    gender = "M"
                } else {
                    gender = "F"
                }
                viewModel.fetchRegistrationResponse(
                    name = binding.edtUsername.text.toString(),
                    phoneNumber = args.mobileNo,
                    passwordStr = "123456",
                    gender = gender,
                    dob = binding.edtDob.text.toString(),
                    emailStr = binding.edtEmail.text.toString(),
                    fName = fName,
                    imgPath = fPath,
                    hlmtLoginStatus = args.loginStatus,
                    hlmtEmpId = args.hlmtEmployeeID,
                    hlmtUserId = args.hlmtUserID
                )
            }else{
//                viewModel.callUpdateUserDetailsApi()
            }
        }

        binding.layoutDob.setOnClickListener {
            showDatePickerDialog()
        }
        binding.edtDob.setOnTouchListener { v, event ->
            if (MotionEvent.ACTION_UP == event.action) showDatePickerDialog() // Instead of your Toast
            false
        }

        binding.imgEditPic.setOnClickListener {
            viewBottomSheet()
        }

        binding.imgUserPic.setOnClickListener {
            viewBottomSheet()
        }
    }

    private fun registerObserver() {
        viewModel.loginResponse.observe(viewLifecycleOwner, {
            Timber.i("Data=> "+it)
        })
        viewModel.uploadProfileImage.observe( viewLifecycleOwner , {})
    }

    private fun validateAndUpdate(name:String) {
        println("Inside 1")
        val username = name
        val newEmail = ""
        val newAlternateEmail = ""
        val newNumber = ""
        val newAlternateNumber = ""
        val address = ""

//            //Helper.showMessage(getContext(),"Details Updated");
//            val newUserDetails = UpdateUserDetailsModel.PersonRequest(
//                id = user.id,
//                firstName = username,
//                dateOfBirth = user.dateOfBirth,
//                gender = user.gender.toString(),
//                contact = UpdateUserDetailsModel.Contact(
//                    emailAddress = user.contact.emailAddress,
//                    primaryContactNo = user.contact.primaryContactNo,
//                    alternateEmailAddress = newAlternateEmail,
//                    alternateContactNo = newAlternateNumber,),
//                address = UpdateUserDetailsModel.Address(
//                    addressLine1 = address)
//            )
//            viewModel.callUpdateUserDetailsApi()

    }

    private fun showDatePickerDialog() {
        mCalendar = Calendar.getInstance()
        mCalendar?.add(Calendar.YEAR, -18)

        DialogHelper().showDatePickerDialog("Your Date of Birth",requireContext(), mCalendar,null, mCalendar, object :DialogHelper.DateListener{
            override fun onDateSet(date: String, year: String, month: String, dayOfMonth: String) {
                binding.edtDob.setText(date)
            }
        })


        /*val year = mCalendar!!.get(Calendar.YEAR)
        val month = mCalendar!!.get(Calendar.MONTH)
        val day = mCalendar!!.get(Calendar.DAY_OF_MONTH)
        val dpd = DatePickerDialog(requireContext(),
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                val _monthOf = DateHelper.returnTwoDigitFromDate(monthOfYear + 1)
                val _monthOfDay = DateHelper.returnTwoDigitFromDate(dayOfMonth)

                val dob = "$year-$_monthOf-$_monthOfDay"
                val strDate = DateHelper.formatDateValue(dob)
                binding.edtDob.setText(strDate)

            },year,month,day
        )
        dpd.setTitle("Your Date of Birth")
        dpd.datePicker.setMaxDate(mCalendar!!.timeInMillis)
        dpd.show()*/
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mCalendar = Calendar.getInstance()
        mCalendar?.add(Calendar.YEAR, -18)

    }

    private fun viewBottomSheet() {
        try {
            val bundle = Bundle()
            bundle.putBoolean(Constants.PROFILE_IMAGE_ID, hasProfileImage)
            val addPhotoBottomDialogFragment = EditProfilePhotoBottomsheetFragment.newInstance(hasProfileImage,this)
            addPhotoBottomDialogFragment.arguments = bundle
            addPhotoBottomDialogFragment.show(requireFragmentManager(), addPhotoBottomDialogFragment.tag)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onEditProfileImageItemClick(position: Int,code:String) {
        if (code == requireContext().resources.getString(R.string.VIEW)) {
            viewProfilePhoto()
        }
        if (code == requireContext().resources.getString(R.string.GALLERY)) {
            proceedWithStoragePermission(gallerySelectCode)
        }
        if (code == requireContext().resources.getString(R.string.PHOTO)) {
            proceedWithCameraPermission()
        }
    }

    private fun showFileChooser(From: Int) {
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT, null)
        galleryIntent.type = "image/*"
        galleryIntent.addCategory(Intent.CATEGORY_OPENABLE)
        val removeProfPic = Intent()
        val intentArray = arrayOf(galleryIntent)
        val chooser = Intent(Intent.ACTION_CHOOSER)
        chooser.putExtra(Intent.EXTRA_TITLE, resources.getString(R.string.SELECT_RECORDS))
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
        chooser.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        chooser.putExtra(Intent.EXTRA_INTENT, removeProfPic)
        startActivityForResult(Intent.createChooser(galleryIntent, resources.getString(R.string.SELECT_A_FILE_TO_UPLOAD)), From)
    }

    private fun proceedWithCameraPermission() {
        val permissionResult : Boolean = PermissionUtil().getInstance()!!.checkCameraPermission( object : PermissionUtil.AppPermissionListener {
            override fun isPermissionGranted(isGranted: Boolean) {
                Timber.e("$isGranted")
                if ( isGranted ) {
                    proceedWithStoragePermission(cameraSelectCode)
                }
            }
        },requireContext())
        if (permissionResult) {
            proceedWithStoragePermission(cameraSelectCode)
        }
    }

    private fun proceedWithStoragePermission( from : Int) {
        val permissionResult : Boolean = PermissionUtil().getInstance()!!.checkStoragePermissionFromFragment( object : PermissionUtil.AppPermissionListener {
            override fun isPermissionGranted(isGranted: Boolean) {
                Timber.e("$isGranted")
                if ( isGranted ) {
                    when(from) {
                        gallerySelectCode -> showFileChooser(gallerySelectCode)
                        cameraSelectCode -> dispatchTakePictureIntent()
                    }
                }
            }
        },requireContext(),this)
        if (permissionResult) {
            when(from) {
                gallerySelectCode -> showFileChooser(gallerySelectCode)
                cameraSelectCode -> dispatchTakePictureIntent()
            }
        }
    }

    private fun viewProfilePhoto() {
        try {
            if ( !Utilities.isNullOrEmpty(completeFilePath) ) {
                val file = File(completeFilePath)
                if (file.exists()) {
                    val type = "image/*"
                    val intent = Intent(Intent.ACTION_VIEW)
                    val uri = Uri.fromFile(file)
                    intent.setDataAndType(uri, type)
                    //intent.setDataAndType(FileProvider.getUriForFile(this, getPackageName().toString() + ".provider", file), type)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    try {
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        Utilities.toastMessageShort(requireContext(), resources.getString(R.string.ERROR_NO_APPLICATION_TO_VIEW_PDF))
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Utilities.toastMessageShort(requireContext(), resources.getString(R.string.ERROR_UNABLE_TO_OPEN_FILE))
                    }
                } else {
                    Utilities.toastMessageShort(requireContext(), resources.getString(R.string.ERROR_FILE_NOT_EXIST))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireContext().packageManager) != null) {
            startActivityForResult(takePictureIntent, cameraSelectCode)
        }
    }

    private fun setProfilePic() {
        try {
            Timber.e("completeFilePath----->$completeFilePath")
            Timber.e("needToSet----->$needToSet")
            if ( needToSet  ) {
                if (!Utilities.isNullOrEmpty(completeFilePath)) {
                    var bitmap: Bitmap? = null
                    bitmap = RealPathUtil.decodeFile(completeFilePath)
                    if (bitmap != null) {
                        binding.imgUserPic.setImageBitmap(bitmap)
                        needToSet = false
                    }
                }
            }
        } catch ( e :Exception ) {
            e.printStackTrace()
        }
    }

    private fun showUcrop(sourceUri: Uri, destinationUri: Uri) {
        val options = UCrop.Options()
        options.setStatusBarColor(appColorHelper.primaryColor())
        options.setToolbarColor(appColorHelper.primaryColor())
        options.setActiveControlsWidgetColor(appColorHelper.primaryColor())
        options.setToolbarWidgetColor(ContextCompat.getColor(requireContext(),R.color.white))
        options.setFreeStyleCropEnabled(true)
        UCrop.of(sourceUri, destinationUri) //.withAspectRatio(16, 9)
            .withOptions(options)
            .start(requireContext(),this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        println("requestCode,resultCode,data----->$requestCode,$resultCode,$data")
        val realPathUtil = RealPathUtil
        //float fileSize = 0;
        //String flePath1 = "";
        var pathTemp = ""
        try {
            if (resultCode == Activity.RESULT_OK && data != null) {
                var documentType = ""
                when (requestCode) {

                    cameraSelectCode -> {
                        val photo = data.extras!!["data"] as Bitmap?
                        val sourceUriCamera: Uri = realPathUtil.getImageUri(requireContext(), photo)
                        val newFlePath1: String = realPathUtil.getPath(requireContext(), sourceUriCamera)!!
                        val origFileName1 = newFlePath1.substring(newFlePath1.lastIndexOf("/") + 1)
                        pathTemp = realPathUtil.getPath(requireContext(), sourceUriCamera)!!
                        val newFile1 = File(Environment.getExternalStorageDirectory().toString(), origFileName1)
                        val destinationUriCamera = Uri.fromFile(newFile1)
                        showUcrop(sourceUriCamera, destinationUriCamera)
                    }

                    gallerySelectCode -> {
                        val sourceUriGallery = data.data
                        val newFilePathGallery: String = realPathUtil.getPath(requireContext(), sourceUriGallery!!)!!
                        val origFileName2 = newFilePathGallery.substring(newFilePathGallery.lastIndexOf("/") + 1)
                        val newFile2 = File(Environment.getExternalStorageDirectory().toString(), origFileName2)
                        val destinationUriGallery = Uri.fromFile(newFile2)
                        showUcrop(sourceUriGallery, destinationUriGallery)
                    }

                    UCrop.REQUEST_CROP ->
                        try {
                            val resultUri = UCrop.getOutput(data)
                            val flePath1: String = realPathUtil.getPath(requireContext(), resultUri!!)!!
                            val fileSize: Float = realPathUtil.calculateFileSize(flePath1)
                            println("File Size : $fileSize")
                            //System.out.println("File Size : " + Helper.calculateFileSize(flePath1));
                            if (fileSize < 5) {
                                var save1 = false
                                val extension1: String = realPathUtil.getFileExt(flePath1)
                                println("Extension : $extension1")
                                // before uploading verifing it's extension
                                if (extension1.equals("JPEG", ignoreCase = true) ||
                                    extension1.equals("jpg", ignoreCase = true) ||
                                    extension1.equals("PNG", ignoreCase = true) ||
                                    extension1.equals("png", ignoreCase = true)) {
                                    documentType = "IMAGE"
                                } else if (extension1.equals("PDF", ignoreCase = true) ||
                                    extension1.equals("pdf", ignoreCase = true)) {
                                    documentType = "PDF"
                                } else if (extension1.equals("txt", ignoreCase = true) ||
                                    extension1.equals("doc", ignoreCase = true) ||
                                    extension1.equals("docx", ignoreCase = true)) {
                                    documentType = "DOC"
                                }
                                if (!documentType.equals("", ignoreCase = true)) {
                                    val fileName: String = realPathUtil.generateUniqueFileName(
                                        Configuration.strAppIdentifier + "_REC", flePath1)
                                    println("File Path : $flePath1")
                                    save1 = realPathUtil.saveFileToExternalCard(flePath1,fileName,Constants.RECORD)
                                    val mainDirectoryPath: String = realPathUtil.getRecordFolderLocation()
                                    if (save1) {
                                        Utilities.deleteFileFromLocalSystem(pathTemp)
                                        //viewModel.callUploadProfileImageApi(this,fileName, mainDirectoryPath, flePath1)
                                        hasProfileImage = true
                                        needToSet = true
                                        val sourceFile = File(mainDirectoryPath,fileName)
                                        val destpath =  RealPathUtil.getProfileFolderLocation()
                                        RealPathUtil.createDirectory(destpath)
                                        val destFile = File(destpath,fileName)
                                        val save = RealPathUtil.copy(sourceFile,destFile)
                                        if ( save ) {
                                            //updateUserProfileImgPath(name,destpath)
                                            sourceFile.delete()
                                            completeFilePath = destpath + "/" + fileName
                                            setProfilePic()
                                            fName = fileName
                                            fPath = destpath
                                            Utilities.deleteFileFromLocalSystem(flePath1)
                                        }
                                    }
                                } else {
                                    Utilities.deleteFileFromLocalSystem(pathTemp)
                                    Utilities.toastMessageShort(requireContext(), "$extension1 ${resources.getString(R.string.ERROR_FILES_NOT_ACCEPTED)}")
                                }
                            } else {
                                Utilities.deleteFileFromLocalSystem(pathTemp)
                                Utilities.toastMessageShort(requireContext(), resources.getString(R.string.ERROR_FILE_SIZE_LESS_THEN_5MB))
                            }
                        } catch (e: java.lang.Exception) {
                            Utilities.toastMessageShort(requireContext(), resources.getString(R.string.ERROR_UNABLE_TO_READ_FILE))
                            e.printStackTrace()
                        }

                    UCrop.RESULT_ERROR -> {
                        val  cropError: Throwable?= UCrop.getError(data)
                    }

                }

            }

/*            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val per = Environment.isExternalStorageManager()
                Timber.e("requestCode---> $requestCode")
                Timber.e("permissionGranted---> $per")
                when(requestCode) {
                    Constants.REQ_CODE_STORAGE -> {
                        if (per) {
                            permissionListener.isPermissionGranted(true)
                        } else {
                            Utilities.toastMessageShort(requireContext(),resources.getString(R.string.ERROR_STORAGE_PERMISSION))
                        }
                    }
                }
            }*/

            //Utilities.hideKeyboard(this)
            super.onActivityResult(requestCode, resultCode, data)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {

        }
    }

}
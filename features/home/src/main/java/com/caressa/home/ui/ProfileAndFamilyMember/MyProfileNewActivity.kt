package com.caressa.home.ui.ProfileAndFamilyMember

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.caressa.common.base.BaseActivity
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Configuration
import com.caressa.common.constants.Constants
import com.caressa.common.utils.*
import com.caressa.home.R
import com.caressa.home.common.DataHandler
import com.caressa.home.databinding.ActivityMyProfileNewBinding
import com.caressa.home.viewmodel.ProfileFamilyMemberViewModel
import com.caressa.model.home.Person
import com.caressa.model.home.UpdateUserDetailsModel
import com.karumi.dexter.Dexter
import com.yalantis.ucrop.UCrop
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import droidninja.filepicker.utils.ContentUriUtils
import kotlinx.android.synthetic.main.activity_my_profile_new.*
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.io.File
import java.lang.StringBuilder
import java.util.*

class MyProfileNewActivity : BaseActivity(),EditProfileImageBottomsheetFragment.EditProfileImageItemClickListener {

    private val viewModel : ProfileFamilyMemberViewModel by viewModel()
    private lateinit var binding : ActivityMyProfileNewBinding

    private val appColorHelper = AppColorHelper.instance!!

    var strAgeGender = ""
    private val gallerySelectCode = 1
    private val cameraSelectCode = 2
    var completeFilePath = ""
    var hasProfileImage = false
    var needToSet = true
    private var mCalendar: Calendar? = null
    var user : Person = Person()

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_profile_new)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        initialise()
        registerObservers()
        setClickable()
    }

    private fun initialise() {
        startImageShimmer()
        startDetailsShimmer()
        viewModel.callGetUserDetailsApi()

        binding.edtUsername.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                if (!editable.toString().equals("", ignoreCase = true)) {
                    binding.tilEdtUsername.error = null
                    binding.tilEdtUsername.isErrorEnabled = false
                    binding.txtUsername.text = editable.toString()
                } else {
                    binding.txtUsername.text = user.firstName
                }
            }
        })

        val toUpperCaseFilter =
            InputFilter { source, start, end, dest, dstart, dend ->
                val stringBuilder = StringBuilder()
                for (i in start until end) {
                    var character = source[i]
                    if(i==0 || source[i-1].equals(' ',true)) {
                        character = Character.toUpperCase(character!!) // THIS IS UPPER CASING
                    }
                    stringBuilder.append(character)
                }
                stringBuilder.toString()
            }

        binding.edtUsername.setFilters(arrayOf(toUpperCaseFilter))

        binding.edtAlternateEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                if (!editable.toString().equals("", ignoreCase = true)) {
                    binding.tilEdtAlternateEmail.error = null
                    binding.tilEdtAlternateEmail.isErrorEnabled = false
                }
            }
        })

        binding.edtAlternateNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                if (!editable.toString().equals("", ignoreCase = true)) {
                    binding.tilEdtAlternateNumber.error = null
                    binding.tilEdtAlternateNumber.isErrorEnabled = false
                }
            }
        })

/*        binding.edtEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                if (!editable.toString().equals("", ignoreCase = true)) {
                    if (!Validation.isValidEmail(editable.toString())) {
                        binding.tilEdtEmail.isErrorEnabled = true
                        binding.tilEdtEmail.error = "Please Enter Valid Email Address"
                    } else {
                        binding.tilEdtEmail.error = null
                        binding.tilEdtEmail.isErrorEnabled = false
                    }
                }
                if (editable.toString().equals("", ignoreCase = true)) {
                    binding.tilEdtEmail.error = null
                    binding.tilEdtEmail.isErrorEnabled = false
                }
            }
        })

        binding.edtNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                if (editable.toString().length > 3) {
                    if (!Validation.isValidPhoneNumber(editable.toString())) {
                        binding.tilEdtNumber.isErrorEnabled = true
                        binding.tilEdtNumber.error = "Please Enter valid Phone Number"
                    } else {
                        binding.tilEdtNumber.error = null
                        binding.tilEdtNumber.isErrorEnabled = false
                    }
                }
                if (editable.toString().equals("", ignoreCase = true)) {
                    binding.tilEdtNumber.error = null
                    binding.tilEdtNumber.isErrorEnabled = false
                }
            }
        })*/

    }

    private fun registerObservers() {

        viewModel.userProfileDetails.observe( this , {
            if ( it != null ) {
                val person = it.person
                if ( !Utilities.isNullOrEmptyOrZero( person.profileImageID.toString() ) ) {
                    hasProfileImage = true
                    viewModel.callGetProfileImageApi(this,person.profileImageID.toString())
                } else {
                    stopImageShimmer()
                }
                user = person
                setUserDetails(person)
                stopDetailsShimmer()
            }
        })

        viewModel.updateUserDetails.observe( this , {
            if ( it != null ) {
                val person = it.person
                user = person
                setUserDetails(person)
                binding.btnCancelProfile.performClick()

            }
        })
        viewModel.profileImage.observe( this , {})
        viewModel.uploadProfileImage.observe( this , {})
        viewModel.removeProfileImage.observe( this , {})
    }

    fun setClickable() {

        binding.imgEditPic.setOnClickListener {
            viewBottomSheet()
        }

        binding.imgUserPic.setOnClickListener {
            viewBottomSheet()
        }

        binding.imgEditProfile.setOnClickListener {
            binding.lblProfile.text = resources.getString(R.string.TITLE_EDIT_PROFILE)
            binding.imgBackMyProfile.visibility = View.GONE
            binding.imgEditProfile.visibility = View.GONE
            binding.layoutBtnProfile.visibility = View.VISIBLE
            binding.layoutEditDetails.visibility = View.VISIBLE
            binding.layoutShowDetails.visibility = View.GONE
        }

        binding.btnCancelProfile.setOnClickListener {
            //setUserDetails(user)
            binding.lblProfile.text = resources.getString(R.string.TITLE_YOUR_PROFILE)
            binding.imgBackMyProfile.visibility = View.VISIBLE
            binding.imgEditProfile.visibility = View.VISIBLE
            binding.layoutBtnProfile.visibility = View.GONE
            binding.layoutEditDetails.visibility = View.GONE
            binding.layoutShowDetails.visibility = View.VISIBLE
            binding.edtUsername.setText(user.firstName)

        }

        binding.btnUpdateProfile.setOnClickListener {
            //viewModel.callUpdateUserDetailsApi(this)
            validateAndUpdate()
        }

        binding.imgBackMyProfile.setOnClickListener {
            onBackPressed()
        }

        binding.layoutDobEdit.setOnClickListener {
            showDatePickerDialog()
        }

        binding.edtDob.setOnTouchListener { v, event ->
            if (MotionEvent.ACTION_UP == event.action) showDatePickerDialog() // Instead of your Toast
            false
        }
    }

    fun showDatePickerDialog(){
        mCalendar = Calendar.getInstance()
        mCalendar?.add(Calendar.YEAR, -18)

        DialogHelper().showDatePickerDialog("Your Date of Birth",this, mCalendar,null, mCalendar, object :DialogHelper.DateListener{
            override fun onDateSet(date: String, year: String, month: String, dayOfMonth: String) {
                binding.edtDob.setText(date.replace('-',' '))
            }
        })
    }

    private fun validateAndUpdate() {
        val username = binding.edtUsername.text.toString().trim { it <= ' ' }
        val newEmail = binding.edtEmail.text.toString().trim { it <= ' ' }
        val newAlternateEmail = binding.edtAlternateEmail.text.toString().trim { it <= ' ' }
        val newNumber = binding.edtNumber.text.toString().trim { it <= ' ' }
        val newAlternateNumber = binding.edtAlternateNumber.text.toString().trim { it <= ' ' }
        val address = binding.edtAddress.text.toString().trim { it <= ' ' }
        val dateOfBirth = binding.edtDob.text.toString().trim { it <= ' ' }

        if ( !Validation.isValidName(username) ) {
            binding.tilEdtUsername.isErrorEnabled = true
            binding.tilEdtUsername.error = resources.getString(R.string.VALIDATE_NAME)
        }
        if (!Utilities.isNullOrEmpty(newAlternateEmail) && !Validation.isValidEmail(newAlternateEmail)) {
            binding.tilEdtAlternateEmail.isErrorEnabled = true
            binding.tilEdtAlternateEmail.error = resources.getString(R.string.VALIDATE_EMAIL)
        }
        if (!Utilities.isNullOrEmpty(newAlternateNumber) && !Validation.isValidPhoneNumber(newAlternateNumber)) {
            binding.tilEdtAlternateNumber.isErrorEnabled = true
            binding.tilEdtAlternateNumber.error = resources.getString(R.string.VALIDATE_PHONE)
        }
        if (newAlternateEmail.equals(newEmail, ignoreCase = true)) {
            binding.tilEdtAlternateEmail.isErrorEnabled = true
            binding.tilEdtAlternateEmail.error = resources.getString(R.string.VALIDATE_DIFFERENT_EMAIL)
        }
        if (newAlternateNumber.equals(newNumber, ignoreCase = true)) {
            binding.tilEdtAlternateNumber.isErrorEnabled = true
            binding.tilEdtAlternateNumber.error = resources.getString(R.string.VALIDATE_DIFFERENT_PHONE)
        }

        if (dateOfBirth.isNullOrEmpty()) {
            binding.tilEdtAlternateNumber.isErrorEnabled = true
            binding.tilEdtAlternateNumber.error = resources.getString(R.string.VALIDATE_DATE_OF_BIRTH)
        }

        if (!binding.tilEdtUsername.isErrorEnabled && !binding.tilEdtAlternateEmail.isErrorEnabled
            && !binding.tilEdtAlternateNumber.isErrorEnabled && !binding.tilEdtAddress.isErrorEnabled
            && !binding.tilEdtDob.isErrorEnabled) {
            //Helper.showMessage(getContext(),"Details Updated");
                var dateOfBirth = DateHelper.convertDateSourceToDestination(dateValue = binding.edtDob.text.toString(),"dd MMM yyyy",DateHelper.SERVER_DATE_YYYYMMDD)

            val newUserDetails = UpdateUserDetailsModel.PersonRequest(
                id = user.id,
                firstName = username,
                dateOfBirth = dateOfBirth,
                gender = user.gender.toString(),
                contact = UpdateUserDetailsModel.Contact(
                    emailAddress = user.contact.emailAddress,
                    primaryContactNo = user.contact.primaryContactNo,
                    alternateEmailAddress = newAlternateEmail,
                    alternateContactNo = newAlternateNumber,),
                address = UpdateUserDetailsModel.Address(
                    addressLine1 = address)
            )
            viewModel.callUpdateUserDetailsApi(newUserDetails)
        }
    }

/*    private fun getNewUserDetails(): ArrayMap<String, String>? {
        val newUserDetails = ArrayMap<String, String>()
        newUserDetails[GlobalConstants.FIRST_NAME] = edt_username.getText().toString().trim({ it <= ' ' })
        newUserDetails[GlobalConstants.DATE_OF_BIRTH] = personalDetails.get(GlobalConstants.DATE_OF_BIRTH)
        newUserDetails[GlobalConstants.GENDER] = personalDetails.get(GlobalConstants.GENDER)
        newUserDetails[GlobalConstants.EMAIL_ADDRESS] = edt_email.getText().toString().trim({ it <= ' ' })
        newUserDetails[GlobalConstants.ALTERNATE_EMAIL_ADDRESS] = edt_alternate_email.getText().toString().trim({ it <= ' ' })
        newUserDetails[GlobalConstants.PRIMARY_CONTACT_NUMBER] = edt_number.getText().toString().trim({ it <= ' ' })
        newUserDetails[GlobalConstants.ALTERNATE_CONTACT_NUMBER] = edt_alternate_number.getText().toString().trim({ it <= ' ' })
        newUserDetails[GlobalConstants.ADDRESS] = edt_address.getText().toString().trim({ it <= ' ' })
        return newUserDetails
    }*/

    private fun viewBottomSheet() {
        try {
            val bundle = Bundle()
            bundle.putBoolean(Constants.PROFILE_IMAGE_ID, hasProfileImage)
            val addPhotoBottomDialogFragment = EditProfileImageBottomsheetFragment.newInstance(hasProfileImage,this)
            addPhotoBottomDialogFragment.arguments = bundle
            addPhotoBottomDialogFragment.show(supportFragmentManager, addPhotoBottomDialogFragment.tag)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onEditProfileImageItemClick(position: Int,code:String) {
        if (code == DataHandler.ProfileImgOption.View) {
            viewProfilePhoto()
        }
        if (code == DataHandler.ProfileImgOption.Gallery) {
            //showFileChooser(gallerySelectCode)
            showPhotoPicker()
        }
        if (code == DataHandler.ProfileImgOption.Photo) {
            proceedWithCameraPermission()
        }
        if (code == DataHandler.ProfileImgOption.Remove) {
            //Utilities.toastMessageShort(this, "Remove Photo Option Clicked")
            val dialogData = DefaultNotificationDialog.DialogData()
            dialogData.title = resources.getString(R.string.REMOVE_PROFILE_PHOTO)
            dialogData.message = resources.getString(R.string.MSG_REMOVE_PROFILE_PHOTO_CONFORMATION)
            dialogData.btnLeftName = resources.getString(R.string.NO)
            dialogData.btnRightName = resources.getString(R.string.YES)
            val defaultNotificationDialog =
                DefaultNotificationDialog(this, object : DefaultNotificationDialog.OnDialogValueListener {
                    override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
                        if (isButtonRight) {
                            viewModel.callRemoveProfileImageApi(this@MyProfileNewActivity,this@MyProfileNewActivity)
                        }
                    }
                }, dialogData)
            defaultNotificationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            defaultNotificationDialog.show()
        }
    }

    private fun viewProfilePhoto() {
        try {
            //viewModel.getLoggedInPersonDetails()
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
                        Utilities.toastMessageShort(this, resources.getString(R.string.ERROR_NO_APPLICATION_TO_VIEW_PDF))
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Utilities.toastMessageShort(this, resources.getString(R.string.ERROR_UNABLE_TO_OPEN_FILE))
                    }
                } else {
                    Utilities.toastMessageShort(this, resources.getString(R.string.ERROR_FILE_NOT_EXIST))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
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
        },this)
        if (permissionResult) {
            dispatchTakePictureIntent()
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(Objects.requireNonNull(this).packageManager) != null) {
            startActivityForResult(takePictureIntent, cameraSelectCode)
        }
    }

    private fun showPhotoPicker() {
        FilePickerBuilder.instance
            .setMaxCount(1)
            .setActivityTitle(resources.getString(R.string.SELECT_PROFILE_PHOTO))
            //.setSelectedFiles(filePaths) //optional
            .setActivityTheme(R.style.FilePickerTheme)
            .enableCameraSupport(false)
            .pickPhoto(this, FilePickerConst.REQUEST_CODE_PHOTO)
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
        startActivityForResult(Intent.createChooser(galleryIntent,resources.getString(R.string.SELECT_A_FILE_TO_UPLOAD)), From)
    }

    private fun showUcrop(sourceUri: Uri, destinationUri: Uri) {
        val options = UCrop.Options()
        options.setStatusBarColor(appColorHelper.primaryColor())
        options.setToolbarColor(appColorHelper.primaryColor())
        options.setActiveControlsWidgetColor(appColorHelper.primaryColor())
        options.setToolbarWidgetColor(ContextCompat.getColor(this,R.color.white))
        options.setFreeStyleCropEnabled(true)
        UCrop.of(sourceUri, destinationUri) //.withAspectRatio(16, 9)
            //.withMaxResultSize(imageWidth, imageHeight)
            .withOptions(options)
            .start(this)
    }

    private fun setUserDetails(user : Person ) {
        try {
            if ( !Utilities.isNullOrEmptyOrZero(user.id.toString()) ) {
                val email = user.contact.emailAddress
                val alternateEmail = user.contact.alternateEmailAddress
                val number = user.contact.primaryContactNo
                val alternateNumber = user.contact.alternateContactNo
                var address = ""
                if ( user.address != null ) {
                    address = user.address.addressLine1
                }

                if ( !user.dateOfBirth.equals("", ignoreCase = true)) {
                    var dateOfBirth = user.dateOfBirth
                    dateOfBirth = DateHelper.formatDateValue(DateHelper.DISPLAY_DATE_DDMMMYYYY, dateOfBirth)!!
                    val viewDob = DateHelper.formatDateValue(DateHelper.DATEFORMAT_DDMMMYYYY_NEW, dateOfBirth)!!
                    Timber.i("DOB--->$dateOfBirth")
                    if (!Utilities.isNullOrEmpty(dateOfBirth)) {
                        val age: String = DateHelper.calculatePersonAge(dateOfBirth)
                        strAgeGender = if ( !Utilities.isNullOrEmptyOrZero(user.age.toString()) ) {
                            user.age.toString() + " Yrs"
                        } else {
                            age
                        }
                        binding.layoutDob.visibility = View.VISIBLE
                        binding.txtDob.text = viewDob
                        binding.edtDob.setText(viewDob)
                    }
                } else {
                    binding.layoutDob.visibility = View.GONE
                }

                if ( user.gender.toString().equals("1", ignoreCase = true)) {
                    if (!Utilities.isNullOrEmpty(strAgeGender)) {
                        binding.txtAgeGender.text = strAgeGender + " , " + resources.getString(R.string.MALE)
                    } else {
                        binding.txtAgeGender.text = resources.getString(R.string.MALE)
                    }
                } else if ( user.gender.toString().equals("2", ignoreCase = true)) {
                    if (!Utilities.isNullOrEmpty(strAgeGender)) {
                        binding.txtAgeGender.text = strAgeGender + " , " + resources.getString(R.string.FEMALE)
                    } else {
                        binding.txtAgeGender.text = resources.getString(R.string.FEMALE)
                    }
                }

                if (!Utilities.isNullOrEmpty(user.firstName)) {
                    binding.txtUsername.text = user.firstName
                    binding.txtName.text = user.firstName
                    binding.edtUsername.setText(user.firstName)
                }

                if (!Utilities.isNullOrEmpty(email)) {
                    binding.txtEmail.text = email
                    binding.edtEmail.setText(email)
                }

                if (!Utilities.isNullOrEmpty(alternateEmail)) {
                    binding.layoutAlternateEmail.visibility = View.VISIBLE
                    binding.tilEdtAlternateEmail.error = null
                    binding.tilEdtAlternateEmail.isErrorEnabled = false
                    binding.txtAlternateEmail.text = alternateEmail
                    binding.edtAlternateEmail.setText(alternateEmail)
                } else {
                    binding.layoutAlternateEmail.visibility = View.GONE
                }

                if (!Utilities.isNullOrEmpty(number)) {
                    binding.layoutNumber.visibility = View.VISIBLE
                    binding.txtNumber.text = number
                    binding.edtNumber.setText(number)
                } else {
                    binding.layoutNumber.visibility = View.GONE
                }

                if (!Utilities.isNullOrEmpty(alternateNumber)) {
                    binding.layoutAlternateNumber.visibility = View.VISIBLE
                    binding.tilEdtAlternateNumber.error = null
                    binding.tilEdtAlternateNumber.isErrorEnabled = false
                    binding.txtAlternateNumber.text = alternateNumber
                    binding.edtAlternateNumber.setText(alternateNumber)
                } else {
                    binding.layoutAlternateNumber.visibility = View.GONE
                }

                if (!Utilities.isNullOrEmpty(address)) {
                    binding.layoutAddress.visibility = View.VISIBLE
                    binding.txtAddress.text = address
                    binding.edtAddress.setText(address)
                } else {
                    binding.layoutAddress.visibility = View.GONE
                }
            }
        } catch ( e : Exception ) {
            e.printStackTrace()
        }
    }

    fun setProfilePic() {
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

    fun removeProfilePic() {
        hasProfileImage = false
        binding.imgUserPic.setImageResource(R.drawable.img_my_profile)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        println("requestCode,resultCode,data----->$requestCode,$resultCode,$data")
        val realPathUtil = RealPathUtil
        var pathTemp = ""
        try {
            if (resultCode == Activity.RESULT_OK && data != null) {
                var documentType = ""
                when (requestCode) {

                    FilePickerConst.REQUEST_CODE_PHOTO -> {
                        val photoUriList = data.getParcelableArrayListExtra<Uri>(FilePickerConst.KEY_SELECTED_MEDIA)!!.toMutableList()
                        val photoUri = photoUriList[0]
                        //Timber.e("photoUri---> $photoUri")
                        val photoPath = ContentUriUtils.getFilePath(this, photoUri)!!
                        val origFileName = photoPath.substring(photoPath.lastIndexOf("/") + 1)
                        val fileSize = RealPathUtil.calculateFileSize(photoPath,"MB")
                        if (fileSize <= 5.0) {
                            val destinationUriGallery = Uri.fromFile(File(Environment.getExternalStorageDirectory().toString(), origFileName))
                            showUcrop(photoUri, destinationUriGallery)
                        } else {
                            Utilities.toastMessageShort(this, resources.getString(R.string.ERROR_FILE_SIZE_LESS_THEN_5MB))
                        }
                    }

                    cameraSelectCode -> {
                        val photo = data.extras!!["data"] as Bitmap?
                        val sourceUriCamera: Uri = realPathUtil.getImageUri(this, photo)
                        val newFlePath1: String = realPathUtil.getPath(this, sourceUriCamera)!!
                        val origFileName1 = newFlePath1.substring(newFlePath1.lastIndexOf("/") + 1)
                        pathTemp = realPathUtil.getPath(this, sourceUriCamera)!!
                        val destinationUriCamera = Uri.fromFile(File(Environment.getExternalStorageDirectory().toString(), origFileName1))
                        showUcrop(sourceUriCamera, destinationUriCamera)
                    }

                    gallerySelectCode -> {
                        val sourceUriGallery = data.data
                        val newFilePathGallery: String = realPathUtil.getPath(this, sourceUriGallery!!)!!
                        val origFileName2 = newFilePathGallery.substring(newFilePathGallery.lastIndexOf("/") + 1)
                        val newFile2 = File(Environment.getExternalStorageDirectory().toString(), origFileName2)
                        val destinationUriGallery = Uri.fromFile(newFile2)
                        showUcrop(sourceUriGallery, destinationUriGallery)
                    }

                    UCrop.REQUEST_CROP ->
                        try {
                            val resultUri = UCrop.getOutput(data)
                            val flePath1: String = realPathUtil.getPath(this, resultUri!!)!!
                            val fileSize = realPathUtil.calculateFileSize(flePath1,"MB")
                            if (fileSize <= 5.0) {
                                var save1 = false
                                val extension1: String = realPathUtil.getFileExt(flePath1)
                                Timber.e("Extension : $extension1")
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
                                    val fileName: String = realPathUtil.generateUniqueFileName(Configuration.strAppIdentifier + "_REC", flePath1)
                                    Timber.e("File Path---> $flePath1")
                                    save1 = realPathUtil.saveFileToExternalCard(flePath1,fileName,Constants.RECORD)
                                    val mainDirectoryPath: String = realPathUtil.getRecordFolderLocation()
                                    if (save1) {
                                        Utilities.deleteFileFromLocalSystem(pathTemp)
                                        viewModel.callUploadProfileImageApi(this,fileName, mainDirectoryPath, flePath1)
                                    }
                                } else {
                                    Utilities.deleteFileFromLocalSystem(pathTemp)
                                    Utilities.toastMessageShort(this, extension1 + " " + resources.getString(R.string.ERROR_FILES_NOT_ACCEPTED))
                                }
                            } else {
                                Utilities.deleteFileFromLocalSystem(pathTemp)
                                Utilities.toastMessageShort(this, resources.getString(R.string.ERROR_FILE_SIZE_LESS_THEN_5MB))
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Utilities.toastMessageShort(this, resources.getString(R.string.ERROR_UNABLE_TO_READ_FILE))
                        }

                    UCrop.RESULT_ERROR -> {
                        val  cropError: Throwable?= UCrop.getError(data)
                    }
                }
            } else {
                //	DialogHelper.showNoticeDialog(" Error: "," Unable to upload this file", 1, context);
            }
            //Utilities.hideKeyboard(this)
            super.onActivityResult(requestCode, resultCode, data)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {

        }
    }

    fun startImageShimmer() {
        binding.layoutImgShimmer.startShimmer()
        binding.layoutImgShimmer.visibility = View.VISIBLE
        binding.layoutImgDetails.visibility = View.GONE
    }

    fun startDetailsShimmer() {
        binding.layoutDetailsShimmer.startShimmer()
        binding.layoutDetailsShimmer.visibility = View.VISIBLE
        binding.layoutShowDetails.visibility = View.GONE
    }

    fun stopImageShimmer() {
        binding.layoutImgShimmer.stopShimmer()
        binding.layoutImgShimmer.visibility = View.GONE
        binding.layoutImgDetails.visibility = View.VISIBLE
    }

    fun stopDetailsShimmer() {
        binding.layoutDetailsShimmer.startShimmer()
        binding.layoutDetailsShimmer.visibility = View.GONE
        binding.layoutShowDetails.visibility = View.VISIBLE
    }

}
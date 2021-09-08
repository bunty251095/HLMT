package com.caressa.common.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.caressa.common.R
import com.caressa.common.constants.Constants
import com.caressa.common.constants.PreferenceConstants
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import timber.log.Timber
import java.io.File

object PermissionUtil : ActivityCompat(), KoinComponent {

    //private val tag = "PermissionUtil : "
    private val preferenceUtils: PreferenceUtils = get()
    private val fileUtils = FileUtils

    interface AppPermissionListener {
        fun isPermissionGranted(isGranted: Boolean)
    }

    interface StorageAccessListener {
        fun isStorageAccessGranted(isGranted: Boolean)
    }

    fun checkStoragePermission(listener: AppPermissionListener, context:Context): Boolean {
        var isPermissionGranted = false
        if (checkSelfPermission(context,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            && checkSelfPermission(context,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            val dialogData = DefaultNotificationDialog.DialogData()
            dialogData.title = context.resources.getString(R.string.PERMISSION_REQUIRED)
            dialogData.message = context.resources.getString(R.string.NEED_STORAGE_PERMISSION)
            dialogData.btnLeftName = context.resources.getString(R.string.CANCEL)
            dialogData.btnRightName = context.resources.getString(R.string.OK)
            val defaultNotificationDialog = DefaultNotificationDialog(context,
                object : DefaultNotificationDialog.OnDialogValueListener {
                    override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
                        if (isButtonRight) {
                            storageDexterPermissionCheck(listener,context)
                        }
                    }
                }, dialogData)
            defaultNotificationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            defaultNotificationDialog.show()
        } else {
            isPermissionGranted = true
        }

        return isPermissionGranted
    }

    fun checkStorageAccessPermissionFromFragment(context:Context,fragment: Fragment): Boolean {
        var isPermissionGranted = false
        if ( !checkFolderUriPermission(context) ) {
            val appName = Utilities.getAppName(context)
            val location = "<B><font color='#3f4846'>storage/Pictures/$appName</font></B>"
            val btnText =  "<B><font color='#3f4846'>"+ "USE THIS FOLDER" +"</font></B>"
            val dialogData = DefaultNotificationDialog.DialogData()
            dialogData.title = context.resources.getString(R.string.FILES_ACCESS_REQUIRED)
            dialogData.message = "${context.resources.getString(R.string.PLEASE_SELECT)} $location ${context.resources.getString(R.string.FILES_ACCESS_MSG1)} $btnText ${context.resources.getString(R.string.FILES_ACCESS_MSG2)}"
            dialogData.btnLeftName = context.resources.getString(R.string.CANCEL)
            dialogData.btnRightName = context.resources.getString(R.string.OK)
            val defaultNotificationDialog = DefaultNotificationDialog(context,
                object : DefaultNotificationDialog.OnDialogValueListener {
                    override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
                        if (isButtonRight) {
                            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                            fragment.startActivityForResult(intent,Constants.REQ_CODE_SAF)
                        }
                    }
                }, dialogData)
            defaultNotificationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            defaultNotificationDialog.show()
        } else {
            isPermissionGranted = true
        }
        return isPermissionGranted
    }

    fun checkStorageAccessPermissionFromActivity(context:Context,activity: Activity): Boolean {
        var isPermissionGranted = false
        if ( !checkFolderUriPermission(context) ) {
            val appName = Utilities.getAppName(context)
            val location = "<B><font color='#3f4846'>storage/Pictures/$appName</font></B>"
            val btnText =  "<B><font color='#3f4846'>"+ "USE THIS FOLDER" +"</font></B>"
            val dialogData = DefaultNotificationDialog.DialogData()
            dialogData.title = context.resources.getString(R.string.FILES_ACCESS_REQUIRED)
            dialogData.message = "${context.resources.getString(R.string.PLEASE_SELECT)} $location ${context.resources.getString(R.string.FILES_ACCESS_MSG1)} $btnText ${context.resources.getString(R.string.FILES_ACCESS_MSG2)}"
            dialogData.btnLeftName = context.resources.getString(R.string.CANCEL)
            dialogData.btnRightName = context.resources.getString(R.string.OK)
            val defaultNotificationDialog = DefaultNotificationDialog(context,
                object : DefaultNotificationDialog.OnDialogValueListener {
                    override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
                        if (isButtonRight) {
                            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                            activity.startActivityForResult(intent,Constants.REQ_CODE_SAF)
                        }
                    }
                }, dialogData)
            defaultNotificationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            defaultNotificationDialog.show()
        } else {
            isPermissionGranted = true
        }
        return isPermissionGranted
    }

/*    fun checkStoragePermissionFromActivity(listener: AppPermissionListener,context:Context,activity: Activity): Boolean {
        var isPermissionGranted = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val permission = Environment.isExternalStorageManager()
            Timber.e("per--->$permission")
            if ( !permission ) {
                val dialogData = DefaultNotificationDialog.DialogData()
                dialogData.title = context.resources.getString(R.string.PERMISSION_REQUIRED)
                dialogData.message = context.resources.getString(R.string.NEED_STORAGE_PERMISSION)
                dialogData.btnLeftName = context.resources.getString(R.string.CANCEL)
                dialogData.btnRightName = context.resources.getString(R.string.OK)
                val defaultNotificationDialog = DefaultNotificationDialog(context,
                    object : DefaultNotificationDialog.OnDialogValueListener {
                        override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
                            if (isButtonRight) {
                                try {
                                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                                    intent.addCategory(Intent.CATEGORY_DEFAULT)
                                    intent.data = Uri.parse(String.format("package:%s", context.applicationContext.packageName))
                                    activity.startActivityForResult(intent,Constants.REQ_CODE_STORAGE)
                                } catch (e:Exception) {
                                    val intent = Intent()
                                    intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                                    activity.startActivityForResult(intent,Constants.REQ_CODE_STORAGE)
                                }
                            }
                        }
                    }, dialogData)
                defaultNotificationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                defaultNotificationDialog.show()
            } else {
                isPermissionGranted = true
            }
        } else {
            if (checkSelfPermission(context,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(context,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                val dialogData = DefaultNotificationDialog.DialogData()
                dialogData.title = context.resources.getString(R.string.PERMISSION_REQUIRED)
                dialogData.message = context.resources.getString(R.string.NEED_STORAGE_PERMISSION)
                dialogData.btnLeftName = context.resources.getString(R.string.CANCEL)
                dialogData.btnRightName = context.resources.getString(R.string.OK)
                val defaultNotificationDialog = DefaultNotificationDialog(context,
                    object : DefaultNotificationDialog.OnDialogValueListener {
                        override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
                            if (isButtonRight) {
                                storageDexterPermissionCheck(listener,context)
                            }
                        }
                    }, dialogData)
                defaultNotificationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                defaultNotificationDialog.show()
            } else {
                isPermissionGranted = true
            }
        }
        return isPermissionGranted
    }

    fun checkStoragePermissionFromFragment(listener: AppPermissionListener,context:Context,fragment: Fragment): Boolean {
        var isPermissionGranted = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val permission = Environment.isExternalStorageManager()
            Timber.e("per--->$permission")
            if ( !permission ) {
                val dialogData = DefaultNotificationDialog.DialogData()
                dialogData.title = context.resources.getString(R.string.PERMISSION_REQUIRED)
                dialogData.message = context.resources.getString(R.string.NEED_STORAGE_PERMISSION)
                dialogData.btnLeftName = context.resources.getString(R.string.CANCEL)
                dialogData.btnRightName = context.resources.getString(R.string.OK)
                val defaultNotificationDialog = DefaultNotificationDialog(context,
                    object : DefaultNotificationDialog.OnDialogValueListener {
                        override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
                            if (isButtonRight) {
                                try {
                                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                                    intent.addCategory(Intent.CATEGORY_DEFAULT)
                                    intent.data = Uri.parse(String.format("package:%s", context.applicationContext.packageName))
                                    fragment.startActivityForResult(intent,Constants.REQ_CODE_STORAGE)
                                } catch (e:Exception) {
                                    val intent = Intent()
                                    intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                                    fragment.startActivityForResult(intent,Constants.REQ_CODE_STORAGE)
                                }
                            }
                        }
                    }, dialogData)
                defaultNotificationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                defaultNotificationDialog.show()
            } else {
                isPermissionGranted = true
            }
        } else {
            if (checkSelfPermission(context,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(context,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                val dialogData = DefaultNotificationDialog.DialogData()
                dialogData.title = context.resources.getString(R.string.PERMISSION_REQUIRED)
                dialogData.message = context.resources.getString(R.string.NEED_STORAGE_PERMISSION)
                dialogData.btnLeftName = context.resources.getString(R.string.CANCEL)
                dialogData.btnRightName = context.resources.getString(R.string.OK)
                val defaultNotificationDialog = DefaultNotificationDialog(context,
                    object : DefaultNotificationDialog.OnDialogValueListener {
                        override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
                            if (isButtonRight) {
                                storageDexterPermissionCheck(listener,context)
                            }
                        }
                    }, dialogData)
                defaultNotificationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                defaultNotificationDialog.show()
            } else {
                isPermissionGranted = true
            }
        }
        return isPermissionGranted
    }*/

    fun storageDexterPermissionCheck(listener: AppPermissionListener, context:Context) {
        Dexter.withContext(context).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener( object  : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        if(report.areAllPermissionsGranted()) {
                            listener.isPermissionGranted(true)
                            fileUtils.makeFolderDirectories(context)
                        }
                        if(report.isAnyPermissionPermanentlyDenied) {
                            Utilities.toastMessageShort(context,context.resources.getString(R.string.ERROR_STORAGE_PERMISSION))
                            listener.isPermissionGranted(false)
                        }
                    }
                }
                override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {
                    token?.continuePermissionRequest()
                }
            })
            .withErrorListener { }
            .check()
    }

    fun checkCameraPermission(listener: AppPermissionListener, context:Context): Boolean {
        var isPermissionGranted = false
        if (checkSelfPermission(context,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            val dialogData = DefaultNotificationDialog.DialogData()
            dialogData.title = context.resources.getString(R.string.PERMISSION_REQUIRED)
            dialogData.message = context.resources.getString(R.string.NEED_CAMERA_PERMISSION)
            dialogData.btnLeftName = context.resources.getString(R.string.CANCEL)
            dialogData.btnRightName = context.resources.getString(R.string.OK)
            val defaultNotificationDialog = DefaultNotificationDialog(context,
                object : DefaultNotificationDialog.OnDialogValueListener {
                    override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
                        if (isButtonRight) {
                            cameraDexterPermissionCheck(listener,context)
                        }
                    }
                }, dialogData)
            defaultNotificationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            defaultNotificationDialog.show()
        } else {
            isPermissionGranted = true
        }
        return isPermissionGranted
    }

    fun cameraDexterPermissionCheck(listener: AppPermissionListener, context:Context) {
        Dexter.withContext(context)
            .withPermission(Manifest.permission.CAMERA)
            .withListener( object : PermissionListener {

                override fun onPermissionGranted(permissionResp: PermissionGrantedResponse?) {
                    listener.isPermissionGranted(true)
                }

                override fun onPermissionDenied(permissionResp: PermissionDeniedResponse?) {
                    Utilities.toastMessageShort(context,context.resources.getString(R.string.ERROR_CAMERA_PERMISSION))
                    listener.isPermissionGranted(false)
                }

                override fun onPermissionRationaleShouldBeShown(p0: PermissionRequest?, token: PermissionToken?) {
                    token?.continuePermissionRequest()
                }

            })
            .withErrorListener { }
            .check()
    }

    private fun checkFolderUriPermission(context:Context) : Boolean {
        val isGranted: Boolean
        val folderUri = preferenceUtils.getPreference(PreferenceConstants.FOLDER_URI)
        Timber.e("FolderUri: $folderUri")
        if ( !Utilities.isNullOrEmpty(folderUri) && File(Utilities.getAppFolderLocation(context)).exists() ) {
            isGranted = arePermissionsGranted(folderUri,context)
        } else {
            fileUtils.makeFolderDirectories(context)
            isGranted = false
        }
        return isGranted
    }

    private fun arePermissionsGranted(uriString: String,context: Context): Boolean {
        // list of all persisted permissions for our app
        val list = context.contentResolver.persistedUriPermissions
        for (i in list.indices) {
            val persistedUriString = list[i].uri.toString()
            if (persistedUriString == uriString && list[i].isWritePermission && list[i].isReadPermission) {
                return true
            }
        }
        return false
    }

    @SuppressLint("TimberArgCount")
    fun releasePermissions(uri: Uri?, context: Context, listener: StorageAccessListener) {
        try {
            Timber.e("Folder_Access_Uri--->$uri")
            if ( uri != null ) {
                if (Uri.decode(uri.toString()).endsWith(":")) {
                    Utilities.toastMessageShort(context,context.resources.getString(R.string.CAN_NOT_USE_ROOT_FOLDER))
                    listener.isStorageAccessGranted(false)
                } else {
                    val appName = Utilities.getAppName(context)
                    if ( uri.toString().contains("primary",ignoreCase = true)
                        && uri.toString().contains("Pictures",ignoreCase = true) ) {

                        val tempUri = uri.toString().replace("%20"," ")
                        Timber.e("tempUri--->$tempUri")
                        val folder = tempUri.takeLast(appName.length)
                        Timber.e("App_Folder--->$folder")

                        if ( folder == appName ) {
                            // here we ask the content resolver to persist the permission for us
                            val flagApp: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                            context.contentResolver.takePersistableUriPermission(uri, flagApp)
                            // we should store the string for further use
                            preferenceUtils.storePreference(PreferenceConstants.FOLDER_URI,uri.toString())
                            listener.isStorageAccessGranted(true)
                        } else {
                            listener.isStorageAccessGranted(false)
                            Utilities.toastMessageShort(context,"${context.resources.getString(R.string.PLEASE_SELECT)} storage/Pictures/$appName ${context.resources.getString(R.string.RELEASE_PERMISSIONS_MSG)}")
                        }
                    } else {
                        listener.isStorageAccessGranted(false)
                        Utilities.toastMessageShort(context,"${context.resources.getString(R.string.PLEASE_SELECT)} storage/Pictures/$appName ${context.resources.getString(R.string.RELEASE_PERMISSIONS_MSG)}")
                    }
                }
            }
        } catch ( e:Exception ) {
            e.printStackTrace()
        }
    }

}
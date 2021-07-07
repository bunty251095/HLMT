package com.caressa.common.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.caressa.common.R
import com.caressa.common.constants.Constants
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import timber.log.Timber

class PermissionUtil : ActivityCompat() {

    private var instance: PermissionUtil? = null

    fun getInstance(): PermissionUtil? {
        if (instance == null) {
            instance = PermissionUtil()
        }
        return instance
    }

    var appPermissionListener: AppPermissionListener? = null

    interface AppPermissionListener {
        fun isPermissionGranted(isGranted: Boolean)
    }

    fun checkStoragePermissionFromActivity(listener: AppPermissionListener,context:Context,activity: Activity): Boolean {
        this.appPermissionListener = listener
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

    fun checkStoragePermissionFromFragment(listener: AppPermissionListener,context:Context,fragment: Fragment): Boolean {
        this.appPermissionListener = listener
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

/*    fun checkStoragePermissionFromActivity(listener: AppPermissionListener,context:Context,activity: Activity): Boolean {
        this.appPermissionListener = listener
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
                                    activity.startActivityForResult(intent, Constants.REQ_CODE_STORAGE)
                                } catch (e:Exception) {
                                    val intent = Intent()
                                    intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                                    activity.startActivityForResult(intent, Constants.REQ_CODE_STORAGE)
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
        this.appPermissionListener = listener
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
                                    fragment.startActivityForResult(intent, Constants.REQ_CODE_STORAGE)
                                } catch (e:Exception) {
                                    val intent = Intent()
                                    intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                                    fragment.startActivityForResult(intent, Constants.REQ_CODE_STORAGE)
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

    fun checkCameraPermission(listener: AppPermissionListener, context:Context): Boolean {
        this.appPermissionListener = listener
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

    fun storageDexterPermissionCheck(listener: AppPermissionListener, context:Context) {
        Dexter.withContext(context).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener( object  : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        if(report.areAllPermissionsGranted()) {
                            listener.isPermissionGranted(true)
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

    /*    fun allStorageDexterPermissionCheck(listener: AppPermissionListener, context:Context) {
        Dexter.withContext(context)
            .withPermission(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
            .withListener( object : PermissionListener {

                override fun onPermissionGranted(permissionResp: PermissionGrantedResponse?) {
                    listener.isPermissionGranted(true)
                }

                override fun onPermissionDenied(permissionResp: PermissionDeniedResponse?) {
                    Utilities.toastMessageShort(context,context.resources.getString(R.string.ERROR_STORAGE_PERMISSION))
                    listener.isPermissionGranted(false)
                }

                override fun onPermissionRationaleShouldBeShown(p0: PermissionRequest?, token: PermissionToken?) {
                    token?.continuePermissionRequest()
                }

            })
            .withErrorListener { }
            .check()
    }*/

}
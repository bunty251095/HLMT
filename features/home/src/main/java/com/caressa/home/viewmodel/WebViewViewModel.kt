package com.caressa.home.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.caressa.common.base.BaseViewModel
import com.caressa.home.common.DataHandler
import com.caressa.home.domain.HomeManagementUseCase
import com.caressa.model.entity.Users
import com.caressa.repository.AppDispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class WebViewViewModel(private val homeManagementUseCase: HomeManagementUseCase, private val dispatchers: AppDispatchers,
                       private val dataHandler : DataHandler , val context: Context) : BaseViewModel() {

    var userDetails = MutableLiveData<Users>()
    var wellnessCentreList = MutableLiveData<List<DataHandler.WellnessCentreDetails>>()

    fun getLoggedInPersonDetails( ) = viewModelScope.launch {
        withContext(dispatchers.io) {
            userDetails.postValue(homeManagementUseCase.invokeGetLoggedInPersonDetails())
        }
    }

    fun getWellnessCentreOptionsList() {
        wellnessCentreList.postValue( dataHandler.getWellnessCentreList() )
    }

    @SuppressLint("BinaryOperationInTimber")
    fun copyAndOpenAssetFile(activity: Context, assetsFolder: String, destinationFolder: String) {
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        try {
            for (fileName in activity.assets.list(assetsFolder)!!) {
                if( fileName == "ApolloStoreListwithPincodes.xlsx" ) {
                    Timber.i("REAL_PATH_UTIL---->$assetsFolder$fileName")
                    Timber.i("REAL_PATH_UTIL : Trying to open---->"
                            + assetsFolder + (if (assetsFolder.endsWith(File.pathSeparator)) "" else File.pathSeparator) + fileName)
                    try {
                        val fileReader = ByteArray(4096)
                        var fileSizeDownloaded: Long = 0
                        inputStream = activity.assets.open("$assetsFolder/$fileName")
                        val file = File(destinationFolder, fileName)
                        outputStream = FileOutputStream(File(destinationFolder, fileName))

                        while (true) {
                            val read = inputStream.read(fileReader)
                            if (read == -1) {
                                break
                            }
                            outputStream.write(fileReader, 0, read)
                            fileSizeDownloaded += read.toLong()
                        }
                        outputStream.flush()
                        openDownloadedFile(file)
                    } catch (e: Exception) {
                        Timber.i("Error..."+e.printStackTrace())
                    } finally {
                        inputStream?.close()
                        outputStream?.close()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openDownloadedFile( file: File) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.ms-excel")
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            //AlertDialogHelper( this , "No Application Available to View Excel." ,"You need Excel viewer to open this file.")
        }
    }

}
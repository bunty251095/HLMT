package com.caressa.common.utils

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Base64
import androidx.annotation.WorkerThread
import com.caressa.common.constants.Constants
import com.caressa.common.R
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.URISyntaxException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min

object FileUtils : KoinComponent {

    private val preferenceUtils: PreferenceUtils = get()

    val isExternalStorageAvailable: Boolean
        get() {
            val state = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == state || Environment.MEDIA_MOUNTED_READ_ONLY == state

        }

    val isExternalStorageWritable: Boolean
        get() {
            val state = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == state
        }

    fun makeFolderDirectories(context: Context) {
        try {
            val dir = File(Utilities.getAppFolderLocation(context))
            if (!dir.exists()) {
                val directoryCreated = dir.mkdirs()
                Timber.e("DirectoryCreated--->$directoryCreated")
                //Timber.e("DirectoryCreated--->$directoryCreated , {$dir} ")
            } else {
                Timber.e("DirectoryAlreadyExist")
                //Timber.e("DirectoryAlreadyExist--->{$dir} ")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getFileExt(fileName: String): String {
        var extension = ""
        if( !Utilities.isNullOrEmpty(fileName) ) {
            val index = fileName.lastIndexOf(".")
            if (index == -1) {
                return ""
            }
            val strFileEXT = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length)
            extension = strFileEXT.trim { it <= ' ' }

        }
        Timber.e("Extension : $extension")
        return extension
    }

    fun generateUniqueFileName(className: String, FilePath: String): String {
        val extension = getFileExt(FilePath)
        var filename = ""
        if ( !Utilities.isNullOrEmpty(extension) ) {

            val sdf = SimpleDateFormat("ddMMyy-hhmmss")
            val random = Random()
            filename = className + "-" + String.format("%s.%s",sdf.format(Date()), random.nextInt(9)) + "." + extension
        }
        return filename
    }

    fun getNameWithoutExtension( fileName : String) : String {
        val dotIndex: Int = fileName.lastIndexOf('.')
        return if (dotIndex == -1) fileName else fileName.substring(0, dotIndex)
    }

/*    fun removeFileExt(fileName: String): String {
        val index = fileName.lastIndexOf(".")
        return if (index == -1) {
            ""
        } else fileName.substring(0, fileName.lastIndexOf("."))
    }*/

    fun getUniqueIdLong(): String {
        val r = Random()
        val systemTime = System.currentTimeMillis()
        val unixtime = (systemTime + r.nextDouble() * 60.0 * 60.0 * 24.0 * 365.0).toLong()

        return unixtime.toString()
    }

    fun getImageUri(inContext: Context, inImage: Bitmap?): Uri {
        //val bytes = ByteArrayOutputStream()
        //inImage!!.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "img_" + System.currentTimeMillis(), null)
        return Uri.parse(path)
    }

    fun calculateFileSize(filepath: String,type:String): Double {
        Timber.e("File Path---> $filepath")
        var calculatedSize = 0.0
        val file = File(filepath)

        val bytes = if (!file.exists()) 0.0 else file.length().toDouble()

        // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
        val kilobytes = bytes / 1024

        // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
        val megabytes = kilobytes / 1024

        val gigabytes = megabytes / 1024
        val terabytes = gigabytes / 1024

        when {
            type.equals("KB",ignoreCase = true) -> {
                calculatedSize = kilobytes
            }
            type.equals("MB",ignoreCase = true) -> {
                calculatedSize = megabytes
            }
            type.equals("GB",ignoreCase = true) -> {
                calculatedSize = gigabytes
            }
            type.equals("TB",ignoreCase = true) -> {
                calculatedSize = terabytes
            }
        }
        val finalSize = Utilities.roundOffPrecision(calculatedSize,2)
        Timber.e("File Size : $finalSize ${type.toUpperCase()}")
        return finalSize
    }

    fun calculateFileSize(file: File,type:String): Double {
        Timber.e("File Path---> ${file.absoluteFile}")
        var calculatedSize = 0.0

        val bytes = if (!file.exists()) 0.0 else file.length().toDouble()

        // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
        val kilobytes = bytes / 1024

        // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
        val megabytes = kilobytes / 1024

        val gigabytes = megabytes / 1024
        val terabytes = gigabytes / 1024

        when {
            type.equals("KB",ignoreCase = true) -> {
                calculatedSize = kilobytes
            }
            type.equals("MB",ignoreCase = true) -> {
                calculatedSize = megabytes
            }
            type.equals("GB",ignoreCase = true) -> {
                calculatedSize = gigabytes
            }
            type.equals("TB",ignoreCase = true) -> {
                calculatedSize = terabytes
            }
        }
        val finalSize = Utilities.roundOffPrecision(calculatedSize,2)
        Timber.e("File Size : $finalSize ${type.toUpperCase()}")
        return finalSize
    }

    fun convertBase64ToBitmap(base64Input: String?): Bitmap? {
        var base64Input = base64Input
        var bmp: Bitmap? = null
        if (base64Input != null && !base64Input.equals("",
                ignoreCase = true) && !base64Input.equals("null", ignoreCase = true)) {
            run {
                var decodedString = Base64.decode(base64Input, Base64.DEFAULT)
                base64Input = null
                System.gc()
                if (decodedString != null) {
                    val save = false
                    bmp = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                }
                decodedString = null
                System.gc()
            }
        }
        return bmp!!
    }

    fun createDirectory(folderName: String)  {
        if (isExternalStorageAvailable && isExternalStorageWritable) {
            val myDirectory = File(folderName)
            if (!myDirectory.exists()) {
                myDirectory.mkdirs()
            }
        }
    }

    fun saveBitmapToExternalStorage(context: Context, bitmap: Bitmap?, fileName: String): File? {
        var recordFile : File? = null
        val folderName = Utilities.getAppFolderLocation(context)

        if (isExternalStorageAvailable && isExternalStorageWritable) {
            val myDirectory = File(folderName)

            if (!myDirectory.exists()) {
                myDirectory.mkdirs()
            }

            val absolutePathDic = myDirectory.absolutePath
            if (bitmap != null && absolutePathDic != null) {

                val file = File(folderName, fileName)
                Timber.e("downloadDocPath--->$file")

                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                var byteArray: ByteArray? = stream.toByteArray() // convert camera photo to byte array
                val compressedBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)

                context.contentResolver.openFileDescriptor(Uri.fromFile(file), "w")?.use { parcelFileDescriptor ->
                    FileOutputStream(parcelFileDescriptor.fileDescriptor).use { outStream ->
                        outStream.write(byteArray)
                        recordFile = file
                    }
                }
                byteArray = null
                compressedBitmap.recycle()
            }
        }
        return recordFile
    }

    fun saveByteArrayToExternalStorage(context: Context, byteArray: ByteArray?, fileName: String ): File? {
        var recordFile : File? = null
        val folderName = Utilities.getAppFolderLocation(context)

        if (isExternalStorageAvailable && isExternalStorageWritable) {

            val myDirectory = File(folderName)
            if (!myDirectory.exists()) {
                myDirectory.mkdirs()
            }

            val file = File(folderName, fileName)
            Timber.e("downloadDocPath--->$file")

            context.contentResolver.openFileDescriptor(Uri.fromFile(file), "w")?.use { parcelFileDescriptor ->
                FileOutputStream(parcelFileDescriptor.fileDescriptor).use { outStream ->
                    outStream.write(byteArray)
                    recordFile = file
                }
            }
        }
        return recordFile
    }

    fun saveRecordToExternalStorage(context:Context, inputPath:String, inputUri:Uri, fileName:String): File? {
        var downloadedFile : File? = null
        val folderName = Utilities.getAppFolderLocation(context)
        if (isExternalStorageAvailable && isExternalStorageWritable) {
            val myDirectory = File(folderName)

            if (!myDirectory.exists()) {
                myDirectory.mkdirs()
            }

            val absolutePathDic = myDirectory.absolutePath
            if (inputPath != null && absolutePathDic != null) {
                val file = File(folderName, fileName)
                Timber.e("downloadDocPath: ----->$file")

                val fileReader = ByteArray(4096)

                context.contentResolver.openFileDescriptor(inputUri, "r")?.use { parcelFileDescriptor ->
                    FileInputStream(parcelFileDescriptor.fileDescriptor).use { inStream ->

                        context.contentResolver.openFileDescriptor(Uri.fromFile(file), "w")?.use { parcelFileDescriptor ->
                            FileOutputStream(parcelFileDescriptor.fileDescriptor).use { outStream ->
                                while (true) {
                                    val read = inStream.read(fileReader)
                                    if (read == -1) {
                                        break
                                    }
                                    outStream.write(fileReader, 0, read)
                                }
                                downloadedFile = file
                            }
                        }
                    }
                }

            }
        }
        return downloadedFile
    }

    /**
     * make sure to use this getFilePath method from worker thread
     */
    @WorkerThread
    @Throws(URISyntaxException::class)
    fun getFilePath(context: Context, uri: Uri): String? {
        Timber.e("SCHEME---> ${uri.scheme}")
        var selection: String? = null
        var selectionArgs: Array<String>? = null
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context, uri)) {
            when {
                // ExternalStorageProvider
                isExternalStorageDocument(uri) -> {
                    Timber.e("isExternalStorageDocument")
                    val docId = DocumentsContract.getDocumentId(uri)
                    Timber.e("docId--->$docId")
                    val split = docId.split(":").toTypedArray()
                    return Constants.primaryStorage + "/" + split[1]
                }
                // DownloadsProvider
                isDownloadsDocument(uri) -> {
                    Timber.e("isDownloadsDocument")
                    val fileName = getPath(context, uri)
                    if (fileName != null) {
                        return Constants.primaryStorage + "/Download/" + fileName
                    }
                    var id = DocumentsContract.getDocumentId(uri)
                    if (id.startsWith("raw:")) {
                        id = id.replaceFirst("raw:".toRegex(), "")
                        val file = File(id)
                        if (file.exists()) return id
                    }
                    val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))
                    return getDataColumn(context, contentUri, null, null)
                }
                /*                isDownloadsDocument(uri) -> {
                    Timber.e("isDownloadsDocument")
                    val docId = DocumentsContract.getDocumentId(uri)
                    Timber.e("docId--->$docId")
                    val split = docId.split(":").toTypedArray()
                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(split[1]))
                    return RealPathUtil.getDataColumn(context, contentUri, null, null)
                }*/
                // MediaProvider
                isMediaDocument(uri) -> {
                    Timber.e("isMediaDocument")
                    val docId = DocumentsContract.getDocumentId(uri)
                    Timber.e("docId--->$docId")
                    val split = docId.split(":").toTypedArray()
                    val type = split[0]
                    var contentUri: Uri? = uri
                    when (type) {
                        "image" -> {
                            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        }
                        "video" -> {
                            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        }
                        "audio" -> {
                            contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                        }
                    }
                    selection = "_id=?"
                    selectionArgs = arrayOf(split[1])
                    return getDataColumn(context, contentUri, selection, selectionArgs)
/*                    try {
                        //val column = MediaStore.Images.Media.DATA
                        val column = "_data"
                        val projection = arrayOf(column)
                        val cursor = context.contentResolver?.query(contentUri!!, projection, selection, selectionArgs, null)
                        var path: String? = null
                        if (cursor != null) {
                            val column_index = cursor.getColumnIndexOrThrow(column)
                            if (cursor.moveToFirst()) {
                                path = cursor.getString(column_index)
                                cursor.close()
                            }
                        }
                        return path
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }*/
                }
                //GoogleDriveProvider
                isGoogleDriveUri(uri) -> {
                    return getGoogleDriveFilePath(uri, context)
                }
            }
        }
        // MediaStore (and general)
        if (ContentResolver.SCHEME_CONTENT.equals(uri.scheme, ignoreCase = true)) {
            // Return the remote address
            if (isGooglePhotosUri(uri)) {
                return uri.lastPathSegment
            }
            // Google drive legacy provider
            else if (isGoogleDriveUri(uri)) {
                return getGoogleDriveFilePath(uri, context)
            }
            return getDataColumn(context, uri, null, null)
        }
        // File
        else if (ContentResolver.SCHEME_FILE.equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    private fun isGoogleDriveUri(uri: Uri): Boolean {
        return "com.google.android.apps.docs.storage" == uri.authority
                || "com.google.android.apps.docs.storage.legacy" == uri.authority
    }

    private fun getGoogleDriveFilePath(uri: Uri, context: Context): String? {
        val returnCursor = context.contentResolver.query(uri, null, null, null, null)
        /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
        val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        //val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        //val size = java.lang.Long.toString(returnCursor.getLong(sizeIndex))
        val file = File(context.cacheDir, name)
        try {
            val inputStream =
                context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            var read: Int
            val maxBufferSize = 1 * 1024 * 1024
            val bytesAvailable = inputStream!!.available()
            val bufferSize = min(bytesAvailable, maxBufferSize)
            val buffers = ByteArray(bufferSize)
            while (inputStream.read(buffers).also { read = it } != -1) {
                outputStream.write(buffers, 0, read)
            }
            inputStream.close()
            outputStream.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return file.path
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private fun getDataColumn(context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)

        try {
            cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(columnIndex)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    private fun getPath(context: Context, uri: Uri?): String? {
        var cursor: Cursor? = null
        val projection = arrayOf(MediaStore.MediaColumns.DISPLAY_NAME)
        try {
            cursor = context.contentResolver.query(uri!!, projection, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    /*    fun getRecordFolderLocation(context: Context): String {
        //return Constants.primaryStorage + "/" + Configuration.strAppIdentifier + "/Records"
        return Constants.appFolderLocation
    }*/

/*    fun getProfileFolderLocation(): String {
        //return Constants.primaryStorage + "/" + Configuration.strAppIdentifier + "/Images"
        return Constants.appFolderLocation
    }

    fun getHRAFolderLocation(): String {
        //return Constants.primaryStorage + "/" + Configuration.strAppIdentifier + "/HRA"
        return Constants.appFolderLocation
    }*/

    /*    fun makeFolderDirectories(context: Context) {
    try {
        val sd = File(Constants.primaryStorage, Configuration.strAppIdentifier)
        if (!sd.exists()) {
            val directoryCreated = sd.mkdirs()
            Timber.e("directoryCreated--->$directoryCreated {${Configuration.strAppIdentifier}}")
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}*/

    /*    fun makeFolderDirectories(context: Context) {
        try {
            val dir = File(context.getExternalFilesDir(null), Configuration.strAppIdentifier)
            if (!dir.exists()) {
                val directoryCreated = dir.mkdirs()
                Timber.e("directoryCreated--->$directoryCreated , {${dir}} ")
                //Timber.e("directoryCreated--->$directoryCreated {${Configuration.strAppIdentifier}}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }*/

    /*    fun makeFilderDirectories() {
        try {
            val sd = File(Constants.primaryStorage, Configuration.strAppIdentifier)
            val RecordsFolder = File(sd.absolutePath + "/" + "Records")
            val dbFolder = File(sd.absolutePath + "/" + "DB FILES")
            val ProfileImagesFolder = File(sd.absolutePath + "/" + "Profile Images")

            if (!RecordsFolder.exists()) {
                dbFolder.mkdir()
            }
            if (!sd.exists()) {
                sd.mkdirs()
            }
            if (!dbFolder.exists()) {
                dbFolder.mkdir()
            }
            if (!ProfileImagesFolder.exists()) {
                dbFolder.mkdir()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }*/

}
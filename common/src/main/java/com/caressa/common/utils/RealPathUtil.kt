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
import android.util.Log
import com.caressa.common.constants.Constants
import timber.log.Timber
import java.io.*
import org.koin.standalone.KoinComponent
import kotlin.math.min

object RealPathUtil : KoinComponent {

    // Retrieving the external storage state Check if available
    private val isExternalStorageAvailable: Boolean
        get() {
            val state = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == state || Environment.MEDIA_MOUNTED_READ_ONLY == state

        }

    // if the external storage is writable.
    private val isExternalStorageWritable: Boolean
        get() {
            val state = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == state
        }

    fun getRecordFolderLocation(): String {
        //return Constants.primaryStorage + "/" + Configuration.strAppIdentifier + "/Records"
        //return Constants.appFolderLocation
        return ""
    }

    private fun getVersion(): Int {
        val currentApiVersion: Int = try {
            Build.VERSION.SDK_INT
        } catch (e: NumberFormatException) {
            //API 3 will crash if SDK_INT is called
            3
        }

        return currentApiVersion
    }

    @Throws(IOException::class)
    fun copy(src: File, dst: File) : Boolean {
        var save = false
        try {
            FileInputStream(src).use { `in` ->
                FileOutputStream(dst).use { out ->
                    // Transfer bytes from in to out
                    val buf = ByteArray(1024)
                    var len: Int
                    while (`in`.read(buf).also { len = it } > 0) {
                        out.write(buf, 0, len)
                        save = true
                    }
                }
            }
            //save = true
        } catch (e: Exception) {
            save = false
            Log.e("Error Coping file=>", e.message.toString())
        }
        return save
    }

    fun copyAssetFolderToFolder(activity: Context, assetsFolder: String, destinationFolder: File) {
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        try {
            for (fileName in activity.assets.list(assetsFolder)!!) {
                //fileName = assetsFolder+fileName;
                if( fileName == "ApolloStoreListwithPincodes.xlsx" ) {
                    Timber.i("REAL_PATH_UTIL---->"+assetsFolder + fileName)

                    Timber.i("REAL_PATH_UTIL : Trying to open---->" + assetsFolder + (if (assetsFolder.endsWith(File.pathSeparator)) "" else File.pathSeparator) + fileName)
                    // outputStream = BufferedOutputStream(FileOutputStream(File(destinationFolder, fileName)))
                    try {
                        val fileReader = ByteArray(4096)

                        var fileSizeDownloaded: Long = 0

                        inputStream = activity.assets.open("$assetsFolder/$fileName")
                        val file = File(destinationFolder, fileName)
                        outputStream = FileOutputStream(File(destinationFolder, fileName))

                        while (true) {
                            val read = inputStream!!.read(fileReader)
                            if (read == -1) {
                                break
                            }
                            outputStream!!.write(fileReader, 0, read)
                            fileSizeDownloaded += read.toLong()
                            //Timber.i("file download: $fileSizeDownloaded of $fileSize")
                        }
                        outputStream!!.flush()
                        // openDownloadedFile(file)
                    } catch (e: Exception) {
                        Timber.i("Error..."+e.printStackTrace())
                    } finally {
                        inputStream?.close()
                        outputStream?.close()
                    }
                }
            }
        } catch (/*any*/e: Exception) {
            e.printStackTrace()
        }
    }

    /*    fun getImageUri(inContext: Context, inImage: Bitmap?): Uri {
        val bytes = ByteArrayOutputStream()
        inImage!!.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        val outImage = Bitmap.createScaledBitmap(inImage!!, 1000, 1000, true)
        val path = Images.Media.insertImage(inContext.contentResolver, outImage, "img_" + System.currentTimeMillis(), null)
        return Uri.parse(path)
    }*/

    fun decodeFile(filePath: String): Bitmap {
        /*
         * Intentionally commented this code. To get original image size. It may
         * require if image is of very high resolution.
         */
        // Decode image size
        val o = BitmapFactory.Options()
        o.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filePath, o)

        // The new size we want to scale to
        val requiredSize = 1024
        // Find the correct scale value. It should be the power of 2.
        var widthTmp = o.outWidth
        var heightTmp = o.outHeight
        var scale = 1

        while (true) {
            if (widthTmp < requiredSize && heightTmp < requiredSize)
                break
            widthTmp /= 2
            heightTmp /= 2
            scale *= 2
        }

        // Decode with inSampleSize
        val o2 = BitmapFactory.Options()
        o2.inSampleSize = scale

        return BitmapFactory.decodeFile(filePath, o2)
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     */
    fun getPath(context: Context, uri: Uri): String? {
        Timber.e("SCHEME---> ${uri.scheme}")
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                if ("primary".equals(type, ignoreCase = true)) {
                    return Constants.primaryStorage + "/" + split[1]
                }

                if ("home".equals(type, ignoreCase = true)) {
                    return Constants.primaryStorage + "/Documents/" + split[1]
                }

/*                if (uri.toString().startsWith("content://com.android.externalstorage.documents/document/home%3A")) {
                    return Constants.primaryStorage + "/Documents/" + split[1]
                }*/

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
/*                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))
                return getDataColumn(context, contentUri, null, null)*/

                val fileName: String = getFilePath(context, uri)!!
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
            // MediaProvider
            else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null

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

                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
            //GoogleDriveProvider
            else if ( isGoogleDriveUri(uri) ) {
                return getGoogleDriveFilePath(uri, context)
            }
        }
        // MediaStore (and general)
        else if (ContentResolver.SCHEME_CONTENT.equals(uri.scheme!!, ignoreCase = true)) {
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
        else if (ContentResolver.SCHEME_FILE.equals(uri.scheme!!, ignoreCase = true)) {
            return uri.path
        }

        return null
    }

    private fun getFilePath(context: Context, uri: Uri?): String? {
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

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    private fun isGoogleDriveUri(uri: Uri): Boolean {
        return "com.google.android.apps.docs.storage" == uri.authority || "com.google.android.apps.docs.storage.legacy" == uri.authority
    }

    private fun getGoogleDriveFilePath(
        uri: Uri,
        context: Context
    ): String? {
        val returnCursor =
            context.contentResolver.query(uri, null, null, null, null)
        /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
        val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        val size = java.lang.Long.toString(returnCursor.getLong(sizeIndex))
        val file = File(context.cacheDir, name)
        try {
            val inputStream =
                context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            var read = 0
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

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    //Not in use

    fun saveFileToExternalCard(inputPath: String, NewfileName: String, from: String): Boolean {
/*        var folderName = ""
        if (from.equals(Constants.PROFILE, ignoreCase = true)) {
            folderName = getProfileFolderLocation()
        } else {
            folderName = getRecordFolderLocation()
        }*/

        var save = false
        val folderName = ""
        //val folderName = getRecordFolderLocation()

        if (isExternalStorageAvailable && isExternalStorageWritable) {
            val myDirectory = File(folderName)

            if (!myDirectory.exists()) {
                myDirectory.mkdirs()
            }

            val absolutePathDic = myDirectory.absolutePath
            if (inputPath != null && absolutePathDic != null) {
                //save = copyFile(inputPath, absolutePathDic, NewfileName)
                save = writeRecordToDisk(inputPath, absolutePathDic, NewfileName)
            } else {
                save = false
            }

        } else {
            save = false
        }

        println("IsFileSaved : $save")
        return save

    }

    private fun writeRecordToDisk(inputPath: String, outputPath: String, fileName : String): Boolean {
        try {
            val file = File( outputPath ,  fileName )
            Timber.e("downloadDocPath--->$file")

            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null

            try {
                val fileReader = ByteArray(4096)

                inputStream = FileInputStream(File(inputPath))
                outputStream = FileOutputStream(file)

                while (true) {
                    val read = inputStream!!.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream!!.write(fileReader, 0, read)
                }
                outputStream!!.flush()
                return true
            } catch (e: Exception) {
                Timber.i("Error..."+e.printStackTrace())
                return false
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        } catch (e: Exception) {
            Timber.i("Error!!!"+e.printStackTrace())
            return false
        }
    }

    fun saveBitampAsFileToExternalCard(bitmap: Bitmap?, NewfileName: String, from: String, folderID: String): Boolean {
/*        var folderName = ""
        if (from.equals(Constants.PROFILE, ignoreCase = true)) {
            folderName = getProfileFolderLocation()
        } else if (from.equals(Constants.RECORD, ignoreCase = true) && folderID.equals("", ignoreCase = true)) {
            folderName = getRecordFolderLocation() + "/"
        } else {
            folderName = getRecordFolderLocation() + "/" + folderID
        }*/

        var save = false
        val folderName = ""
        //val folderName = getRecordFolderLocation()

        if (isExternalStorageAvailable && isExternalStorageWritable) {
            val myDirectory = File(folderName)

            if (!myDirectory.exists()) {
                myDirectory.mkdirs()
            }

            val absolutePathDic = myDirectory.absolutePath
            println("Check Absolute PAth : $absolutePathDic")


            if (bitmap != null && absolutePathDic != null) {
                val stream = ByteArrayOutputStream()
                //		  				 bitmap.compress(Bitmap.CompressFormat.JPEG, 75, stream);
                // Compress the bitmap with JPEG format and quality 50%
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                //						Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(stream.toByteArray()));
                var byteArray: ByteArray? = stream.toByteArray() // convert camera photo to byte array
                val compressedBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)
                // save it in your external storage.
                val fo = FileOutputStream(File(absolutePathDic , NewfileName))
                fo.write(byteArray)
                fo.flush()
                fo.close()
                byteArray = null
                compressedBitmap.recycle()
                save = true
            } else {
                save = false
            }

        } else {
            save = false
        }

        println("save-----> $save")
        return save

    }

    fun saveCameraBitmapAsFileToRecordsFolder(bitmap: Bitmap?,file:File,folderName:String): Boolean {
        var save = false

        if (isExternalStorageAvailable && isExternalStorageWritable) {
            val myDirectory = File(folderName)

            if (!myDirectory.exists()) {
                myDirectory.mkdirs()
            }

            if (bitmap != null && myDirectory.absolutePath != null) {
                val stream = ByteArrayOutputStream()
                // Compress the bitmap with PNG format and quality 75%
                bitmap.compress(Bitmap.CompressFormat.PNG, 75, stream)
                // Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(stream.toByteArray()));
                var byteArray: ByteArray? = stream.toByteArray()
                // convert camera photo to byte array
                val compressedBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)
                // save it in your external storage.
                val fo = FileOutputStream(file)
                fo.write(byteArray)
                fo.flush()
                fo.close()
                byteArray = null
                compressedBitmap.recycle()
                save = true
            } else {
                save = false
            }

        } else {
            save = false
        }

        println("save-----> $save")
        return save

    }

    /*        var folderName = ""
        if (from.equals(Constants.PROFILE, ignoreCase = true)) {
            folderName = getProfileFolderLocation()
        } else if( from.equals(Constants.RECORD, ignoreCase = true) && folderID.equals("", ignoreCase = true) ) {
            folderName = getRecordFolderLocation()
        } else {
            folderName = getRecordFolderLocation() + "/" + folderID
        }*/

}
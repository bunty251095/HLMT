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
import android.util.Log
import com.caressa.common.constants.Configuration
import com.caressa.common.constants.Constants
import timber.log.Timber
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

// Get Path From android System
object RealPathUtil {
    //private val context: Context

    // Retrieving the external storage state
    // Check if available
    var PROFILE_FOLDER = ""
    var RECORD_FOLDER = ""
    val isExternalStorageAvailable: Boolean
        get() {
            val state = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == state || Environment.MEDIA_MOUNTED_READ_ONLY == state

        }

    /**
     * @return True if the external storage is writable.
     * False otherwise.
     */
    /* Checks if external storage is available for read and write */
    val isExternalStorageWritable: Boolean
        get() {
            val state = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == state
        }

/*    val uniqueIdLong: String
        get() {
            val r = Random()
            val systemTime = System.currentTimeMillis()
            val unixtime = (systemTime + r.nextDouble() * 60.0 * 60.0 * 24.0 * 365.0).toLong()

            return unixtime.toString()
        }*/

    init {
    }

    fun getProfileFolderLocation(): String {
        // Logger.i("Profile path", "The image path is " + Environment.getExternalStorageDirectory().path + "/" + ClientConfiguration.strAppIdentifier + "/Images/", null)
        return Environment.getExternalStorageDirectory().path + "/" + Configuration.strAppIdentifier + "/Images"
    }

    fun setProfileFolderLocation(value: String) {
        PROFILE_FOLDER = value
    }

    fun getRecordFolderLocation(): String {
        //return RECORD_FOLDER + "/"
        return Environment.getExternalStorageDirectory().path + "/" + Configuration.strAppIdentifier + "/Records"
    }

    fun setRecordFolderLocation(value: String) {
        RECORD_FOLDER = value
    }

    fun creatingLocalDirctories(): Boolean {
        var done = false
        if (isExternalStorageAvailable && isExternalStorageWritable) {
            val myDirectory =
                File(Environment.getExternalStorageDirectory(), Configuration.strAppIdentifier)

            if (!myDirectory.exists()) {
                myDirectory.mkdirs()
                var absolutePathDic =
                    myDirectory.absolutePath + "/" + Constants.APPLICATION_SUBFOLDER_PROFILE_IMAGES
                println("Cehck Path : $absolutePathDic")


                val myDirectorySubFolder1 = File(absolutePathDic)
                if (!myDirectorySubFolder1.exists()) {
                    done = true
                    myDirectorySubFolder1.mkdirs()
                    setProfileFolderLocation(absolutePathDic)
                } else {
                    done = false
                }


                absolutePathDic =
                    myDirectory.absolutePath + "/" + Constants.APPLICATION_SUBFOLDER_RECORDS

                println("Cehck Path subfolder: $absolutePathDic")
                val myDirectorySubFolder2 = File(absolutePathDic)
                if (!myDirectorySubFolder2.exists()) {
                    myDirectorySubFolder2.mkdirs()
                    done = true
                    setRecordFolderLocation(absolutePathDic)
                } else {
                    done = false
                }
            } else {
                var absolutePathDic =
                    myDirectory.absolutePath + "/" + Constants.APPLICATION_SUBFOLDER_PROFILE_IMAGES
                setProfileFolderLocation(absolutePathDic)

                absolutePathDic =
                    myDirectory.absolutePath + "/" + Constants.APPLICATION_SUBFOLDER_RECORDS

                setRecordFolderLocation(absolutePathDic)

                done = false
            }
        }

        return done
    }

    fun generateUniqueFileName(className: String, FilePath: String): String {
        val Extension = getFileExt(FilePath)
        var filename = ""
        if (Extension != null) {

            val sdf = SimpleDateFormat("ddMMyy-hhmmss", Locale.ENGLISH)
            val random = Random()
            filename = className + "-" + String.format(
                "%s.%s",
                sdf.format(Date()),
                random.nextInt(9)
            ) + "." + Extension
        }

        return filename
    }

    fun saveOtherAsFileToExternalCard(
        arraybyte: ByteArray?,
        NewfileName: String,
        from: String,
        folderID: String
    ): Boolean {
        var save = false
        var folderName = ""
        if (from.equals(Constants.PROFILE, ignoreCase = true)) {
            folderName = getProfileFolderLocation()
        } else if (from.equals(Constants.RECORD, ignoreCase = true) && folderID.equals(
                "",
                ignoreCase = true
            )
        ) {
            folderName = getRecordFolderLocation()
        } else {
            folderName = getRecordFolderLocation() + "/" + folderID
        }

        if (isExternalStorageAvailable && isExternalStorageWritable) {

            val myDirectory = File(folderName)
            if (!myDirectory.exists()) {
                myDirectory.mkdirs()
            }
            val absolutePathDic = myDirectory.absolutePath + "/" + NewfileName
            println("Chekc_Absolute_PAth----->$absolutePathDic")
            if (arraybyte != null) {
                val out = FileOutputStream(absolutePathDic)
                out.write(arraybyte)
                out.close()
                save = true
            } else {
                save = false
            }

        } else {
            save = false
        }

        return save
    }

    fun saveFileToExternalCard(inputPath: String, NewfileName: String, from: String): Boolean {
        var save = false
        var folderName = ""
        if (from.equals(Constants.PROFILE, ignoreCase = true)) {
            folderName = getProfileFolderLocation()
        } else {
            folderName = getRecordFolderLocation()
        }
        if (isExternalStorageAvailable && isExternalStorageWritable) {
            val myDirectory = File(folderName)

            if (!myDirectory.exists()) {
                myDirectory.mkdirs()
            }

            val absolutePathDic = myDirectory.absolutePath
            println("Check_Absolute_Path_1----->$folderName")
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

    fun createDirectory(folderName: String) {
        if (isExternalStorageAvailable && isExternalStorageWritable) {
            val myDirectory = File(folderName)
            if (!myDirectory.exists()) {
                myDirectory.mkdirs()
            }
        }
    }

    @Throws(IOException::class)
    fun copy(src: File, dst: File): Boolean {
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

    fun saveBitampAsFileToExternalCard(
        bitmap: Bitmap?,
        NewfileName: String,
        from: String,
        folderID: String
    ): Boolean {
        var save = false
        var folderName = ""
        if (from.equals(Constants.PROFILE, ignoreCase = true)) {
            folderName = getProfileFolderLocation()
        } else if (from.equals(Constants.RECORD, ignoreCase = true) && folderID.equals(
                "",
                ignoreCase = true
            )
        ) {
            folderName = getRecordFolderLocation() + "/"
        } else {
            folderName = getRecordFolderLocation() + "/" + folderID
        }

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
                var byteArray: ByteArray? =
                    stream.toByteArray() // convert camera photo to byte array
                val compressedBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)
                // save it in your external storage.
                val fo = FileOutputStream(File(absolutePathDic, NewfileName))
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

    fun calculateFileSize(filepath: String): Float {
        Log.d("Helper", "calculateFileSize-->file path--> $filepath")
        //String filepathstr=filepath.toString();
        val file = File(filepath)
        //
        //        // Get length of file in bytes
        //        long fileSizeInBytes = file.length();
        //        // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
        //        float fileSizeInKB = fileSizeInBytes / 1024;
        //        // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
        //        float fileSizeInMB = fileSizeInKB / 1024;
        //
        //        String calString = Float.toString(fileSizeInMB);
        var calString = 0f
        if (file.exists()) {
            val bytes = file.length()
            val kilobytes = bytes / 1024
            val megabytes = (kilobytes / 1024).toFloat()
            calString = megabytes

        }
        return calString
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

/*     fun copyFile(inputPath: String, outputPath: String, FileName: String): Boolean {
        var save = false
        var `in`: InputStream? = null
        var out: OutputStream? = null
        try {
            //create output directory if it doesn't exist
            val dir = File(outputPath)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            //		  					in = new FileInputStream(inputPath);
            `in` = FileInputStream(File(inputPath))
            out = FileOutputStream(outputPath + FileName)
            val buffer = ByteArray(1024)
            val read: Int = `in`.read(buffer)
            while ( read  != -1) {
                out.write(buffer, 0, read)
            }
            `in`.close()
            `in` = null
            // write the output file (You have now copied the file)
            out.flush()
            out.close()
            out = null
            save = true
        } catch (fnfe1: FileNotFoundException) {
            save = false
            Log.e("tag1", fnfe1.message)
        } catch (e: Exception) {
            save = false
            Log.e("tag2", e.message)
        }
        return save

    }*/


    fun writeRecordToDisk(inputPath: String, outputPath: String, fileName: String): Boolean {
        try {
            val path = Environment.getExternalStorageDirectory()
            val file = File(outputPath, fileName)
            Timber.i("downloadDocPath: ----->" + file)

            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null

            try {
                val fileReader = ByteArray(4096)
                var fileSizeDownloaded: Long = 0

                inputStream = FileInputStream(File(inputPath))
                outputStream = FileOutputStream(file)

                while (true) {
                    val read = inputStream!!.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream!!.write(fileReader, 0, read)
                    fileSizeDownloaded += read.toLong()
                    //  Timber.i("file download: $fileSizeDownloaded of $fileSize")
                }
                outputStream!!.flush()
                return true
            } catch (e: Exception) {
                Timber.i("Error..." + e.printStackTrace())
                return false
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        } catch (e: Exception) {
            Timber.i("Error!!!" + e.printStackTrace())
            return false
        }
    }

    fun getFileExt(fileName: String): String {
        if (!Utilities.isNullOrEmpty(fileName)) {
            val index = fileName.lastIndexOf(".")
            if (index == -1) {
                return ""
            }
            val strFileEXT = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length)
            return strFileEXT.trim { it <= ' ' }

        } else {
            return ""
        }
    }

    fun removeFileExt(fileName: String): String {
        val index = fileName.lastIndexOf(".")
        return if (index == -1) {
            ""
        } else fileName.substring(0, fileName.lastIndexOf("."))
    }

    fun copyAssetFolderToFolder(activity: Context, assetsFolder: String, destinationFolder: File) {
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        try {
            for (fileName in activity.assets.list(assetsFolder)!!) {
                //fileName = assetsFolder+fileName;
                if (fileName == "ApolloStoreListwithPincodes.xlsx") {
                    Timber.i("REAL_PATH_UTIL---->" + assetsFolder + fileName)

                    Timber.i(
                        "REAL_PATH_UTIL : Trying to open---->"
                                + assetsFolder + (if (assetsFolder.endsWith(File.pathSeparator)) "" else File.pathSeparator) + fileName
                    )
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
                        Timber.i("Error..." + e.printStackTrace())
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

    fun getImageUri(inContext: Context, inImage: Bitmap?): Uri {
        //ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        //inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        val OutImage = Bitmap.createScaledBitmap(inImage!!, 1000, 1000, true)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            OutImage,
            "img_" + System.currentTimeMillis(),
            null
        )
        return Uri.parse(path)
    }

    fun convertBase64ToBitmap(base64Input: String?): Bitmap {
        var base64Input = base64Input
        var bmp: Bitmap? = null
        if (base64Input != null && !base64Input.equals(
                "",
                ignoreCase = true
            ) && !base64Input.equals("null", ignoreCase = true)
        ) {
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

/*    fun  openDownloadedFile( file: File ) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(
            Uri.fromFile(file), "application/vnd.ms-excel")
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        // intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        try {
            this.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            //AlertDialogHelper( this , "No Application Available to View Excel." ,"You need Excel viewer to open this file.")
        }
    }*/

/*    fun copyAssetFolderToFolder(activity: Context, assetsFolder: String, destinationFolder: File) {
        var stream: InputStream? = null
        var output: OutputStream? = null
        try {
            for (fileName in activity.assets.list(assetsFolder)!!) {
                //fileName = assetsFolder+fileName;
                Timber.i("REAL_PATH_UTIL---->"+assetsFolder + fileName)

                Timber.i("REAL_PATH_UTIL : Trying to open---->"
                        + assetsFolder + (if (assetsFolder.endsWith(File.pathSeparator)) "" else File.pathSeparator) + fileName)
                stream = activity.assets.open("$assetsFolder/$fileName")
                output = BufferedOutputStream(FileOutputStream(File(destinationFolder, fileName)))

                val data = ByteArray(1024)
                val count: Int = stream!!.read(data)

                while (count  != -1) {
                    output.write(data, 0, count)
                }

                output.flush()
                output.close()
                stream!!.close()

                stream = null
                output = null
            }
        } catch (*//*any*//*e: Exception) {
            e.printStackTrace()
        }
    }*/

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     */
    fun getPath(context: Context, uri: Uri): String? {
        Timber.e("uri--->$uri")
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
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }

                if ("home".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory()
                        .toString() + "/Documents/" + split[1]
                }

/*                if (uri.toString().startsWith("content://com.android.externalstorage.documents/document/home%3A")) {
                    return Environment.getExternalStorageDirectory().toString() + "/Documents/" + split[1]
                }*/

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
/*                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))
                return getDataColumn(context, contentUri, null, null)*/

                val fileName: String = getFilePath(context, uri)!!
                if (fileName != null) {
                    return Environment.getExternalStorageDirectory()
                        .toString() + "/Download/" + fileName
                }
                var id = DocumentsContract.getDocumentId(uri)
                if (id.startsWith("raw:")) {
                    id = id.replaceFirst("raw:".toRegex(), "")
                    val file = File(id)
                    if (file.exists()) return id
                }
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    java.lang.Long.valueOf(id)
                )
                return getDataColumn(context, contentUri, null, null)

            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null

                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }

                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
            //GoogleDriveProvider
            else if (isGoogleDriveUri(uri)) {
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

    fun getFilePath(context: Context, uri: Uri?): String? {
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
            val bufferSize = Math.min(bytesAvailable, maxBufferSize)
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
    fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)

        try {
            cursor =
                context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    fun isFileExist(path: String): Boolean {
        val isHasfile = false
        val myDirectory = File(path)

        return if (myDirectory.exists()) {
            true
        } else isHasfile
    }

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
        val REQUIRED_SIZE = 1024
        // Find the correct scale value. It should be the power of 2.
        var width_tmp = o.outWidth
        var height_tmp = o.outHeight
        var scale = 1

        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break
            width_tmp /= 2
            height_tmp /= 2
            scale *= 2
        }

        // Decode with inSampleSize
        val o2 = BitmapFactory.Options()
        o2.inSampleSize = scale

        /*  bitmap = BitmapFactory.decodeFile(filePath);

		        imgView.setImageBitmap(bitmap);*/
        return BitmapFactory.decodeFile(filePath, o2)
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    fun getAlbumStorageDir(context: Context, albumName: String): File {
        // Get the directory for the app's private pictures directory.
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), albumName)
        if (!file.mkdirs()) {
            Log.e("SHP : ", "Directory not created")
        }
        return file
    }

    private fun getversion(): Int {
        var currentApiVersion: Int
        try {
            currentApiVersion = Build.VERSION.SDK_INT
        } catch (e: NumberFormatException) {
            //API 3 will crash if SDK_INT is called
            currentApiVersion = 3
        }

        return currentApiVersion
    }

    fun getUniqueIdLong(): String {
        val r = Random()
        val systemTime = System.currentTimeMillis()
        val unixtime = (systemTime + r.nextDouble() * 60.0 * 60.0 * 24.0 * 365.0).toLong()

        return unixtime.toString()
    }

    // delete file from particular location.
    fun deleteFileFromLocalSystem(Path: String): Boolean {
        var isDeleted = false
        try {
            val isExist = isFileExist(Path)
            if (isExist) {
                val file = File(Path)
                isDeleted = file.delete()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return isDeleted
    }

    fun makeFilderDirectories() {
        try {
            val sd = File(Environment.getExternalStorageDirectory(), Configuration.strAppIdentifier)
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
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun exportDatabase(databaseName: String, context: Context) {
        try {
            val sd = File(Environment.getExternalStorageDirectory(), Configuration.strAppIdentifier)
            val data = Environment.getDataDirectory()
            val dbFolder = File(sd.absolutePath + "/" + "DB FILES")
            val RecordsFolder = File(sd.absolutePath + "/" + "Records")
            val ProfileImagesFolder = File(sd.absolutePath + "/" + "Profile Images")

            if (!sd.exists()) {
                sd.mkdirs()
            }
            if (!dbFolder.exists()) {
                dbFolder.mkdir()
            }
            if (!RecordsFolder.exists()) {
                dbFolder.mkdir()
            }
            if (!ProfileImagesFolder.exists()) {
                dbFolder.mkdir()
            }
            if (dbFolder.listFiles() != null) {
                for (child in dbFolder.listFiles()) {
                    if (child.name.contains(databaseName)) {
                        child.delete()
                    }
                }
            }
            if (sd.canWrite()) {
                val currentDBPath = "//data//" + context.getPackageName()
                    .toString() + "//databases//" + databaseName + ""
                val backupDBPath = "$databaseName " + SimpleDateFormat(
                    DateHelper.DATETIMEFORMAT,
                    Locale.ENGLISH
                ).format(Calendar.getInstance().time) + "_temp.db"
                val currentDB = File(data, currentDBPath)
                val backupDB = File(dbFolder, backupDBPath)
                val src = FileInputStream(currentDB).channel
                val dst = FileOutputStream(backupDB).channel
                dst.transferFrom(src, 0, src.size())
                src.close()
                dst.close()
                Log.d("DB_HELPER", "DB_HELPER Copied DB file to--> $backupDB")
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


}
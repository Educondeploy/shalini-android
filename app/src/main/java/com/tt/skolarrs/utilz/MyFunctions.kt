package com.tt.skolarrs.utilz

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfRenderer
import android.media.MediaRecorder
import android.net.ConnectivityManager
import android.net.Uri
import android.os.*
import android.os.StrictMode.ThreadPolicy
import android.preference.PreferenceManager
import android.provider.OpenableColumns
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.method.PasswordTransformationMethod
import android.util.Base64
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.tt.skolarrs.R
import de.hdodenhof.circleimageview.BuildConfig
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.*
import java.math.BigInteger
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class MyFunctions {
    companion object {
        var appName = "Login"
        var dialog: ProgressDialog? = null
        var alertDialog: AlertDialog? = null
        var pdfRenderer: PdfRenderer? = null
        var currentPage: PdfRenderer.Page? = null
        var parcelFileDescriptor: ParcelFileDescriptor? = null
        var filename = "proof_Images"
        private val BUFFER_SIZE = 1024 * 2
        private var recorder: MediaRecorder? = null


        fun isEmpty(editTextName: EditText, context: Context): Boolean {
            if (editTextName.text.toString().trim { it <= ' ' }.isEmpty()) {
                editTextName.error = context.getText(R.string.isEmpty_errorMsg)
                editTextName.requestFocus()
                return true
            }
            return false
        }

        fun isEmptyTextView(
            editTextName: TextView,
            context: Context?,
            msg: String?,
            inflater: LayoutInflater?
        ): Boolean {
            if (editTextName.text.toString().trim { it <= ' ' }.isEmpty()) {
                Toast.makeText(
                    context,
                    msg,
                    Toast.LENGTH_SHORT
                ).show()
                /* editTextName.setError(context.getText(R.string.isEmpty_errorMsg));
                editTextName.requestFocus();*/return true
            }
            return false
        }

        fun formatSeconds(seconds: Int): String {
            val hours = seconds / 3600
            val minutes = (seconds % 3600) / 60
            val remainingSeconds = seconds % 60
            return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
        }


        fun emailPattern(editText: EditText, context: Context): Boolean {
            if (!Patterns.EMAIL_ADDRESS.matcher(editText.text.toString().trim { it <= ' ' })
                    .matches()
            ) {
                editText.error = context.getText(R.string.enter_the_valid_email)
                editText.requestFocus()
                return true
            }
            return false
        }


        fun spinnerSelection(
            context: Context?,
            spinner: Spinner,
            errorMsg: CharSequence?
        ): Boolean {
            if (spinner.selectedItemPosition == 0) {
                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                return true
            }
            return false
        }

        fun passwordMatch(editText: EditText, editText1: EditText, context: Context): Boolean {
            if (editText.text.toString().trim { it <= ' ' } != editText1.text.toString()
                    .trim { it <= ' ' }) {
                editText1.error = context.getText(R.string.password_does_not_match)
                editText1.requestFocus()
                return true
            }
            return false
        }

        fun passwordLength(editText: EditText, context: Context): Boolean {
            if (editText.text.toString().trim { it <= ' ' }.length < 8) {
                editText.error = context.getString(R.string.password_must_contains)
                editText.requestFocus()
                return true
            }
            return false
        }

        fun mobileLength(editText: EditText, context: Context): Boolean {
            if (editText.text.toString().trim { it <= ' ' }.length < 10) {
                editText.error = context.getString(R.string.enter_digit_mobile_number)
                editText.requestFocus()
                return true
            }
            return false
        }

        fun mobileNumberZeroValidation(editText: EditText, context: Context): Boolean {
            try {
                val i = Integer.valueOf(editText.text.toString())
                return if (i == 0) {
                    editText.error = context.getString(R.string.enter_a_valid_mobile_number)
                    editText.requestFocus()
                    true
                } else {
                    false
                }
            } catch (ex: NumberFormatException) {
            }
            return false
        }


        fun getSharedPreference(
            applicationContext: Context,
            key: String?,
            default_value: String?
        ): String? {
            val shared = applicationContext.getSharedPreferences(appName, Context.MODE_PRIVATE)
            return shared.getString(key, default_value)
        }


        fun getSharedPreference(
            applicationContext: Context, key: String?,
            default_value: Boolean
        ): Boolean {
            val shared = applicationContext.getSharedPreferences(appName, Context.MODE_PRIVATE)
            return shared.getBoolean(key, default_value)
        }


        fun setSharedPreference(applicationContext: Context, key: String?, value: String?) {
            val shared = applicationContext.getSharedPreferences(appName, Context.MODE_PRIVATE)
            val edit = shared.edit()
            edit.putString(key, value)
            edit.apply()
        }

        fun setSharedPreference(applicationContext: Context, key: String?, value: Boolean) {
            val shared = applicationContext.getSharedPreferences(appName, Context.MODE_PRIVATE)
            //SharedPreferences.Editor edit = shared.edit();
            val edit = shared.edit()
            edit.putBoolean(key, value)
            edit.apply()
        }

        fun deleteSharedPreference(context: Context) {
            val sharedPreferences = context.getSharedPreferences(appName, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.commit()
            context.deleteFile(
                context.getSharedPreferences(appName, Context.MODE_PRIVATE)
                    .getString("com.example.app", "")
            )
        }

        /*   public static void deleteSharedPreference(Context context) {
            // Get the SharedPreferences file by name
            SharedPreferences prefs = context.getSharedPreferences(appName, Context.MODE_PRIVATE);

    // Delete the file
            prefs.edit().clear().commit();

    // Confirm that the file has been deleted
            Log.d("TAG", "deleteSharedPreference:0 " + context.getApplicationInfo().dataDir + "/shared_prefs/" + appName + ".xml");

    //
            boolean fileExists = new File(context.getApplicationInfo().dataDir + "/shared_prefs/" + appName + ".xml").exists();
            if (!fileExists) {
                Log.d("TAG", "deleteSharedPreference: 1" + context.getApplicationInfo().dataDir + "/shared_prefs/" + appName + ".xml");
                // File has been deleted
            }
        }*/

        /*    public static void deleteSharedPreference(Context context){
            SharedPreferences sharedPreferences = context.getSharedPreferences(appName, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
        }*/

        /*   public static void deleteSharedPreference(Context context) {
            // Get the SharedPreferences file by name
            SharedPreferences prefs = context.getSharedPreferences(appName, Context.MODE_PRIVATE);

    // Delete the file
            prefs.edit().clear().commit();

    // Confirm that the file has been deleted
            Log.d("TAG", "deleteSharedPreference:0 " + context.getApplicationInfo().dataDir + "/shared_prefs/" + appName + ".xml");

    //
            boolean fileExists = new File(context.getApplicationInfo().dataDir + "/shared_prefs/" + appName + ".xml").exists();
            if (!fileExists) {
                Log.d("TAG", "deleteSharedPreference: 1" + context.getApplicationInfo().dataDir + "/shared_prefs/" + appName + ".xml");
                // File has been deleted
            }
        }*/
        /*    public static void deleteSharedPreference(Context context){
            SharedPreferences sharedPreferences = context.getSharedPreferences(appName, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
        }*/

        fun progressDialogShow(context: Context) {
            alertDialog = AlertDialog.Builder(context).create()
            alertDialog?.setTitle(context.resources.getString(R.string.loading))
            alertDialog?.setMessage("Please wait...")
            alertDialog?.setCancelable(false)
            alertDialog?.setCanceledOnTouchOutside(false)

            val progressBar = ProgressBar(context)
            alertDialog?.setView(progressBar)

            alertDialog?.show()

            }

    /*    fun progressDialogShow(applicationContext: Context) {
            dialog = ProgressDialog(applicationContext)
            Log.d("TAG", "progressDialogShow: " + dialog!!.progress)
            dialog!!.setTitle(applicationContext.resources.getString(R.string.loading))
            dialog!!.setCancelable(false)
            dialog!!.setCanceledOnTouchOutside(false)
            dialog!!.window!!.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            dialog!!.show()
        }*/

        /*fun progressDialogDismiss() {
            if (dialog!!.isShowing) dialog!!.dismiss()
        }*/

        fun progressDialogDismiss() {
            if (alertDialog!!.isShowing) alertDialog!!.dismiss()
        }



        fun showPassword(editText: EditText, textView: TextView) {
            if (textView.text.toString() == Constant.SHOW) {
                editText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                textView.setText(Constant.HIDE)
            } else {
                editText.transformationMethod = PasswordTransformationMethod.getInstance()
                textView.setText(Constant.SHOW)
            }
        }

        @Throws(ParseException::class)
        fun formatDate(
            date: String?,
            initDateFormat: String?,
            endDateFormat: String?
        ): String? {
            val initDate =
                SimpleDateFormat(initDateFormat).parse(date) as Date
            val formatter = SimpleDateFormat(endDateFormat)
            return formatter.format(initDate)
        }

        @Throws(ParseException::class)
        fun dateFormat(date: String): String? {
            val outputFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy")
            val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
            val dateTime = inputFormat.parse(date) as Date
            return outputFormat.format(dateTime)
        }

        fun getMd5(input: String): String? {
            return try {
                val md = MessageDigest.getInstance("MD5")
                val messageDigest = md.digest(input.toByteArray())
                val no = BigInteger(1, messageDigest)
                var hashtext = no.toString(16)
                while (hashtext.length < 32) {
                    hashtext = "0$hashtext"
                }
                hashtext
            } catch (e: NoSuchAlgorithmException) {
                throw RuntimeException(e)
            }
        }

        fun printHashKey(pContext: Context) {
            try {
                val info = pContext.packageManager.getPackageInfo(
                    pContext.packageName,
                    PackageManager.GET_SIGNATURES
                )
                for (signature in info.signatures) {
                    val md = MessageDigest.getInstance("SHA")
                    md.update(signature.toByteArray())
                    val hashKey = String(Base64.encode(md.digest(), 0))
                    Log.i("TAG", "printHashKey() Hash Key: $hashKey")
                }
            } catch (e: NoSuchAlgorithmException) {
                Log.e("TAG", "printHashKey()", e)
            } catch (e: Exception) {
                Log.e("TAG", "printHashKey()", e)
            }
        }

        fun convertStringArrayToString(strArr: Array<String?>, delimiter: String?): String? {
            val sb = StringBuilder()
            for (str in strArr) sb.append(str).append(delimiter)
            return sb.substring(0, sb.length - 1)
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Throws(IOException::class)
        fun openRenderer(context: Context, uri: Uri, path: String) {
            // In this sample, we read a PDF from the assets directory.
            Log.d("TAG", "openRenderer: $uri")
            val imageFolder = File(context.cacheDir, "proofImages")
            imageFolder.mkdirs()
            val file = File(imageFolder, "proof_images$path.pdf")
            if (!file.exists()) {
                // Since PdfRenderer cannot handle the compressed asset file directly, we copy it into
                // the cache directory.
                Log.d("TAG", "openRenderer:context $file")
                val asset = context.contentResolver.openInputStream(uri)
                val output = FileOutputStream(file)
                val buffer = ByteArray(1024)
                var size: Int
                while (asset!!.read(buffer).also { size = it } != -1) {
                    Log.d("TAG", "output: while$file")
                    output.write(buffer, 0, size)
                }
                Log.d("TAG", "output: close $file")
                output.flush()
                asset.close()
                output.close()
                Log.d("TAG", "output: flush $file")
            }
            Log.d("TAG", "output: $file")
            parcelFileDescriptor =
                ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            Log.d("TAG", "asset: $file")
            // This is the PdfRenderer we use to render the PDF.
            if (parcelFileDescriptor != null) {
                Log.d("TAG", "parcelFileDescriptor: ")
                pdfRenderer = PdfRenderer(parcelFileDescriptor!!)
            }
        }


        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Throws(IOException::class)
        fun closeRenderer() {
            if (null != currentPage) {
                currentPage!!.close()
            }
            pdfRenderer!!.close()
            parcelFileDescriptor!!.close()
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        fun updateUi() {
            val index = currentPage!!.index
            val pageCount = pdfRenderer!!.pageCount
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        fun getPageCount(): Int {
            return pdfRenderer!!.pageCount
        }

        object LocaleHelper {
            private const val SELECTED_LANGUAGE = "Locale.Helper.Selected.Language"

            // the method is used to set the language at runtime
            fun setLocale(context: Context, language: String?): Context {
                persist(context, language)

                // updating the language for devices above android nougat
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    updateResources(context, language)
                } else updateResourcesLegacy(context, language)
                // for devices having lower version of android os
            }

            fun persist(context: Context?, language: String?) {
                val preferences = PreferenceManager.getDefaultSharedPreferences(context)
                val editor = preferences.edit()
                editor.putString(SELECTED_LANGUAGE, language)
                editor.apply()
            }

            // the method is used update the language of application by creating
            // object of inbuilt Locale class and passing language argument to it
            @TargetApi(Build.VERSION_CODES.N)
            fun updateResources(context: Context, language: String?): Context {
                val locale = Locale(language)
                Locale.setDefault(locale)
                val configuration = context.resources.configuration
                configuration.setLocale(locale)
                configuration.setLayoutDirection(locale)
                return context.createConfigurationContext(configuration)
            }

            private fun updateResourcesLegacy(context: Context, language: String?): Context {
                val locale = Locale(language)
                Locale.setDefault(locale)
                val resources = context.resources
                val configuration = resources.configuration
                configuration.locale = locale
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    configuration.setLayoutDirection(locale)
                }
                resources.updateConfiguration(configuration, resources.displayMetrics)
                return context
            }
        }

        fun isConnected(context: Context): Boolean {
            var connected = false
            try {
                val cm =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val nInfo = cm.activeNetworkInfo
                connected = nInfo != null && nInfo.isAvailable && nInfo.isConnected
                return connected
            } catch (e: Exception) {
                Log.e("Connectivity Exception", e.message!!)
            }
            return connected
        }

        fun checkNetworkConnection(context: Context) {
            if (isConnected(context)) {
                //   Toast.makeText(getApplicationContext(), "Internet Connected", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(
                    context,
                    context.resources.getText(R.string.Please_turn_on_your_internet_connection),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        fun getBitmapFromURL(src: String): Bitmap? {
            Log.d("TAG", "getBitmapFromURL: $src")
            return try {
                val policy = ThreadPolicy.Builder().permitAll().build()
                StrictMode.setThreadPolicy(policy)
                val url = URL(src)
                val connection = url
                    .openConnection() as HttpURLConnection
                Log.d("TAG", "getBitmapFromURL:3 $url")
                connection.doInput = true
                connection.connect()
                val input = connection.inputStream
                val myBitmap = BitmapFactory.decodeStream(input)
                input.close()
                Log.d("TAG", "getBitmapFromURL: 2$myBitmap")
                myBitmap
            } catch (e: IOException) {
                Log.d("TAG", "getBitmapFromURL:1 " + e.message)
                e.printStackTrace()
                null
            }
        }

        /*public static Uri getImageUri(Context inContext, Bitmap inImage, String path1) {
            try {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                Log.d("TAG", "getImageUri: " +inContext.getFilesDir().getPath());

                //File.createTempFile()

               //String path = inContext.getFilesDir().getPath() +  "/GramSootra/StockImages" + path1 ;
                String path = inContext.getExternalCacheDir() +  "/GramSootra/StockImages" + path1+".jpg" ;
             // String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "/.Gram/Title" + path1, null);
                return Uri.parse(path);
            } catch (Exception e) {
                Log.d("TAG", "getImageUri: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        }*/

        /*public static Uri getImageUri(Context inContext, Bitmap inImage, String path1) {
            try {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                Log.d("TAG", "getImageUri: " +inContext.getFilesDir().getPath());

                //File.createTempFile()

               //String path = inContext.getFilesDir().getPath() +  "/GramSootra/StockImages" + path1 ;
                String path = inContext.getExternalCacheDir() +  "/GramSootra/StockImages" + path1+".jpg" ;
             // String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "/.Gram/Title" + path1, null);
                return Uri.parse(path);
            } catch (Exception e) {
                Log.d("TAG", "getImageUri: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        }*/
        fun storeImage(context: Context, bitmap: Bitmap, path1: String): Uri? {
            val imagefolder = File(context.cacheDir, "images")
            var uri: Uri? = null
            try {
                imagefolder.mkdirs()
                val file = File(imagefolder, "stock_images$path1")
                val outputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream)
                outputStream.flush()
                outputStream.close()
                uri = FileProvider.getUriForFile(
                    Objects.requireNonNull(context.applicationContext),
                    BuildConfig.APPLICATION_ID + ".provider", file
                )
            } catch (e: Exception) {
                Log.d("TAG", "storeImage: " + e.message)
            }
            return uri
        }


        fun deleteCache(context: Context) {
            try {
                val dir = context.cacheDir
                dir?.let { deleteDir(it) } ?: Log.d("TAG", "deleteCache: ")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun deleteDir(file1: File?): Boolean {
            Log.d("TAG", "deleteDir: $file1")
            return if (file1 != null && file1.isDirectory) {
                val children = file1.list()
                Log.d("TAG", "deleteDir: " + children.size)
                for (i in children.indices) {
                    Log.d("TAG", "deleteDir: $i")
                    val file = File(file1, children[i])
                    file.delete()
                    val success = deleteDir(File(file1, children[i]))
                    if (!success) {
                        Log.d("TAG", "deleteDir: " + "if")
                        //return false;
                    }
                }
                file1.delete()
            } else if (file1 != null && file1.isFile) {
                file1.delete()
            } else {
                false
            }
        }

        fun downloadFile(fileUrl: String?, context: Context, path: String): String? {
            val MEGABYTE = 1024 * 1024
            var filePath: String? = null
            val gfgPolicy = ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(gfgPolicy)
            try {
                val imagefolder = File(context.cacheDir, "proofImages")
                imagefolder.mkdirs()
                val directory = File(imagefolder, "proof_images$path.pdf")
                Log.d("TAG", "downloadFile: $directory")
                if (fileUrl != null) {
                    val url = URL(fileUrl)
                    val urlConnection = url.openConnection() as HttpURLConnection
                    //urlConnection.setRequestMethod("GET");
                    //urlConnection.setDoOutput(true);
                    urlConnection.connect()
                    val inputStream = urlConnection.inputStream
                    val fileOutputStream = FileOutputStream(directory)
                    val totalSize = urlConnection.contentLength
                    val buffer = ByteArray(MEGABYTE)
                    var bufferLength = 0
                    while (inputStream.read(buffer).also { bufferLength = it } > 0) {
                        fileOutputStream.write(buffer, 0, bufferLength)
                    }
                    fileOutputStream.close()
                    filePath = directory.path
                }
                // uri = Uri.fromFile(new File(directory.getPath()));
                /* uri = Uri.fromFile(new File(fileUrl + directory));*/
                /*  uri = FileProvider.getUriForFile(Objects.requireNonNull(context.getApplicationContext()),
                        BuildConfig.APPLICATION_ID + ".provider", directory);*/
            } catch (e: FileNotFoundException) {
                Log.d("TAG", "downloadFile: " + e.message)
                e.printStackTrace()
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: NullPointerException) {
                e.printStackTrace()
            }
            Log.d("TAG", "downloadFile: $filePath")
            //return uri;
            return filePath
        }

        fun getFilePathFromURI(context: Context, contentUri: Uri?): String? {
            //copy file and send new file path
            val fileName = getFileName(contentUri)
            val wallpaperDirectory = File(
                context.cacheDir.toString() + "/proofImages"
            )
            // have the object build the directory structure, if needed.
            if (!wallpaperDirectory.exists()) {
                wallpaperDirectory.mkdirs()
            }
            if (!TextUtils.isEmpty(fileName)) {
                val copyFile = File(wallpaperDirectory.toString() + File.separator + fileName)
                // create folder if not exists
                copy(context, contentUri, copyFile)
                return copyFile.absolutePath
            }
            return null
        }

        fun getFileName(uri: Uri?): String? {
            if (uri == null) return null
            var fileName: String? = null
            val path = uri.path
            val cut = path!!.lastIndexOf('/')
            if (cut != -1) {
                fileName = path.substring(cut + 1)
            }
            return fileName
        }

        fun copy(context: Context, srcUri: Uri?, dstFile: File?) {
            try {
                val inputStream = context.contentResolver.openInputStream(srcUri!!) ?: return
                val outputStream: OutputStream = FileOutputStream(dstFile)
                copystream(inputStream, outputStream)
                inputStream.close()
                outputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        @Throws(Exception::class, IOException::class)
        fun copystream(input: InputStream?, output: OutputStream?): Int {
            val buffer = ByteArray(BUFFER_SIZE)
            val `in` = BufferedInputStream(input, BUFFER_SIZE)
            val out = BufferedOutputStream(output, BUFFER_SIZE)
            var count = 0
            var n = 0
            try {
                while (`in`.read(buffer, 0, BUFFER_SIZE).also { n = it } != -1) {
                    out.write(buffer, 0, n)
                    count += n
                }
                out.flush()
            } finally {
                try {
                    out.close()
                } catch (e: IOException) {
                    Log.e(e.message, e.toString())
                }
                try {
                    `in`.close()
                } catch (e: IOException) {
                    Log.e(e.message, e.toString())
                }
            }
            return count
        }


        fun deleteUriFile(path: String) {
            val fdelete = File(path)
            if (fdelete.exists()) {
                if (fdelete.delete()) {
                    Log.d("TAG", "deleteUriFile: ")
                    println("file Deleted :$path")
                } else {
                    println("file not Deleted :$path")
                }
            }
        }

        fun convertStringToList(csv: String?, delimiter: String): List<String>? {
            var csv = csv
            val tagsList: MutableList<String> = ArrayList()
            if (csv == null || csv.trim { it <= ' ' }.isEmpty()) {
                return tagsList
            }
            csv = csv.replace(" ", "")
            val values: Array<String> =
                csv.split(delimiter.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            tagsList.addAll(Arrays.asList(*values))
            return tagsList
        }

        @SuppressLint("Range")
        fun getActivityResultDataToFileName(uri: Uri, context: Context): String? {
            var result: String? = null
            if (uri.scheme == "content") {
                val cursor = context.contentResolver.query(uri, null, null, null, null)
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        result =
                            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                } finally {
                    cursor!!.close()
                }
            }
            if (result == null) {
                result = uri.path
                val cut = result!!.lastIndexOf('/')
                if (cut != -1) {
                    result = result.substring(cut + 1)
                }
            }
            return result
        }


        fun setLocale(locale: Locale?, context: Context) {
            Locale.setDefault(locale)
            val configuration = Configuration()
            configuration.locale = locale
            context.resources.updateConfiguration(
                configuration,
                context.resources.displayMetrics
            )
            /*Intent i = new Intent(this, ProfileActivity.class);
            startActivity(i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK));*/
        }


        fun limitTheNumberOfDecimalValues(s: Editable) {
            val text = s.toString()

            // check if the text contains a decimal point
            val decimalIndex = text.indexOf(".")
            if (decimalIndex >= 0) {
                // limit the number of decimal places to 2
                val numDecimalPlaces = text.length - decimalIndex - 1
                if (numDecimalPlaces > 2) {
                    s.delete(decimalIndex + 3, decimalIndex + numDecimalPlaces + 1)
                }
            }
        }

//        fun prepareFilePart(partName: String?, fileUri: Uri, context: Context): MultipartBody.Part {
//            Log.d("TAG", "prepareFilePart: $fileUri")
//            Log.d("TAG", "prepareFilePart: $fileUri")
//            Log.d("TAG", "prepareFilePart: " + context.contentResolver.getType(fileUri))
//
//            // if (context.getContentResolver().getType(fileUri).contains(Constant.PDF)) {
//            return if (fileUri.path!!.endsWith(".mp3")) {
//
//                val path = getFilePathFromURI(context, fileUri)
//                val file1 = File(path)
//                // Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider",file);*/
//                // Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider",file);*/
//                val requestFile = RequestBody.create(
//                    "audio/mpeg".toMediaTypeOrNull(), file1
//                )
//                Log.d("TAG", "prepareFilePart: " + context.contentResolver.getType(fileUri))
//                return MultipartBody.Part.createFormData(partName!!, file1.name, requestFile)
//            } else {
//                // File file = FileUtils.getFile(context, fileUri);
//                val path = getFilePathFromURI(context, fileUri)
//                val file1 = File(path)
//                //        Log.d("TAG", "prepareFilePart: " + file.getName());
//                // create RequestBody instance from file
//                //        Log.d("TAG", "prepareFilePart: " + file.getName());
//                // create RequestBody instance from file
//                val requestFile = RequestBody.create(
//                    (context.contentResolver.getType(fileUri))?.toMediaTypeOrNull(), file1
//                )
//                return MultipartBody.Part.createFormData(partName!!, file1.name, requestFile)
//
//            }
//            /* if (context.getContentResolver().getType(fileUri).contains(Constant.PDF)) {
//
//            Log.d("TAG", "prepar eFilePart: " + (fileUri.getAuthority()));
//            Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
//            requestFile = RequestBody.create(
//                    MediaType.parse(context.getContentResolver().getType(uri)), file);
//
//        } else {
//            Log.d("TAG", "prepareFilePart: " + (context.getContentResolver().getType(fileUri)) + "," + file.getName());
//            requestFile = RequestBody.create(
//                    MediaType.parse(context.getContentResolver().getType(fileUri)), file);
//        }*/
//        }

        fun prepareFilePart(partName: String?, fileUri: File, context: Context): MultipartBody.Part? {
            // Log the URI and content type for debugging
            Log.d("TAG", "prepareFilePart: $fileUri")
            Log.d("TAG", "prepareFilePart: Content Type: " +fileUri.name)

            // Check if the file is an MP3 audio file
            if (fileUri.path!!.endsWith(".mp3")) {
                try {
//                    val path = getFilePathFromURI(context, fileUri)
//                    val file = File(path)

                    // Create a request body for the audio file
                    val requestFile = RequestBody.create(
                        "audio/mpeg".toMediaTypeOrNull(), // Use the correct MIME type for MP3 files
                        fileUri
                    )

                    // Create the MultipartBody.Part
                    return MultipartBody.Part.createFormData(partName!!, fileUri.name, requestFile)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d("TAG", "prepareFilePart: " + e.message)
                    // Handle the exception if necessary
                }
            }

            return null // Return null if the file is not an MP3 or an exception occurs
        }


        fun createPartFromString(value: String?): RequestBody? {
            return RequestBody.create(MultipartBody.FORM, value!!)
        }




        fun getExcelColumnName(columnNumber: Int): String? {
            var columnNumber = columnNumber
            val columnName = StringBuilder()
            while (columnNumber > 0) {
                val rem = columnNumber % 26
                columnNumber = if (rem == 0) {
                    columnName.append('Z')
                    columnNumber / 26 - 1
                } else {
                    columnName.append((rem - 1 + 'A'.code).toChar())
                    columnNumber / 26
                }
            }
            return columnName.reverse().toString()
        }

        fun changeToProperCase(inputText: String): String {
            val words = inputText.split(" ")
            val wordsInProperCase = words.map { it.toLowerCase().capitalize() }
            return wordsInProperCase.joinToString(" ")
        }

//         fun requestAllPermission(context: Context) {
//            val requiredPermissions = arrayOf(
//                Manifest.permission.RECORD_AUDIO,
//            )
//
//            val anyPermissionNotGranted = requiredPermissions.all { permission ->
//                context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED
//            }
//
//
//
//            Log.d("TAG", "requestPermission: " + anyPermissionNotGranted)
//
//            if (context.checkSelfPermission(
//                    Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//
//                Toast.makeText(context,
//                    "Please allow all permission",
//                    Toast.LENGTH_SHORT
//                ).show()
//            } else {
//                startRecording(context)
//            }
//        }
//
//        fun onRecord(start: Boolean,context: Context) = if (start) {
//            requestAllPermission(context)
//        } else {
//            stopRecording()
//        }
//        fun startRecording(context: Context) {
//
//            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
//            val storageDir = context?.cacheDir
//            val outputFile = File(storageDir, "Call_$timestamp.3gp")
//
//
//            recorder = MediaRecorder();
//
//            recorder?.apply {
//                try {
//                    setAudioSource(MediaRecorder.AudioSource.MIC)
//                    setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
//                    setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
//                    setOutputFile(outputFile?.absolutePath)
////                    setAudioSource(MediaRecorder.AudioSource.MIC)
////                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)  // Use MPEG_4 format
////                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)     // Use AAC encoding
////                setOutputFile(outputFile?.absolutePath)
////            prepare()
////            start()
//            }
//            catch(e: Exception) {
//                Log.e("LOG_TAG", e.message!!)
//            }}
//
//            try {
//                recorder?.prepare()
//                recorder?.start()
//            } catch (e: IOException) {
//                Log.e("LOG_TAG", "prepare() failed")
//            }
//
//
//        }

//        fun startRecording(context: Context): MediaRecorder? {
//            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
//            val storageDir = context?.cacheDir
//           // val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
//            val outputFile = File(storageDir, "Call_$timestamp.3gp")
//
//            val recorder = MediaRecorder()
//
//            try {
//                recorder.apply {
//                    setAudioSource(MediaRecorder.AudioSource.MIC)
//                    setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
//                    setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
//                    setOutputFile(outputFile.absolutePath)
//                    prepare()
//                    start()
//                }
//            } catch (e: IOException) {
//                Log.e("LOG_TAG", "prepare() failed: ${e.message}")
//                return null
//            } catch (e: IllegalStateException) {
//                Log.e("LOG_TAG", "IllegalStateException: ${e.message}")
//                return null
//            }
//
//            return recorder
//        }

        private fun getRecordedFiles(context: Context): List<File> {
            val storageDir = context?.cacheDir
            val recordedFiles = mutableListOf<File>()

            storageDir?.listFiles()?.let { files ->
                for (file in files) {
                    if (file.isFile) {
                        recordedFiles.add(file)
                    }
                }
            }

            Log.d("TAG", "getRecordedFiles: " + recordedFiles)
            return recordedFiles
        }
//        fun stopRecording() {
//            recorder?.apply {
//                try {
//                    stop()
//                    reset()
//                    release()
//                } catch (e: IllegalStateException) {
//                    Log.d("TAG", "stopRecording: " + e.message)
//                }
//                recorder = null
//            }
//        }

        private var uplinkRecorder: MediaRecorder? = null
        private var downlinkRecorder: MediaRecorder? = null

         fun startRecording(context: Context) {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val storageDir = context.getExternalFilesDir(null)
            val uplinkOutputFile = File(storageDir, "Uplink_$timestamp.3gp")
            val downlinkOutputFile = File(storageDir, "Downlink_$timestamp.3gp")

            uplinkRecorder = MediaRecorder().apply {
                try {
                    setAudioSource(MediaRecorder.AudioSource.VOICE_UPLINK)
                    setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                    setOutputFile(uplinkOutputFile.absolutePath)
                    prepare()
                    start()
                } catch (e: Exception) {
                    Log.e("CallRecordingService", "Failed to record uplink: ", e)
                }
            }

            downlinkRecorder = MediaRecorder().apply {
                try {
                    setAudioSource(MediaRecorder.AudioSource.VOICE_DOWNLINK)
                    setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                    setOutputFile(downlinkOutputFile.absolutePath)
                    prepare()
                    start()
                } catch (e: Exception) {
                    Log.e("CallRecordingService", "Failed to record downlink: ", e)
                }
            }
        }

         fun stopRecording() {
            uplinkRecorder?.apply {
                try {
                    stop()
                    release()
                } catch (e: Exception) {
                    Log.e("CallRecordingService", "Failed to stop uplink recording: ", e)
                }
            }
            uplinkRecorder = null

            downlinkRecorder?.apply {
                try {
                    stop()
                    release()
                } catch (e: Exception) {
                    Log.e("CallRecordingService", "Failed to stop downlink recording: ", e)
                }
            }
            downlinkRecorder = null
        }

    }



}
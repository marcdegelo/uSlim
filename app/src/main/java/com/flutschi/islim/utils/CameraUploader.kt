package com.flutschi.islim.camera

import android.app.Activity
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.flutschi.islim.api.ApiService
import com.flutschi.islim.api.RetrofitInstance
import com.flutschi.islim.utils.GLOBALS
import com.flutschi.islim.utils.WebhookUtils.sendDiscordMessage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CameraUploader(private val activity: ComponentActivity) {

    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private lateinit var photoFile: File
    lateinit var photoUri: Uri

    private var pendingInfo: String? = null  // <-- Add this field
    private var uploadResultCallback: ((String) -> Unit)? = null

    fun registerCameraLauncher() {
        cameraLauncher = activity.registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                Log.i("CameraUploader", "✅ Picture saved to: $photoUri")

                uploadImageToBackend(
                    info = pendingInfo ?: "No info",
                    onSuccess = { status ->
                        activity.runOnUiThread {
                            uploadResultCallback?.invoke(status) // <-- Important! Callback after upload
                        }
                    },
                    onError = { error ->
                        activity.runOnUiThread {
                            uploadResultCallback?.invoke("try_again")
                        }
                    }
                )
            } else {
                Log.e("CameraUploader", "❌ Picture capture failed")
                activity.runOnUiThread {
                    uploadResultCallback?.invoke("try_again")
                }
            }
        }
    }


    fun dispatchTakePictureIntent(
        info: String,
        onUploadResult: (String) -> Unit  // <-- ADD THIS CALLBACK
    ) {
        pendingInfo = info
        uploadResultCallback = onUploadResult // <-- Save the callback
        photoFile = createImageFile()
        photoUri = FileProvider.getUriForFile(
            activity,
            "${activity.packageName}.fileprovider",
            photoFile
        )
        cameraLauncher.launch(photoUri)
    }


    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir!!)
    }

    private fun uploadImageToBackend(
        info: String,
        onSuccess: (String) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val requestFile = photoFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("image", photoFile.name, requestFile)

        val infoPart = MultipartBody.Part.createFormData("info", info)

        val api = RetrofitInstance.getApi()
        val call = api.uploadImage(imagePart, infoPart)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val bodyString = response.body()?.string()
                if (bodyString != null) {
                    try {
                        val json = JSONObject(bodyString)
                        val status = json.optString("status", "try_again")

                        // 🔧 run success on UI thread
                        activity.runOnUiThread {
                            onSuccess(status)
                        }

                    } catch (e: Exception) {
                        activity.runOnUiThread {
                            onError(e)
                        }
                    }
                } else {
                    activity.runOnUiThread {
                        onError(Exception("Empty response body"))
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                activity.runOnUiThread {
                    onError(t)
                }
            }
        })
    }



}

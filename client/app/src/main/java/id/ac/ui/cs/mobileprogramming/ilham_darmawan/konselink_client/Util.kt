package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.BindingAdapter
import androidx.room.TypeConverter
import androidx.transition.Fade
import androidx.transition.TransitionManager
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobileconnectors.s3.transferutility.*
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.BuildConfig.*
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.constant.Education
import java.io.File
import java.util.*

val dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun fromDateToTimeStamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}

@BindingAdapter("app:dataReady")
fun setVisibility(view: View, dataReady: Any?) {
    val transition = Fade()
    transition.duration = 300
    transition.addTarget(view)
    TransitionManager.beginDelayedTransition(view.parent as ViewGroup, transition)
    if (dataReady == null) {
        view.visibility = View.VISIBLE
    } else {
        view.visibility = View.GONE
    }
}


@BindingAdapter(value = ["android:selectedEducation", "android:educationType"], requireAll = false)
fun setVisibility(view: View, selectedEducation: String?, level: Int) {
    Log.d("SELECTED EDU", selectedEducation.toString())
    if (selectedEducation != null) {
        var selectedDegree = 0
        Education.values().forEach {
            if (it.educationType.equals(selectedEducation))
                selectedDegree = it.level
        }

        Log.d("degree", "$selectedDegree : $level")
        if (selectedDegree >= level) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
        Log.d("condition", (selectedDegree >= level).toString())
    }
}

@BindingAdapter("app:hasConsulted")
fun setVisibility(view: View, hasConsulted: Boolean?) {
    Log.d("HASCONSULTED2", hasConsulted.toString())
    if (hasConsulted != null) {
        view.visibility = if (hasConsulted) View.VISIBLE else View.GONE
    }
}

@BindingAdapter(value = ["app:isAgree", "app:enabledDrawable", "app:disabledDrawable"], requireAll = true)
fun setEnable(button: View, isAgree: Boolean?, enabledDrawable: Drawable, disabledDrawable: Drawable) {
    if (isAgree != null) {
        button.background = if (isAgree) enabledDrawable else disabledDrawable
    }
}

@BindingAdapter("app:avatar")
fun loadImage(imageView: ImageView, imageURL: String?) {
    Glide.with(imageView.context)
        .setDefaultRequestOptions(
            RequestOptions()
                .circleCrop()
        )
        .load(imageURL)
        .placeholder(R.drawable.user_placeholder)
        .into(imageView)
}

fun connectedToInternet(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val activeNetwork: Network? =  cm.activeNetwork
        activeNetwork != null
    } else {
        val networkInfo = cm.activeNetworkInfo
        networkInfo?.isConnectedOrConnecting == true
    }
}

fun uploadFileToS3(context: Context, photoPath: String, userId: Int, withFileName: Boolean) {

    val credentials = BasicAWSCredentials(S3_ACCESS_KEY, S3_ACCESS_SECRET)
    TransferNetworkLossHandler.getInstance(context);
    val s3: AmazonS3 = AmazonS3Client(credentials, Region.getRegion(Regions.AP_SOUTHEAST_1))
    val transferUtility = TransferUtility.builder().s3Client(s3).context(context).build()
    val file = File(photoPath)
    val fileName = if (withFileName) file.name else "image.jpg"
    val observer: TransferObserver = transferUtility.upload(
        AWS_S3_BUCKET,
        "${S3_CLIENT_DISPLAY_PICTURE_PREFIX}/${userId}/${fileName}",
        file,
        CannedAccessControlList.PublicRead
    )
    observer.setTransferListener(object : TransferListener {
        override fun onStateChanged(id: Int, state: TransferState) {
            Log.e("onStateChanged", "${id} + ${state.name}")
            if (state === TransferState.COMPLETED) {
                val url =
                    "https://" + AWS_S3_BUCKET + ".s3.amazonaws.com/" + observer.key
                Toast.makeText(context, "Sukses mengupload gambar", Toast.LENGTH_SHORT).show()
            }

            if(state == TransferState.PAUSED || state == TransferState.FAILED || state == TransferState.WAITING_FOR_NETWORK){
                Toast.makeText(context, "Tidak ada konseksi internet", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onProgressChanged(
            id: Int,
            bytesCurrent: Long,
            bytesTotal: Long
        ) {
        }

        override fun onError(id: Int, ex: Exception) {
            Toast.makeText(context, "Gagal mengirim foto", Toast.LENGTH_SHORT).show()
            ex.printStackTrace()
        }
    })
}

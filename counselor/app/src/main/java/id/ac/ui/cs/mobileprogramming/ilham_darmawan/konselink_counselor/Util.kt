package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.BuildConfig.*
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.constant.Specialization
import org.threeten.bp.DayOfWeek
import org.threeten.bp.temporal.WeekFields
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

val indonesianDays = listOf("Minggu", "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu")
val dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
val DASH = "-"
val COLON = ":"
val SESSION_INTERVAL_MILLIS = 90 * 60 * 1000


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

@BindingAdapter("app:selectedSpecialization")
fun setSelectedSpinnerItem(spinner: Spinner, selectedSpecialization: String?) {
    if (selectedSpecialization != null && spinner.adapter != null) {
        val specializationSpinnerAdapter = ArrayAdapter<Specialization>(
            spinner.context,
            R.layout.item_spinner,
            R.id.text_spinner,
            Specialization.values()
        )
        val specialization = Specialization.values().filter { it.specializationName.equals(selectedSpecialization) }.first()

        spinner.setSelection(specializationSpinnerAdapter.getPosition(specialization))
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

@BindingAdapter("app:photoPath")
fun setPhotoPath(imageView: ImageView, photoPath: String?) {
    if (photoPath != null && photoPath.isNotEmpty()) {
        val imgFile = File(photoPath)

        if (imgFile.exists()) {
            val bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath())
            imageView.setImageBitmap(bitmap)
        }
    }
}

@BindingAdapter(value = ["app:isConfirmed", "app:enabledDrawable", "app:disabledDrawable"], requireAll = true)
fun setEnable(button: View, isConfirmed: Boolean?, enabledDrawable: Drawable, disabledDrawable: Drawable) {
    if (isConfirmed != null) {
        button.background = if (isConfirmed) enabledDrawable else disabledDrawable
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

@BindingAdapter(value = ["app:startTime", "app:endTime"], requireAll = true)
fun setDateFormatting(textView: TextView, startTime: String?, endTime: String?) {
    val format = SimpleDateFormat(dateFormat)
    if (startTime != null && endTime != null) {
        val startDate = format.parse(startTime)
        val endDate = format.parse(endTime)
        val startCalendar = Calendar.getInstance()

        startCalendar.time = startDate

        val endCalendar = Calendar.getInstance()
        endCalendar.time = endDate

        var text = ""

        text = text.plus( indonesianDays[startCalendar.get(Calendar.DAY_OF_WEEK)-1]).plus(", ")
            .plus(startCalendar.get(Calendar.DATE)).plus(DASH)
            .plus(startCalendar.get(Calendar.MONTH)+1).plus(DASH)
            .plus(startCalendar.get(Calendar.YEAR))

        text = text.plus(" ").plus(parseTimeString(startCalendar.get(Calendar.HOUR_OF_DAY).toString())).plus(COLON)
            .plus(parseTimeString(startCalendar.get(Calendar.MINUTE).toString())).plus(DASH)
            .plus(parseTimeString(endCalendar.get(Calendar.HOUR_OF_DAY).toString())).plus(COLON)
            .plus(parseTimeString(endCalendar.get(Calendar.MINUTE).toString()))

        textView.text = text
    }
}

@BindingAdapter("app:startSessionTime")
fun setMaxValue(numberPicker: NumberPicker, startTime: String?) {
    if (startTime != null) {
        val dateFormatter = SimpleDateFormat("HH:mm")
        val startTimestamp = dateFormatter.parse(startTime)!!
        val endTimeStamp = dateFormatter.parse("23:59")!!
        val difference = endTimeStamp.time - startTimestamp.time

        val minValue = 0
        val maxValue = (difference / SESSION_INTERVAL_MILLIS).toInt()
        numberPicker.minValue = minValue
        numberPicker.maxValue = maxValue
        numberPicker.value = 0
    } else {
        numberPicker.minValue = 0
        numberPicker.maxValue = 0
    }
}

fun parseTimeString(time: String): String {
    return if (time.length == 1)
        ("0$time")
    else time
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
        "${S3_COUNSELOR_DISPLAY_PICTURE_PREFIX}/${userId}/${fileName}",
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

            if (state == TransferState.PAUSED || state == TransferState.FAILED || state == TransferState.WAITING_FOR_NETWORK) {
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

fun daysOfWeekFromLocale(): Array<DayOfWeek> {
    val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
    var daysOfWeek = DayOfWeek.values()
    // Order `daysOfWeek` array so that firstDayOfWeek is at index 0.
    if (firstDayOfWeek != DayOfWeek.MONDAY) {
        val rhs = daysOfWeek.sliceArray(firstDayOfWeek.ordinal..daysOfWeek.indices.last)
        val lhs = daysOfWeek.sliceArray(0 until firstDayOfWeek.ordinal)
        daysOfWeek = rhs + lhs
    }
    return daysOfWeek
}

@BindingAdapter("app:hasConsultedBefore")
fun setBooleanText(textview: TextView, hasConsultedBefore: Boolean?) {
    if (hasConsultedBefore != null) {
        if (hasConsultedBefore) {
            textview.text = "Ya"
        } else {
            textview.text = "Tidak"
        }
    }

    textview.text = "Tidak"
}

@BindingAdapter("app:siblingDataReady")
fun setVisibilityFromSiblingData(linearLayout: LinearLayout, siblingName: String?) {
    if (siblingName.isNullOrBlank()) {
        val transition = Fade()
        transition.duration = 300
        transition.addTarget(linearLayout)
        TransitionManager.beginDelayedTransition(linearLayout.parent as ViewGroup, transition)
        linearLayout.visibility = LinearLayout.GONE
    }

    linearLayout.visibility = LinearLayout.VISIBLE
}

@BindingAdapter("useDiagnosis")
fun setVisibilityFromUseDiagnosis(view: View, useDiagnosis: Boolean) {
    if (useDiagnosis) {
        view.visibility = View.VISIBLE
    } else {
        view.visibility = View.GONE
    }
}
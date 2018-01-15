package kr.nomade.apivod


import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.util.Base64
import android.util.Log
import android.view.View.*
import android.widget.EditText
import com.kakao.util.helper.Utility.getPackageInfo
import com.squareup.picasso.Picasso
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)
}

fun ImageView.loadImg(imageUrl: String?) {
    if (TextUtils.isEmpty(imageUrl)) {
        Picasso.with(context).load(R.mipmap.ic_launcher).into(this)
    } else {
        Picasso.with(context).load(imageUrl).into(this)
    }
}

fun <T> androidLazy(initializer: () -> T) : Lazy<T> = lazy(LazyThreadSafetyMode.NONE, initializer)

fun AppCompatActivity.toast(text: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, text, duration).show()
}

fun AppCompatActivity.choiceDialog(choices: Array<String>, title: String? = null, callback: (Int, String) -> Unit) {
    val builder = AlertDialog.Builder(this)

    if ( title != null )
        builder.setTitle(title)

    builder.setItems(choices,
        DialogInterface.OnClickListener { dialog, which ->
            val choice = choices[which]
            callback(which, choice)
            dialog.dismiss()
        })

    val dialog = builder.create()
    dialog.show()
}

fun AppCompatActivity.promptDialog(title: String, callback: (String) -> Unit) {
    val builder = AlertDialog.Builder(this)

    builder.setTitle(title)

    val input = EditText(this)
    input.inputType = InputType.TYPE_CLASS_TEXT //or InputType.TYPE_TEXT_VARIATION_PASSWORD
    builder.setView(input)

    builder.setPositiveButton("OK", { dialog, which ->
        callback(input.text.toString())
    })

    builder.setNegativeButton("Cancel", { dialog, which ->
        dialog.cancel()
    })

    val dialog = builder.create()
    dialog.show()
}

fun View.visible(isVisible: Boolean=true): View {
    visibility = if ( isVisible ) VISIBLE else GONE
    return this
}

fun View.gone(): View {
    visibility = GONE
    return this
}

fun View.invisible(): View {
    visibility = INVISIBLE
    return this
}


fun AppCompatActivity.getKeyHash(): String? {
    val packageInfo = getPackageInfo(this, PackageManager.GET_SIGNATURES)
    if ( packageInfo == null )
        return null;

    for( signature in packageInfo.signatures ) {
        try {
            val messageDigest = MessageDigest.getInstance("SHA")
            messageDigest.update(signature.toByteArray())
            return Base64.encodeToString(messageDigest.digest(), Base64.NO_WRAP)
        }
        catch(e: NoSuchAlgorithmException) {
            Log.w("KeyHash", "Unable to get MessageDigest. signature=${signature}", e)
        }
    }

    return null
}

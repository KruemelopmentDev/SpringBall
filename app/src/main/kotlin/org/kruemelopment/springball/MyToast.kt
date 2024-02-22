package org.kruemelopment.springball

import android.app.Activity
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast

class MyToast(context: Activity) {
    private var toast: Toast
    private var textView: TextView
    private var myToastView: MyToastView
    private var relativeLayout: RelativeLayout
    private var screenWidth: Int

    init {
        val layout = context.layoutInflater.inflate(
            R.layout.toast,
            context.findViewById(R.id.root)
        )
        toast = Toast(context)
        toast.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 200)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        myToastView = layout.findViewById(R.id.mytoastview)
        textView = layout.findViewById(R.id.toasttext)
        val typeface = Typeface.createFromAsset(context.assets, "fonts/Ba.ttf")
        textView.typeface = typeface
        val dm = Resources.getSystem().displayMetrics
        relativeLayout = layout.findViewById(R.id.root)
        screenWidth = dm.widthPixels
    }

    fun showSuccess(text: String?) {
        textView.text = text
        myToastView.setColor(GREEN)
        checkLayout()
        toast.show()
    }

    fun showWarning(text: String?) {
        textView.text = text
        myToastView.setColor(ORANGE)
        checkLayout()
        toast.show()
    }

    fun showInfo(text: String?) {
        textView.text = text
        myToastView.setColor(BLUE)
        checkLayout()
        toast.show()
    }

    private fun checkLayout() {
        relativeLayout.measure(0, 0)
        if (relativeLayout.measuredWidth >= screenWidth * 0.7) relativeLayout.layoutParams =
            RelativeLayout.LayoutParams(
                (screenWidth * 0.7).toInt(),
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ) else relativeLayout.layoutParams = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
    }

    companion object {
        val GREEN = Color.parseColor("#06d6a0")
        val ORANGE = Color.parseColor("#e36414")
        val BLUE = Color.parseColor("#4ea8de")
    }
}

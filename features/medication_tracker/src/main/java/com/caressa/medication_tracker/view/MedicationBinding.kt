package com.caressa.medication_tracker.view

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.caressa.common.utils.KeyboardUtils
import com.caressa.medication_tracker.adapter.DrugsListAdapter
import com.caressa.model.medication.DrugsModel
import timber.log.Timber

object MedicationBinding {

    @BindingAdapter("android:loadImage")
    @JvmStatic fun AppCompatImageView.setImageView( resource: Int ) {
        setImageResource(resource)
    }

    @BindingAdapter("app:drugitems")
    @JvmStatic fun AutoCompleteTextView.setItems( resource: DrugsModel.DrugsResponse? ) {
        try {
            Timber.i("BindingAdapter=> $resource")
            if ( resource != null ) {
                with(this.adapter as DrugsListAdapter) {
                    resource.drugs.let { updateData(it) }
                    //Utilities.hideKeyboard(view, context)
//                    KeyboardUtils.hideSoftInput(context as Activity)
                    showDropDown()
                }
            }
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Hides keyboard when the [EditText] is focused.
     *
     * Note that there can only be one [TextView.OnEditorActionListener] on each [EditText] and
     * this [BindingAdapter] sets it.
     */
    @BindingAdapter("hideKeyboardOnInputDone")
    @JvmStatic fun EditText.hideKeyboardOnInputDone( enabled: Boolean ) {
        if (!enabled) return
        val listener = TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                clearFocus()
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(windowToken, 0)
            }
            false
        }
        setOnEditorActionListener(listener)
    }

    /*
     * Instead of having if-else statements in the XML layout, you can create your own binding
     * adapters, making the layout easier to read.
     *
     * Instead of
     *
     * `android:visibility="@{viewmodel.isStopped ? View.INVISIBLE : View.VISIBLE}"`
     *
     * you use:
     *
     * `android:invisibleUnless="@{viewmodel.isStopped}"`
     *
     */

    /**
     * Makes the View [View.INVISIBLE] unless the condition is met.
     */
    @Suppress("unused")
    @BindingAdapter("invisibleUnless")
    @JvmStatic fun View.invisibleUnless( visible: Boolean ) {
        visibility = if (visible) View.VISIBLE else View.INVISIBLE
    }

    /**
     * Makes the View [View.GONE] unless the condition is met.
     */
    @Suppress("unused")
    @BindingAdapter("goneUnless")
    @JvmStatic fun View.goneUnless( visible: Boolean) {
        visibility = if (visible) View.VISIBLE else View.GONE
    }

    /**
     * In [ProgressBar], [ProgressBar.setMax] must be called before [ProgressBar.setProgress].
     * By grouping both attributes in a BindingAdapter we can make sure the order is met.
     *
     * Also, this showcases how to deal with multiple API levels.
     */
    @BindingAdapter(value=["android:max", "android:progress"], requireAll = true)

    @JvmStatic fun ProgressBar.updateProgress( max: Int, progress: Int) {
        setMax(max)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setProgress(progress, false)
        } else {
            setProgress(progress)
        }
    }

    @BindingAdapter("loseFocusWhen")
    @JvmStatic fun EditText.loseFocusWhen( condition: Boolean ) {
        if (condition) clearFocus()
    }

}
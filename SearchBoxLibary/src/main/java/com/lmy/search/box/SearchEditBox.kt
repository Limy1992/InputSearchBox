package com.lmy.search.box

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

/**
 * 搜索框
 * CreateDate:2019/6/21
 * Author:lmy
 */
class SearchEditBox(context: Context, attrs: AttributeSet?) :
    LinearLayout(context, attrs) {
    private var mContext: Context = context

    private var mOnClearInputContent: OnClearInputContent? = null
    private var mOnTextWatcher: OnTextWatcher? = null
    private val editText = EditText(context)
    private val closeImage = ImageView(context)
    private val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    init {
        orientation = HORIZONTAL
        initWidget()

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SearchEditBoxStyle)
        val backgroundBox =
            typedArray.getResourceId(R.styleable.SearchEditBoxStyle_backgroundBox, R.drawable.bg_search_box)

        val clearImageBox = typedArray.getResourceId(R.styleable.SearchEditBoxStyle_clearImageBox, R.drawable.ic_s_c)

        val widthBox = typedArray.getInt(R.styleable.SearchEditBoxStyle_widthBox, ViewGroup.LayoutParams.MATCH_PARENT)

        val heightBox = typedArray.getInt(R.styleable.SearchEditBoxStyle_heightBox, dip2px(30f))

        val paddingLeft = typedArray.getInt(R.styleable.SearchEditBoxStyle_paddingLeftBox, dip2px(10f))
        val paddingRight = typedArray.getInt(R.styleable.SearchEditBoxStyle_paddingRightBox, 0)
        val paddingTop = typedArray.getInt(R.styleable.SearchEditBoxStyle_paddingTopBox, 0)
        val paddingBottom = typedArray.getInt(R.styleable.SearchEditBoxStyle_paddingBottomBox, 0)

        val textColorBox = typedArray.getInt(R.styleable.SearchEditBoxStyle_textColorBox, Color.parseColor("#333333"))

        val textSizeBox = typedArray.getFloat(R.styleable.SearchEditBoxStyle_textSizeBox, 14f)

        val backgroundEditBox = typedArray.getResourceId(R.styleable.SearchEditBoxStyle_backgroundEditBox, 0)

        val hintBox = typedArray.getString(R.styleable.SearchEditBoxStyle_hintBox) ?: ""

        val tetCursorDrawable = typedArray.getResourceId(R.styleable.SearchEditBoxStyle_textCursorDrawable, -1)

        setBackgroundResource(backgroundBox)

        editText.setSingleLine(true)
        editText.ellipsize = TextUtils.TruncateAt.END
        editText.hint = hintBox
        val edParams = LayoutParams(widthBox, heightBox)
        edParams.weight = 1f
        edParams.gravity = Gravity.CENTER_VERTICAL
        editText.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
        editText.setTextColor(textColorBox)
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeBox)
        editText.setBackgroundResource(backgroundEditBox)
        if (tetCursorDrawable != -1) {
            try {
                val f = TextView::class.java.getDeclaredField("mCursorDrawableRes")
                f.isAccessible = true
                f.set(editText, tetCursorDrawable)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        val params = LayoutParams(dip2px(20f), dip2px(20f))
        params.gravity = Gravity.CENTER_VERTICAL
        params.rightMargin = dip2px(10f)
        closeImage.setImageResource(clearImageBox)
        closeImage.visibility = View.GONE
        addView(editText, edParams)
        addView(closeImage, params)

        closeImage.setOnClickListener {
            editText.setText("")
            closeImage.visibility = View.GONE
            mOnClearInputContent?.onClear()

        }

        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty()) {
                    closeImage.visibility = View.VISIBLE
                } else {
                    closeImage.visibility = View.GONE
                    mOnClearInputContent?.onClear()
                }
                mOnTextWatcher?.afterTextChanged(s)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                mOnTextWatcher?.beforeTextChanged(s, start, count, after)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mOnTextWatcher?.onTextChanged(s, start, before, count)
            }

        })

        typedArray.recycle()
    }

    private fun initWidget() {

    }

    fun getText(): Editable {
        return editText.text
    }

    fun addTextChangedListener(onTextWatcher: OnTextWatcher) {
        this.mOnTextWatcher = onTextWatcher
    }

    interface OnTextWatcher {
        fun afterTextChanged(s: Editable) {
        }

        fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }

    fun closeInputMethod() {
        inputMethodManager.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    fun setOnClearInputContent(onClearInputContent: OnClearInputContent) {
        this.mOnClearInputContent = onClearInputContent
    }

    fun setRequestFocus() {
        editText.isFocusable = true
        editText.requestFocus()
        inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    fun setHint(hintText: String) {
        editText.hint = hintText
    }

    interface OnClearInputContent {
        fun onClear()
    }

    private fun dip2px(dpValue: Float): Int {
        val scale = mContext.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

}
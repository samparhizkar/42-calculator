package com.sepidsa.fortytwocalculator.ui.history

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.text.InputType
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.sepidsa.fortytwocalculator.MainActivity
import com.sepidsa.fortytwocalculator.R
import com.sepidsa.fortytwocalculator.data.LogEntity

class LogAdapter(
    private val context: Context,
    private val viewModel: HistoryViewModel
) : BaseAdapter() {

    private var logs: List<LogEntity> = emptyList()

    fun updateLogs(newLogs: List<LogEntity>) {
        logs = newLogs
        notifyDataSetChanged()
    }

    override fun getCount(): Int = logs.size
    override fun getItem(position: Int): LogEntity = logs[position]
    override fun getItemId(position: Int): Long = logs[position].id

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_history, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val log = getItem(position)
        val mainActivity = context as MainActivity
        val iconColor = mainActivity.dialpadFontColor

        viewHolder.id = log.id
        viewHolder.resultView.text = log.result
        viewHolder.operationView.text = log.operation
        viewHolder.tagView.text = log.tag
        viewHolder.starredButton.setOnCheckedChangeListener(null)
        viewHolder.starredButton.isChecked = log.starred != 0
        viewHolder.starredButton.setOnCheckedChangeListener { _, isChecked ->
            viewModel.starLogEntry(log.id, isChecked)
        }

        viewHolder.tagView.setBackgroundColor(mainActivity.accentColorCode)
        viewHolder.tagView.typeface = Typeface.createFromAsset(context.assets, "notoregular.ttf")
        viewHolder.tagView.setOnClickListener { showLabelDialog(log) }

        viewHolder.deleteButton.setOnClickListener {
            mainActivity.playSound(mainActivity.clearSoundID)
            viewModel.deleteLogEntry(log.id)
        }
        viewHolder.deleteButton.typeface = Typeface.createFromAsset(context.assets, "flaticon.ttf")
        
        viewHolder.labelButton.setOnClickListener { showLabelDialog(log) }
        viewHolder.labelButton.typeface = Typeface.createFromAsset(context.assets, "flaticon.ttf")
        
        viewHolder.shareButton.setOnClickListener { shareLog(log) }
        viewHolder.shareButton.typeface = Typeface.createFromAsset(context.assets, "flaticon.ttf")

        viewHolder.useButton.setOnClickListener {
            mainActivity.addNumberToCalculation(log.result)
            mainActivity.switchToMainFragment()
        }
        viewHolder.useButton.typeface = Typeface.createFromAsset(context.assets, "flaticon.ttf")

        viewHolder.resultView.setTextColor(mainActivity.accentColorCode)
        viewHolder.operationView.setTextColor(iconColor)
        
        if (mainActivity.keypadBackgroundColorCode == Color.WHITE) {
            viewHolder.arrow.setTextColor(Color.parseColor("#EEEEEE"))
        } else {
            viewHolder.arrow.setTextColor(iconColor)
        }

        viewHolder.shareButton.setTextColor(iconColor)
        viewHolder.labelButton.setTextColor(iconColor)
        viewHolder.deleteButton.setTextColor(iconColor)
        viewHolder.useButton.setTextColor(iconColor)

        if ((viewHolder.toolbar.visibility == View.VISIBLE) && !viewHolder.checked) {
            val viewLayoutParams = viewHolder.toolbar.layoutParams as LinearLayout.LayoutParams
            viewLayoutParams.bottomMargin = 0 - viewHolder.toolbar.height
            viewHolder.toolbar.visibility = View.GONE
        } else {
            viewHolder.toolbar.visibility = View.VISIBLE
            viewHolder.checked = false
        }
        
        viewHolder.arrow.typeface = Typeface.createFromAsset(context.assets, "flaticon.ttf")
        viewHolder.arrow.rotation = 0f
        viewHolder.arrow.translationY = 0f
        viewHolder.arrow.alpha = 1f

        return view
    }

    private fun showLabelDialog(log: LogEntity) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.farsi_label))

        val input = EditText(context)
        val inputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)
        builder.setCancelable(false)
        input.setText(log.tag)
        input.requestFocus()
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

        builder.setPositiveButton(context.getString(R.string.farsi_ok)) { _: DialogInterface, _: Int ->
            val newLabel = input.text.toString()
            viewModel.updateTag(log.id, newLabel)
            inputMethodManager.hideSoftInputFromWindow(input.windowToken, 0)
        }
        builder.setNegativeButton(context.getString(R.string.farsi_cancel)) { dialog, _ ->
            inputMethodManager.hideSoftInputFromWindow(input.windowToken, 0)
            dialog.cancel()
        }

        val dialog = builder.create()
        dialog.show()
        
        input.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && 
                (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER)) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick()
                true
            } else false
        }
    }

    private fun shareLog(log: LogEntity) {
        val shareBody = "${log.operation} = ${log.result}"
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        val subject = log.tag

        if (subject.isNotEmpty()) {
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
            sharingIntent.putExtra(Intent.EXTRA_TEXT, "$subject: $shareBody")
        } else {
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
        }

        context.startActivity(Intent.createChooser(sharingIntent, "Share"))
    }

    class ViewHolder(view: View) {
        val resultView: TextView = view.findViewById(R.id.result)
        val operationView: TextView = view.findViewById(R.id.operation)
        val tagView: TextView = view.findViewById(R.id.LOG_tag)
        val starredButton: CheckBox = view.findViewById(R.id.log_checkbox)
        val toolbar: View = view.findViewById(R.id.toolbar)
        val arrow: TextView = view.findViewById(R.id.arrow)
        val tagBackground: FrameLayout = view.findViewById(R.id.tag_background)

        var id: Long = 0
        var checked: Boolean = false

        val deleteButton: Button = view.findViewById(R.id.deleteButton)
        val labelButton: Button = view.findViewById(R.id.labelButton)
        val shareButton: Button = view.findViewById(R.id.shareButton)
        val useButton: Button = view.findViewById(R.id.useButton)
    }
}

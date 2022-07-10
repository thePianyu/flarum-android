package cn.duoduo.flarum.ui.discussion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.duoduo.flarum.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class ReplyBottomSheet(private val listener: OnClickListener) : BottomSheetDialogFragment() {

    interface OnClickListener {
        fun onSubmit(content: String)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.reply_bottom_sheet, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val btn = view.findViewById<MaterialButton>(R.id.btn_submit)
        val edit = view.findViewById<TextInputEditText>(R.id.edit_content)
        btn.setOnClickListener {
            listener.onSubmit(edit.text.toString())
        }
    }

    companion object {
        const val TAG = "ReplyBottomSheet"
    }
}
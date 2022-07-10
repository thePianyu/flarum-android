package cn.duoduo.flarum.ui.discussion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.duoduo.flarum.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ReplyBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.reply_bottom_sheet, container, false)

    companion object {
        const val TAG = "ReplyBottomSheet"
    }
}
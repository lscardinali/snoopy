package com.cortinadev.snoopy.ui.common

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cortinadev.snoopy.R
import com.cortinadev.snoopy.util.ErrorHandler
import com.cortinadev.snoopy.util.ToastUtil
import kotlinx.coroutines.launch

fun <T : ViewModel> T.executeWithToast(
    context: Context,
    tag: String,
    onError: (String) -> Unit = { ToastUtil.showError(context, it) },
    block: suspend () -> Unit
) {
    viewModelScope.launch(ErrorHandler.createExceptionHandler(tag)) {
        try {
            block()
        } catch (e: Exception) {
            val errorMsg = context.getString(R.string.error_operation_failed, e.message ?: context.getString(R.string.error_unknown))
            ErrorHandler.logError(tag, errorMsg, e)
            onError(errorMsg)
        }
    }
}

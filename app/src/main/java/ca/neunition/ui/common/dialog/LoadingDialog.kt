/*
 * Copyright 2022 Neunition. All rights reserved.
 *
 * Show a loading spinner overlay to the user.
 *
 * @author Nelaven Subaskaran
 * @since 1.0.0
 */

package ca.neunition.ui.common.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import ca.neunition.R

class LoadingDialog(myActivity: Activity) {
    private val activity: Activity = myActivity
    private lateinit var dialog: AlertDialog

    @SuppressLint("InflateParams")
    fun startDialog() {
        val builder = AlertDialog.Builder(activity)

        val inflater: LayoutInflater = activity.layoutInflater
        builder.setView(inflater.inflate(R.layout.loading_dialog, null)).setCancelable(false);

        dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show()
    }

    fun dismissDialog() {
        dialog.dismiss()
    }
}

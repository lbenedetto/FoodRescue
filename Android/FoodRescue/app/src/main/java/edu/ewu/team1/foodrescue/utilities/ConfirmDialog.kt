package edu.ewu.team1.foodrescue.utilities

import android.app.AlertDialog
import android.content.Context

class ConfirmDialog {
	companion object {
		fun confirmAction(r: Runnable, message: Int, context: Context) {
			AlertDialog.Builder(context)
					.setMessage(message)
					.setPositiveButton("Yes", { _, _ ->
						r.run()
					})
					.setNegativeButton("No", { _, _ ->

					})
					.create()
					.show()
		}
	}
}
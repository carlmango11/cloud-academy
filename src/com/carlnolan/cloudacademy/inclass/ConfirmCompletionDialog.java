package com.carlnolan.cloudacademy.inclass;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;

import com.carlnolan.cloudacademy.R;

/**
 * Dialog which asks user to confirm that they have finished a
 * piece of homework.
 * @author Carl
 *
 */
public class ConfirmCompletionDialog extends DialogFragment {
	private Homework thisHomework;
	private HomeworkCompleteDialogCallback callback;
	
	public interface HomeworkCompleteDialogCallback {
		public void onHomeworkCompleteDialogResponse(Homework homework, boolean complete);
	}
	
	public static ConfirmCompletionDialog newInstance(Homework h, HomeworkCompleteDialogCallback c) {
		ConfirmCompletionDialog instance = new ConfirmCompletionDialog();
		
		instance.callback = c;
		instance.thisHomework = h;
		
		return instance;
	}
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		
		int alertMessageId;
		if(thisHomework.isComplete()) {
			alertMessageId = R.string.homework_confirm_noncompletion;
		} else {
			alertMessageId = R.string.homework_confirm_completion;
		}
		
		alert.setTitle(alertMessageId)
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					callback.onHomeworkCompleteDialogResponse(thisHomework, !thisHomework.isComplete());
				}
			}).setNegativeButton("No", null);
		
		return alert.create();
	}
}
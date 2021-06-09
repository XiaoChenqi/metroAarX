package com.facilityone.wireless.fm_library.dialog;

import android.content.Context;


/**
 * The loading dialog.
 * @author tessi
 *
 */
public class WaittingDialog extends SweetAlertDialog {

	public WaittingDialog(Context context, String tip) {
		super(context, SweetAlertDialog.PROGRESS_TYPE);
		setTipText(tip);
	}
	
	public void setProgessTip(String tip) {
		setTitleText(tip);
	}
}

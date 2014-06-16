package com.teambugallergy.resourceshare.activities;

import com.teambugallergy.resourceshare.R;

import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import android.view.View.OnClickListener;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * General class to show a Dialog.
 * Layout used is dialog_layout.
 * There is a message displayed and a OK button.
 * 
 * 05-04-2014
 * @author TeamBugAllergy
 *
 */
public class CustomDialog extends Dialog implements OnClickListener{

	/**
	 * Title of the dialog.
	 */
	String title;
	
	/**
	 * Message to be displayed by the dialog.
	 */
	String dialog;
	
	/**
	 * Context of the caller activity.
	 */
	Context callerContext;
	
	/**
	 * TextView to hold the dialog.
	 */
	private TextView message;
	
	/**
	 * ProgressBar tha displays a spinner
	 */
	private ProgressBar progress_bar;
	
	/**
	 * OK button.
	 */
	private Button ok;
	
	// -----------------------------------------------------------------------------------
	
	/*
	 * Initializes a CustomDialog object
	 */
	public CustomDialog(Context context, String title, String dialog) {
		super(context);
		
		//save the context
		callerContext = context;
		
		//save the title
		this.title = title;
		
		//Save the dialog
		this.dialog = dialog;
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_layout);
		
		//Intial setup
				//Keep the screen on
				//This flag will be cleared when this activity is destroyed
				getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				
		//set the title
		setTitle(title);
		
		message = (TextView)findViewById(R.id.message);
		message.setText(dialog);
		
		progress_bar = (ProgressBar)findViewById(R.id.progress_bar);
		
		ok = (Button)findViewById(R.id.ok);
		ok.setOnClickListener(this);
		
	}
	
	/**
	 * Sets the <b>title</b> as the title.
	 * @param title Title of the dialog to be displayed.
	 */
	public void changeTitle(String title)
	{
		//save the title
		this.title = title;
		
		//set the title
		setTitle(title);
	}
	
	/**
	 * Sets the <b>dialog</b> as the dialog.
	 * @param dialog Dialog to be displayed.
	 */
	public void changeDialog(String dialog)
	{
		//save the dialog
		this.dialog = dialog;
		
		//set the dialog
		message.setText(dialog);
	}
	
	/**
	 * Sets the visibility of Button ok
	 */
	public void showOkButton(boolean visibility)
	{
		if(visibility)
			ok.setVisibility(View.VISIBLE);
		else
			ok.setVisibility(View.GONE);
	}
	
	/**
	 * Sets the visibility of ProgressBar progress_bar 
	 */
	public void showProgressBar(boolean visibility)
	{
		if(visibility)
			progress_bar.setVisibility(View.VISIBLE);
		else
			progress_bar.setVisibility(View.GONE);
	}
	
	/**
	 * Closes the dialog.
	 */
	public void closeDialog()
	{
		//Close the dialog
		dismiss();
	}
	
	//OnClick for OK button
	public void onClick(View v) {
			
		if(v.getId() == ok.getId())
		{
			//Close the dialog
			dismiss();
		}
		
	}
	
	/**
	 * Avoids closing of dialog for touch events outside the Dialog.
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		//Notify that event has been handled here only.
		return true;
	}
	
	/**
	 * This definition finishes the activity and returns to the MainActivity.
	 */
	@Override
	public void onBackPressed() {
		
		 LogMsg("INSIDE:onBackPressed");

		
				LogMsg("Finishing the ProviderActivity");
				((Activity) callerContext).finish();
				
		super.onBackPressed();
	}

	private static void LogMsg(String msg) {
		Log.d("CustomDialog", msg);
	}

}

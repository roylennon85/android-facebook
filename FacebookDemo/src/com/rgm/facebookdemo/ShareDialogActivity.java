package com.rgm.facebookdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.FacebookDialog.PendingCall;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

public class ShareDialogActivity extends Activity {

	private UiLifecycleHelper uiHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_dialog);

		uiHelper = new UiLifecycleHelper(this, null);
		uiHelper.onCreate(savedInstanceState);

		share();
	}

	@Override
	protected void onResume() {
		super.onResume();
		uiHelper.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// TODO: Callback handler for share dialog.
		uiHelper.onActivityResult(requestCode, resultCode, data,
				new FacebookDialog.Callback() {

					@Override
					public void onError(PendingCall pendingCall,
							Exception error, Bundle data) {
						Toast.makeText(ShareDialogActivity.this,
								"Facebook dialog error", Toast.LENGTH_SHORT)
								.show();

						finish();
					}

					@Override
					public void onComplete(PendingCall pendingCall, Bundle data) {
						String postId = FacebookDialog
								.getNativeDialogPostId(data);
						String completionGesture = FacebookDialog
								.getNativeDialogCompletionGesture(data);
						boolean didComplete = FacebookDialog
								.getNativeDialogDidComplete(data);

						Toast.makeText(
								ShareDialogActivity.this,
								"Post Id: " + postId + ", Completion Gesture: "
										+ completionGesture + ", didComplete: "
										+ didComplete, Toast.LENGTH_SHORT)
								.show();

						finish();
					}
				});
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	private void share() {
		if (FacebookDialog.canPresentShareDialog(getApplicationContext(),
				FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
			// TODO: Share a Link with Share Dialog
			FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(
					this)
					.setLink("https://developers.facebook.com/android")
					.setName("Sharing a link with share dialog")
					.setCaption("Sharing a link set caption")
					.setDescription("Sharing description")
					.setPicture(
							"http://howmanyarethere.net/wp-content/uploads/2013/08/beatles-300x300.jpg")
					.build();

			uiHelper.trackPendingDialogCall(shareDialog.present());
		} else {
			// TODO: Use feed dialog.
			publishFeedDialog();
		}
	}

	private void publishFeedDialog() {
		Bundle params = new Bundle();
		params.putString("name", "Facebook SDK for Android");
		params.putString("caption", "Feed dialog caption");
		params.putString("description", "Feed dialog description");
		params.putString("link", "https://developers.facebook.com/android");
		params.putString("picture",
				"http://howmanyarethere.net/wp-content/uploads/2013/08/beatles-300x300.jpg");

		WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(this,
				Session.getActiveSession(), params)).setOnCompleteListener(
				new OnCompleteListener() {

					@Override
					public void onComplete(Bundle values,
							FacebookException error) {
						if (error == null) {
							final String postId = values.getString("post_id");
							if (postId != null) {
								Toast.makeText(ShareDialogActivity.this,
										"Posted story, id: " + postId,
										Toast.LENGTH_SHORT).show();
							} else {
								// User clicked the Cancel button
								Toast.makeText(
										getApplicationContext(),
										"Publish cancelled", Toast.LENGTH_SHORT)
										.show();
							}
						} else if(error instanceof FacebookOperationCanceledException){
							// user clicked x button
							Toast.makeText(getApplicationContext(), 
			                        "Publish cancelled", 
			                        Toast.LENGTH_SHORT).show();
						}else{
							Toast.makeText(getApplicationContext(), 
			                        "Error posting story", 
			                        Toast.LENGTH_SHORT).show();
						}
					}
				}).build();
		feedDialog.show();
	}

}

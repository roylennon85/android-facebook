package com.rgm.facebookdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.OpenGraphAction;
import com.facebook.model.OpenGraphObject;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.FacebookDialog.PendingCall;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

public class OpenGraphStoryActivity extends Activity {

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
						Toast.makeText(OpenGraphStoryActivity.this,
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
								OpenGraphStoryActivity.this,
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
			// TODO: share dialog with custom story myapprgmdemo is the app namespace.
			OpenGraphObject song = OpenGraphObject.Factory.createForPost("myapprgmdemo:Song");
			song.setProperty("title", "Rock Show");
			song.setProperty("image", "http://rymimg.com/lk/f/l/713a42213f0000ec8dfea8c549cf893d/3783981.jpg");
			song.setProperty("description", "Listening to Rock Show by Paul McCartney & Wings");
			
			OpenGraphAction action = GraphObject.Factory.create(OpenGraphAction.class);
			action.setType("myapprgmdemo:escuchando");
			action.setProperty("song", song);
			
			FacebookDialog shareDialog = new FacebookDialog.OpenGraphActionDialogBuilder(this, action, "song").build();
			uiHelper.trackPendingDialogCall(shareDialog.present());
		} else {
			
		}
	}
}

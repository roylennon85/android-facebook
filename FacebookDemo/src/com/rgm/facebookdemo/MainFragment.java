package com.rgm.facebookdemo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Request.Callback;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.OpenGraphAction;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.LoginButton;

public class MainFragment extends Fragment {
	
	// TODO: Listen to station status changes.
	private Session.StatusCallback callback = new StatusCallback() {
		
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			onSessionStateChange(session, state, exception);			
		}
	};
	
	// TODO: UiLifecycleHelper helps to handle session changes.
	private UiLifecycleHelper uiHelper;
	
	private Button btnShareLink, btnShareLinkApi, btnShareLinkMessageDialog, btnSharePhotosMessageDialog, btnShareOpenGraph;
	
	// API publish variables.
	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
	private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
	private boolean pendingPublishReauthorization = false;
	
	// TODO: Add uiHelper.onCreate, onResume, onActivityResult, onPause,
	// onDestroy and onSaveInstanceState methods.
	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		
		// TODO: initialize helper.
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(savedInstanceState);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		// TODO: Trigger session state changes if the session is open.
		Session session = Session.getActiveSession();
		if(session != null && (session.isOpened() || session.isClosed())){
			onSessionStateChange(session, session.getState(), null);
		}
		uiHelper.onResume();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
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
		// Save pending publish reauthorization value in case the activity is stopped before it is done.
		outState.putBoolean(PENDING_PUBLISH_KEY, pendingPublishReauthorization);
		uiHelper.onSaveInstanceState(outState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_main, container, false);
		
		// Make the fragment respond to the onActivityResult() instead of parent activity.
		LoginButton loginButton = (LoginButton) view.findViewById(R.id.authButton);
		loginButton.setFragment(this);
		
		// TODO: Ask for permissions.
		loginButton.setReadPermissions(Arrays.asList("user_likes", "user_status", "public_profile"));
		
		if(savedInstanceState != null){
			pendingPublishReauthorization = savedInstanceState.getBoolean(PENDING_PUBLISH_KEY, false);
		}
		return view;
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		btnShareLink = (Button) view.findViewById(R.id.btnShareLink);
		btnShareLink.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), ShareDialogActivity.class);
				startActivity(intent);
			}
		});
		
		btnShareLinkApi = (Button) view.findViewById(R.id.btnShareLinkApi);
		btnShareLinkApi.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				publishStory();				
			}
		});
		
		btnShareLinkMessageDialog = (Button) view.findViewById(R.id.btnMessageDialogLink);
		btnShareLinkMessageDialog.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				shareLinkMessage();				
			}
		});
		
		btnSharePhotosMessageDialog = (Button) view.findViewById(R.id.btnMessageDialogPhotos);
		btnSharePhotosMessageDialog.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sharePhotosMessage();				
			}
		});
		
		btnShareOpenGraph = (Button) view.findViewById(R.id.btnMessageDialogOpenGraph);
		btnShareOpenGraph.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				shareOpenGraphMessage();
			}
		});
	}
	
	// TODO: Handle state changes.
	private void onSessionStateChange(Session session, SessionState state, Exception exception){
		if(state.isOpened()){
			Toast.makeText(getActivity(), "Logged in", Toast.LENGTH_SHORT).show();
			btnShareLink.setVisibility(View.VISIBLE);
			btnShareLinkApi.setVisibility(View.VISIBLE);
			btnShareLinkMessageDialog.setVisibility(View.VISIBLE);
			btnSharePhotosMessageDialog.setVisibility(View.VISIBLE);
			btnShareOpenGraph.setVisibility(View.VISIBLE);
			
			// See if there was a pending publish authorization and if the token was updated.
			if(pendingPublishReauthorization && state.equals(SessionState.OPENED_TOKEN_UPDATED)){
				pendingPublishReauthorization = false;
				publishStory();
			}
		}else if(state.isClosed()){
			Toast.makeText(getActivity(), "Logged out", Toast.LENGTH_SHORT).show();
			btnShareLink.setVisibility(View.GONE);
			btnShareLinkApi.setVisibility(View.GONE);
			btnShareLinkMessageDialog.setVisibility(View.GONE);
			btnSharePhotosMessageDialog.setVisibility(View.GONE);
			btnShareOpenGraph.setVisibility(View.GONE);
		}
	}
	
	// TODO: Publish using API calls.
	private void publishStory(){
		Session session = Session.getActiveSession();
		
		if(session != null){
			// Check for publish permissions
			List<String> permissions = session.getPermissions();
			if(!isSubsetOf(PERMISSIONS, permissions)){
				pendingPublishReauthorization = true;
				// Request publish permissions.
				Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(this, PERMISSIONS);
				session.requestNewPublishPermissions(newPermissionsRequest);
				return;
			}
			
			// Parameters to be shown in post.
			Bundle postParams = new Bundle();
			postParams.putString("name", "Facebook SDK for Android");
	        postParams.putString("caption", "Build great social apps and get more installs.");
	        postParams.putString("description", "The Facebook SDK for Android makes it easier and faster to develop Facebook integrated Android apps.");
	        postParams.putString("link", "https://developers.facebook.com/android");
	        postParams.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");
	        
	        // Handle request response.
	        Request.Callback callback = new Callback() {
				
				@Override
				public void onCompleted(Response response) {
					JSONObject graphResponse = response.getGraphObject().getInnerJSONObject();
					String postId = null;
					
					try{
						postId = graphResponse.getString("id");
					}catch(JSONException e){
						Log.i("facebook", "JSON error: " + e.getMessage());
					}
					
					// Verify if there was an error in the request.
					FacebookRequestError error = response.getError();
					if(error != null){
						Toast.makeText(getActivity().getApplicationContext(), error.getErrorMessage(), Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(getActivity().getApplicationContext(), postId, Toast.LENGTH_SHORT).show();
					}
				}
			};
			
			// Make the request
			Request request = new Request(session, "me/feed", postParams, HttpMethod.POST, callback);
			RequestAsyncTask task = new RequestAsyncTask(request);
			task.execute();
		}
	}
	
	// Check for permissions.
	private boolean isSubsetOf(Collection<String> subset, Collection<String> superset){
		for(String string : subset){
			if(!superset.contains(string)){
				return false;
			}
		}
		return true;
	}
	
	// TODO: Share a Message (link)
	private void shareLinkMessage(){
		// Create Message Dialog
		FacebookDialog.MessageDialogBuilder builder = new FacebookDialog.MessageDialogBuilder(getActivity())
			.setLink("https://developers.facebook.com/docs/android/share/")
		    .setName("Message Dialog Tutorial")
		    .setCaption("Build great social apps that engage your friends.")
		    .setPicture("http://i.imgur.com/g3Qc1HN.png")
		    .setDescription("Allow your users to message links from your app using the Android SDK.")
		    .setFragment(this);
			
		// Check if Facebook Messenger is installed.
		if(builder.canPresent()){
			// Show Message Dialog.
			FacebookDialog dialog = builder.build();
			dialog.present();
		}else{
			Toast.makeText(getActivity(), "Facebook Messenger needed for this", Toast.LENGTH_SHORT).show();
		}
	}
	
	// TODO: Share photos on a Message.
	private void sharePhotosMessage(){
		// Check if Facebook Messenger is installed.
		if(FacebookDialog.canPresentMessageDialog(getActivity().getApplicationContext(), FacebookDialog.MessageDialogFeature.PHOTOS)){
			FacebookDialog.PhotoMessageDialogBuilder builder = new FacebookDialog.PhotoMessageDialogBuilder(getActivity());
			Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.test_image);
			List<Bitmap> photos = new ArrayList<Bitmap>();
			photos.add(image);
			
			builder.addPhotos(photos);
			builder.build().present();
		}else{
			Toast.makeText(getActivity(), "Facebook Messenger needed for this", Toast.LENGTH_SHORT).show();
		}
	}
	
	// TODO: Share open graph message.
	private void shareOpenGraphMessage(){
		/*FacebookDialog.OpenGraphMessageDialogBuilder builder = new FacebookDialog.OpenGraphMessageDialogBuilder(getActivity(), action, previewPropertyName);
		
		if(builder.canPresent()){
			FacebookDialog dialog = builder.build();
			dialog.present();
		}else{
			Toast.makeText(getActivity(), "Facebook Messenger needed for this", Toast.LENGTH_SHORT).show();
		}*/
	}
}

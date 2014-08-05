package com.rgm.facebookdemo;

import java.util.Arrays;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.FacebookDialog.PendingCall;
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
	
	private Button btnShareLink;
	
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
		
		return view;
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		btnShareLink = (Button) getActivity().findViewById(R.id.btnShareLink);
		btnShareLink.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), ShareDialogActivity.class);
				startActivity(intent);
			}
		});
	}
	
	// TODO: Handle state changes.
	private void onSessionStateChange(Session session, SessionState state, Exception exception){
		if(state.isOpened()){
			Toast.makeText(getActivity(), "Logged in", Toast.LENGTH_SHORT).show();
			btnShareLink.setVisibility(View.VISIBLE);
		}else if(state.isClosed()){
			Toast.makeText(getActivity(), "Logged out", Toast.LENGTH_SHORT).show();
			btnShareLink.setVisibility(View.GONE);
		}
	}
}

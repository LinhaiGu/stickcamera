package com.yxiaolv.camerasample;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

public class ShowImageActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_layout);
		ImageView show = (ImageView) findViewById(R.id.show);
		String path = getIntent().getStringExtra("imagpath");
		show.setImageURI(Uri.parse(path));
	}

}

package com.sieae.jamaicaobserver.cartoons;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.sieae.jamaicaobserver.R;
import com.sieae.jamaicaobserver.cartoons.TouchImageView;
import com.squareup.picasso.Picasso;

public class ScaleImageViewActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scale_image_view_activity);
		TouchImageView view = (TouchImageView) findViewById(R.id.imageView);

		Intent intent = getIntent();
		String thumburl = intent.getStringExtra("url");

		try {

			if (thumburl != "" && thumburl != null){
				Picasso.with(this).load(thumburl).into(view);
			} else {
				//					holder.listThumb.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
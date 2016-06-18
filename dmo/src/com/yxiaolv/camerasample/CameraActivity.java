package com.yxiaolv.camerasample;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.yxiaolv.camerasample.StickerView.OnStickerDeleteListener;
import com.yxiaolv.camerasample.util.BitmapUtil;

public class CameraActivity extends Activity {

	private StickerView stickerView;

	private ImageView iv_show;

	private RelativeLayout group;

	private Camera mCamera;
	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	private Button btnCapture;
	private Button btnTo;
	private boolean previewing;

	private String imagUrl = "";

	int mCurrentCamIndex = 0;
	SurfaceViewCallback surfaceViewCallback;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initViews();
		initEvent();
	}

	private void initEvent() {
		btnCapture.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {
				if (previewing) {
					mCamera.takePicture(shutterCallback, rawPictureCallback,
							jpegPictureCallback);

				}

			}
		});
		btnTo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				to();
			}
		});
	}

	private void to() {
		Intent intent = new Intent(CameraActivity.this, ShowImageActivity.class);
		intent.putExtra("imagpath", imagUrl);
		startActivity(intent);
	}

	/**
	 * 初始化View
	 */
	private void initViews() {
		btnCapture = (Button) findViewById(R.id.btn_capture);
		btnTo = (Button) findViewById(R.id.btn_to);
		group = (RelativeLayout) findViewById(R.id.group);
		iv_show = (ImageView) findViewById(R.id.iv_show);
		stickerView = (StickerView) findViewById(R.id.sticker);
		iv_show.setVisibility(View.INVISIBLE);
		group.setDrawingCacheEnabled(true);
		group.buildDrawingCache();
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.katong);
		stickerView.setWaterMark(bitmap);
		stickerView.setOnStickerDeleteListener(new OnStickerDeleteListener() {

			@Override
			public void onDelete() {

			}
		});
		surfaceViewCallback = new SurfaceViewCallback();
		surfaceView = (SurfaceView) findViewById(R.id.surfaceView1);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(surfaceViewCallback);
		// surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	ShutterCallback shutterCallback = new ShutterCallback() {
		@Override
		public void onShutter() {
		}
	};

	PictureCallback rawPictureCallback = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] arg0, Camera arg1) {

		}
	};

	PictureCallback jpegPictureCallback = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] arg0, Camera arg1) {
			save(arg0);
		}

		/**
		 * 保存图片
		 * 
		 * @param data
		 */
		private void save(byte[] data) {
			String fileName = Environment.getExternalStoragePublicDirectory(
					Environment.DIRECTORY_DCIM).toString()
					+ File.separator + System.currentTimeMillis() + ".jpg";
			File file = new File(fileName);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdir();
			}
			try {
				BufferedOutputStream bos = new BufferedOutputStream(
						new FileOutputStream(file));
				bos.write(data);
				mCamera.stopPreview();
				previewing = false;
				bos.flush();
				bos.close();
				scanFileToPhotoAlbum(file.getAbsolutePath());
				configPhoto(file);
				saveNewPhoto();
			} catch (Exception e) {
			}
			mCamera.startPreview();
			previewing = true;
		}

		/**
		 * 保持合成后的图片
		 */
		private void saveNewPhoto() {
			Bitmap bitmap = group.getDrawingCache();
			iv_show.setImageBitmap(bitmap);
			saveImageToGallery(CameraActivity.this, bitmap);
			iv_show.setVisibility(View.INVISIBLE);
		}

		/**
		 * 配置照片
		 * 
		 * @param file
		 */
		private void configPhoto(File file) {
			Bitmap imagbitmap = null;
			setCameraDisplayOrientation(CameraActivity.this, mCurrentCamIndex,
					mCamera);
			if (cameraPosition == 1) {
				imagbitmap = BitmapUtil.convert(BitmapUtil.rotaingImageView(
						270, BitmapFactory.decodeFile(file.getAbsolutePath())),
						0);
			}else{
				imagbitmap=BitmapUtil.decodeSampledBitmapFromResource(file.getAbsolutePath(), 200, 300);
				imagbitmap=BitmapUtil.rotaingImageView(90, imagbitmap);
			}
			iv_show.setImageBitmap(imagbitmap);
			iv_show.setVisibility(View.VISIBLE);

		};
	};

	public void scanFileToPhotoAlbum(String path) {

		MediaScannerConnection.scanFile(CameraActivity.this,
				new String[] { path }, null,
				new MediaScannerConnection.OnScanCompletedListener() {

					public void onScanCompleted(String path, Uri uri) {
					}
				});
	}

	private final class SurfaceViewCallback implements
			android.view.SurfaceHolder.Callback {
		public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2,
				int arg3) {
			if (previewing) {
				mCamera.stopPreview();
				previewing = false;
			}

			try {
				mCamera.setPreviewDisplay(arg0);
				mCamera.startPreview();
				previewing = true;
				setCameraDisplayOrientation(CameraActivity.this,
						mCurrentCamIndex, mCamera);
			} catch (Exception e) {
			}
		}

		public void surfaceCreated(SurfaceHolder holder) {
			mCamera = openFrontFacingCameraGingerbread();

		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
			previewing = false;
		}
	}

	// 0表示后置，1表示前置
	private int cameraPosition = 1;

	private Camera openFrontFacingCameraGingerbread() {
		int cameraCount = 0;
		Camera cam = null;
		CameraInfo cameraInfo = new CameraInfo();
		cameraCount = Camera.getNumberOfCameras();// 得到摄像头的个数
		 if (cameraCount > 1) {
		 cameraPosition = 1;
		 } else {
		 cameraPosition = 0;
		 }
		for (int i = 0; i < cameraCount; i++) {
			Camera.getCameraInfo(i, cameraInfo);// 得到每一个摄像头的信息
			if (cameraPosition == 1) {
				// 现在是后置，变更为前置
				if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
					cam = Camera.open(i);
					break;
				}
			} else {
				// 现在是前置， 变更为后置
				if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
					cam = Camera.open(i);
					break;
				}
			}

		}

		return cam;
	}

	// 根据横竖屏自动调节preview方向
	private static void setCameraDisplayOrientation(Activity activity,
			int cameraId, Camera camera) {
		Camera.CameraInfo info = new Camera.CameraInfo();
		Camera.getCameraInfo(cameraId, info);
		int rotation = activity.getWindowManager().getDefaultDisplay()
				.getRotation();

		int degrees = 0;
		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}
		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360;
		} else {

			result = (info.orientation - degrees + 360) % 360;
		}
		camera.setDisplayOrientation(result);

	}

	/**
	 * 保存合成后的图片
	 * 
	 * @param context
	 * @param bmp
	 */
	public void saveImageToGallery(Context context, Bitmap bmp) {
		// 首先保存图片
		String fileName = Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_DCIM).toString()
				+ File.separator + System.currentTimeMillis() + ".jpg";
		File file = new File(fileName);

		try {
			FileOutputStream fos = new FileOutputStream(file);
			bmp.compress(CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 其次把文件插入到系统图库
		try {
			MediaStore.Images.Media.insertImage(context.getContentResolver(),
					file.getAbsolutePath(), fileName, null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// 最后通知图库更新
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
				Uri.parse(file.getAbsolutePath())));
		imagUrl = file.getAbsolutePath();
	}

}

package com.example.sooglejay.scannews.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.tedcoder.wkvideoplayer.dlna.engine.DLNAContainer;
import com.android.tedcoder.wkvideoplayer.dlna.service.DLNAService;
import com.android.tedcoder.wkvideoplayer.model.Video;
import com.android.tedcoder.wkvideoplayer.model.VideoUrl;
import com.android.tedcoder.wkvideoplayer.util.DensityUtil;
import com.android.tedcoder.wkvideoplayer.view.MediaController;
import com.android.tedcoder.wkvideoplayer.view.SuperVideoPlayer;
import com.example.sooglejay.scannews.R;
import com.example.sooglejay.scannews.constant.Constant;
import com.example.sooglejay.scannews.util.PicShrink;
import com.qq.wx.img.imgsearcher.ImgListener;
import com.qq.wx.img.imgsearcher.ImgResult;
import com.qq.wx.img.imgsearcher.ImgSearcher;
import com.qq.wx.img.imgsearcher.ImgSearcherState;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class VideoTestActivity extends Activity implements View.OnClickListener, SurfaceHolder.Callback, Camera.PreviewCallback, ImgListener {

    Camera camera;
    SurfaceView mPreView;
    private Camera.AutoFocusCallback mAutoFocusCallback = new AutoFocusCallback();// AutoFocusCallback自动对焦的回调对象

    private SuperVideoPlayer mSuperVideoPlayer;
    private View mPlayBtnView;

    private int mInitSucc;
    private String mResMD5;

    private FrameLayout layout;

    private String mp4Online = "https://pan.baidu.com/play/video#video/path=%2Fv.mp4&t=-1";
    private String mp4Local = "android.resource://com.example.sooglejay.scannews/" + R.raw.v;
    private SuperVideoPlayer.VideoPlayCallbackImpl mVideoPlayCallback = new SuperVideoPlayer.VideoPlayCallbackImpl() {
        @Override
        public void onCloseVideo() {
            mSuperVideoPlayer.close();
            mPlayBtnView.setVisibility(View.VISIBLE);
            mSuperVideoPlayer.setVisibility(View.GONE);
            resetPageToPortrait();

            mSuperVideoPlayer.pausePlay(false);
            layout.setVisibility(View.GONE);
        }

        @Override
        public void onSwitchPageType() {
            if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                mSuperVideoPlayer.setPageType(MediaController.PageType.SHRINK);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                mSuperVideoPlayer.setPageType(MediaController.PageType.EXPAND);
            }
        }

        @Override
        public void onPlayFinish() {

        }
    };

    @Override
    public void onClick(View view) {
        prepareVideo1();

    }


    private void prepareVideo1() {
        mPlayBtnView.setVisibility(View.GONE);
        mSuperVideoPlayer.setVisibility(View.VISIBLE);
        mSuperVideoPlayer.setAutoHideController(false);

        Video video = new Video();
        VideoUrl videoUrl1 = new VideoUrl();
        videoUrl1.setFormatName("720P");
        videoUrl1.setFormatUrl(mp4Local);
        videoUrl1.setIsOnlineVideo(false);

        VideoUrl videoUrl2 = new VideoUrl();
        videoUrl2.setFormatName("480P");
        videoUrl2.setIsOnlineVideo(false);
        videoUrl2.setFormatUrl(mp4Local);

        ArrayList<VideoUrl> arrayList1 = new ArrayList<>();
        arrayList1.add(videoUrl1);
        arrayList1.add(videoUrl2);
        video.setVideoName("测试视频一");
        video.setVideoUrl(arrayList1);
        ArrayList<Video> videoArrayList = new ArrayList<>();
        videoArrayList.add(video);
        mSuperVideoPlayer.loadMultipleVideo(videoArrayList, 0, 0, 0);

    }


    @Override
    protected void onPause() {
        destroyCamera();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopDLNAService();
    }


    /* 停止相机的method */
    private void destroyCamera() {
        if (camera != null) {
            try {
                /* 停止预览 */
                camera.stopPreview();
                camera.release();
                camera = null;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                System.gc();
            }
        }

    }


    //控制图像的正确显示方向
    private void setDispaly(Camera.Parameters parameters, Camera camera) {
        if (Integer.parseInt(Build.VERSION.SDK) >= 8) {
            setDisplayOrientation(camera, 90);
        } else {
            parameters.setRotation(90);
        }

    }

    //实现的图像的正确显示
    private void setDisplayOrientation(Camera camera, int i) {
        Method downPolymorphic;
        try {
            downPolymorphic = camera.getClass().getMethod("setDisplayOrientation", new Class[]{int.class});
            if (downPolymorphic != null) {
                downPolymorphic.invoke(camera, new Object[]{i});
            }
        } catch (Exception e) {
            Log.e("Came_e", "图像出错");
        }
    }


    /***
     * 旋转屏幕之后回调
     *
     * @param newConfig newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Toast.makeText(VideoTestActivity.this, "进入回调  ！", Toast.LENGTH_SHORT).show();

        if (null == mSuperVideoPlayer) return;
        Toast.makeText(VideoTestActivity.this, "离开回调  ！", Toast.LENGTH_SHORT).show();

        /***
         * 根据屏幕方向重新设置播放器的大小
         */
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().getDecorView().invalidate();

            float height = DensityUtil.getWidthInPx(this);
            float width = DensityUtil.getHeightInPx(this);
            mSuperVideoPlayer.getLayoutParams().height = (int) width - 100;
            mSuperVideoPlayer.getLayoutParams().width = (int) height;

        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(VideoTestActivity.this, "portait  ！", Toast.LENGTH_SHORT).show();
            final WindowManager.LayoutParams attrs = getWindow().getAttributes();
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attrs);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            float width = DensityUtil.getWidthInPx(this);
            float height = DensityUtil.dip2px(this, 200.f);
            mSuperVideoPlayer.getLayoutParams().height = (int) height;
            mSuperVideoPlayer.getLayoutParams().width = (int) width;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_test_layout);

        layout = (FrameLayout) findViewById(R.id.layout);
        mPreView = (SurfaceView) findViewById(R.id.mSurfaceView);
        mPreView.getHolder().addCallback(this);
        mPreView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


        mSuperVideoPlayer = (SuperVideoPlayer) findViewById(R.id.video_player_item_1);
        mPlayBtnView = findViewById(R.id.play_btn);
        mPlayBtnView.setOnClickListener(this);
        mSuperVideoPlayer.setVideoPlayCallback(mVideoPlayCallback);
        mSuperVideoPlayer.setExpandAndShrinkCallBack(new SuperVideoPlayer.ExpandAndShrinkCallBack() {

            @Override
            public void toExpand() {
//                Toast.makeText(VideoTestActivity.this,"放大",Toast.LENGTH_LONG).show();
            }

            @Override
            public void toShrink() {
//                Toast.makeText(VideoTestActivity.this,"suoxiao ",Toast.LENGTH_LONG).show();
//
            }
        });
        startDLNAService();
        //开始播放
        mPlayBtnView.performClick();

        preInitImg();
    }


    private void preInitImg() {
        ImgSearcher.shareInstance().setListener(this);
        mInitSucc = ImgSearcher.shareInstance().init(this, Constant.AppSecret);

        if (mInitSucc != 0) {
            Toast.makeText(this, "初始化失败",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private int startImgSearching(byte[] img) {
        if (mInitSucc != 0) {
            mInitSucc = ImgSearcher.shareInstance().init(this, Constant.AppSecret);
        }
        if (mInitSucc != 0) {
            Toast.makeText(this, "初始化失败",
                    Toast.LENGTH_SHORT).show();
            return -1;
        }

        int ret = ImgSearcher.shareInstance().start(img, Constant.IMG);
        if (0 == ret) {
            return 0;
        } else {
            Toast.makeText(this, "ErrorCode = " + ret, Toast.LENGTH_LONG).show();
            return -1;
        }
    }


    /***
     * 恢复屏幕至竖屏
     */
    private void resetPageToPortrait() {
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mSuperVideoPlayer.setPageType(MediaController.PageType.SHRINK);
        }
    }

    private void startDLNAService() {
        // Clear the device container.
        DLNAContainer.getInstance().clear();
        Intent intent = new Intent(getApplicationContext(), DLNAService.class);
        startService(intent);
    }

    private void stopDLNAService() {
        Intent intent = new Intent(getApplicationContext(), DLNAService.class);
        stopService(intent);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        // TODO Auto-generated method stub
        if (null == camera) {
            camera = Camera.open();
            try {
                camera.setPreviewDisplay(surfaceHolder);
                initCamera();
                camera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void initCamera() {
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPictureFormat(PixelFormat.JPEG);
        //闪光灯
//        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//1连续对焦

        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        Camera.Size selected = sizes.get(0);
        parameters.setPreviewSize(selected.width, selected.height);
        setDispaly(parameters, camera);


        camera.setParameters(parameters);
        camera.autoFocus(mAutoFocusCallback);
        camera.startPreview();

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {

        //开始图像处理和识别
        startImgSearching(PicShrink.compressBytes(bytes));
    }

    @Override
    public void onGetResult(ImgResult imgResult) {
        if (imgResult != null) {
            if (1 == imgResult.ret && imgResult.res != null) {
                int resSize = imgResult.res.size();
                for (int i = 0; i < resSize; ++i) {
                    ImgResult.Result res = (ImgResult.Result) imgResult.res.get(i);
                    if (res != null) {
                        mResMD5 = res.md5;
//                        mResUrl = res.url;
//                        mResPicDesc = res.picDesc;
                    }
                }
            }
        }
        if (!TextUtils.isEmpty(mResMD5)) {
            layout.setVisibility(View.VISIBLE);
            mSuperVideoPlayer.goOnPlay();
        } else {
            mSuperVideoPlayer.pausePlay(true);
            layout.setVisibility(View.GONE);
        }
        mResMD5 = "";
    }

    @Override
    public void onGetError(int i) {
        Toast.makeText(this, "ErrorCode = " + i, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onGetState(ImgSearcherState imgSearcherState) {

    }

    /* 自定义class AutoFocusCallback */
    private final class AutoFocusCallback implements Camera.AutoFocusCallback {

        @Override
        public void onAutoFocus(boolean b, Camera camera) {
            Log.e("jwjw", "come.....");
            if (b) {
                camera.setOneShotPreviewCallback(VideoTestActivity.this);
            } else {
                Log.e("jwjw", "none");
            }
        }
    }

    ;

}
package com.a403.mars;


import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.GLException;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GestureDetectorCompat;

import com.a403.mars.draw.Rendering.BackgroundRenderer;
import com.a403.mars.draw.Rendering.BiquadFilter;
import com.a403.mars.draw.Rendering.LineShaderRenderer;
import com.a403.mars.draw.Rendering.LineUtils;
import com.a403.mars.draw.Utils.DisplayRotationHelper;
import com.a403.mars.draw.Utils.PermissionsHelper;
import com.a403.mars.draw.Utils.Settings;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Camera;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.IntBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import top.defaults.colorpicker.ColorPickerPopup;

public class DrawActivity extends AppCompatActivity implements GLSurfaceView.Renderer,
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener {

    private String TAG = DrawActivity.class.getSimpleName();
    private GLSurfaceView surfaceView;
    private Session session;
    private boolean installRequested;
    private DisplayRotationHelper displayRotationHelper;
    private boolean paused = false;
    private float screenWidth = 0;
    private float screenHeight = 0;
    private BackgroundRenderer backgroundRenderer = new BackgroundRenderer();
    private Frame frame;
    private AtomicBoolean isTracking = new AtomicBoolean(true);
    private float[] zeroMatrix = new float[16];
    private Vector3f lastPoint;
    private GestureDetectorCompat detector;
    private ArrayList<ArrayList<Vector3f>> strokes;
    private float[] projmtx = new float[16];
    private float[] viewmtx = new float[16];
    private LineShaderRenderer lineShaderRenderer = new LineShaderRenderer();
    private AtomicBoolean touchDown = new AtomicBoolean(false);
    private float[] lastFramePosition;
    private AtomicBoolean newStroke = new AtomicBoolean(false);
    private AtomicReference<Vector2f> lastTouch = new AtomicReference<>();
    private AtomicBoolean reCenterView = new AtomicBoolean(false);
    private AtomicBoolean lineParameters = new AtomicBoolean(false);

    private BiquadFilter biquadFilter;

    private FloatingActionButton fab_main, fab_color, fab_line, fab_clear, fab_undo;
    private Animation fab_open, fab_close, fab_clock, fab_anticlock;

    private AtomicBoolean clearDrawing = new AtomicBoolean(false);
    private AtomicBoolean undoMove = new AtomicBoolean(false);

    Boolean isOpen = false;

    private SeekBar lineWidthBar;

    private float lineWidthMax = 0.33f;

    // CreateCapsuleActivity로 보낼 이미지 Uri
    String imgPath = "";

    // 화면 촬영 버튼
    ImageView camera_btn;
    private volatile boolean saveFrame;

    // 갤러리에서 사진 불러오기, 스킵버튼
    private final int GET_GALLERY_IMAGE = 200;
    ImageView gallery_btn, skip_btn;

    // 촬영 후, 사진 미리보기
    ConstraintLayout captureLayout, cameraLayout;
    ImageView captureImg, useBtn, recaptureBtn;
    String imageFileName, mCurrentPhotoPath;
    RelativeLayout brushLayout;
    Context mContext;

    String jwt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow()
                .setFlags(
                        WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_draw);

//        Objects.requireNonNull(getSupportActionBar()).hide();
        Intent getIntent = getIntent();
        jwt = getIntent.getExtras().getString("jwt");

        mContext = this;

        surfaceView = findViewById(R.id.surfaceview);
        captureLayout = (ConstraintLayout) findViewById(R.id.captureLayout);
        cameraLayout = (ConstraintLayout) findViewById(R.id.content_camera);
        brushLayout = (RelativeLayout) findViewById(R.id.content_brush);
        captureImg = (ImageView) findViewById(R.id.captureImg);

        final SharedPreferences sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);

        displayRotationHelper = new DisplayRotationHelper(this);
        Matrix.setIdentityM(zeroMatrix, 0);

        lastPoint = new Vector3f(0, 0, 0);
        installRequested = false;

        surfaceView.setPreserveEGLContextOnPause(true);
        surfaceView.setEGLContextClientVersion(2);
        surfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        surfaceView.setRenderer(this);
        surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        detector = new GestureDetectorCompat(this, this);
        detector.setOnDoubleTapListener(this);
        strokes = new ArrayList<>();

        fab_main = findViewById(R.id.fab);
        fab_color = findViewById(R.id.color);
        fab_line = findViewById(R.id.line);
        fab_clear = findViewById(R.id.clear);
        fab_undo = findViewById(R.id.undo);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_clock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_clock);
        fab_anticlock =
                AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_anitclock);

        fab_main.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (isOpen) {
                            fab_clear.startAnimation(fab_close);
                            fab_undo.startAnimation(fab_close);
                            fab_line.startAnimation(fab_close);
                            fab_color.startAnimation(fab_close);
                            fab_main.startAnimation(fab_anticlock);
                            fab_clear.setClickable(false);
                            fab_line.setClickable(false);
                            fab_color.setClickable(false);
                            fab_undo.setClickable(false);
                            isOpen = false;
                        } else {
                            fab_clear.startAnimation(fab_open);
                            fab_undo.startAnimation(fab_open);
                            fab_line.startAnimation(fab_open);
                            fab_color.startAnimation(fab_open);
                            fab_main.startAnimation(fab_clock);
                            fab_clear.setClickable(true);
                            fab_line.setClickable(true);
                            fab_color.setClickable(true);
                            fab_undo.setClickable(true);
                            isOpen = true;
                        }
                    }
                });

        fab_line.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Dialog dialog = new Dialog(DrawActivity.this);
                        dialog.setContentView(R.layout.dialog_line);
                        dialog.show();

                        lineWidthBar = dialog.findViewById(R.id.lineWidth);

                        lineWidthBar.setProgress(sharedPreferences.getInt("LineWidth", 10));

                        lineWidthMax =
                                LineUtils.map((float) lineWidthBar.getProgress(), 0f, 100f, 0.1f, 5f, true);

                        SeekBar.OnSeekBarChangeListener seekBarChangeListener =
                                new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        SharedPreferences.Editor editor = sharedPreferences.edit();

                                        if (seekBar == lineWidthBar) {
                                            editor.putInt("LineWidth", progress);
                                            lineWidthMax = LineUtils.map((float) progress, 0f, 100f, 0.1f, 5f, true);
                                        }
                                        lineShaderRenderer.bNeedsUpdate.set(true);

                                        editor.apply();
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {
                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                    }
                                };

                        lineWidthBar.setOnSeekBarChangeListener(seekBarChangeListener);
                    }
                });

        fab_color.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new ColorPickerPopup.Builder(getBaseContext())
                                .initialColor(Color.WHITE)
                                .enableBrightness(true)
                                .enableAlpha(true)
                                .okTitle("Choose")
                                .cancelTitle(null)
                                .showIndicator(true)
                                .showValue(false)
                                .build()
                                .show(
                                        view,
                                        new ColorPickerPopup.ColorPickerObserver() {
                                            @Override
                                            public void onColorPicked(int color) {
                                                Vector3f curColor =
                                                        new Vector3f(
                                                                Color.red(color) / 255f,
                                                                Color.green(color) / 255f,
                                                                Color.blue(color) / 255f);
                                                Settings.setColor(curColor);
                                            }
                                        });
                    }
                });

        fab_clear.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clearDrawing.set(true);
                    }
                });

        fab_undo.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        undoMove.set(true);
                    }
                });

        // 카메라 촬영 버튼
        // http://blog.naver.com/PostView.nhn?blogId=miraclehwan&logNo=220611579792&redirect=Dlog&widgetTypeCall=true
        // https://stackoverflow.com/questions/21753519/display-black-screen-while-capture-screenshot-of-glsurfaceview
        camera_btn = findViewById(R.id.camera_btn);
        camera_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFrame = true;
                surfaceView.requestRender();
                Toast.makeText(DrawActivity.this, "촬영 완료. 갤러리를 확인해주세요 (폴더명 - mars)", Toast.LENGTH_SHORT).show();

                // 촬영한 이미지 미리보기
                brushLayout.setVisibility(View.INVISIBLE);
                cameraLayout.setVisibility(View.INVISIBLE);
                captureLayout.setVisibility(View.VISIBLE);
//                captureImg.setImageURI(Uri.parse(mCurrentPhotoPath + "/" + imageFileName + ".png"));    // 사진 미리보기로 보여주기
                surfaceView.setVisibility(View.INVISIBLE);

                Log.d("이미지경로", mCurrentPhotoPath + "/" + imageFileName + ".png");
            }
        });

        // 갤러리에서 사진 불러오기 버튼
        gallery_btn = (ImageView) findViewById(R.id.gallery_btn);
        gallery_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
                brushLayout.setVisibility(View.INVISIBLE);
                cameraLayout.setVisibility(View.INVISIBLE);
                captureLayout.setVisibility(View.VISIBLE);
                surfaceView.setVisibility(View.INVISIBLE);
            }
        });

        skip_btn = (ImageView) findViewById(R.id.skip_btn);
        skip_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 캡슐 내용 작성 페이지로 이동
                Intent intent = new Intent(DrawActivity.this, CreateCapsuleActivity.class);
                intent.putExtra("isSkip", true);
                intent.putExtra("jwt", jwt);
                startActivity(intent);
                finish();
            }
        });

        // 사진 미리보기 레이아웃의 "다시찍기 버튼"
        recaptureBtn = (ImageView) findViewById(R.id.reCapture);
        recaptureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 다시 카메라 화면 보이도록
                captureLayout.setVisibility(View.INVISIBLE);
                brushLayout.setVisibility(View.VISIBLE);
                cameraLayout.setVisibility(View.VISIBLE);
                surfaceView.setVisibility(View.VISIBLE);
            }
        });

        // 사진 미리보기 레이아웃의 "사용하기 버튼"
        useBtn = (ImageView) findViewById(R.id.useBtn);
        useBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 캡슐 내용 작성 페이지로 이동
                Intent intent = new Intent(DrawActivity.this, CreateCapsuleActivity.class);
                intent.putExtra("isSkip", false);
                intent.putExtra("imageURI", imgPath);
                intent.putExtra("jwt", jwt);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            captureImg.setImageURI(selectedImageUri);
            imgPath = selectedImageUri.toString();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (session == null) {
            Exception exception = null;
            try {
                switch (ArCoreApk.getInstance().requestInstall(this, !installRequested)) {
                    case INSTALL_REQUESTED:
                        installRequested = true;
                        return;
                    case INSTALLED:
                        break;
                }

                if (!PermissionsHelper.hasCameraPermission(this)) {
                    PermissionsHelper.requestCameraPermission(this);
                    return;
                }

                session = new Session(this);
            } catch (Exception e) {
                exception = e;
            }

            assert session != null;
            Config config = new Config(session);
            if (!session.isSupported(config)) {
                Log.e(TAG, "Exception creating session Device Does Not Support ARCore", exception);
            }
            session.configure(config);
        }
        try {
            session.resume();
        } catch (CameraNotAvailableException e) {
            e.printStackTrace();
        }
        surfaceView.onResume();
        displayRotationHelper.onResume();
        paused = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (session != null) {
            displayRotationHelper.onPause();
            surfaceView.onPause();
            session.pause();
        }

        paused = true;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;
    }

    private void update() {

        if (session == null) {
            return;
        }

        displayRotationHelper.updateSessionIfNeeded(session);

        try {

            session.setCameraTextureName(backgroundRenderer.getTextureId());

            frame = session.update();
            Camera camera = frame.getCamera();

            TrackingState state = camera.getTrackingState();

            if (state == TrackingState.TRACKING && !isTracking.get()) {
                isTracking.set(true);
            } else if (state == TrackingState.STOPPED && isTracking.get()) {
                isTracking.set(false);
                touchDown.set(false);
            }

            camera.getProjectionMatrix(projmtx, 0, Settings.getNearClip(), Settings.getFarClip());
            camera.getViewMatrix(viewmtx, 0);

            float[] position = new float[3];
            camera.getPose().getTranslation(position, 0);

            if (lastFramePosition != null) {
                Vector3f distance = new Vector3f(position[0], position[1], position[2]);
                distance.sub(
                        new Vector3f(lastFramePosition[0], lastFramePosition[1], lastFramePosition[2]));

                if (distance.length() > 0.15) {
                    touchDown.set(false);
                }
            }
            lastFramePosition = position;

            Matrix.multiplyMM(viewmtx, 0, viewmtx, 0, zeroMatrix, 0);

            if (newStroke.get()) {
                newStroke.set(false);
                addStroke(lastTouch.get());
                lineShaderRenderer.bNeedsUpdate.set(true);
            } else if (touchDown.get()) {
                addPoint(lastTouch.get());
                lineShaderRenderer.bNeedsUpdate.set(true);
            }

            if (reCenterView.get()) {
                reCenterView.set(false);
                zeroMatrix = getCalibrationMatrix();
            }

            if (undoMove.get()) {
                undoMove.set(false);
                if (strokes.size() > 0) {
                    strokes.remove(strokes.size() - 1);
                    lineShaderRenderer.bNeedsUpdate.set(true);
                }
            }

            if (clearDrawing.get()) {
                clearDrawing.set(false);
                clearScreen();
                lineShaderRenderer.bNeedsUpdate.set(true);
            }

            lineShaderRenderer.setDrawDebug(lineParameters.get());
            if (lineShaderRenderer.bNeedsUpdate.get()) {
                lineShaderRenderer.setColor(Settings.getColor());
                lineShaderRenderer.mDrawDistance = Settings.getStrokeDrawDistance();
                float distanceScale = 0.0f;
                lineShaderRenderer.setDistanceScale(distanceScale);
                lineShaderRenderer.setLineWidth(lineWidthMax);
                lineShaderRenderer.clear();
                lineShaderRenderer.updateStrokes(strokes);
                lineShaderRenderer.upload();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearScreen() {
        strokes.clear();
        lineShaderRenderer.clear();
    }

    private void addPoint(Vector2f touchPoint) {
        Vector3f newPoint =
                LineUtils.GetWorldCoords(touchPoint, screenWidth, screenHeight, projmtx, viewmtx);
        addPoint(newPoint);
    }

    private void addStroke(Vector3f newPoint) {
        float lineSmoothing = 0.1f;
        biquadFilter = new BiquadFilter(lineSmoothing);
        for (int i = 0; i < 1500; i++) {
            biquadFilter.update(newPoint);
        }
        Vector3f p = biquadFilter.update(newPoint);
        lastPoint = new Vector3f(p);
        strokes.add(new ArrayList<Vector3f>());
        strokes.get(strokes.size() - 1).add(lastPoint);
    }

    private void addPoint(Vector3f newPoint) {
        if (LineUtils.distanceCheck(newPoint, lastPoint)) {
            Vector3f p = biquadFilter.update(newPoint);
            lastPoint = new Vector3f(p);
            strokes.get(strokes.size() - 1).add(lastPoint);
        }
    }

    public float[] getCalibrationMatrix() {
        float[] t = new float[3];
        float[] m = new float[16];

        frame.getCamera().getPose().getTranslation(t, 0);
        float[] z = frame.getCamera().getPose().getZAxis();
        Vector3f zAxis = new Vector3f(z[0], z[1], z[2]);
        zAxis.y = 0;
        zAxis.normalize();

        double rotate = Math.atan2(zAxis.x, zAxis.z);

        Matrix.setIdentityM(m, 0);
        Matrix.translateM(m, 0, t[0], t[1], t[2]);
        Matrix.rotateM(m, 0, (float) Math.toDegrees(rotate), 0, 1, 0);
        return m;
    }

    private void addStroke(Vector2f touchPoint) {
        Vector3f newPoint =
                LineUtils.GetWorldCoords(touchPoint, screenWidth, screenHeight, projmtx, viewmtx);
        addStroke(newPoint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent tap) {
        this.detector.onTouchEvent(tap);

        if (tap.getAction() == MotionEvent.ACTION_DOWN) {
            lastTouch.set(new Vector2f(tap.getX(), tap.getY()));
            touchDown.set(true);
            newStroke.set(true);
            return true;
        } else if (tap.getAction() == MotionEvent.ACTION_MOVE
                || tap.getAction() == MotionEvent.ACTION_POINTER_DOWN) {
            lastTouch.set(new Vector2f(tap.getX(), tap.getY()));
            touchDown.set(true);
            return true;
        } else if (tap.getAction() == MotionEvent.ACTION_UP
                || tap.getAction() == MotionEvent.ACTION_CANCEL) {
            touchDown.set(false);
            lastTouch.set(new Vector2f(tap.getX(), tap.getY()));
            return true;
        }

        return super.onTouchEvent(tap);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public void onBackPressed() {
        // 캡슐 찾기 페이지로 이동
        Intent intent = new Intent(DrawActivity.this, ArActivity.class);
        intent.putExtra("jwt", jwt);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (!PermissionsHelper.hasCameraPermission(this)) {
            Toast.makeText(this, "Camera permission is needed", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        if (session == null) {
            return;
        }

        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

        backgroundRenderer.createOnGlThread(this);

        try {

            session.setCameraTextureName(backgroundRenderer.getTextureId());
            lineShaderRenderer.createOnGlThread(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        displayRotationHelper.onSurfaceChanged(width, height);
        screenWidth = width;
        screenHeight = height;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (paused) return;

        update();

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        if (frame == null) {
            return;
        }

        backgroundRenderer.draw(frame);

        if (frame.getCamera().getTrackingState() == TrackingState.TRACKING) {
            lineShaderRenderer.draw(
                    viewmtx,
                    projmtx,
                    screenWidth,
                    screenHeight,
                    Settings.getNearClip(),
                    Settings.getFarClip());
        }

        // 현재 화면 촬영 후 저장하러가기업로드 키를 분실해서 재설정 하고 싶습니다. upload_certificate.pem 파일을 첨부하니 확인해주세요.
        if (saveFrame) {
//            saveBitmap(takeScreenshot(gl));
            try {
                saveImage(takeScreenshot(gl));
            } catch (IOException e) {
                e.printStackTrace();
            }
            saveFrame = false;
        }
    }

    // 현재 화면 촬영 후, 저장하는 메서드
    private void saveBitmap(Bitmap bitmap) {

        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mars";
        mCurrentPhotoPath = new File(Environment.getExternalStorageDirectory(), "mars").getAbsolutePath();

        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
            Toast.makeText(DrawActivity.this, "폴더가 생성되었습니다.", Toast.LENGTH_SHORT).show();
        }
        Date date = new Date();
        String day = new SimpleDateFormat("yyyyMMddHHmmss").format(date);
        imageFileName = "Mars" + day;

        try {
            FileOutputStream stream = new FileOutputStream(path + "/Mars" + day + ".png");
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path + "/" + imageFileName + ".PNG")));
            stream.flush();
            stream.close();
            Log.i("TAG", "SAVED");
        } catch (Exception e) {
            Log.e("TAG", e.toString(), e);
        }
        captureImg.setImageURI(Uri.parse(mCurrentPhotoPath + "/" + imageFileName + ".png"));    // 사진 미리보기로 보여주기
        imgPath = mCurrentPhotoPath + "/" + imageFileName + ".png";
    }

    // API 29버전에서 bitmap저장하는 방법
    // https://stackoverflow.com/questions/63776744/save-bitmap-image-to-specific-location-of-gallery-android-10
    private void saveImage(Bitmap bitmap) throws IOException {
        boolean saved;
        OutputStream fos;

        Date date = new Date();
        String day = new SimpleDateFormat("yyyyMMddHHmmss").format(date);
        imageFileName = "Mars" + day;
        mCurrentPhotoPath = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM).getPath() + "/mars";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = mContext.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, imageFileName);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/" + "mars");
            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            fos = resolver.openOutputStream(imageUri);
        } else {
            String imagesDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM).toString() + File.separator + "mars";

            File file = new File(imagesDir);

            if (!file.exists()) {
                file.mkdir();
            }

            File image = new File(imagesDir, imageFileName + ".png");
            fos = new FileOutputStream(image);

        }

        saved = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.flush();
        fos.close();

        captureImg.setImageURI(Uri.parse(mCurrentPhotoPath + "/" + imageFileName + ".png"));    // 사진 미리보기로 보여주기
        imgPath = mCurrentPhotoPath + "/" + imageFileName + ".png";
    }

    // 현재 화면 스크린샷 찍는 메서드
    public Bitmap takeScreenshot(GL10 mGL) {
        final int mWidth = surfaceView.getWidth();
        final int mHeight = surfaceView.getHeight();
        IntBuffer ib = IntBuffer.allocate(mWidth * mHeight);
        IntBuffer ibt = IntBuffer.allocate(mWidth * mHeight);
        mGL.glReadPixels(0, 0, mWidth, mHeight, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, ib);

        // Convert upside down mirror-reversed image to right-side up normal image.
        for (int i = 0; i < mHeight; i++) {
            for (int j = 0; j < mWidth; j++) {
                ibt.put((mHeight - i - 1) * mWidth + j, ib.get(i * mWidth + j));
            }
        }

        Bitmap mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        mBitmap.copyPixelsFromBuffer(ibt);
        return mBitmap;
    }
}
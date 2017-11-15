package com.example.eunyoungha.r_multi_note;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreateMemoActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    private GoogleApiClient mGoogleApiClient = null;
    private GoogleMap mGoogleMap = null;
    private Marker currentMarker = null;

    private RelativeLayout mapLayout;

    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초

    private AppCompatActivity mActivity;
    boolean askPermissionOnceAgain = false;
    boolean mRequestingLocationUpdates = false;
    Location mCurrentLocatiion;
    boolean mMoveMapByUser = true;
    boolean mMoveMapByAPI = true;

    LocationRequest locationRequest = new LocationRequest()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(UPDATE_INTERVAL_MS)
            .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);

    private static final int REQUEST_TAKE_PHOTO = 1001;
    private static final int REQUEST_PHOTO_LIBRARY = 1002;
    private static final int REQUEST_TAKE_VIDEO = 1003;
    private static final int REQUEST_VIDEO_LIBRARY = 1004;
    private static final int REQUEST_RECORD_VOICE = 1005;

    private static final int MEDIA_TYPE_PHOTO = 1;
    private static final int MEDIA_TYPE_VIDEO = 2;
    private static final int MEDIA_TYPE_VOICE = 3;

    private static final int MAP_LATITUDE = 1;
    private static final int MAP_LONGITUDE = 2;

    private static final int NEW_MAP = 1;
    private static final int CALL_MAP = 2;

    private Toolbar toolbar;
    private RelativeLayout mMemoTitle;
    private RelativeLayout mBackButton;
    private RelativeLayout mDoneButton;
    private EditText textEditView;
    private ImageView mInsertPhotoButton;
    private ImageView mInsertVideoButton;
    private ImageView mInsertVoiceButton;
    private ImageView mInsertMapButton;
    private RelativeLayout mMultiMemoView;
    private ImageView mPhoto;
    private VideoView mVideo;
    private RelativeLayout mMemoView;
    private RelativeLayout mVoice;
    private Button mPlayButton;

    private DatabaseHelper dbHelper;
    private DatabaseHelper dbHelperPhoto;
    private DatabaseHelper dbHelperVideo;
    private DatabaseHelper dbHelperVoice;
    private DatabaseHelper dbHelperMap;

    private MemoList memo;

    private Uri mImageUri;
    private Uri mVideoUri;
    private Uri mVoiceUri;
    private File file =  null;
    private File videoFile = null;
    private String voicePath;

    private boolean mapFlag = false;
    private double mLatitude;
    private double mLongitude;
    private String mMarkerTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_create_memo);

        mActivity = this;

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        dbHelper = new DatabaseHelper(getApplicationContext(), "memo.db", null, 1);
        dbHelperPhoto = new DatabaseHelper(getApplicationContext(),"photo.db",null,1);
        dbHelperVideo = new DatabaseHelper(getApplicationContext(),"video.db",null,1);
        dbHelperVoice = new DatabaseHelper(getApplicationContext(),"voice.db",null,1);
        dbHelperMap = new DatabaseHelper(getApplicationContext(),"map.db",null,1);

        //툴바 세팅
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //툴바 타이틀 세팅
        mMemoTitle = (RelativeLayout) findViewById(R.id.layout_toolbar_new_memo_title);
        mMemoTitle.setVisibility(View.VISIBLE);

        //메모 내용이 들어가는 뷰 초기화
        mMemoView = (RelativeLayout) findViewById(R.id.memo_text_view);
        textEditView = (EditText) findViewById(R.id.memo_text_edit);

        //사진, 비디오, 보이스, 맵 등이 들어가는 레이아웃
        mMultiMemoView = (RelativeLayout) findViewById(R.id.memo_multi_view);
        mPhoto = (ImageView) findViewById(R.id.memo_multi_view_photo);
        mVideo = (VideoView) findViewById(R.id.memo_multi_view_video);
        mapLayout = (RelativeLayout) findViewById(R.id.map_layout);
        mVoice = (RelativeLayout) findViewById(R.id.voice_layout);
        mPlayButton = (Button) findViewById(R.id.play_button);

        //메모 화면 하단의 버튼들
        LinearLayout multiButtons = (LinearLayout) findViewById(R.id.layout_multi_bar);
        mInsertPhotoButton = (ImageView) findViewById(R.id.multi_photo_button);
        mInsertVideoButton = (ImageView) findViewById(R.id.multi_video_button);
        mInsertVoiceButton = (ImageView) findViewById(R.id.multi_voice_button);
        mInsertMapButton = (ImageView) findViewById(R.id.multi_map_button);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //툴바 백버튼 아이콘 세팅
        mBackButton = (RelativeLayout) findViewById(R.id.layout_toolbar_back_button);
        mBackButton.setVisibility(View.VISIBLE);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listIntent();
            }
        });

        //새로운 메모 작성 시의 툴바 Done 버튼 세팅
        mDoneButton = (RelativeLayout) findViewById(R.id.layout_toolbar_done_button);
        mDoneButton.setVisibility(View.VISIBLE);
        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = currentDate();
                String content = textEditView.getText().toString();

                if(content.length() != 0){
                    int photoId = -1;
                    int videoId = -1;
                    int voiceId = -1;
                    int mapId = -1;
                    if(mImageUri != null){
                        String photoUri = mImageUri.toString();
                        dbHelperPhoto.photoInsert(date, photoUri);
                        photoId = dbHelperPhoto.getPhotoId(photoUri);
                        //dbHelper.insert(date,content,photoId,videoId,voiceId);
                    }
                    if(mVideoUri != null){
                        String videoUri = mVideoUri.toString();
                        dbHelperVideo.videoInsert(date,videoUri);
                        videoId = dbHelperVideo.getVideoId(videoUri);
                        //dbHelper.insert(date,content,photoId,videoId,voiceId);
                    }
                    if(mVoiceUri != null){
                        String voiceUri = mVoiceUri.toString();
                        dbHelperVoice.voiceInsert(date,voiceUri);
                        voiceId = dbHelperVoice.getVoiceId(voiceUri);
                        //dbHelper.insert(date,content,photoId,videoId,voiceId);
                    }
                    if(mLatitude != 0 && mLongitude != 0){
                        String realTime = "time:"+String.valueOf(System.currentTimeMillis());
                        dbHelperMap.mapInsert(date,mMarkerTitle,mLatitude,mLongitude,realTime);
                        mapId = dbHelperMap.geMapId(realTime);
                    }
                    dbHelper.insert(date,content,photoId,videoId,voiceId,mapId);
                }
                listIntent();
            }
        });

        //이미 작성된 메모를 클릭했을 때 화면 및 Done 버튼 세팅
        if(getIntent().getBundleExtra("bundle") != null){
            memo = getIntent().getParcelableExtra("memo");

            int photoId = memo.getId_photo();
            int videoId = memo.getId_video();
            int voiceId = memo.getId_voice();
            int mapId = memo.getId_map();
            if(photoId != -1){
                mImageUri = getUri(MEDIA_TYPE_PHOTO, photoId);
                setPhoto();
            }else if(videoId != -1){
                mVideoUri = getUri(MEDIA_TYPE_VIDEO, videoId);
                setVideo();
            }else if(voiceId != -1){
                mVoiceUri = getUri(MEDIA_TYPE_VOICE,voiceId);
                setVoice();
            }
            if(mapId != -1){
                mLatitude = getLatLng(MAP_LATITUDE,mapId);
                mLongitude = getLatLng(MAP_LONGITUDE,mapId);
                setMap();
            }

            textEditView.setText(memo.getContent_text());
            textEditView.setSelection(textEditView.length());

            //Delete 버튼 생성
            ImageView mDeleteButton = new ImageView(getApplicationContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 50;
            mDeleteButton.setLayoutParams(params);
            mDeleteButton.setImageResource(R.drawable.icon_trash_can_button);
            multiButtons.addView(mDeleteButton);

            mDoneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = memo.getId();
                    String context_text = textEditView.getText().toString();

                    if(context_text.length() == 0){
                        dbHelper.delete(id);
                    } else {
                        dbHelper.update(id,context_text);
                    }

                    listIntent();
                }
            });

            //Delete 버튼을 눌렀을 때 동작
            mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = memo.getId();
                    dbHelper.delete(id);
                    listIntent();
                }
            });
        }

        //멀티바의 사진버튼을 눌렀을 때 동작
        mInsertPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhotoMethod();
            }
        });

        //멀티바의 비디오버튼을 눌렀을 때 동작
        mInsertVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectVideoMethod();
            }
        });

        //멀티바의 보이스버튼을 눌렀을 때 동작
        mInsertVoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
             startActivityForResult(intent,REQUEST_RECORD_VOICE);
            }
        });

        //멀티바의 맵버튼을 눌렀을 때 동작
        mInsertMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeMargin();
                mapLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mGoogleApiClient.isConnected()) {
            Log.d(TAG, "onResume : call startLocationUpdates");
            if (!mRequestingLocationUpdates) startLocationUpdates();
        }

        //앱 정보에서 퍼미션을 허가했는지를 다시 검사해봐야 한다.
        if (askPermissionOnceAgain) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                askPermissionOnceAgain = false;
                checkPermissions();
            }
        }
    }

    private void startLocationUpdates() {
        if (!checkLocationServicesStatus()) {
            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        }else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "startLocationUpdates : 퍼미션 안가지고 있음");
                return;
            }
            Log.d(TAG, "startLocationUpdates : call FusedLocationApi.requestLocationUpdates");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
            mRequestingLocationUpdates = true;

            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    private void stopLocationUpdates() {
        Log.d(TAG,"stopLocationUpdates : LocationServices.FusedLocationApi.removeLocationUpdates");
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        mRequestingLocationUpdates = false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady :");

        mGoogleMap = googleMap;

        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에
        //지도의 초기위치를 crimson house 로 이동
        setDefaultLocation();

        //mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mGoogleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener(){

            @Override
            public boolean onMyLocationButtonClick() {
                Log.d( TAG, "onMyLocationButtonClick : 위치에 따른 카메라 이동 활성화");
                mMoveMapByAPI = true;
                return true;
            }
        });

        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                Log.d( TAG, "onMapClick :");
            }
        });

        mGoogleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {

            @Override
            public void onCameraMoveStarted(int i) {
                if (mMoveMapByUser == true && mRequestingLocationUpdates){

                    Log.d(TAG, "onCameraMove : 위치에 따른 카메라 이동 비활성화");
                    mMoveMapByAPI = false;
                }
                mMoveMapByUser = true;
            }
        });

        mGoogleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged : ");

        String markerTitle = getCurrentAddress(location);
        String markerSnippet = "latitude:" + String.valueOf(location.getLatitude())
                + " longitude:" + String.valueOf(location.getLongitude());

        //현재 위치에 마커 생성하고 이동
        if(!mapFlag){
            setCurrentLocation(location, markerTitle, markerSnippet,NEW_MAP);
        }else{
            setCurrentLocation(location, markerTitle, markerSnippet,CALL_MAP);
        }


        mCurrentLocatiion = location;
    }

    @Override
    protected void onStart() {
        if(mGoogleApiClient != null && mGoogleApiClient.isConnected() == false){
            Log.d(TAG, "onStart: mGoogleApiClient connect");
            mGoogleApiClient.connect();
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (mRequestingLocationUpdates) {
            Log.d(TAG, "onStop : call stopLocationUpdates");
            stopLocationUpdates();
        }
        if ( mGoogleApiClient.isConnected()) {
            Log.d(TAG, "onStop : mGoogleApiClient disconnect");
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if ( mRequestingLocationUpdates == false ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (hasFineLocationPermission == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(mActivity,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                } else {
                    Log.d(TAG, "onConnected : 퍼미션 가지고 있음");
                    Log.d(TAG, "onConnected : call startLocationUpdates");
                    startLocationUpdates();
                    mGoogleMap.setMyLocationEnabled(true);
                }
            }else{
                Log.d(TAG, "onConnected : call startLocationUpdates");
                startLocationUpdates();
                mGoogleMap.setMyLocationEnabled(true);
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed");
//        setDefaultLocation();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "onConnectionSuspended");
        if (cause == CAUSE_NETWORK_LOST)
            Log.e(TAG, "onConnectionSuspended(): Google Play services " +
                    "connection lost.  Cause: network lost.");
        else if (cause == CAUSE_SERVICE_DISCONNECTED)
            Log.e(TAG, "onConnectionSuspended():  Google Play services " +
                    "connection lost.  Cause: service disconnected");
    }

    public String getCurrentAddress(Location location) {
        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }
        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet,int type) {
        mMoveMapByUser = false;

        if (currentMarker != null) currentMarker.remove();

        LatLng currentLatLng = null;
        if(type == NEW_MAP){
            currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();
            mMarkerTitle = markerTitle;
        }else{
            currentLatLng = new LatLng(35.05, 127.0);
        }

        //구글맵의 디폴트 현재 위치는 파란색 동그라미로 표시
        //마커를 원하는 이미지로 변경하여 현재 위치 표시하도록 수정해야함.
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title(mMarkerTitle);
        //markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currentMarker = mGoogleMap.addMarker(markerOptions);

        if ( mMoveMapByAPI ) {
            Log.d( TAG, "setCurrentLocation :  mGoogleMap moveCamera "
                    + location.getLatitude() + " " + location.getLongitude() ) ;
            // CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, 15);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
            mGoogleMap.moveCamera(cameraUpdate);
        }
    }

    public void setDefaultLocation() {
        mMoveMapByUser = false;

        //default location, crimson house
        LatLng DEFAULT_LOCATION = new LatLng(35.610508, 139.630020);
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요";

        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = mGoogleMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        mGoogleMap.moveCamera(cameraUpdate);
    }

    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions() {
        boolean fineLocationRationale = ActivityCompat
                .shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasFineLocationPermission == PackageManager
                .PERMISSION_DENIED && fineLocationRationale)
            showDialogForPermission("You need to allow permission for running app");

        else if (hasFineLocationPermission
                == PackageManager.PERMISSION_DENIED && !fineLocationRationale) {
            showDialogForPermissionSetting("퍼미션 거부 + Don't ask again(다시 묻지 않음) " +
                    "체크 박스를 설정한 경우로 설정에서 퍼미션 허가해야합니다.");
        } else if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "checkPermissions : 퍼미션 가지고 있음");
            if ( mGoogleApiClient.isConnected() == false) {
                Log.d(TAG, "checkPermissions : 퍼미션 가지고 있음");
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (permsRequestCode
                == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION && grantResults.length > 0) {

            boolean permissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

            if (permissionAccepted) {
                if ( mGoogleApiClient.isConnected() == false) {

                    Log.d(TAG, "onRequestPermissionsResult : mGoogleApiClient connect");
                    mGoogleApiClient.connect();
                }
            } else {
                checkPermissions();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(CreateMemoActivity.this);
        builder.setTitle("Alert");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ActivityCompat.requestPermissions(mActivity,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.create().show();
    }

    private void showDialogForPermissionSetting(String msg) {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(CreateMemoActivity.this);
        builder.setTitle("Alert");
        builder.setMessage(msg);
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                askPermissionOnceAgain = true;

                Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + mActivity.getPackageName()));
                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(myAppSettings);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.create().show();
    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(CreateMemoActivity.this);
        builder.setTitle("Location permission");
        builder.setMessage("For using this app location permission is required");
        builder.setCancelable(true);
        builder.setPositiveButton("Setting", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    protected Uri getUri(int type, int id){
        Uri uri = null;
        if(type == MEDIA_TYPE_PHOTO){
            String photoUri = dbHelperPhoto.getPhotoUri(id);
            uri = Uri.parse(photoUri);
        }else if(type == MEDIA_TYPE_VIDEO){
            String videoUri = dbHelperVideo.getVideoUri(id);
            uri = Uri.parse(videoUri);
        }else if(type == MEDIA_TYPE_VOICE){
            String voiceUri = dbHelperVoice.getVoiceUri(id);
            uri = Uri.parse(voiceUri);
        }
        return uri;
    }

    protected double getLatLng(int type, int id){
        double latLng = 0;
        if(type == MAP_LATITUDE){
            latLng = dbHelperMap.getMapLatitude(id);
        }else if(type == MAP_LONGITUDE){
            latLng = dbHelperMap.getMapLongitude(id);
        }
        return latLng;
    }

    protected void selectPhotoMethod(){
        final CharSequence[] content = {"Take Photo","Photo Library"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select the method")
                .setItems(content, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0: //take photo 를 눌렀을 때 동작
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if(intent.resolveActivity(getPackageManager()) != null){
                                    try{
                                        file = createFile(MEDIA_TYPE_PHOTO);
                                    }catch(IOException e){

                                    }
                                }
                                if(file != null){
                                    mImageUri = Uri.fromFile(file);
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT,mImageUri);
                                    startActivityForResult(intent,REQUEST_TAKE_PHOTO);
                                }
                                break;
                            case 1: //photo library 를 눌렀을 때 동작
                                Intent libraryIntent = new Intent(Intent.ACTION_PICK);
                                libraryIntent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                                libraryIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(libraryIntent,REQUEST_PHOTO_LIBRARY);
                                break;
                            default:
                                finish();
                                break;
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    protected void selectVideoMethod(){
        final CharSequence[] content = {"Take Video","Video Library"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select the method")
                .setItems(content, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0: //take video 를 눌렀을 때 동작
                                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                                //빌드버전에 따라 분기처리해주어야 할 수도 있음
                                if(intent.resolveActivity(getPackageManager()) != null){
                                    try{
                                        videoFile = createFile(MEDIA_TYPE_VIDEO);
                                    }catch(IOException e){

                                    }
                                }
                                if(videoFile != null){
                                    mVideoUri = Uri.fromFile(videoFile);
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT,mVideoUri);
                                    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,1);
                                    startActivityForResult(intent,REQUEST_TAKE_VIDEO);
                                }
                                break;
                            case 1: //video library 를 눌렀을 때 동작
                                Intent libraryIntent = new Intent(Intent.ACTION_PICK);
                                libraryIntent.setType("video/*");
                                libraryIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                //libraryIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(libraryIntent,REQUEST_VIDEO_LIBRARY);
                                break;
                            default:
                                finish();
                                break;
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    protected File createFile(int type) throws IOException{
        String fileName = null;
        if(type == MEDIA_TYPE_PHOTO){
            fileName = "tmp_"+ String.valueOf(System.currentTimeMillis())+".jpg";
        }else if(type == MEDIA_TYPE_VIDEO){
            fileName = "tmp_"+ String.valueOf(System.currentTimeMillis())+".mp4";
        }
        File mediaFile = new File(Environment.getExternalStorageDirectory(),fileName);
        return mediaFile;
    }

    protected void listIntent(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    protected String currentDate(){

        Long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");

        String currentDate = simpleDateFormat.format(date);

        return currentDate;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_TAKE_PHOTO:
            if(resultCode == RESULT_OK){
                setPhoto();
            }else Toast.makeText(getApplicationContext(),"didn't respond",Toast.LENGTH_SHORT).show();
            break;
            case REQUEST_PHOTO_LIBRARY:
                if(resultCode == RESULT_OK){
                    Uri uri = data.getData();
                    mImageUri = uri;
                    setPhoto();
                }else Toast.makeText(getApplicationContext(),"didn't respond",Toast.LENGTH_SHORT).show();
                break;
            case REQUEST_TAKE_VIDEO:
                if(resultCode == RESULT_OK){
                    setVideo();
                }else Toast.makeText(getApplicationContext(),"didn't respond",Toast.LENGTH_SHORT).show();
                break;
            case REQUEST_VIDEO_LIBRARY:
                if(resultCode == RESULT_OK){
                    Uri uri = data.getData();
                    mVideoUri = uri;
                    changeMargin();
                    mVideo.setVisibility(View.VISIBLE);
                    String path = getPath(mVideoUri);
                    mVideo.setVideoPath(path);
                    //mVideo.setVideoURI(mVideoUri);
                    final MediaController mediaController = new MediaController(this);
                    mVideo.setMediaController(mediaController);
                    mVideo.start();
                    mVideo.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mediaController.show(0);
                            mVideo.pause();
                        }
                    },100);
                }else Toast.makeText(getApplicationContext(),"didn't respond",Toast.LENGTH_SHORT).show();
                break;
            case REQUEST_RECORD_VOICE:
                if(resultCode == RESULT_OK){
                    Uri uri = data.getData();
                    mVoiceUri = uri;
                    setVoice();
                }else Toast.makeText(getApplicationContext(),"didn't respond",Toast.LENGTH_SHORT).show();
                break;
            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d(TAG, "onActivityResult : 퍼미션 가지고 있음");


                        if ( mGoogleApiClient.isConnected() == false ) {

                            Log.d( TAG, "onActivityResult : mGoogleApiClient connect ");
                            mGoogleApiClient.connect();
                        }
                        return;
                    }
                }
                break;
        }
    }

    protected String getPath(Uri uri){
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if(cursor != null){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            while(cursor.moveToFirst()){
                return cursor.getString(column_index);
            }
        }
        return null;
    }

    protected void setPhoto(){
        changeMargin();
        mPhoto.setVisibility(View.VISIBLE);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 10;
        Bitmap bitmap = null;
        try{
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(mImageUri),null,options);
            ExifInterface exif = new ExifInterface(mImageUri.getPath());
            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int exifDegree = exifOrientationToDegrees(exifOrientation);
            bitmap = rotate(bitmap,exifDegree);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        mPhoto.setImageBitmap(bitmap);
    }

    protected void setVideo(){
        changeMargin();
        mVideo.setVisibility(View.VISIBLE);
        //String path = videoFile.getAbsolutePath();
        //mVideo.setVideoPath(path);
        mVideo.setVideoURI(mVideoUri);
        final MediaController mediaController = new MediaController(this);
        mVideo.setMediaController(mediaController);
        mVideo.start();
        mVideo.postDelayed(new Runnable() {
            @Override
            public void run() {
                mediaController.show(0);
                mVideo.pause();
            }
        },100);
    }

    protected void setVoice(){
        changeMargin();
        mVoice.setVisibility(View.VISIBLE);
        voicePath = getPath(mVoiceUri);
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    MediaPlayer mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(voicePath);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                }catch(IOException e){

                }

            }
        });
    }

    protected void setMap(){
        changeMargin();
        mapLayout.setVisibility(View.VISIBLE);
    }

    protected void changeMargin(){
        //사진이 들어가는 레이아웃 보여주기
        mMultiMemoView.setVisibility(View.VISIBLE);

        //텍스트메모가 들어가는 레이아웃의 마진값 수정
        RelativeLayout.LayoutParams mLayoutParams = (RelativeLayout.LayoutParams) mMemoView.getLayoutParams();
        mLayoutParams.topMargin = 40;
        mMemoView.setLayoutParams(mLayoutParams);
    }

    public int exifOrientationToDegrees(int exifOrientation){
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90){
            return 90;
        } else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180){
            return 180;
        } else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270){
            return 270;
        }
        return 0;
    }

    public Bitmap rotate(Bitmap bitmap, int degrees){
        if(degrees != 0 && bitmap != null){
            Matrix matrix = new Matrix();
            matrix.setRotate(degrees,(float)bitmap.getWidth()/2,(float)bitmap.getHeight()/2);

            try{
                Bitmap converted = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
                if(bitmap != converted){
                    bitmap.recycle();
                    bitmap = converted;
                }
            } catch(OutOfMemoryError e){}
        }
        return bitmap;
    }
}

package com.example.guruapp

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.guruapp.databinding.FragmentHomeBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private var user: FirebaseUser? = null

    private lateinit var mActivity: MainActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as MainActivity
    }
    // 런타임 권한 요청 시 필요한 요청 코드
    private val PERMISSIONS_REQUEST_CODE = 100

    //요청할 권한 목록
    var REQUIRED_PERMISSIONS = arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)

    // 위치 서비스 요청 시 필요한 런처
    lateinit var getGPSPermissionLauncher: ActivityResultLauncher<Intent>

    // 지도 관련
    private var mapView: KakaoMap? = null
    private val startZoomLevel = 15
    private lateinit var startPosition: LatLng

    // MapLifeCycleCallback 을 통해 지도의 LifeCycle 관련 이벤트를 수신
    private val lifeCycleCallback: MapLifeCycleCallback = object : MapLifeCycleCallback() {
        override fun onMapDestroy() {
            // 지도 API 가 정상적으로 종료될 때 호출됨
        }

        override fun onMapError(error: Exception) {  // 인증 실패 및 지도 사용 중 에러가 발생할 때 호출됨
            Log.e("k3f", "onMapError: ${error.message}")
        }
    }

    // MapReadyCallback 을 통해 지도가 정상적으로 시작된 후에 수신
    private val readyCallback: KakaoMapReadyCallback = object : KakaoMapReadyCallback() {
        override fun onMapReady(kakaoMap: KakaoMap) {  // 인증 후 API 가 정상적으로 실행될 때 호출
            Log.i("k3f", "startPosition: ${kakaoMap.cameraPosition!!.position}")
            Log.i("k3f", "startZoomLevel: ${kakaoMap.zoomLevel}")
        }

        override fun getPosition(): LatLng {  // 지도 시작 시 위치 좌표를 설정
            return startPosition
        }

        override fun getZoomLevel(): Int {  // 지도 시작 시 확대/축소 줌 레벨 설정
            return startZoomLevel
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)

        // 현재 로그인한 사용자 가져오기
        user = Firebase.auth.currentUser
        Log.d("MyTag", "HomeFragment user info: ${user}")

        checkAllPermissions()

        return binding.root
    }

    fun checkAllPermissions() {
        if (checkLocationService()) {  // GPS, NETWORK 가 켜져 있으면
            checkPermission()  // FINE, COARSE 허용 확인
        } else {
            showDialogForLocationServiceSetting()
        }
    }

    // GPS, NETWORK 확인
    fun checkLocationService(): Boolean {
        val locationManager =
            mActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // GPS_PROVIDER, NETWORK_PROVIDER 둘 중 하나라도 false면 다이얼로그 띄움
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
    }

    // FINE, COARSE 허용 확인
    private fun checkPermission() {
        val finePermissionCheck =
            ContextCompat.checkSelfPermission(mActivity, ACCESS_FINE_LOCATION)
        val coarsePermissionCheck =
            ContextCompat.checkSelfPermission(mActivity, ACCESS_COARSE_LOCATION)

        if (finePermissionCheck != PackageManager.PERMISSION_GRANTED || coarsePermissionCheck != PackageManager.PERMISSION_GRANTED) {
            // 권한이 한 개라도 없다면 퍼미션 요청
            ActivityCompat.requestPermissions(
                mActivity,
                REQUIRED_PERMISSIONS,
                PERMISSIONS_REQUEST_CODE
            )
        } else {
            val locationProvider = LocationProvider(mActivity)
            val uLatitude = locationProvider.getLocationLatitude()
            val uLongtitude = locationProvider.getLocationLongitude()
            Log.d("MyTag", "Latitude: $uLatitude, Longitude: $uLongtitude")

            // 원래 startPosition = LatLng.from(uLatitude, uLongtitude) 코드 이지만,
            // 현재 애뮬레이터라 현위치 받아오기가 불가능해서 판교역으로 처음 위치 설정 => 실제 핸드폰으로 실행 시 위의 코드로 바꿔주면 현위치로 잡힘
            startPosition = LatLng.from(37.394660,127.111182)

            val mapView: MapView = binding.mapView
            // 맵 뷰 띄우기
            mapView.start(lifeCycleCallback, readyCallback)
        }
    }


    // GPS, NETWORK 둘 중 하나라도 false면 다이얼로그 띄움
    private fun showDialogForLocationServiceSetting() {
        // getGPSPermissionLauncher => 결괏값을 반환해야 하는 인텐트 실행해줌
        getGPSPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                // 결괏값을 받았을 때
                    result ->
                if (result.resultCode == FragmentActivity.RESULT_OK) {
                    // 사용자가 GPS를 활성화시켰는지 확인
                    if (checkLocationService()) {
                        // 런타임 권한 확인
                        checkPermission()
                    } else {
                        // 위치 서비스가 허용되지 않았다면 다이얼로그 다시 띄움
                        Toast.makeText(mActivity, "위치 서비스를 사용할 수 없습니다. 위치 권한을 설정해주세요.", Toast.LENGTH_LONG).show()
                        showDialogForLocationServiceSetting()
                    }
                }
            }

        // 사용자에게 의사를 물어보는 AlertDialog 생성
        val builder: AlertDialog.Builder = AlertDialog.Builder(mActivity)
        builder.setTitle("위치 서비스 비활성화")  // 제목
        builder.setMessage("위치 서비스가 꺼져 있습니다. 설정해야 앱을 사용할 수 있습니다.")  // 내용
        builder.setCancelable(true)  // 다이얼로그 창 바깥 터치 시 창 닫힘
        builder.setPositiveButton("설정", DialogInterface.OnClickListener {  // 확인 버튼
                dialog, id ->
            val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            getGPSPermissionLauncher.launch(callGPSSettingIntent)
        })
        builder.setNegativeButton("취소", DialogInterface.OnClickListener {  // 취소 버튼
                dialog, id ->
            dialog.cancel()
            Toast.makeText(mActivity, "기기에서 위치서비스(GPS) 설정 후 사용해주세요.", Toast.LENGTH_SHORT).show()
        })
        // 다이얼로그 생성 및 출력
        builder.create().show()
    }
}

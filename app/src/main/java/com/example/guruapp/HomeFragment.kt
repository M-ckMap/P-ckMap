package com.example.guruapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.guruapp.databinding.FragmentHomeBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class HomeFragment : Fragment(), OnMapReadyCallback {

    companion object {
        const val TAG = "HomeFragment"
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var googleMap: GoogleMap
    private var currentMarker: Marker? = null
    private lateinit var mapView: MapView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        // Set up button click listener for navigating to SearchActivity
        val searchButton: Button = binding.root.findViewById(R.id.toSearch)
        searchButton.setOnClickListener {
            val intent = Intent(activity, SearchActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        // 현재 에뮬레이터라 현위치를 뜨게 할 수 없으므로 디폴트 위치 서울여자대학교로 설정
        currentMarker = setupMarker(LatLngEntity(37.628274, 127.090446))
        currentMarker?.showInfoWindow()
    }

    private fun setupMarker(locationLatLngEntity: LatLngEntity): Marker? {
        val positionLatLng = LatLng(locationLatLngEntity.latitude!!, locationLatLngEntity.longitude!!)
        val markerOption = MarkerOptions().apply {
            position(positionLatLng)
            title("위치")
            snippet("서울여자대학교")
        }

        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(positionLatLng, 15f))
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15f))
        return googleMap.addMarker(markerOption)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * LatLngEntity data class
     *
     * @property latitude 위도 (ex. 37.5562)
     * @property longitude 경도 (ex. 126.9724)
     */
    data class LatLngEntity(
        var latitude: Double?,
        var longitude: Double?
    )
}

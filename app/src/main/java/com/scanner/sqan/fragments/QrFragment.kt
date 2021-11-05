package com.scanner.sqan.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.Barcode.FORMAT_QR_CODE
import com.scanner.sqan.R
import com.scanner.sqan.analyzer.BarCodeAndQRCodeAnalyser
import com.scanner.sqan.databinding.QrFragmentBinding
import com.scanner.sqan.models.Progress
import com.scanner.sqan.viewModels.DeviceViewModel
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

const val RATIO_4_3_VALUE = 4.0 / 3.0
const val RATIO_16_9_VALUE = 16.0 / 9.0
typealias BarcodeAnalyzerListener = (barcode: MutableList<Barcode>) -> Unit

class QrFragment : Fragment() {
    private val requiredPermissions = arrayOf(Manifest.permission.CAMERA)
    private var processingBarcode = AtomicBoolean(true)
    private lateinit var cameraInfo: CameraInfo
    private lateinit var cameraControl: CameraControl
    private lateinit var binding: QrFragmentBinding
    private val viewModel: DeviceViewModel by activityViewModels()
    private var expect:Boolean=false

    private val executor by lazy {
        Executors.newSingleThreadExecutor()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = QrFragmentBinding.inflate(inflater)
        addObservers()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(!allPermissionsGranted()) {
            requestAllPermissions()
        }
        if (allPermissionsGranted()) {
           binding.viewFinder?.let {
               binding.viewFinder.post {
                   startCamera()
               }
           }
        } else {
            requestAllPermissions()
        }
    }

    private val multiPermissionCallback =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
            if (map.entries.isEmpty()) {
                Toast.makeText(activity, "Please Accept all the permissions", Toast.LENGTH_SHORT)
                    .show()
            } else {
                binding.viewFinder.post {
                    startCamera()
                }
            }

        }

    private fun requestAllPermissions() {
        multiPermissionCallback.launch(
            requiredPermissions
        )
    }

    /**
     * Check if all permission specified in the manifest have been granted
     */
    private fun allPermissionsGranted() = requiredPermissions.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    private fun startCamera() {

        // Get screen metrics used to setup camera for full screen resolution
        val metrics = DisplayMetrics()

        val screenAspectRatio = aspectRatio(200,200)

        // Bind the CameraProvider to the LifeCycleOwner
        val cameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({

            // CameraProvider
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .setTargetAspectRatio(screenAspectRatio)
                // Set initial target rotation
                .build()

            preview.setSurfaceProvider(binding.viewFinder.surfaceProvider)

            // ImageAnalysis
            val textBarcodeAnalyzer = initializeAnalyzer(screenAspectRatio,0)
            cameraProvider.unbindAll()

            try {
                val camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, textBarcodeAnalyzer
                )
                cameraControl = camera.cameraControl
                cameraInfo = camera.cameraInfo
                cameraControl.setLinearZoom(0.5f)


            } catch (exc: Exception) {
                exc.printStackTrace()
                //Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    private fun initializeAnalyzer(screenAspectRatio: Int, rotation: Int): UseCase {
        return ImageAnalysis.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setTargetRotation(rotation)
            .build()
            .also {

                it.setAnalyzer(executor, BarCodeAndQRCodeAnalyser { barcode ->
                    /**
                     * Change update  to true if you want to scan only one barcode or it will continue scaning after detecting for the first time
                     */
                    /**
                     * Change update  to true if you want to scan only one barcode or it will continue scaning after detecting for the first time
                     */
                   if(processingBarcode.get())
                       onBarcodeDetected(barcode)
                })
            }
    }

    private fun onBarcodeDetected(barcodes: List<Barcode>) {
        processingBarcode.set(false)
        if (barcodes.isNotEmpty()) {

            //binding.BarcodeValue.text = barcodes[0].rawValue
            if (barcodes[0].format == FORMAT_QR_CODE)
                viewModel.getDeviceWithId(barcodes[0].rawValue!!)
        }
    }

    private fun addObservers() {
        binding.apply {
            viewModel.deviceLoadingProgress.observe(viewLifecycleOwner, {
                when (it) {
                    is Progress.Loading -> {
                        progressBar.isVisible = true
                    }
                    is Progress.Error -> {
                        progressBar.isVisible = false
                        Toast.makeText(activity, it.error, Toast.LENGTH_LONG).show()
                    }
                    is Progress.DeviceLoaded -> {
                        progressBar.isVisible = false
                        findNavController().navigate(
                            R.id.action_qrFragment_to_deviceInfoFragment,
                            it.bundle
                        )
                    }
                    else -> {
                        progressBar.isVisible = false
                    }
                }
            })
        }
    }

    override fun onPause() {
        super.onPause()
         viewModel.clear()
    }

    override fun onResume() {
        super.onResume()
        startCamera()
        processingBarcode.set(true)
    }

}
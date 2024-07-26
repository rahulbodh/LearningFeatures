package com.scopegenx.microscope

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.slider.Slider
import com.hoho.android.usbserial.driver.CdcAcmSerialDriver
import com.hoho.android.usbserial.driver.ProbeTable
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import com.scopegenx.R

class FlashLightFragment : BottomSheetDialogFragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var manager: UsbManager
    private var usbSerialPort: UsbSerialPort? = null
    private var isUsbPermissionGranted = false

    companion object {
        const val ACTION_USB_PERMISSION = "com.scopegenx.microscope.USB_PERMISSION"
    }

    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d("FlashLightSheet", "onReceive: $intent")
            when (intent.action) {
                ACTION_USB_PERMISSION -> {
                    synchronized(this) {
                        val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            Log.d("FlashLightSheet", "Permission granted for device $device")
                            device?.let {
                                isUsbPermissionGranted = true
                                connectToDevice(it)
                            }
                        } else {
                            Log.d("FlashLightSheet", "Permission denied for device $device")
                        }
                    }
                }
                UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                    Log.d("FlashLightSheet", "USB device attached")
                    initUsb()
                }
                UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                    Log.d("FlashLightSheet", "USB device detached")
                    usbSerialPort?.close()
                }
            }
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedPreferences = context.getSharedPreferences("slider_values", Context.MODE_PRIVATE)
        manager = context.getSystemService(Context.USB_SERVICE) as UsbManager
        Log.d("FlashLightSheet", "onAttach: $manager")

        // Register the BroadcastReceiver for USB permission and device events
        val filter = IntentFilter().apply {
            addAction(ACTION_USB_PERMISSION)
            addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
            addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        }

        // Register the receiver based on the Android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.registerReceiver(usbReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            context.registerReceiver(usbReceiver, filter)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_flash_light, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        dialog?.let {
            it.setOnShowListener { dialog ->
                val bottomSheetDialog = dialog as BottomSheetDialog
                val bottomSheet = bottomSheetDialog.findViewById<View>(R.id.flash_sheet)
                bottomSheet?.let { sheet ->
                    bottomSheetDialog.behavior.peekHeight = sheet.height
                    sheet.parent.parent.requestLayout()
                }
            }
        }
        initUsb()
    }

    private fun initView(rootView: View) {
        val bigLightSlider = rootView.findViewById<Slider>(R.id.big_light)
        val multiLightSlider = rootView.findViewById<Slider>(R.id.multi_light)
        val saveBtn = rootView.findViewById<Button>(R.id.lightSaveBtn)

        bigLightSlider.valueFrom = 0f
        bigLightSlider.valueTo = 100f
        multiLightSlider.valueFrom = 0f
        multiLightSlider.valueTo = 100f

        bigLightSlider.addOnChangeListener { _, value, _ ->
            sharedPreferences.edit().putInt("big_light_value", value.toInt()).apply()
        }
        multiLightSlider.addOnChangeListener { _, value, _ ->
            sharedPreferences.edit().putInt("multi_light_value", value.toInt()).apply()
        }

        saveBtn.setOnClickListener {
            val value1 = sharedPreferences.getInt("big_light_value", 0)
            val value2 = sharedPreferences.getInt("multi_light_value", 0)
            Log.d("lights", "$value1 $value2")
            sendLightValues(value1, value2)
            dismiss()
        }

        // Retrieve and set slider values from SharedPreferences
        val bigLightValue = sharedPreferences.getInt("big_light_value", 0)
        val multiLightValue = sharedPreferences.getInt("multi_light_value", 0)
        bigLightSlider.value = bigLightValue.toFloat()
        multiLightSlider.value = multiLightValue.toFloat()
    }

    private fun initUsb() {
        val permissionIntent = PendingIntent.getBroadcast(
            context, 0, Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE
        )

        val devices = manager.deviceList
        val targetVendorId = 1155
        val targetProductId = 22336

        if (devices.isEmpty()) {
            Log.d("FlashLightSheet", "No USB devices found")
            return
        }

        devices.values.forEach { device ->
            Log.d("FlashLightSheet", "Found USB device: $device")
            if (device.vendorId == targetVendorId && device.productId == targetProductId) {
                if (!manager.hasPermission(device)) {
                    manager.requestPermission(device, permissionIntent)
                } else {
                    isUsbPermissionGranted = true
                    connectToDevice(device)
                }
            } else {
                Log.d("FlashLightSheet", "Ignoring USB device: $device")
            }
        }
    }


    private fun sendLightValues(bigLightValue: Int, multiLightValue: Int) {
        Log.d("FlashLightSheet", "Send Light value : bigLightValue: $bigLightValue, multiLightValue: $multiLightValue")
        Log.d("FlashLightSheet", "isUsbPermissionGranted: $isUsbPermissionGranted")
        Log.d("FlashLightSheet", "usbSerialPort: $usbSerialPort")
        if (isUsbPermissionGranted && usbSerialPort != null) {
            try {
                // Validate and clamp values to 0-100
                val validatedBigLightValue = bigLightValue.coerceIn(0, 100)
                val validatedMultiLightValue = multiLightValue.coerceIn(0, 100)

                val bigLightCommand = "L1:$validatedBigLightValue\n".toByteArray()  // Add newline
                usbSerialPort?.write(bigLightCommand, 1000)
                Log.d("FlashLightSheet", "bigLightCommand: $bigLightCommand")

                val multiLightCommand = "L2:$validatedMultiLightValue\n".toByteArray() // Add newline
                usbSerialPort?.write(multiLightCommand, 1000)
                Log.d("FlashLightSheet", "multiLightCommand: $multiLightCommand")

            } catch (e: Exception) {
                Log.e("FlashLightSheet", "Error writing to USB device", e)
                Toast.makeText(context, "Error writing to USB device", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.d("FlashLightSheet", "USB device is not ready yet")
            Toast.makeText(context, "USB device is not ready yet", Toast.LENGTH_SHORT).show()
        }
    }

    private fun connectToDevice(device: UsbDevice) {
        Log.d("FlashLightSheet", "connectToDevice: $device")
        val  connection = manager.openDevice(device)
        if (connection == null) {
            Log.d("FlashLightSheet", "Unable to open connection to device")
            return
        }

        // Attempt to find the driver using the default prober
        var driver: UsbSerialDriver? = UsbSerialProber.getDefaultProber().probeDevice(device)

        // If the default prober fails, attempt to find the driver using a custom prober
        if (driver == null) {
            val customTable = ProbeTable()
            customTable.addProduct(device.vendorId, device.productId, CdcAcmSerialDriver::class.java)
            driver = UsbSerialProber(customTable).probeDevice(device)
        }

        if (driver != null) {
            usbSerialPort = driver.ports[0]
            Log.d("FlashLightSheet", "USB driver found: $driver")
            Log.d("FlashLightSheet", "Vid : ${device.vendorId}, Pid : ${device.productId} , endPoint : ${usbSerialPort?.portNumber} , dn : ${device.getInterface(0).getEndpoint(0)} , sn : ${device.serialNumber}")

            usbSerialPort?.apply {
                try {
                    open(connection)
                    setParameters(115200, UsbSerialPort.DATABITS_8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
                    // Check if port is open before sending data
                    if (isOpen) {
                        sendLightValues(
                            sharedPreferences.getInt("big_light_value", 0),
                            sharedPreferences.getInt("multi_light_value", 0)
                        )
                    } else {
                        Log.d("FlashLightSheet", "USB port is not open yet")
                    }
                } catch (e: Exception) {
                    Log.e("FlashLightSheet", "Error setting up USB serial connection", e)
                }
            }
        } else {
            Log.d("FlashLightSheet", "No driver found for device: $driver")
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        context?.unregisterReceiver(usbReceiver)
        usbSerialPort?.apply {
            try {
                close()
            } catch (e: Exception) {
                Log.e("FlashLightSheet", "Error closing USB port", e)
            }
        }
    }

}

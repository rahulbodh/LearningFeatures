package com.example.myapplication.modelBottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentActivity
import com.example.myapplication.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ModelBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_model_bottom_sheet, container, false)
        val standardBottomSheet = view.findViewById<FrameLayout>(R.id.standard_bottom_sheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(standardBottomSheet)

        // Back callback to handle back navigation while bottom sheet is open
        val bottomSheetBackCallback = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED ||
                    bottomSheetBehavior.state == BottomSheetBehavior.STATE_HALF_EXPANDED) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                } else {
                    isEnabled = false
                    requireActivity().onBackPressed()
                }
            }
        }

        // Add back pressed dispatcher callback
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, bottomSheetBackCallback)

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                bottomSheetBackCallback.isEnabled = when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED, BottomSheetBehavior.STATE_HALF_EXPANDED -> true
                    BottomSheetBehavior.STATE_COLLAPSED, BottomSheetBehavior.STATE_HIDDEN -> false
                    else -> false
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        return view
    }
}

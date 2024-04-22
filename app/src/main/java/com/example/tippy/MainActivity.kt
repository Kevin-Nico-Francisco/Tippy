package com.example.tippy

import android.animation.ArgbEvaluator
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.example.tippy.ui.theme.TippyTheme

private const val TAG = "MainActivity"
private const val INITIAL_TIP_PERCENT = 10
class MainActivity : ComponentActivity() {
    // Declare UI components
    private lateinit var etBaseAmount: EditText
    private lateinit var seekBarTip: SeekBar
    private lateinit var tvTipPercentLabel: TextView
    private lateinit var tvTipAmount: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var tvTipDescription: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Set the layout for the activity

        // Initialize UI components
        etBaseAmount = findViewById(R.id.etBaseAmount)
        seekBarTip = findViewById(R.id.seekBarTip)
        tvTipPercentLabel = findViewById(R.id.tvTipPercentLabel)
        tvTipAmount = findViewById(R.id.tvTipAmount)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        tvTipDescription = findViewById(R.id.tvTipDescription)

        // Set initial value and label for tip seekbar
        seekBarTip.progress = INITIAL_TIP_PERCENT
        tvTipPercentLabel.text = "$INITIAL_TIP_PERCENT%"
        updateTipDescription(INITIAL_TIP_PERCENT)

        // Listen for changes in tip seekbar value
        seekBarTip.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                Log.i(TAG, "OnProgressChanged $p1") // Log the progress change
                tvTipPercentLabel.text = "$p1%" // Update tip percent label
                computeTipAndTotal() // Recalculate tip and total amount
                updateTipDescription(p1) // Update tip description based on percent
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}

        })

        // Listen for changes in base amount input
        etBaseAmount.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                Log.i(TAG, "OnBaseAmountChanged $p0") // Log the base amount change
                computeTipAndTotal() // Recalculate tip and total amount
            }

        })
    }

    // Function to calculate tip and total amount
    private fun computeTipAndTotal() {
        // Check if base amount is empty to avoid number format exception
        if (etBaseAmount.text.isEmpty()) {
            tvTipAmount.text = "" // Clear tip amount text
            tvTotalAmount.text = "" // Clear total amount text
        } else {
            val baseAmount = etBaseAmount.text.toString().toDouble() // Get base amount from EditText
            val tipPercent = seekBarTip.progress // Get tip percentage from seekbar

            // Calculate tip amount and total amount
            val tipAmount = baseAmount * tipPercent / 100
            val totalAmount = baseAmount + tipAmount

            // Display tip amount and total amount with formatting
            tvTipAmount.text = "%.2f".format(tipAmount)
            tvTotalAmount.text = "%.2f".format(totalAmount)
        }
    }

    // Function to update tip description based on tip percent
    private fun updateTipDescription(tipPercent: Int) {
        val tipDescription = when (tipPercent) {
            in 0..9 -> "Poor"
            in 10..14 -> "Acceptable"
            in 15..20 -> "Good"
            in 21..24 -> "Great"
            else -> {"Amazing"}
        }
        // Evaluate color based on tip percent and update tip description text and color
        val color = ArgbEvaluator().evaluate(
            tipPercent.toFloat() / seekBarTip.max,
            ContextCompat.getColor(this, R.color.worst_tip),
            ContextCompat.getColor(this, R.color.best_tip)
        ) as Int

        tvTipDescription.text = tipDescription
        tvTipDescription.setTextColor(color)
    }
}

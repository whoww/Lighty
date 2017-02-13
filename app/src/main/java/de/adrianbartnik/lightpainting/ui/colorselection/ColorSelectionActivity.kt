package de.adrianbartnik.lightpainting.ui.colorselection


import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.FrameLayout
import com.android.colorpicker.ColorPickerPalette
import com.android.colorpicker.ColorPickerSwatch
import com.pes.androidmaterialcolorpickerdialog.ColorPicker
import de.adrianbartnik.lightpainting.R
import de.adrianbartnik.lightpainting.layout.RevealLayout
import de.adrianbartnik.lightpainting.ui.painting.PaintingActivity


class ColorSelectionActivity : AppCompatActivity() {

    lateinit var colorPickerPalette : ColorPickerPalette

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_selection)

        setupActionbar()

        colorPickerPalette = findViewById(R.id.picker) as ColorPickerPalette

        val colors = resources.getIntArray(R.array.color_palette)

        colorPickerPalette.init(5)
        colorPickerPalette.drawPalette(colors, colors[0])

        setupCustomColor()
        setupFAB()
    }

    private fun setupFAB() {

        val mFab = findViewById(R.id.fab) as FloatingActionButton
        val mRevealLayout = findViewById(R.id.reveal_layout) as RevealLayout
        val mRevealView = findViewById(R.id.reveal_view)

        mFab.setOnClickListener {
            mFab.setClickable(false) // Avoid naughty guys clicking FAB again and again...
            val location = IntArray(2)
            mFab.getLocationOnScreen(location)
            location[0] += mFab.getWidth() / 2
            location[1] += mFab.getHeight() / 2

            val intent = PaintingActivity.GetStartIntent(this@ColorSelectionActivity, PaintingActivity.PaintShape.CircleShape)

            mRevealView.setVisibility(View.VISIBLE)
            mRevealLayout.setVisibility(View.VISIBLE)

            mRevealLayout.show(location[0], location[1]) // Expand from center of FAB. Actually, it just plays reveal animation.

            mFab.postDelayed({
                startActivity(intent)
                 // Without using R.anim.hold, the screen will flash because of transition of Activities.
                overridePendingTransition(0, R.anim.hold)
            }, 600) // 600 is default duration of reveal animation in RevealLayout

            mFab.postDelayed({
                mFab.setClickable(true)
                mRevealLayout.setVisibility(View.INVISIBLE)
                mRevealView.setVisibility(View.INVISIBLE)
            }, 960) // Or some numbers larger than 600.
        }
    }

    private fun setupCustomColor() {
        val defaultColor = ContextCompat.getColor(applicationContext, R.color.blue_grey)
        val view = ColorPickerSwatch(this@ColorSelectionActivity, defaultColor, false, null)

        val framelayout = findViewById(R.id.color_selection_custom_color) as FrameLayout
        framelayout.addView(view)

        view.setOnClickListener {
            val defaultRed = resources.getInteger(R.integer.default_red)
            val defaulGreen = resources.getInteger(R.integer.default_green)
            val defaultBlue = resources.getInteger(R.integer.default_blue)

            val cp = ColorPicker(this@ColorSelectionActivity, defaultRed, defaulGreen, defaultBlue)
            cp.setCanceledOnTouchOutside(true)
            cp.setCancelable(true)
            cp.show()

            cp.setOnColorSelected { col ->
                view.setColor(col)
                view.setChecked(true)
                colorPickerPalette.clearCurrentSelection()
                cp.dismiss()
            }
        }
    }

    private fun setupActionbar() {
        val myToolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(myToolbar)

        val actionbar = supportActionBar
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setDisplayShowTitleEnabled(false)

        val upArrow = ContextCompat.getDrawable(application, R.drawable.abc_ic_ab_back_material)
        upArrow.setColorFilter(ContextCompat.getColor(application, R.color.upindicator_color), PorterDuff.Mode.SRC_ATOP)
        actionbar?.setHomeAsUpIndicator(upArrow)
    }

    companion object {

        private val TAG = ColorSelectionActivity::class.java.simpleName

        fun GetStartIntent(context: Context): Intent {
            return Intent(context, ColorSelectionActivity::class.java)
        }
    }
}

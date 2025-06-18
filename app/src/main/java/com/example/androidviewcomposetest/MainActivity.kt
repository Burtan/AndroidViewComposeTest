package com.example.androidviewcomposetest

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.TouchDelegate
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.allViews
import com.example.androidviewcomposetest.ui.theme.AndroidViewComposeTestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidViewComposeTestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AndroidView(
                        factory = {
                            val root = RelativeLayout(this)
                                .apply {
                                    setBackgroundColor(Color.BLACK)
                                    val child = ScaledView(this@MainActivity)
                                    addView(child)
                                }

                            root
                        },
                        update = { root ->
                            val child = root.allViews
                                .mapNotNull { it as? FrameLayout }
                                .first()

                            // !!
                            // size is zero?
                            // !!
                            /**
                            root.touchDelegate = TouchDelegate(
                                Rect(root.left, root.top, root.right, root.bottom),
                                child
                            )
                            **/
                            println("Width: ${root.width}, Height: ${root.height}")
                        },
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    )
                }
            }
        }
    }
}

private class ScaledView(context: Context) : FrameLayout(context) {

    init {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        setBackgroundColor(Color.GREEN)
        scaleX = 0.5f
        scaleY = 0.5f
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // Works as a delegate, but the touch position is not correctly delegated.
        (parent as RelativeLayout)
            .apply {
                touchDelegate = TouchDelegate(
                    Rect(left, top, right, bottom),
                    this@ScaledView
                )
            }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        println("Got Touch event on ${ev.x}/${ev.y}")
        return super.dispatchTouchEvent(ev)
    }
}
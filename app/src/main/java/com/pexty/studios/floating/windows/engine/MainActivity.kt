package com.pexty.studios.floating.windows.engine

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import pexty.floatingapp.FloatingPermissions
import pexty.floatingapp.window.BaseWindow
import pexty.floatingapp.window.WindowManager

class MainActivity : AppCompatActivity() {
    private lateinit var baseWindow: BaseWindow
    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab = findViewById(R.id.fab)
        fab.setOnClickListener {
            FloatingPermissions.take(this)
            baseWindow.open()
        }

        baseWindow = BaseWindow(this, 500, 500)
        baseWindow.windowLifecycleListener = object: BaseWindow.WindowLifecycleListener {
            override fun onOpen(baseWindow: BaseWindow) {
                println("Window opened $baseWindow")
            }

            override fun onQuit(baseWindow: BaseWindow) {
                baseWindow.kill()
                println("Window closed $baseWindow")
            }
        }
        baseWindow.windowFocusListener = object: BaseWindow.WindowFocusListener {
            override fun onFocused(baseWindow: BaseWindow) {
                println("Window focused $baseWindow")
            }

            override fun onUnfocused(baseWindow: BaseWindow) {
                println("Window unfocused $baseWindow")
            }
        }
    }

    override fun onDestroy() {
        WindowManager.softKillAll()
        super.onDestroy()
    }
}
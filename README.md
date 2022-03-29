# AndroidFloatingWindowsEngine
Floating windows engine for Android

### First API Version (OLD)
<a href="https://github.com/TeaCondemns/android-libs-FloatingWindows">
  <img align="" src="https://github-readme-stats.vercel.app/api/pin/?username=TeaCondemns&repo=android-libs-FloatingWindows&theme=github_dark" />
</a>

### Navigation
- [Library Source](https://github.com/TeaCondemns/AndroidFloatingWindowsEngine/tree/main/app/src/main/java/pexty)
- [How to use](#how-to-use)

### Preview
![Screenshot_1647277648](https://user-images.githubusercontent.com/83653555/158368218-6b71e8e6-33ac-4a0c-b547-d8103a1404cc.png)
![image](https://user-images.githubusercontent.com/83653555/158224707-7a098524-99d5-4694-ae6a-d7b621e8a7e2.png)

# How to use
### Navigation
Windows:
- [BaseWindow](#basewindow)

Other:
- [FloatingPermissions](#floatingpermissions)
- [FloatingObject](#floatingobject)
  - [Flags](#flags)
- [FloatingObject (Example)](#floatingobject-example)

### BaseWindow

### FloatingPermissions
Allows you to check/get the necessary permissions.

To use
```kt
import pexty.floatingapp.FloatingPermissions
```

Check permissions (`MainActivity.kt`)
```kt
if (FloatingPermissions.has(this)) 
    print("Has permissions!")
else
    print("Needs permissions!")
```

Get permissions (`MainActivity.kt`)
```kt
if (!FloatingPermissions.has(this))
    FloatingPermissions.take(this)
```

### FloatingObject
Allows you to make floating view

To use
```kt
import pexty.floatingapp.FloatingObject
```

Initialize (`MainActivity.kt`)
```kt
val floatingObject = FloatingObject(
    this,                          // context
    view,                          // our view
    12,                            // position X at screen
    15,                            // position Y at screen
    FloatingObject.FLAG_DRAGGABLE  // flags (0 - defaul value)
)
```
#### Flags

`FloatingObject.FLAG_DRAGGABLE` (allows to move an object across the screen)
```kt
const val FLAG_DRAGGABLE =         1 shl 0
```
[`FloatingObject.FLAG_LAYOUT_NO_LIMITS`](https://developer.android.com/reference/android/view/WindowManager.LayoutParams#FLAG_LAYOUT_NO_LIMITS)
```kt
const val FLAG_LAYOUT_NO_LIMITS =  1 shl 1
```
[`FloatingObject.FLAG_LAYOUT_IN_SCREEN`](https://developer.android.com/reference/android/view/WindowManager.LayoutParams#FLAG_LAYOUT_IN_SCREEN)
```kt
const val FLAG_LAYOUT_IN_SCREEN =  1 shl 2
```
[`FloatingObject.FLAG_ANDROID_SECURE`](https://developer.android.com/reference/android/view/WindowManager.LayoutParams#FLAG_SECURE)
```kt
const val FLAG_ANDROID_SECURE =    1 shl 3
```
[`FloatingObject.FLAG_ANDROID_FOCUSABLE`](https://developer.android.com/reference/android/view/WindowManager.LayoutParams#FLAG_NOT_FOCUSABLE) (If specified cancels the flag `WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE`)
```kt
const val FLAG_ANDROID_FOCUSABLE = 1 shl 4
```

### FloatingObject (Example)
Making our view in `activity_main.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.coordinatorlayout.widget.CoordinatorLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <TableLayout
        android:id="@+id/tableLayout"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="horizontal"
        android:background="#ffffff">
    </TableLayout>

</androidx.coordinatorlayout.widget.CoordinatorLayout>
```

Inserting our view into FloatingObject in `MainActivity.kt`
```kt
package com.pexty.studios.floating.windows.engine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TableLayout

import pexty.floatingapp.FloatingObject

class MainActivityfake0: AppCompatActivity() {
    lateinit tableLayout: TableLayout
    lateinit floatingObject: FloatingObject
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tableLayout = findViewById<TableLayout>(R.id.tableLayout)
        floatingObject = FloatingObject(this, tableLayout, _flags=FloatingObject.FLAG_DRAGGABLE or FloatingObject.FLAG_LAYOUT_NO_LIMITS)
        floatingObject.create()
    }
    
    override fun onDestroy() {
        floatingObject.destroy()
        super.onDestroy()
    }
}
```

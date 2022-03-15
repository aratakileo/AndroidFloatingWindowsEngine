# AndroidFloatingWindowsEngine
Floating windows engine for Android

- [Library Source](https://github.com/TeaCondemns/AndroidFloatingWindowsEngine/tree/main/app/src/main/java/pexty)
- [First API version (OLD)](https://github.com/TeaCondemns/android-libs-FloatingWindows)
- [How to use](#how-to-use)

### Preview
![Screenshot_1647277648](https://user-images.githubusercontent.com/83653555/158368218-6b71e8e6-33ac-4a0c-b547-d8103a1404cc.png)
![image](https://user-images.githubusercontent.com/83653555/158224707-7a098524-99d5-4694-ae6a-d7b621e8a7e2.png)

# How to use
Other:
- [FloatingPermissions](#floatingpermissions)
- [FloatingObject](#floatingobject)
- [FloatingObject (Example)](#floatingobject-example)

Windows:
- [BaseWindow](#basewindow)
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

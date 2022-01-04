package pexty.floatingapp.window

object WindowManager {
    private var floatingWindows = ArrayList<BaseWindow>()

    fun _initFocus(baseWindow: BaseWindow) {
        _remove(baseWindow)

        floatingWindows.forEach {it.unfocus()}
        floatingWindows.add(baseWindow)
    }

    fun _remove(baseWindow: BaseWindow) {
        if (floatingWindows.indexOf(baseWindow) != -1) floatingWindows.remove(baseWindow)
    }

    fun _unfocusCurrent() {
        if (floatingWindows.size > 0) floatingWindows[floatingWindows.size - 1].unfocus()
    }

    fun closeAll() {
        for (i in 0 until floatingWindows.size) floatingWindows[0].close()
    }

    fun killAll() {
        for (i in 0 until floatingWindows.size) floatingWindows[0].kill()
    }

    fun softKillAll() {
        for (i in 0 until floatingWindows.size) {
            floatingWindows[0].close()

            if (floatingWindows[0].isExists) floatingWindows[0].kill()
        }
    }
}
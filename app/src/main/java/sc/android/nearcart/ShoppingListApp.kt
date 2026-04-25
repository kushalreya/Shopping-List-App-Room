package sc.android.nearcart

import android.app.Application

class ShoppingListApp: Application() {
    override fun onCreate() {
        super.onCreate()
        Graph.provide(this)
    }
}
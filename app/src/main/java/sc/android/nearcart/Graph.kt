package sc.android.nearcart

import android.content.Context
import androidx.room.Room
import sc.android.nearcart.model.ShoppingDatabase
import sc.android.nearcart.model.ShoppingRepository

object Graph {
    lateinit var database: ShoppingDatabase

    val shoppingRepository by lazy{
        ShoppingRepository(shoppingDao = database.shoppingDao())
    }

    fun provide(context: Context){
        database=Room.databaseBuilder(
            context=context,
            klass = ShoppingDatabase::class.java,
            name="shoppinglist.db"
        ).build()
    }
}
package sc.android.shoppinglistapp_room.model

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ShoppingItem::class],
    version = 1,
    exportSchema = false
)
abstract class ShoppingDatabase: RoomDatabase() {
    abstract fun ShoppingDao() : ShoppingDao
}
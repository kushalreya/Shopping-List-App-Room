package sc.android.shoppinglistapp_room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("shopping_table")
data class ShoppingItem(
    @PrimaryKey(autoGenerate = true)
    val id:Long=0L,
    @ColumnInfo("item_name")
    val name:String="",
    @ColumnInfo("item_quantity")
    val quantity:String="",
    @ColumnInfo("item_unit")
    val unit: String=""
)

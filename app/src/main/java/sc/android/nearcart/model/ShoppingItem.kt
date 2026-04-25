package sc.android.nearcart.model

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
    val quantity:Int=0,
    @ColumnInfo("item_unit")
    val unit: String=""
)

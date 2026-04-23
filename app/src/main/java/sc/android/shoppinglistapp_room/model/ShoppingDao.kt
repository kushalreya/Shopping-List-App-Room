package sc.android.shoppinglistapp_room.model

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingDao {

    // adding item to the table
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addItem(shoppingEntity: ShoppingItem): Long  // returns inserted rowId

    // update item
    @Update
    suspend fun updateItem(shoppingEntity: ShoppingItem): Int // rows affected

    // delete item
    @Delete
    suspend fun deleteItem(shoppingEntity: ShoppingItem): Int // rows deleted

    // load all items
    @Query("SELECT * FROM `shopping_table`")
    fun getAllItems(): Flow<List<ShoppingItem>>

    // get single item by id
    @Query("SELECT * FROM `shopping_table` WHERE id = :id")
    fun getItemById(id: Long): Flow<ShoppingItem>
}
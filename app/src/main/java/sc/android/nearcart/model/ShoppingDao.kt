package sc.android.nearcart.model

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingDao {

    // adding item to the table
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addItem(shoppingItem: ShoppingItem): Long  // returns inserted rowId

    // update item
    @Update
    suspend fun updateItem(shoppingItem: ShoppingItem): Int // rows affected

    // delete item
    @Delete
    suspend fun deleteItem(shoppingItem: ShoppingItem): Int // rows deleted

    // load all items
    @Query("SELECT * FROM `shopping_table`")
    fun getAllItems(): Flow<List<ShoppingItem>>

    // get single item by id
    @Query("SELECT * FROM `shopping_table` WHERE id = :id")
    fun getItemById(id: Long): Flow<ShoppingItem>
}
package sc.android.shoppinglistapp_room.model

import kotlinx.coroutines.flow.Flow

class ShoppingRepository (private val shoppingListDao : ShoppingDao) {

    //adding item
    suspend fun addItem(shoppingItem: ShoppingItem) {
        shoppingListDao.addItem(shoppingEntity = shoppingItem) }

    //update item
    suspend fun updateItem(shoppingItem: ShoppingItem) {
        shoppingListDao.updateItem(shoppingEntity = shoppingItem) }

    //delete item
    suspend fun deleteItem(shoppingItem: ShoppingItem) {
        shoppingListDao.deleteItem(shoppingEntity = shoppingItem) }

    //getting all items
    fun getAllItems() : Flow<List<ShoppingItem>> = shoppingListDao.getAllItems()

    //get item by id
    fun getItemById(id : Long) : Flow<ShoppingItem> { return shoppingListDao.getItemById(id) }

}
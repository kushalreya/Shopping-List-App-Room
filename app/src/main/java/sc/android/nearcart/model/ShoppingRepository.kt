package sc.android.nearcart.model

import kotlinx.coroutines.flow.Flow

class ShoppingRepository (private val shoppingDao : ShoppingDao) {

    //adding item
    suspend fun addItem(shoppingItem: ShoppingItem) {
        shoppingDao.addItem(shoppingItem = shoppingItem) }

    //update item
    suspend fun updateItem(shoppingItem: ShoppingItem) {
        shoppingDao.updateItem(shoppingItem = shoppingItem) }

    //delete item
    suspend fun deleteItem(shoppingItem: ShoppingItem) {
        shoppingDao.deleteItem(shoppingItem = shoppingItem) }

    //getting all items
    fun getAllItems() : Flow<List<ShoppingItem>> = shoppingDao.getAllItems()

    //get item by id
    fun getItemById(id : Long) : Flow<ShoppingItem> { return shoppingDao.getItemById(id) }

}
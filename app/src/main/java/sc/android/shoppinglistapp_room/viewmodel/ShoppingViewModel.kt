package sc.android.shoppinglistapp_room.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import sc.android.shoppinglistapp_room.Graph
import sc.android.shoppinglistapp_room.model.ShoppingItem
import sc.android.shoppinglistapp_room.model.ShoppingRepository

class ShoppingViewModel(
    private val shoppingRepository: ShoppingRepository = Graph.shoppingRepository
) : ViewModel() {

    var shoppingItemName by mutableStateOf("")  //item name
    var shoppingItemQuantity by mutableStateOf(0)   //item quantity
    var shoppingItemUnit by mutableStateOf("")  //item quantity  unit

    //-------------------------------
    //----- DATABASE FUNCTIONS -----
    //-------------------------------
    //adding an item
    fun addItem (shoppingItem: ShoppingItem) {
        viewModelScope.launch (Dispatchers.IO) {
            shoppingRepository.addItem(shoppingItem)
        }
    }

    //updating an item
    fun updateItem (shoppingItem: ShoppingItem){
        viewModelScope.launch (Dispatchers.IO) {
            shoppingRepository.updateItem(shoppingItem)
        }
    }

    //deleting an item
    fun deleteItem (shoppingItem: ShoppingItem) {
        viewModelScope.launch (Dispatchers.IO) {
            shoppingRepository.deleteItem(shoppingItem)
        }
    }

    //get all the items
    val getAllItems : Flow<List<ShoppingItem>> = shoppingRepository.getAllItems()

    //get item by id
    fun getItemById (id : Long) : Flow<ShoppingItem> {
        return shoppingRepository.getItemById(id)
    }

    //-------------------------------
    //-------- UI FUNCTIONS --------
    //-------------------------------

    //updating name field
    fun onNameChange(newName: String) {
        shoppingItemName = newName.take(25)
    }

    //updating quantity field
    fun onQuantityChange(input: String) {
        if (input.isEmpty()) {
            shoppingItemQuantity = 0
            return
        }

        val number = input.toIntOrNull() ?: return

        if (number in 0..999) {
            shoppingItemQuantity = number
        }
    }

    //quantity increment button
    fun incrementQuantity() {
        if (shoppingItemQuantity < 999) {
            shoppingItemQuantity++
        }
    }

    //quantity decrement button
    fun decrementQuantity() {
        if (shoppingItemQuantity > 0) {
            shoppingItemQuantity--
        }
    }

    //updating item quantity unit
    fun onUnitSelected(unit: String) {
        shoppingItemUnit = unit
    }

}
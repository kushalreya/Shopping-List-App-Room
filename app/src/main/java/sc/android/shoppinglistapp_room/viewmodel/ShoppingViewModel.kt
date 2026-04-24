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
): ViewModel() {
    var shoppingItemName by mutableStateOf("")
    var shoppingItemQuantity by mutableStateOf(0)
    var shoppingItemUnit by mutableStateOf("")

    //updating data in viewmodel from ui

    fun updateShoppingItemName(newName: String){
        shoppingItemName=newName
    }

    fun updateShoppingItemQuantity(newQuantity: Int){
        shoppingItemQuantity=newQuantity
    }

    fun updateShoppingItemUnit(newUnit: String){
        shoppingItemUnit=newUnit
    }

    //updating data in shopping repository

    fun addItem(shoppingItem: ShoppingItem){
        viewModelScope.launch(Dispatchers.IO) {
            shoppingRepository.addItem(shoppingItem)
        }
    }

    fun updateItem(shoppingItem: ShoppingItem){
        viewModelScope.launch {
            shoppingRepository.updateItem(shoppingItem)
        }
    }

    fun deleteItem(shoppingItem: ShoppingItem){
        viewModelScope.launch {
            shoppingRepository.deleteItem(shoppingItem)
        }
    }

    val getAllItems: Flow<List<ShoppingItem>> = shoppingRepository.getAllItems()

    fun getItemById(id:Long): Flow<ShoppingItem>{
        return shoppingRepository.getItemById(id)
    }
}
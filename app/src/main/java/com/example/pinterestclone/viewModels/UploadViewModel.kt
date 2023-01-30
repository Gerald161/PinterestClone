package com.example.pinterestclone.viewModels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pinterestclone.api.RetrofitInstance
import com.example.pinterestclone.model.AlbumUriModel
import com.example.pinterestclone.model.PinObject
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException

class UploadViewModel: ViewModel() {
    var isLoaded = mutableStateOf(false)

    var allPins = mutableStateListOf<PinObject>()

    var isTextSearchResultsLoaded = mutableStateOf(false)

    var allTextSearchPins = mutableStateListOf<PinObject>()

    var isImageSearchResultsLoaded = mutableStateOf(false)

    var allImageSearchPins = mutableStateListOf<PinObject>()

    fun getPins(){
        viewModelScope.launch {
            val response = try {
                RetrofitInstance.api.GetPins()
            }catch(e: IOException){
                Log.e("internet error", e.toString())
                return@launch
            }catch(e: HttpException){
                Log.e("unexpected error", "Http Exception, unexpected response")
                return@launch
            }

            allPins.clear()

            response.body()?.forEach {
                allPins.add(it)
            }

            isLoaded.value = true
        }
    }

    fun sendImageRequest(file: File, title:String, context: Context){
        viewModelScope.launch {
            val response = try {
                RetrofitInstance.api.UploadPin(
                    MultipartBody.Part.createFormData(
                        "image",
                        file.name,
                        file.asRequestBody()
                    ),
                    MultipartBody.Part.createFormData(
                        "name",
                        title
                    )
                )
            }catch(e: IOException){
                Log.e("internet error", e.toString())
                return@launch
            }catch(e: HttpException){
                Log.e("unexpected error", "Http Exception, unexpected response")
                return@launch
            }

            if(response.body()?.status == "saved"){
                Toast.makeText(
                    context,
                    "Pin has been uploaded",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun sendImageSearchRequest(file: File){
        viewModelScope.launch {
            val response = try {
                RetrofitInstance.api.ImageSearch(
                    MultipartBody.Part.createFormData(
                        "image",
                        file.name,
                        file.asRequestBody()
                    ),
                )
            }catch(e: IOException){
                Log.e("internet error", e.toString())
                return@launch
            }catch(e: HttpException){
                Log.e("unexpected error", "Http Exception, unexpected response")
                return@launch
            }

            allImageSearchPins.clear()

            println(response.body())

            response.body()?.forEach {
                allImageSearchPins.add(it)
            }

            isImageSearchResultsLoaded.value = true
        }
    }

    fun sendWordSearchRequest(word: String){
        viewModelScope.launch {
            val response = try {
                RetrofitInstance.api.WordSearch(
                    word
                )
            }catch(e: IOException){
                Log.e("internet error", e.toString())
                return@launch
            }catch(e: HttpException){
                Log.e("unexpected error", "Http Exception, unexpected response")
                return@launch
            }

            allTextSearchPins.clear()

            println(response.body())

            response.body()?.forEach {
                allTextSearchPins.add(it)
            }

            isTextSearchResultsLoaded.value = true
        }
    }
}
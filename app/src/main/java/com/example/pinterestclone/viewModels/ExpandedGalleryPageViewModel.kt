package com.example.pinterestclone.viewModels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.pinterestclone.model.AlbumUriModel

class ExpandedGalleryPageViewModel: ViewModel() {
    var allAlbums = mutableStateListOf<AlbumUriModel>()

    fun updateGalleryList(album: AlbumUriModel){
        allAlbums.add(album)
    }

//    fun sendImageRequest(file: File){
//        viewModelScope.launch {
//            val response = try {
//                com.example.pinterestclone.api.RetrofitInstance.api.UploadImage(
//                    MultipartBody.Part.createFormData(
//                        "image",
//                        file.name,
//                        file.asRequestBody()
//                    )
//                )
//            }catch(e: IOException){
//                Log.e("internet error", e.toString())
//                return@launch
//            }catch(e: HttpException){
//                Log.e("unexpected error", "Http Exception, unexpected response")
//                return@launch
//            }
//        }
//    }
}
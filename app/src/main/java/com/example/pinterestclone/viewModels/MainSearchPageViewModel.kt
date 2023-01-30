package com.example.pinterestclone.viewModels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.pinterestclone.model.Album

class MainSearchPageViewModel: ViewModel() {
    var searchTerm = mutableStateOf("")
}

class GalleryViewModel: ViewModel() {
    var allAlbums = mutableStateListOf<Album>()

    fun updateGalleryList(album: Album){
        allAlbums.add(album)
    }
}
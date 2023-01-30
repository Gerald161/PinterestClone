package com.example.pinterestclone.viewModels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.pinterestclone.model.AlbumUriModel

class UploadDataViewModel: ViewModel() {
    var name = mutableStateOf("")
}
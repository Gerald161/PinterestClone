package com.example.pinterestclone.model

import android.net.Uri

data class AlbumUriModel (
    val name: String,
    val path: String,
    val id: String,
    val uri: Uri
)
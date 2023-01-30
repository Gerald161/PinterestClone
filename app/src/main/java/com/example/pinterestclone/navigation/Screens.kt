package com.example.pinterestclone.navigation

sealed class Screens(val route: String){
    object HomeScreen: Screens("home")
    object MainSearchScreen: Screens("search/{route}")
    object ExpandedScreen: Screens("expandedScreen/{imageUrl}"){
        fun addPath(imageUrl: String?): String{
            return "expandedScreen/${imageUrl}"
        }
    }
    object GallerySelectScreen: Screens("gallerySelectScreen/{previousRoute}"){
        fun addPath(previousRoute: String): String{
            return "gallerySelectScreen/${previousRoute}"
        }
    }
    object ImageSearchResultsScreen: Screens("ImageSearchResultsScreen")
    object PinUploadScreen: Screens("pinUploadScreen")
    object TextSearchResultsScreen: Screens("TextSearchResultsScreen/{searchTerm}"){
        fun addPath(searchTerm: String): String{
            return "TextSearchResultsScreen/${searchTerm}"
        }
    }
    object ExpandedGalleryScreen : Screens("ExpandedGalleryScreen/{name}/{id}/{route}"){
        fun addPath(name: String, id:String, route: String): String{
            return "ExpandedGalleryScreen/${name}/${id}/${route}"
        }
    }
}

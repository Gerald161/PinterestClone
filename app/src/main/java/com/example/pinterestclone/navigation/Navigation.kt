package com.example.pinterestclone.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pinterestclone.galleryPages.ExpandedGalleryPage
import com.example.pinterestclone.pages.ExpandedPage
import com.example.pinterestclone.galleryPages.GallerySelectPage
import com.example.pinterestclone.pages.HomePage
import com.example.pinterestclone.pages.ImageSearchResultsPage
import com.example.pinterestclone.pages.MainSearchPage
import com.example.pinterestclone.pages.PinUploadPage
import com.example.pinterestclone.pages.TextSearchResultsPage

@Composable
fun Navigation(){
    val navController = rememberNavController()

//    val details_uri = "https://www.pinterestclone.com"

    NavHost(navController = navController, startDestination = Screens.HomeScreen.route,){
        composable(Screens.HomeScreen.route){
            HomePage(navController = navController)
        }
        composable(
            Screens.MainSearchScreen.route,
        ){
            MainSearchPage(navController = navController)
        }
        composable(
            Screens.ExpandedScreen.route,
            arguments = listOf(
                navArgument("imageUrl"){
                    type = NavType.StringType
                    nullable= true
                    defaultValue=""
                },
            )
        ){
            ExpandedPage(it.arguments?.getString("imageUrl"))
        }
        composable(Screens.ImageSearchResultsScreen.route){
            ImageSearchResultsPage(navController)
        }
        composable(
            Screens.GallerySelectScreen.route,
            arguments = listOf(
                navArgument("previousRoute"){
                    type = NavType.StringType
                    nullable= true
                    defaultValue=""
                },
            )
        ){
            GallerySelectPage(navController = navController, it.arguments?.getString("previousRoute"))
        }
        composable(Screens.PinUploadScreen.route){
            PinUploadPage(navController)
        }
        composable(
            Screens.TextSearchResultsScreen.route,
            arguments = listOf(
                navArgument("searchTerm"){
                    type = NavType.StringType
                    nullable= true
                    defaultValue=""
                },
            )
        ){
            TextSearchResultsPage(navController = navController, it.arguments?.getString("searchTerm"))
        }

        composable(
            Screens.ExpandedGalleryScreen.route,
            arguments = listOf(
                navArgument("name"){
                    type = NavType.StringType
                    nullable= true
                    defaultValue="Art"
                },
                navArgument("id"){
                    type = NavType.StringType
                    nullable= true
                    defaultValue="id"
                },
                navArgument("route"){
                    type = NavType.StringType
                    nullable= true
                    defaultValue="route"
                }
            )
        ){
            ExpandedGalleryPage(it.arguments?.getString("name"), it.arguments?.getString("id"), navController, it.arguments?.getString("route"))
        }
    }
}
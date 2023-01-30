package com.example.pinterestclone.pages
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DrawerValue
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.pinterestclone.R
import com.example.pinterestclone.galleryPages.selectedGalleryPhotoPath
import com.example.pinterestclone.galleryPages.selectedGalleryPhotoUri
import com.example.pinterestclone.navigation.Screens
import com.example.pinterestclone.viewModels.UploadViewModel
import java.io.File

@Composable
fun ImageSearchResultsPage(
    navController: NavController
){
    BackHandler{
        selectedGalleryPhotoUri = null

        navController.popBackStack()
    }

    if(selectedGalleryPhotoUri != null){
        val scaffoldState = rememberScaffoldState(rememberDrawerState(initialValue = DrawerValue.Closed))

        val uploadViewModel = viewModel<UploadViewModel>()

        LaunchedEffect(Unit){
            if(!uploadViewModel.isImageSearchResultsLoaded.value){
                val file = File(selectedGalleryPhotoPath!!)

                uploadViewModel.sendImageSearchRequest(file)
            }
        }

        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "Search Results for Image")
                    },
                )
            },
        ){
            Column(
                modifier = Modifier.padding(it)
            ) {
                Spacer(modifier = Modifier.height(25.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ){
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(selectedGalleryPhotoUri)
                                .crossfade(true)
                                .build(),
                            placeholder = painterResource(id = if (isSystemInDarkTheme()) R.drawable.ic_placeholder_dark else R.drawable.ic_placeholder),
                        ),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(100.dp)
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(15.dp))

                if(uploadViewModel.isImageSearchResultsLoaded.value){
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                    ){
                        items(uploadViewModel.allImageSearchPins.size){
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data("http://10.0.2.2:8000/media/${uploadViewModel.allImageSearchPins[it].image}")
                                            .crossfade(true)
                                            .build(),
                                        placeholder = painterResource(if (isSystemInDarkTheme()) R.drawable.ic_placeholder_dark else R.drawable.ic_placeholder),
                                    ),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .aspectRatio(1f)
                                        .border(
                                            width = 0.5.dp,
                                            color = MaterialTheme.colors.background
                                        )
                                        .clickable {
                                            navController.navigate(
                                                Screens.ExpandedScreen.addPath(
                                                    uploadViewModel.allImageSearchPins[it].image
                                                )
                                            )
                                        }
                                )
                            }
                        }
                    }
                }else{
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ){
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }else{
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            CircularProgressIndicator()
        }
    }
}
package com.example.pinterestclone.galleryPages

import android.content.Context
import android.os.Build
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.pinterestclone.R
import com.example.pinterestclone.model.Album
import com.example.pinterestclone.navigation.Screens
import com.example.pinterestclone.viewModels.GalleryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun GallerySelectPage(
    navController: NavController,
    previousRoute: String?
){
    val galleryViewModel = viewModel<GalleryViewModel>()

    val scaffoldState = rememberScaffoldState(rememberDrawerState(initialValue = DrawerValue.Closed))

    val context = LocalContext.current

    LaunchedEffect(Unit){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getGalleryList(context, galleryViewModel)
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Gallery")
                },
            )
        }
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.scale(1.01f)
                .padding(it)
        ){
            items(galleryViewModel.allAlbums.size){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val albumFile = File(galleryViewModel.allAlbums[it].path)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(albumFile)
                                    .crossfade(true)
                                    .build(),
                                placeholder = painterResource(id = if (isSystemInDarkTheme()) R.drawable.ic_placeholder_dark else R.drawable.ic_placeholder),
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
                                    Screens.ExpandedGalleryScreen.addPath(
                                        galleryViewModel.allAlbums[it].name,
                                        galleryViewModel.allAlbums[it].id,
                                        if(previousRoute != null) previousRoute else ""
                                        )
                                    )
                                }
                        )
                        Text(galleryViewModel.allAlbums[it].name)
                    }
                }
            }

        }
    }
}

suspend fun getGalleryList(context: Context, myViewModel: GalleryViewModel){
    myViewModel.allAlbums.clear()

    val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    val projection = arrayOf(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.BUCKET_ID)

    withContext(Dispatchers.IO){
        val cursor = context.contentResolver.query(
            contentUri,
            projection,
            null,
            null,
            "${MediaStore.Images.ImageColumns.DATE_MODIFIED} DESC"
        )

        val allAlbums: MutableList<String> = arrayListOf()

        if (cursor != null) {
            while (cursor.moveToNext()) {
                val name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME))
                val path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)) // Thumb image path
                val id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.BUCKET_ID))

                if(allAlbums.contains(id)){
                    continue
                }else{
                    allAlbums.add(id)

                    if(name != null){
                        myViewModel.updateGalleryList(Album(name, path, id))
                    }else{
                        myViewModel.updateGalleryList(Album("0", path, id))
                    }
                }
            }

            myViewModel.allAlbums.sortBy {
                it.name
            }

            cursor.close()
        }
    }

}
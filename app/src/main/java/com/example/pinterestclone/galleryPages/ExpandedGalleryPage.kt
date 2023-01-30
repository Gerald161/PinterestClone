package com.example.pinterestclone.galleryPages

import android.content.ContentUris
import android.content.Context
import android.net.Uri
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
import com.example.pinterestclone.model.AlbumUriModel
import com.example.pinterestclone.navigation.Screens
import com.example.pinterestclone.viewModels.ExpandedGalleryPageViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

var selectedGalleryPhotoUri : Uri? = null

var selectedGalleryPhotoPath : String? = null

@Composable
fun ExpandedGalleryPage(
    name: String?,
    id: String?,
    navController: NavController,
    route: String?
){
    val context = LocalContext.current

    val scaffoldState = rememberScaffoldState(rememberDrawerState(initialValue = DrawerValue.Closed))

    val myViewModel = viewModel<ExpandedGalleryPageViewModel>()

    LaunchedEffect(Unit){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getSpecificGalleryImages(context, myViewModel, id)
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    if (name != null) {
                        Text(text = name)
                    }else{
                        Text("")
                    }
                },
            )
        }
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.scale(1.01f)
                .padding(it)
        ){
            items(myViewModel.allAlbums.size){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val albumFile = File(myViewModel.allAlbums[it].path)

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
                                ).clickable {
                                    selectedGalleryPhotoUri = myViewModel.allAlbums[it].uri

                                    selectedGalleryPhotoPath = myViewModel.allAlbums[it].path

                                    if(route == "image"){
                                        navController.popBackStack(route = Screens.MainSearchScreen.route, inclusive = false)
                                    }else{
                                        navController.popBackStack(route = Screens.PinUploadScreen.route, inclusive = false)
                                    }
                                }
                        )
                    }
                }
            }

        }
    }
}

suspend fun getSpecificGalleryImages(context: Context, myViewModel: ExpandedGalleryPageViewModel, ID:String?){
    myViewModel.allAlbums.clear()

    val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    val projection = arrayOf(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.BUCKET_ID, MediaStore.Images.Media._ID)

    withContext(Dispatchers.IO){
        val cursor = context.contentResolver.query(
            contentUri,
            projection,
//            "${MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME} = ?",
//            arrayOf(AlbumName),
            "${MediaStore.Images.ImageColumns.BUCKET_ID} = ?",
            arrayOf(ID),
            "${MediaStore.Images.ImageColumns.DATE_MODIFIED} DESC"
        )

        if (cursor != null) {
            while (cursor.moveToNext()) {
                val name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME))
                val path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))
                val id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.BUCKET_ID))
                val contentID = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))

                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentID
                )

                if(name != null){
                    myViewModel.updateGalleryList(AlbumUriModel(name, path, id, contentUri))
                }else{
                    myViewModel.updateGalleryList(AlbumUriModel("0", path, id, contentUri))
                }

            }

            cursor.close()

        }
    }
}
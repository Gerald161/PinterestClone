package com.example.pinterestclone.pages

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.DrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pinterestclone.BuildConfig
import com.example.pinterestclone.galleryPages.selectedGalleryPhotoUri
import com.example.pinterestclone.navigation.Screens
import com.example.pinterestclone.viewModels.MainSearchPageViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainSearchPage(
    navController: NavController,
){
    val scaffoldState = rememberScaffoldState(rememberDrawerState(initialValue = DrawerValue.Closed))

    val searchTermViewModel = viewModel<MainSearchPageViewModel>()

    val openDialog = remember { mutableStateOf(false) }

    val context = LocalContext.current

    LaunchedEffect(Unit){
        if(selectedGalleryPhotoUri != null){
            navController.navigate(Screens.ImageSearchResultsScreen.route)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {isGranted ->
            if(!isGranted){
                if(ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, Manifest.permission.READ_EXTERNAL_STORAGE)){
                    openDialog.value = true
                }else{
                    Toast.makeText(
                        context,
                        "Please enable 'Files and Media Permissions' in settings before you may proceed",
                        Toast.LENGTH_LONG
                    ).show()

                    val i = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+ BuildConfig.APPLICATION_ID))

                    context.startActivity(i)
                }
            }
        }
    )

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false

                Toast.makeText(
                    context,
                    "Please enable Files and Media Permissions in settings before you may proceed",
                    Toast.LENGTH_LONG
                ).show()

                //open app settings
                val i = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+ BuildConfig.APPLICATION_ID))

                context.startActivity(i)
            },
            title = {
                Text(text = "Permission needed")
            },
            text = {
                Text("Permission needed before photos may be accessed and uploaded ")
            },
            confirmButton = {
                Button(
                    onClick = {
                        openDialog.value = false

                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }) {
                    Text("Ok")
                }
            },
        )
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
            ) {
                TextField(
                    value = searchTermViewModel.searchTerm.value,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(6f),
                    onValueChange = {
                        searchTermViewModel.searchTerm.value = it
                    },
                    placeholder = {
                        Text(
                            text = "Type here...",
                        )
                    },
                    textStyle = TextStyle(
                        fontSize = MaterialTheme.typography.subtitle1.fontSize
                    ),
                    singleLine = true,
                    leadingIcon = {
                        IconButton(
                            onClick = {
                                navController.popBackStack()
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Search",
                            )
                        }
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                navController.navigate(Screens.TextSearchResultsScreen.addPath(searchTermViewModel.searchTerm.value))
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            navController.navigate(Screens.TextSearchResultsScreen.addPath(searchTermViewModel.searchTerm.value))
                        }
                    )
                )
                IconButton(
                    onClick = {
                        val hasReadPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.READ_MEDIA_IMAGES
                            ) == PackageManager.PERMISSION_GRANTED
                        } else {
                            ContextCompat.checkSelfPermission(context,
                                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        }

                        if(!hasReadPermission){
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                            }else{
                                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                            }
                        }else{
                            navController.navigate(Screens.GallerySelectScreen.addPath("image"))
                        }
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(
                        imageVector = Icons.Default.ImageSearch,
                        contentDescription = "Image Search"
                    )
                }
            }
        }
    ) {
        Box(modifier = Modifier.padding(it))
    }
}
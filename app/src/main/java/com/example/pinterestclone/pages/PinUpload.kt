package com.example.pinterestclone.pages
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.DrawerValue
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.pinterestclone.BuildConfig
import com.example.pinterestclone.R
import com.example.pinterestclone.galleryPages.selectedGalleryPhotoPath
import com.example.pinterestclone.galleryPages.selectedGalleryPhotoUri
import com.example.pinterestclone.navigation.Screens
import com.example.pinterestclone.viewModels.UploadDataViewModel
import com.example.pinterestclone.viewModels.UploadViewModel
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PinUploadPage(
    navController: NavController
){
    BackHandler{
        selectedGalleryPhotoUri = null

        navController.popBackStack()
    }

    val scaffoldState = rememberScaffoldState(rememberDrawerState(initialValue = DrawerValue.Closed))

    val nameViewModel = viewModel<UploadDataViewModel>()

    val uploadViewModel = viewModel<UploadViewModel>()

    val context = LocalContext.current

    val keyboardController = LocalSoftwareKeyboardController.current

    val openDialog = remember { mutableStateOf(false) }

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
            TopAppBar(
                title = {
                    Text(text = "Upload Pin")
                },
            )
        },
    ){
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = nameViewModel.name.value,
                onValueChange = {
                    nameViewModel.name.value = it
                },
                label = {
                    Text("Name")
                },
                placeholder = {
                    Text("Type Here...")
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Abc, contentDescription = "Name")
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {keyboardController?.hide()}
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Image(
                painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(
                            if(selectedGalleryPhotoUri != null){
                                selectedGalleryPhotoUri
                            }else{
                                if (isSystemInDarkTheme()) R.drawable.ic_placeholder_dark else R.drawable.ic_placeholder
                            }
                        )
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(id = if (isSystemInDarkTheme()) R.drawable.ic_placeholder_dark else R.drawable.ic_placeholder),
                ),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.clip(RoundedCornerShape(15.dp))
                    .clickable {
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
                            navController.navigate(Screens.GallerySelectScreen.addPath("stuff"))
                        }
                    }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                if(selectedGalleryPhotoUri == null && nameViewModel.name.value == ""){
                    Toast.makeText(
                        context,
                        "Please provide a name and upload an image",
                        Toast.LENGTH_SHORT
                    ).show()
                }else if(selectedGalleryPhotoUri != null && nameViewModel.name.value == ""){
                    Toast.makeText(
                        context,
                        "Please provide a name",
                        Toast.LENGTH_SHORT
                    ).show()
                }else if(selectedGalleryPhotoUri == null && nameViewModel.name.value != ""){
                    Toast.makeText(
                        context,
                        "Please upload an image",
                        Toast.LENGTH_SHORT
                    ).show()
                }else{
                    Toast.makeText(
                        context,
                        "Uploading pin please wait",
                        Toast.LENGTH_SHORT
                    ).show()
                    val file = File(selectedGalleryPhotoPath!!)

                    uploadViewModel.sendImageRequest(file, nameViewModel.name.value, context)
                }
            }) {
                Text("Upload")
            }
        }
    }
}
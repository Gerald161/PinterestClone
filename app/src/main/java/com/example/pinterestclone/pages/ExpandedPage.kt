package com.example.pinterestclone.pages

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.webkit.CookieManager
import android.webkit.URLUtil
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.pinterestclone.R

@Composable
fun ExpandedPage(
    imageUrl: String?
){
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Image(
            painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("http://10.0.2.2:8000/media/$imageUrl")
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(id = R.drawable.ic_placeholder),
            ),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .aspectRatio(1f)
//                .clickable {
//                    val url = "http://10.0.2.2:8000/media/$imageUrl"
//
//                    val request = DownloadManager.Request(Uri.parse(url))
//
//                    val title = URLUtil.guessFileName(url, null, null)
//
//                    request.setTitle(title)
//
//                    request.setDescription("Downloading image please wait...")
//
//                    val cookie = CookieManager.getInstance().getCookie(url)
//
//                    request.addRequestHeader("cookie", cookie)
//
//                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//
//                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title)
//
//                    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
//
//                    downloadManager.enqueue(request)
//
//                    Toast.makeText(
//                        context,
//                        "Image Downloaded",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
        )
    }
}
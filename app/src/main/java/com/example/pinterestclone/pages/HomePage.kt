package com.example.pinterestclone.pages

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.pinterestclone.R
import com.example.pinterestclone.navigation.Screens
import com.example.pinterestclone.viewModels.UploadViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterialApi::class)
@Composable
fun HomePage(
    navController: NavController
){
    val scaffoldState = rememberScaffoldState(rememberDrawerState(initialValue = DrawerValue.Closed))

    val coroutineScope = rememberCoroutineScope()

    var selectedIndex by remember {
        mutableStateOf(0)
    }

    val pagerState = rememberPagerState(pageCount = 2)

    val uploadViewModel = viewModel<UploadViewModel>()

    val refreshScope = rememberCoroutineScope()

    var refreshing by remember { mutableStateOf(false) }

    fun refresh() = refreshScope.launch {
        refreshing = true

        uploadViewModel.getPins()

        refreshing = false
    }

    val pullRefreshState = rememberPullRefreshState(refreshing, ::refresh)

    LaunchedEffect(Unit){
        uploadViewModel.getPins()
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Pinterest Clone")
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate(Screens.MainSearchScreen.route)
                    }) {
                        Icon(imageVector = Icons.Filled.Search, contentDescription = "Search")
                    }
                },
            )
        },
        bottomBar = {
            BottomAppBar {
                BottomNavigation {
                    BottomNavigationItem(
                        selected = selectedIndex == 0,
                        onClick = {
                            selectedIndex = 0

                            coroutineScope.launch {
                                pagerState.animateScrollToPage(0)
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Home"
                            )
                        },
                        label = {
                            Text("Home")
                        }
                    )
                    BottomNavigationItem(
                        selected = selectedIndex == 1,
                        onClick = {
                            selectedIndex = 1

                            coroutineScope.launch {
                                pagerState.animateScrollToPage(1)
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Profile Page"
                            )
                        },
                        label = {
                            Text("Profile")
                        }
                    )
                }
            }
        },

        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Screens.PinUploadScreen.route)
                },
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = it)
                .pullRefresh(pullRefreshState)
        ) {
            HorizontalPager(
                state = pagerState,
            ) {pager->
                selectedIndex = if(pagerState.currentPage == 0){
                    0
                }else{
                    1
                }

                when(pager){
                    0->{
                        if(uploadViewModel.isLoaded.value){
                            Box(Modifier.pullRefresh(pullRefreshState)) {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    modifier=Modifier.fillMaxSize()
                                ){
                                    items(uploadViewModel.allPins.size){
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                            Image(
                                                painter = rememberAsyncImagePainter(
                                                    model = ImageRequest.Builder(LocalContext.current)
                                                        .data("http://10.0.2.2:8000/media/${uploadViewModel.allPins[it].image}")
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
                                                                uploadViewModel.allPins[it].image
                                                            )
                                                        )
                                                    }
                                            )
                                        }
                                    }
                                }

                                PullRefreshIndicator(refreshing, pullRefreshState, Modifier.align(Alignment.TopCenter))
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
                    1->{
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ){
                            Text("Profile")
                        }
                    }
                }
            }
        }
    }
}
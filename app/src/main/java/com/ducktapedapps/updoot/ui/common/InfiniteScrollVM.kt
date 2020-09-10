package com.ducktapedapps.updoot.ui.common

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

@ExperimentalCoroutinesApi
interface InfiniteScrollVM {
    fun loadPage()
    fun hasNextPage(): Boolean
    val isLoading: StateFlow<Boolean>
}
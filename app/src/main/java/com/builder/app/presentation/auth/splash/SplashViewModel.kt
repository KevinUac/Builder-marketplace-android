package com.builder.app.presentation.auth.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.builder.app.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _navigationEvent = MutableSharedFlow<SplashNavigation>(replay = 1)
    val navigationEvent = _navigationEvent.asSharedFlow()

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            val user = repository.getSession().first()
            if (user != null) {
                _navigationEvent.emit(SplashNavigation.NavigateToHome)
            } else {
                _navigationEvent.emit(SplashNavigation.NavigateToRoleSelection)
            }
        }
    }
}

sealed class SplashNavigation {
    data object NavigateToHome : SplashNavigation()
    data object NavigateToRoleSelection : SplashNavigation()
}

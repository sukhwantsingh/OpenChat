package com.codingwithmitch.openchat.auth.framework.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.codingwithmitch.openchat.R
import com.codingwithmitch.openchat.common.framework.presentation.BaseApplication
import com.codingwithmitch.openchat.auth.framework.presentation.navigation.AuthScreen
import com.codingwithmitch.openchat.auth.framework.presentation.screens.CreateAccountScreen
import com.codingwithmitch.openchat.auth.framework.presentation.screens.LoginScreen
import com.codingwithmitch.openchat.auth.framework.presentation.screens.PasswordResetScreen
import com.codingwithmitch.openchat.common.business.domain.util.printLogD
import com.codingwithmitch.openchat.common.framework.presentation.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalFocus
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class AuthFragment: Fragment() {

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setupChannel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.compose_view, container, false
        ).apply {
            findViewById<ComposeView>(R.id.compose_view).setContent {
                val progressBarState by viewModel.shouldDisplayProgressBar.collectAsState()
                printLogD("AuthFragment", "PROGRESS BAR: ${progressBarState}")

                val stateMessageState by viewModel.stateMessage.collectAsState()
                printLogD("AuthFragment", "STATE MESSAGE: ${stateMessageState}")

                AppTheme(
                        darkTheme = !(activity?.application as BaseApplication).isLight,
                ) {
                    val viewState by viewModel.viewState.collectAsState()
                    val screen = viewState.screen

                    Surface(
                            color = MaterialTheme.colors.background
                    ) {
                        when(screen){
                            is AuthScreen.Login -> {
                                LoginScreen(viewModel = viewModel)
                            }
                            is AuthScreen.PasswordReset -> {
                                PasswordResetScreen(viewModel = viewModel)
                            }
                            is AuthScreen.CreateAccount -> {
                                CreateAccountScreen(viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBackPressDispatcher()

        viewModel.sessionState.observe(viewLifecycleOwner, {sessionState ->
            if(sessionState?.authToken != null){
                findNavController().navigate(R.id.action_authFragment_to_mainFragment)
            }
        })
    }

    private fun initBackPressDispatcher(){
        activity?.let { activity ->
            activity.onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if(!viewModel.onBack()){
                        isEnabled = false

                        /**
                         * Work-around for know memory leak issue:
                         * https://issuetracker.google.com/issues/139738913
                         */
                        if (activity.isTaskRoot) {
                            activity.finishAfterTransition()
                        }
                        else {
                            activity.onBackPressed()
                        }
                    }
                }
            })
        }

    }

}






















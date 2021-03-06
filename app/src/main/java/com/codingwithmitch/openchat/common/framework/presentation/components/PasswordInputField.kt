package com.codingwithmitch.openchat.common.framework.presentation.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focusObserver
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.codingwithmitch.openchat.R
import com.codingwithmitch.openchat.common.framework.presentation.state.TextFieldState

@ExperimentalFocus
@Composable
fun PasswordInputField(
    passwordState: TextFieldState,
    onPasswordChange: (String) -> Unit,
    modifier: Modifier,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit,
    showPassword: Boolean,
    onShowPasswordChange: (Boolean) -> Unit,
) {
    TextField(
            value = passwordState.text,
            onValueChange = {
                onPasswordChange(it)
            },
            label = {
                Text(text = passwordState.getLabel())
            },
            visualTransformation = if (showPassword) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            modifier = modifier.focusObserver { focusState ->
                val focused = focusState == FocusState.Active
                passwordState.onFocusChange(focused)
                if (!focused) {
                    passwordState.checkEnableShowErrors()
                }
            },
            keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = imeAction,
            ),
            leadingIcon = { Icon(Icons.Filled.Lock) },
            onImeActionPerformed = { action, softKeyboardController ->
                if (action == ImeAction.Done || action == ImeAction.Next) {
                    softKeyboardController?.hideSoftwareKeyboard()
                }
                onImeAction()
            },
            trailingIcon = {
                if (showPassword) {
                    IconButton(onClick = {
                        onShowPasswordChange(!showPassword)
                    }) {
                        Icon(asset = vectorResource(id = R.drawable.ic_baseline_visibility_on_24))
                    }
                } else {
                    IconButton(onClick = {
                        onShowPasswordChange(!showPassword)
                    }) {
                        Icon(asset = vectorResource(id = R.drawable.ic_baseline_visibility_off_24))
                    }
                }
            },
            isErrorValue = passwordState.isErrors(),
    )
    if(passwordState.isErrors()) TextFieldError(textError = passwordState.getErrorMessage())

}
















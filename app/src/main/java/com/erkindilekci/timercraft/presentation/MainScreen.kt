package com.erkindilekci.timercraft.presentation

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.erkindilekci.timercraft.presentation.ui.theme.Blue
import com.erkindilekci.timercraft.presentation.ui.theme.Light
import com.erkindilekci.timercraft.presentation.ui.theme.Red
import com.erkindilekci.timercraft.service.ServiceHelper
import com.erkindilekci.timercraft.service.StopwatchService
import com.erkindilekci.timercraft.service.StopwatchState
import com.erkindilekci.timercraft.util.Constants.ACTION_SERVICE_CANCEL
import com.erkindilekci.timercraft.util.Constants.ACTION_SERVICE_START
import com.erkindilekci.timercraft.util.Constants.ACTION_SERVICE_STOP
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@ExperimentalAnimationApi
@Composable
fun MainScreen(stopwatchService: StopwatchService) {
    val context = LocalContext.current
    val hours by stopwatchService.hours
    val minutes by stopwatchService.minutes
    val seconds by stopwatchService.seconds
    val currentState by stopwatchService.currentState

    val configuration = LocalConfiguration.current
    val weight = if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 1f else 2f

    val systemUiController = rememberSystemUiController()
    val statusBarColor = MaterialTheme.colorScheme.surface

    SideEffect {
        systemUiController.setStatusBarColor(statusBarColor)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(30.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(weight = 9f),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedContent(targetState = hours, transitionSpec = { addAnimation() }) {
                Text(
                    text = hours,
                    style = TextStyle(
                        fontSize = 70.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (hours == "00") if (isSystemInDarkTheme()) Color.White else Color(
                            0xFF030303
                        ) else Blue
                    )
                )
            }
            AnimatedContent(targetState = minutes, transitionSpec = { addAnimation() }) {
                Text(
                    text = minutes, style = TextStyle(
                        fontSize = 70.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (minutes == "00") if (isSystemInDarkTheme()) Color.White else Color(
                            0xFF030303
                        ) else Blue
                    )
                )
            }
            AnimatedContent(targetState = seconds, transitionSpec = { addAnimation() }) {
                Text(
                    text = seconds, style = TextStyle(
                        fontSize = 70.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (seconds == "00") if (isSystemInDarkTheme()) Color.White else Color(
                            0xFF030303
                        ) else Blue
                    )
                )
            }
        }

        Row(modifier = Modifier.weight(weight = weight)) {
            Button(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(0.8f),
                onClick = {
                    ServiceHelper.triggerForegroundService(
                        context = context,
                        action = if (currentState == StopwatchState.Started) ACTION_SERVICE_STOP
                        else ACTION_SERVICE_START
                    )
                }, colors = ButtonDefaults.buttonColors(
                    containerColor = if (currentState == StopwatchState.Started) Red else Blue,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = if (currentState == StopwatchState.Started) "Stop"
                    else if ((currentState == StopwatchState.Stopped)) "Resume"
                    else "Start"
                )
            }
            Spacer(modifier = Modifier.width(30.dp))
            Button(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(0.8f),
                onClick = {
                    ServiceHelper.triggerForegroundService(
                        context = context, action = ACTION_SERVICE_CANCEL
                    )
                },
                enabled = seconds != "00" && currentState != StopwatchState.Started,
                colors = ButtonDefaults.buttonColors(disabledContainerColor = if (isSystemInDarkTheme()) Light else Color.LightGray)
            ) {
                Text(text = "Cancel", color = Color.White)
            }
        }
    }
}

@ExperimentalAnimationApi
fun addAnimation(duration: Int = 600): ContentTransform {
    return slideInVertically(animationSpec = tween(durationMillis = duration)) { height -> height } + fadeIn(
        animationSpec = tween(durationMillis = duration)
    ) with slideOutVertically(animationSpec = tween(durationMillis = duration)) { height -> height } + fadeOut(
        animationSpec = tween(durationMillis = duration)
    )
}

package me.niwat.mvvm.presenter.camera

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.niwat.mvvm.base.BaseViewModel
import me.niwat.mvvm.utils.SingleLiveEvent

class CameraFragmentViewModel : BaseViewModel() {
    val isDetected: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val isDidCondition: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val isPlaySound: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val textSuggestion: SingleLiveEvent<String> = SingleLiveEvent()
    val rotY: SingleLiveEvent<Float> = SingleLiveEvent()

    fun doCondition() {
        if (isDetected.value == true) {
            viewModelScope.launch {
                delay(2000)
                isPlaySound.value = true
                turnLeft()
            }
        }
    }

    private fun turnLeft() {
        textSuggestion.value = "Look over left shoulder"
    }

    private fun turnRight() {
        textSuggestion.value = "Look over right shoulder"
    }

    private fun lookDown() {
        textSuggestion.value = "Look down"
    }
}

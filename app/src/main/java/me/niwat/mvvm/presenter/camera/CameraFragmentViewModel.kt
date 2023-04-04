package me.niwat.mvvm.presenter.camera

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.niwat.mvvm.base.BaseViewModel

class CameraFragmentViewModel : BaseViewModel() {
    val isDetected: MutableLiveData<Boolean> = MutableLiveData()
    val isDidCondition: MutableLiveData<Boolean> = MutableLiveData()
    val textSuggestion: MutableLiveData<String> = MutableLiveData()
    val rotY: MutableLiveData<Float> = MutableLiveData()

    fun doCondition() {
        if (isDetected.value == true) {
            viewModelScope.launch {
                delay(2000)
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

    fun success() {
        viewModelScope.launch {
            delay(1000)
        }
    }
}
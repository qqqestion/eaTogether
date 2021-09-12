package ru.blackbull.eatogether.ui.main.myparties

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.blackbull.eatogether.other.Event
import ru.blackbull.domain.Resource
import ru.blackbull.domain.PartyDataSource
import ru.blackbull.domain.UseCase
import ru.blackbull.domain.models.firebase.DomainPartyWithUser
import ru.blackbull.domain.usecases.GetPartiesByCurrentUserCase
import ru.blackbull.eatogether.R
import ru.blackbull.eatogether.other.UiStateWithData
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MyPartyViewModel @Inject constructor(
    private val useCase: GetPartiesByCurrentUserCase
) : ViewModel() {

    private val _userParties: MutableLiveData<UiStateWithData<List<DomainPartyWithUser>>> =
        MutableLiveData()
    val userParties: LiveData<UiStateWithData<List<DomainPartyWithUser>>> = _userParties

    fun getPartiesByCurrentUser() = viewModelScope.launch {
        _userParties.postValue(UiStateWithData.Loading())
        useCase.invoke(UseCase.None,viewModelScope){ result ->
            result.fold({
                _userParties.postValue(UiStateWithData.Failure(error = it,messageId = R.string.error_default))
            },{
                _userParties.postValue(UiStateWithData.Success(it))
            })
        }

    }

}
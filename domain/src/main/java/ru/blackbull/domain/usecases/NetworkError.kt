package ru.blackbull.domain.usecases

sealed interface NetworkError :
    GetUserError,
    CheckAuthenticatedUseCaseError,
    SignInError,
    CheckRegistrationCompleteUseCaseError,
    SignUpError,
    CompleteRegistrationUseCaseError

object NoInternetError : NetworkError
object UnexpectedNetworkCommunicationError : NetworkError

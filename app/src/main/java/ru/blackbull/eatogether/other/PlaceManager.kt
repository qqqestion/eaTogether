package ru.blackbull.eatogether.other

import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.*
import com.yandex.runtime.Error
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import ru.blackbull.data.models.mapkit.PlaceDetail
import ru.blackbull.data.models.mapkit.toPlaceDetail
import ru.blackbull.domain.AppCoroutineDispatchers
import ru.blackbull.domain.Constants
import ru.blackbull.domain.functional.Either
import ru.blackbull.domain.functional.Left
import ru.blackbull.domain.functional.Right
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

data class Cuisine(
    val id: String,
    val name: String,
)

class SearchListener(
    private val onSuccess: (response: Response) -> Unit,
    private val onError: (error: Error) -> Unit,
) : Session.SearchListener {
    override fun onSearchResponse(response: Response) = onSuccess(response)

    override fun onSearchError(error: Error) = onError(error)
}

sealed interface MapError {

    object RemoteError : MapError
    object NetworkError : MapError
    object DefaultError : MapError
}

/**
 * Репозиторий для работы с Yandex MapKit
 *
 * @property searchManager
 */
@Singleton
class PlaceManager @Inject constructor(
    private val searchManager: SearchManager,
    private val dispatchers: AppCoroutineDispatchers
) {

    private val job = SupervisorJob()

    private val _placeDetail = MutableSharedFlow<Either<MapError, PlaceDetail>>(
        0,
        1,
        BufferOverflow.DROP_OLDEST
    )
    val placeDetail = _placeDetail.asSharedFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val cuisines = callbackFlow {
        val point = Geometry.fromPoint(Point(59.95, 30.32))

        filterSession = withContext(dispatchers.main) {
            searchManager.submit(
                "cafe",
                point,
                SearchOptions(),
                SearchListener(
                    { response ->
                        val cuisines = response.metadata.businessResultMetadata?.businessFilters
                            ?.find { it.id == "type_cuisine" }
                            ?.values
                            ?.enums
                            ?.map { Cuisine(it.value.id, it.value.name) }
                            ?.sortedBy { it.name }
                            ?: listOf()
                        Timber.d("Cuisine list: $cuisines")
                        trySend(Either.Right(cuisines))
                    },
                    { error ->
                        trySend(Either.Left(error))
                    }
                )
            )
        }
        awaitClose {}
    }.stateIn(CoroutineScope(job), SharingStarted.Lazily, Either.Right(emptyList()))

    private var filterSession: Session? = null

    private val _places: MutableStateFlow<Either<MapError, List<PlaceDetail>>> =
        MutableStateFlow(Either.Right(emptyList()))
    val places = _places.asStateFlow()

    private var searchSession: Session? = null

    fun search(query: String, geometry: Geometry, cuisines: Set<String> = emptySet()) {
        searchSession = searchManager.submit(
            query,
            geometry,
            SearchOptions().apply {
                snippets = Snippet.BUSINESS_RATING1X.value // Соединять сниппеты можно через ИЛИ
            },
            SearchListener(::handleSearchResponse, ::handleSearchError)
        )
        if (cuisines.isNotEmpty()) {
            val featureList = cuisines
                .map {
                    BusinessFilter.EnumValue(
                        Feature.FeatureEnumValue(
                            it,
                            "",
                            ""
                        ),
                        true,
                        true
                    )
                }
            val cuisineTypeEnumFilter = BusinessFilter(
                "type_cuisine",
                "",
                false,
                false,
                BusinessFilter.Values.fromEnums(
                    featureList
                )
            )
            searchSession?.setFilters(listOf(cuisineTypeEnumFilter))
            // Так как нельзя сразу поставить фильтры, сначала делаем обычный запрос по параметру поиска (query)
            // Потом ставим фильтры и делаем перезапрос
            searchSession?.resubmit(SearchListener(::handleSearchResponse, ::handleSearchError))
        }
    }

    /**
     * Обрабатывает ответ с яндекс карт.
     * Оставляет только те объекты, категория которых подходит под общественное питание
     *
     * @param response
     */
    private fun handleSearchResponse(response: Response) {
        val placeDetails = response.collection.children
            .mapNotNull { item ->
                item.obj
            }
            .filter { geoObj ->
                geoObj.metadataContainer.getItem(BusinessObjectMetadata::class.java).categories.any {
                    it.categoryClass in Constants.FOOD_CATEGORIES
                }
            }
            .map { geoObj ->
                geoObj.toPlaceDetail()
            }
        _places.value = Right(placeDetails)
    }

    private fun handleSearchError(error: Error) {
        Timber.d("Search error: $error")
        _places.value = Left(mapError(error))
    }

    private fun mapError(error: Error) = when (error) {
        is RemoteError -> MapError.RemoteError
        is NetworkError -> MapError.NetworkError
        else -> MapError.DefaultError
    }

    private val baseUri = "ymapsbm1://org?oid=%s"

    fun fetchPlaceDetail(placeId: String) {
        searchSession = searchManager.resolveURI(
            baseUri.format(placeId),
            SearchOptions().apply {
                snippets = Snippet.BUSINESS_RATING1X.value // Дополнительная информация о местах
            },
            object : Session.SearchListener {
                override fun onSearchResponse(response: Response) {
                    val detail = response.collection.children.firstOrNull()?.obj?.toPlaceDetail()
                    if (detail == null) {
                        Timber.e(
                            "Place detail is null: ${
                                response.collection.children.map { it.obj?.toPlaceDetail() }
                            }"
                        )
                        return
                    }
                    _placeDetail.tryEmit(Right(detail))
                }

                override fun onSearchError(error: Error) {
                    _placeDetail.tryEmit(Left(mapError(error)))
                }
            }
        )
    }
}

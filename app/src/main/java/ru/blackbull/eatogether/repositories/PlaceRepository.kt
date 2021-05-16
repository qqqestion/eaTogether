package ru.blackbull.eatogether.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.*
import com.yandex.runtime.Error
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError
import ru.blackbull.eatogether.models.CuisineType
import ru.blackbull.eatogether.models.PlaceDetail
import ru.blackbull.eatogether.models.mappers.PlaceDetailMapper
import ru.blackbull.eatogether.other.Constants
import ru.blackbull.eatogether.other.Event
import ru.blackbull.eatogether.other.Resource
import timber.log.Timber
import javax.inject.Inject


class PlaceRepository @Inject constructor(
    private val searchManager: SearchManager ,
    private val placeDetailMapper: PlaceDetailMapper
) {
//
//    suspend fun getNearbyPlaces(
//        location: String
//    ): Resource<List<BasicLocation>> = withContext(Dispatchers.IO) {
//        val response = googlePlaceApiService.getNearbyPlaces(location)
//        return@withContext if (response.status == "OK") {
//            Resource.Success(response.placeList)
//        } else {
//            Resource.Error(msg = response.errorMessage)
//        }
//    }

    private val _placeDetail: MutableLiveData<Event<Resource<PlaceDetail>>> = MutableLiveData()
    val placeDetail: LiveData<Event<Resource<PlaceDetail>>> = _placeDetail

    val cuisine: MutableLiveData<Event<Resource<List<CuisineType>>>> = MutableLiveData()

    private var filterSession: Session? = null

    /**
     * Делаем запрос объектов "кафе", находим в бизнес фильтрах тип кухни и вытаскиваем значения
     * "кафе" - как оптимальный запрос (хотя можно и "ресторан" или что-то такое)
     * point - просто точка
     * Если типы кухонь уже имеются, то еще раз запрос не происходит
     *
     */
    fun getCuisineList() {
        if (cuisine.value != null) {
            return
        }
        val point = Geometry.fromPoint(Point(59.95 , 30.32))

        filterSession = searchManager.submit(
            "cafe" ,
            point ,
            SearchOptions() ,
            object : Session.SearchListener {
                override fun onSearchResponse(response: Response) {
                    val cuisineList = response.metadata.businessResultMetadata?.businessFilters
                        ?.find { it.id == "type_cuisine" }
                        ?.values
                        ?.enums
                        ?.map { CuisineType(it.value.id , it.value.name) }
                        ?.sortedBy { it.name }
                        ?: listOf()
                    Timber.d("Cuisine list: $cuisineList")
                    cuisine.postValue(Event(Resource.Success(cuisineList)))
                }

                override fun onSearchError(error: Error) {
                    val errorMessage = when (error) {
                        is RemoteError -> "Remote error"
                        is NetworkError -> "Network error"
                        else -> "Unknown error"
                    }
                    Timber.d("Error during map sdk request: $errorMessage")
                    cuisine.postValue(Event(Resource.Error(null , errorMessage)))
                }
            }
        )
    }

    private val _searchPlaces: MutableLiveData<Event<Resource<List<PlaceDetail>>>> =
        MutableLiveData()
    val searchPlaces: LiveData<Event<Resource<List<PlaceDetail>>>> = _searchPlaces

    private var searchSession: Session? = null

    fun search(query: String , geometry: Geometry) {
        searchSession = searchManager.submit(
            query ,
            geometry ,
            SearchOptions().apply {
                /**
                 * Соединять сниппеты можно через ИЛИ
                 */
                snippets = Snippet.BUSINESS_RATING1X.value
            } ,
            object : Session.SearchListener {
                override fun onSearchResponse(response: Response) {
                    handleSearchResponse(response)
                }

                override fun onSearchError(error: Error) {
                    handleSearchError(error)
                }
            }
        )
        if (cuisine.value != null) {
            /**
             * Берем значение из LiveData (если оно там есть)
             * Отбираем выбранные виды кухни
             * Создаем BusinessFilter.EnumValue()
             */
            val featureList = cuisine.value!!.peekContent().data
                ?.filter {
                    it.isChecked
                }
                ?.map {
                    Timber.d("Cuisine: ${it.name}")
                    BusinessFilter.EnumValue(
                        Feature.FeatureEnumValue(
                            it.id ,
                            "" ,
                            ""
                        ) ,
                        true ,
                        true
                    )
                }
                ?: listOf()
            val cuisineTypeEnumFilter = BusinessFilter(
                "type_cuisine" ,
                "" ,
                false ,
                false ,
                BusinessFilter.Values.fromEnums(
                    featureList
                )
            )
            searchSession?.setFilters(listOf(cuisineTypeEnumFilter))
            /**
             * Так как нельзя сразу поставить фильтры, сначала делаем обычный запрос по параметру поиска (query)
             * Потом ставим фильтры и делаем перезапрос
             */
            searchSession?.resubmit(
                object : Session.SearchListener {
                    override fun onSearchResponse(response: Response) {
                        handleSearchResponse(response)
                    }

                    override fun onSearchError(error: Error) {
                        handleSearchError(error)
                    }
                }
            )
        }
    }

    /**
     * Обрабатывает ответ с яндекс карт.
     * Оставляет только те объекты, категория которых подходит под общественное питание
     *
     * @param response
     */
    private fun handleSearchResponse(response: Response) {
        Timber.d("Search: ${filters(response)}")
        _searchPlaces.postValue(Event(Resource.Success(
            response.collection.children.filter { item ->
                item.obj!!.metadataContainer.getItem(BusinessObjectMetadata::class.java).categories.find {
                    it.categoryClass in Constants.FOOD_CATEGORIES
                } != null
            }.map {
                placeDetailMapper.toPlaceDetail(it.obj!!)
            }
        )))
    }

    private fun handleSearchError(error: Error) {
        val errorMessage = when (error) {
            is RemoteError -> "Remote error"
            is NetworkError -> "Network error"
            else -> "Unknown error"
        }
        Timber.d("Search error: $errorMessage")
        _searchPlaces.postValue(Event(Resource.Error(null , errorMessage)))
    }

    /**
     * Просмотр всех возможных фильтров мест
     *
     * @param response
     * @return
     */
    private fun filters(response: Response): String? {
        fun enumValues(filter: BusinessFilter) = filter
            .values
            .enums
            ?.joinToString(prefix = " -> ") { e -> e.value.name }
            ?: ""

        return response
            .metadata
            .businessResultMetadata
            ?.businessFilters
            ?.joinToString(separator = "\n") { f -> "${f.id}${enumValues(f)}" }
    }

    private val baseUri = "ymapsbm1://org?oid=%s"

    fun getPlaceDetail(placeId: String) {
        _placeDetail.postValue(Event(Resource.Loading()))
        searchSession = searchManager.resolveURI(
            baseUri.format(placeId) ,
            SearchOptions().apply {
                /**
                 * Дополнительная информация о местах
                 */
                snippets = Snippet.BUSINESS_RATING1X.value
            } ,
            object : Session.SearchListener {
                override fun onSearchResponse(response: Response) {
                    Timber.d("Place detail: ${filters(response)}")
                    for (searchResult in response.collection.children) {
                        Timber.d("Place detail success: ${placeDetailMapper.toPlaceDetail(searchResult.obj!!)}")
                        _placeDetail.postValue(
                            Event(
                                Resource.Success(
                                    placeDetailMapper.toPlaceDetail(searchResult.obj!!)
                                )
                            )
                        )
                    }
                }

                override fun onSearchError(error: Error) {
                    val errorMessage = when (error) {
                        is RemoteError -> "Remote error"
                        is NetworkError -> "Network error"
                        else -> "Unknown error"
                    }
                    Timber.d("Place detail error: $errorMessage")
                    _placeDetail.postValue(Event(Resource.Error(null , errorMessage)))
                }
            }
        )
    }
}

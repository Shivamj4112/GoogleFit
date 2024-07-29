package com.example.googlefit

import android.content.Context
import android.health.connect.HealthConnectManager
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.BloodGlucoseRecord
import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.BodyFatRecord
import androidx.health.connect.client.records.BodyTemperatureRecord
import androidx.health.connect.client.records.CervicalMucusRecord
import androidx.health.connect.client.records.CyclingPedalingCadenceRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.HeartRateVariabilityRmssdRecord
import androidx.health.connect.client.records.HeightRecord
import androidx.health.connect.client.records.HydrationRecord
import androidx.health.connect.client.records.MenstruationPeriodRecord
import androidx.health.connect.client.records.NutritionRecord
import androidx.health.connect.client.records.OxygenSaturationRecord
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.RespiratoryRateRecord
import androidx.health.connect.client.records.RestingHeartRateRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.reflect.KClass

class HealthManager(context: Context) : ViewModel() {

    private val healthConnectClient by lazy { HealthConnectClient.getOrCreate(context) }

    private val _stepsRecords = MutableLiveData<List<StepsRecord>>()
    val stepsRecords: LiveData<List<StepsRecord>> get() = _stepsRecords

    private val _distanceRecords = MutableLiveData<List<DistanceRecord>>()
    val distanceRecords: LiveData<List<DistanceRecord>> get() = _distanceRecords

    private val _speedRecords = MutableLiveData<List<SpeedRecord>>()
    val speedRecords: LiveData<List<SpeedRecord>> get() = _speedRecords

    private val _caloriesRecords = MutableLiveData<List<TotalCaloriesBurnedRecord>>()
    val caloriesRecords: LiveData<List<TotalCaloriesBurnedRecord>> get() = _caloriesRecords

    private val _heartRateRecords = MutableLiveData<List<HeartRateRecord>>()
    val heartRateRecords: LiveData<List<HeartRateRecord>> get() = _heartRateRecords

    private val _bloodPressureRecords = MutableLiveData<List<BloodPressureRecord>>()
    val bloodPressureRecords: LiveData<List<BloodPressureRecord>> get() = _bloodPressureRecords

    private val _respiratoryRateRecords = MutableLiveData<List<RespiratoryRateRecord>>()
    val respiratoryRateRecords: LiveData<List<RespiratoryRateRecord>> get() = _respiratoryRateRecords

    private val _oxygenSaturationRecords = MutableLiveData<List<OxygenSaturationRecord>>()
    val oxygenSaturationRecords: LiveData<List<OxygenSaturationRecord>> get() = _oxygenSaturationRecords

    private val _bodyTemperatureRecords = MutableLiveData<List<BodyTemperatureRecord>>()
    val bodyTemperatureRecords: LiveData<List<BodyTemperatureRecord>> get() = _bodyTemperatureRecords

    private val _hydrationRecords = MutableLiveData<List<HydrationRecord>>()
    val hydrationRecords: LiveData<List<HydrationRecord>> get() = _hydrationRecords

    private val _nutritionRecords = MutableLiveData<List<NutritionRecord>>()
    val nutritionRecords: LiveData<List<NutritionRecord>> get() = _nutritionRecords


    private val _dateRange = MutableLiveData<Pair<Instant, Instant>>()
    val dateRange: LiveData<Pair<Instant, Instant>> get() = _dateRange

    private val _timeIntervals = MutableLiveData<List<Pair<Instant, Instant>>>()
    val timeIntervals: LiveData<List<Pair<Instant, Instant>>> get() = _timeIntervals

    private val _range = MutableLiveData("Day")
    val range : LiveData<String> = _range


    val permissions = setOf(
        HealthPermission.getReadPermission(WeightRecord::class),
        HealthPermission.getReadPermission(HeartRateVariabilityRmssdRecord::class),
        HealthPermission.getReadPermission(HeightRecord::class),
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getReadPermission(RestingHeartRateRecord::class),
        HealthPermission.getReadPermission(BloodPressureRecord::class),
        HealthPermission.getReadPermission(RespiratoryRateRecord::class),
        HealthPermission.getReadPermission(BloodGlucoseRecord::class),
        HealthPermission.getReadPermission(OxygenSaturationRecord::class),
        HealthPermission.getReadPermission(BodyTemperatureRecord::class),
        HealthPermission.getReadPermission(ExerciseSessionRecord::class),
        HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(DistanceRecord::class),
        HealthPermission.getReadPermission(SpeedRecord::class),
        HealthPermission.getReadPermission(BodyFatRecord::class),
        HealthPermission.getReadPermission(HydrationRecord::class),
        HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(SleepSessionRecord::class),
        HealthPermission.getReadPermission(CyclingPedalingCadenceRecord::class),
        HealthPermission.getReadPermission(MenstruationPeriodRecord::class),
        HealthPermission.getReadPermission(CervicalMucusRecord::class),
        HealthPermission.getReadPermission(NutritionRecord::class),
    )

    init {
        updateDateRange("${range.value}")
    }

    fun setRange(range: String) {
        _range.value = range
    }

    suspend fun hasAllPermissions(permissions: Set<String>): Boolean {
        return healthConnectClient.permissionController.getGrantedPermissions().containsAll(permissions)
    }

    fun requestPermissionsActivityContract(): ActivityResultContract<Set<String>, Set<String>> {
        return PermissionController.createRequestPermissionResultContract()
    }

    private fun updateDateRange(range: String) {
        val now = Instant.now()
        val start = when (range) {
            "Day" -> now.truncatedTo(ChronoUnit.DAYS)
            "Week" -> now.minus(7, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS)
            "Month" -> now.minus(30, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS)
            else -> now.minus(7, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS)
        }
        _dateRange.value = start to now

        if (range == "Day") {
            val intervals = (0 until 6).map { i ->
                val startInterval = start.plus((i * 3.5).toLong(), ChronoUnit.HOURS)
                val endInterval = start.plus(((i + 1) * 3.5).toLong(), ChronoUnit.HOURS)
                startInterval to endInterval
            }
            _timeIntervals.value = intervals
        } else {
            _timeIntervals.value = emptyList()
        }
    }


    fun setDateRange(range: String) {
        updateDateRange(range)
    }

    fun fetchStepsData() {
        viewModelScope.launch {
            val (start, end) = dateRange.value ?: return@launch
            val data = if (_timeIntervals.value.isNullOrEmpty()) {
                readRecords(StepsRecord::class, start, end)
            } else {
                _timeIntervals.value!!.flatMap { (startInterval, endInterval) ->
                    readRecords(StepsRecord::class, startInterval, endInterval)
                }
            }
            _stepsRecords.value = data
        }

    }

    fun fetchDistanceData() {
        viewModelScope.launch {
            val (start, end) = dateRange.value ?: return@launch
            val data = if (_timeIntervals.value.isNullOrEmpty()) {
                readRecords(DistanceRecord::class, start, end)
            } else {
                _timeIntervals.value!!.flatMap { (startInterval, endInterval) ->
                    readRecords(DistanceRecord::class, startInterval, endInterval)
                }
            }
            _distanceRecords.value = data
        }
    }

    fun fetchSpeedData() {
        viewModelScope.launch {
            val (start, end) = dateRange.value ?: return@launch
            val data = if (_timeIntervals.value.isNullOrEmpty()) {
                readRecords(SpeedRecord::class, start, end)
            } else {
                _timeIntervals.value!!.flatMap { (startInterval, endInterval) ->
                    readRecords(SpeedRecord::class, startInterval, endInterval)
                }
            }
            _speedRecords.value = data
        }
    }

    fun fetchCaloriesData() {
        viewModelScope.launch {
            val (start, end) = dateRange.value ?: return@launch
            val data = if (_timeIntervals.value.isNullOrEmpty()) {
                readRecords(TotalCaloriesBurnedRecord::class, start, end)
            } else {
                _timeIntervals.value!!.flatMap { (startInterval, endInterval) ->
                    readRecords(TotalCaloriesBurnedRecord::class, startInterval, endInterval)
                }
            }
            _caloriesRecords.value = data
        }
    }

    fun fetchActivityData() {
        fetchStepsData()
        fetchDistanceData()
        fetchSpeedData()
        fetchCaloriesData()
    }

    fun fetchVitalsData(){
        fetchHeartRateData()
        fetchBloodPressureData()
        fetchRespiratoryRateData()
        fetchOxygenSaturationData()
        fetchBodyTemperatureData()
    }

    fun fetchHeartRateData() {
        viewModelScope.launch {
            val (start, end) = dateRange.value ?: return@launch
            val data = if (_timeIntervals.value.isNullOrEmpty()) {
                readRecords(HeartRateRecord::class, start, end)
            } else {
                _timeIntervals.value!!.flatMap { (startInterval, endInterval) ->
                    readRecords(HeartRateRecord::class, startInterval, endInterval)
                }
            }
            _heartRateRecords.value = data
        }
    }
    fun fetchRespiratoryRateData() {
        viewModelScope.launch {
            val (start, end) = dateRange.value ?: return@launch
            val data = if (_timeIntervals.value.isNullOrEmpty()) {
                readRecords(RespiratoryRateRecord::class, start, end)
            } else {
                _timeIntervals.value!!.flatMap { (startInterval, endInterval) ->
                    readRecords(RespiratoryRateRecord::class, startInterval, endInterval)
                }
            }
            _respiratoryRateRecords.value = data
        }
    }
    fun fetchBloodPressureData() {
        viewModelScope.launch {
            val (start, end) = dateRange.value ?: return@launch
            val data = if (_timeIntervals.value.isNullOrEmpty()) {
                readRecords(BloodPressureRecord::class, start, end)
            } else {
                _timeIntervals.value!!.flatMap { (startInterval, endInterval) ->
                    readRecords(BloodPressureRecord::class, startInterval, endInterval)
                }
            }
            _bloodPressureRecords.value = data
        }
    }
    fun fetchOxygenSaturationData() {
        viewModelScope.launch {
            val (start, end) = dateRange.value ?: return@launch
            val data = if (_timeIntervals.value.isNullOrEmpty()) {
                readRecords(OxygenSaturationRecord::class, start, end)
            } else {
                _timeIntervals.value!!.flatMap { (startInterval, endInterval) ->
                    readRecords(OxygenSaturationRecord::class, startInterval, endInterval)
                }
            }
            _oxygenSaturationRecords.value = data
        }
    }
    fun fetchBodyTemperatureData() {
        viewModelScope.launch {
            val (start, end) = dateRange.value ?: return@launch
            val data = if (_timeIntervals.value.isNullOrEmpty()) {
                readRecords(BodyTemperatureRecord::class, start, end)
            } else {
                _timeIntervals.value!!.flatMap { (startInterval, endInterval) ->
                    readRecords(BodyTemperatureRecord::class, startInterval, endInterval)
                }
            }
            _bodyTemperatureRecords.value = data
        }
    }

    fun fetchNutritionData(){
        fetchHydrationRecordsData()
        fetchNutritionRecordData()
    }

    fun fetchHydrationRecordsData() {
        viewModelScope.launch {
            val (start, end) = dateRange.value ?: return@launch
            val data = if (_timeIntervals.value.isNullOrEmpty()) {
                readRecords(HydrationRecord::class, start, end)
            } else {
                _timeIntervals.value!!.flatMap { (startInterval, endInterval) ->
                    readRecords(HydrationRecord::class, startInterval, endInterval)
                }
            }
            _hydrationRecords.value = data
        }
    }

    fun fetchNutritionRecordData() {
        viewModelScope.launch {
            val (start, end) = dateRange.value ?: return@launch
            val data = if (_timeIntervals.value.isNullOrEmpty()) {
                readRecords(NutritionRecord::class, start, end)
            } else {
                _timeIntervals.value!!.flatMap { (startInterval, endInterval) ->
                    readRecords(NutritionRecord::class, startInterval, endInterval)
                }
            }
            _nutritionRecords.value = data
        }
    }


    private suspend fun <T : Record> readRecords(recordType: KClass<T>, start: Instant, end: Instant): List<T> {
        return try {
            val request = ReadRecordsRequest(
                recordType = recordType,
                timeRangeFilter = TimeRangeFilter.between(start, end)
            )
            val response = healthConnectClient.readRecords(request)
            Log.e("HealthManager", "Success reading ${recordType.simpleName} records")
            response.records
        } catch (e: Exception) {
            Log.e("HealthManager", "Error reading ${recordType.simpleName} records", e)
            emptyList()
        }
    }

    suspend fun readHeartRateInputs(start: Instant, end: Instant): List<HeartRateRecord> {
        return readRecords(HeartRateRecord::class, start, end)
    }

    suspend fun readRestingHeartInputs(start: Instant, end: Instant): List<RestingHeartRateRecord> {
        return readRecords(RestingHeartRateRecord::class, start, end)
    }

    suspend fun readBloodPressureInputs(start: Instant, end: Instant): List<BloodPressureRecord> {
        return readRecords(BloodPressureRecord::class, start, end)
    }

    suspend fun readRespiratoryInputs(start: Instant, end: Instant): List<RespiratoryRateRecord> {
        return readRecords(RespiratoryRateRecord::class, start, end)
    }

    suspend fun readBloodGlucoseInputs(start: Instant, end: Instant): List<BloodGlucoseRecord> {
        return readRecords(BloodGlucoseRecord::class, start, end)
    }

    suspend fun readOxygenSaturationInputs(start: Instant, end: Instant): List<OxygenSaturationRecord> {
        return readRecords(OxygenSaturationRecord::class, start, end)
    }

    suspend fun readBodyTemperatureInputs(start: Instant, end: Instant): List<BodyTemperatureRecord> {
        return readRecords(BodyTemperatureRecord::class, start, end)
    }

    suspend fun readDistancesInputs(start: Instant, end: Instant): List<DistanceRecord> {
        return readRecords(DistanceRecord::class, start, end)
    }

    suspend fun readSpeedInputs(start: Instant, end: Instant): List<SpeedRecord> {
        return readRecords(SpeedRecord::class, start, end)
    }

    suspend fun readTotalCaloriesBurnedInputs(start: Instant, end: Instant): List<TotalCaloriesBurnedRecord> {
        return readRecords(TotalCaloriesBurnedRecord::class, start, end)
    }

    suspend fun readWeightInputs(start: Instant, end: Instant): List<WeightRecord> {
        return readRecords(WeightRecord::class, start, end)
    }

    suspend fun readHeightInputs(start: Instant, end: Instant): List<HeightRecord> {
        return readRecords(HeightRecord::class, start, end)
    }

    suspend fun readBodyFatInputs(start: Instant, end: Instant): List<BodyFatRecord> {
        return readRecords(BodyFatRecord::class, start, end)
    }

    suspend fun readExerciseSessions(start: Instant, end: Instant): List<ExerciseSessionRecord> {
        return readRecords(ExerciseSessionRecord::class, start, end)
    }

    suspend fun readHydrationInputs(start: Instant, end: Instant): List<HydrationRecord> {
        return readRecords(HydrationRecord::class, start, end)
    }

    suspend fun readNutritionRecordInputs(start: Instant, end: Instant): List<NutritionRecord> {
        return readRecords(NutritionRecord::class, start, end)
    }

    suspend fun readSleepInputs(start: Instant, end: Instant): List<SleepSessionRecord> {
        return readRecords(SleepSessionRecord::class, start, end)
    }

    suspend fun readCyclePedalingCadence(start: Instant, end: Instant): List<CervicalMucusRecord> {
        return readRecords(CervicalMucusRecord::class, start, end)
    }

}


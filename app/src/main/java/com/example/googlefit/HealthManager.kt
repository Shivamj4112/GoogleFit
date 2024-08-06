package com.example.googlefit

import android.content.Context
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.BasalMetabolicRateRecord
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
import androidx.health.connect.client.records.PowerRecord
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.RespiratoryRateRecord
import androidx.health.connect.client.records.RestingHeartRateRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.records.WheelchairPushesRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import kotlin.reflect.KClass

class HealthManager(context: Context) : ViewModel() {

    private val healthConnectClient by lazy { HealthConnectClient.getOrCreate(context) }

    //  TODO Activity
    private val _stepsRecords = MutableLiveData<List<StepsRecord>>()
    val stepsRecords: LiveData<List<StepsRecord>> get() = _stepsRecords

    private val _distanceRecords = MutableLiveData<List<DistanceRecord>>()
    val distanceRecords: LiveData<List<DistanceRecord>> get() = _distanceRecords

    private val _powerRecord = MutableLiveData<List<PowerRecord>>()
    val powerRecord: LiveData<List<PowerRecord>> get() = _powerRecord

    private val _cyclingRecords = MutableLiveData<List<CyclingPedalingCadenceRecord>>()
    val cyclingRecords: LiveData<List<CyclingPedalingCadenceRecord>> get() = _cyclingRecords

    private val _exerciseSessionRecords = MutableLiveData<List<ExerciseSessionRecord>>()
    val exerciseSessionRecords: LiveData<List<ExerciseSessionRecord>> get() = _exerciseSessionRecords

    private val _speedRecords = MutableLiveData<List<SpeedRecord>>()
    val speedRecords: LiveData<List<SpeedRecord>> get() = _speedRecords

    private val _caloriesRecords = MutableLiveData<List<TotalCaloriesBurnedRecord>>()
    val caloriesRecords: LiveData<List<TotalCaloriesBurnedRecord>> get() = _caloriesRecords

    //  TODO Body Measurements
    private val _weightRecords = MutableLiveData<List<WeightRecord>>()
    val weightRecords: LiveData<List<WeightRecord>> get() = _weightRecords

    private val _heightRecords = MutableLiveData<List<HeightRecord>>()
    val heightRecords: LiveData<List<HeightRecord>> get() = _heightRecords

    private val _bodyFatRecords = MutableLiveData<List<BodyFatRecord>>()
    val bodyFatRecords: LiveData<List<BodyFatRecord>> get() = _bodyFatRecords

    private val _basalMetabolicRate = MutableLiveData<List<BasalMetabolicRateRecord>>()
    val basalMetabolicRate: LiveData<List<BasalMetabolicRateRecord>> get() = _basalMetabolicRate

    // TODO Vitals
    private val _heartRateRecords = MutableLiveData<List<HeartRateRecord>>()
    val heartRateRecords: LiveData<List<HeartRateRecord>> get() = _heartRateRecords

    private val _bloodPressureRecords = MutableLiveData<List<BloodPressureRecord>>()
    val bloodPressureRecords: LiveData<List<BloodPressureRecord>> get() = _bloodPressureRecords

    private val _bloodGlucoseRecords = MutableLiveData<List<BloodGlucoseRecord>>()
    val bloodGlucoseRecords: LiveData<List<BloodGlucoseRecord>> get() = _bloodGlucoseRecords

    private val _respiratoryRateRecords = MutableLiveData<List<RespiratoryRateRecord>>()
    val respiratoryRateRecords: LiveData<List<RespiratoryRateRecord>> get() = _respiratoryRateRecords

    private val _oxygenSaturationRecords = MutableLiveData<List<OxygenSaturationRecord>>()
    val oxygenSaturationRecords: LiveData<List<OxygenSaturationRecord>> get() = _oxygenSaturationRecords

    private val _bodyTemperatureRecords = MutableLiveData<List<BodyTemperatureRecord>>()
    val bodyTemperatureRecords: LiveData<List<BodyTemperatureRecord>> get() = _bodyTemperatureRecords

    //  TODO Nutrition
    private val _hydrationRecords = MutableLiveData<List<HydrationRecord>>()
    val hydrationRecords: LiveData<List<HydrationRecord>> get() = _hydrationRecords

    private val _nutritionRecords = MutableLiveData<List<NutritionRecord>>()
    val nutritionRecords: LiveData<List<NutritionRecord>> get() = _nutritionRecords

    //  TODO Sleep
    private val _sleepSessionRecords = MutableLiveData<List<SleepSessionRecord>>()
    val sleepSessionRecords: LiveData<List<SleepSessionRecord>> get() = _sleepSessionRecords


    // TODO Date and Time Intervarls
    private val _dateRange = MutableLiveData<Pair<Instant, Instant>>()
    val dateRange: LiveData<Pair<Instant, Instant>> get() = _dateRange

    private val _timeIntervals = MutableLiveData<List<Pair<Instant, Instant>>>()
    val timeIntervals: LiveData<List<Pair<Instant, Instant>>> get() = _timeIntervals

    private val _range = MutableLiveData("Week")
    val range: LiveData<String> = _range


    val permissions = setOf(
        HealthPermission.getReadPermission(WeightRecord::class),
        HealthPermission.getReadPermission(HeartRateVariabilityRmssdRecord::class),
        HealthPermission.getReadPermission(BasalMetabolicRateRecord::class),
        HealthPermission.getReadPermission(HeightRecord::class),
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getReadPermission(RestingHeartRateRecord::class),
        HealthPermission.getReadPermission(BloodPressureRecord::class),
        HealthPermission.getReadPermission(RespiratoryRateRecord::class),
        HealthPermission.getReadPermission(BloodGlucoseRecord::class),
        HealthPermission.getReadPermission(OxygenSaturationRecord::class),
        HealthPermission.getReadPermission(BodyTemperatureRecord::class),
        HealthPermission.getReadPermission(PowerRecord::class),
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
        return healthConnectClient.permissionController.getGrantedPermissions()
            .containsAll(permissions)
    }

    fun requestPermissionsActivityContract(): ActivityResultContract<Set<String>, Set<String>> {
        return PermissionController.createRequestPermissionResultContract()
    }

    private fun updateDateRange(range: String) {
        val now = Instant.now()
        val start = when (range) {
            "Day" -> now.truncatedTo(ChronoUnit.DAYS)
            "Week" -> now.minus(7, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS)
            "Month" -> now.atZone(ZoneOffset.UTC).withDayOfYear(1).truncatedTo(ChronoUnit.DAYS).toInstant()
//            "Month" -> now.minus(30, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS)
            else -> now.atZone(ZoneOffset.UTC).withDayOfYear(1).truncatedTo(ChronoUnit.DAYS).toInstant()
        }
        _dateRange.value = start to now
        _range.value = range

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

    // TODO Activity Records
    private fun fetchStepsData() {
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
    private fun fetchExerciseData() {
        viewModelScope.launch {
            val (start, end) = dateRange.value ?: return@launch
            val data = if (_timeIntervals.value.isNullOrEmpty()) {
                readRecords(ExerciseSessionRecord::class, start, end)
            } else {
                _timeIntervals.value!!.flatMap { (startInterval, endInterval) ->
                    readRecords(ExerciseSessionRecord::class, startInterval, endInterval)
                }
            }
            _exerciseSessionRecords.value = data
        }

    }
    private fun fetchPowerData() {
        viewModelScope.launch {
            val (start, end) = dateRange.value ?: return@launch
            val data = if (_timeIntervals.value.isNullOrEmpty()) {
                readRecords(PowerRecord::class, start, end)
            } else {
                _timeIntervals.value!!.flatMap { (startInterval, endInterval) ->
                    readRecords(PowerRecord::class, startInterval, endInterval)
                }
            }
            _powerRecord.value = data
        }

    }
    private fun fetchDistanceData() {
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
    private fun fetchCyclingData() {
        viewModelScope.launch {
            val (start, end) = dateRange.value ?: return@launch
            val data = if (_timeIntervals.value.isNullOrEmpty()) {
                readRecords(CyclingPedalingCadenceRecord::class, start, end)
            } else {
                _timeIntervals.value!!.flatMap { (startInterval, endInterval) ->
                    readRecords(CyclingPedalingCadenceRecord::class, startInterval, endInterval)
                }
            }
            _cyclingRecords.value = data
        }
    }

    private fun fetchSpeedData() {
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
    private fun fetchCaloriesData() {
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
        fetchCyclingData()
        fetchExerciseData()
        fetchSpeedData()
        fetchPowerData()
        fetchCaloriesData()
    }

    // TODO Body Measurements

    fun fetchBodyMeasurementsData() {
        fetchWeight()
        fetchHeight()
        fetchBodyFat()
        fetchMetabolicRate()
    }

    private fun fetchWeight(){
        viewModelScope.launch {
            val (start, end) = dateRange.value ?: return@launch
            val data = if (_timeIntervals.value.isNullOrEmpty()) {
                readRecords(WeightRecord::class, start, end)
            } else {
                _timeIntervals.value!!.flatMap { (startInterval, endInterval) ->
                    readRecords(WeightRecord::class, startInterval, endInterval)
                }
            }
            _weightRecords.value = data
        }
    }

    private fun fetchHeight(){
        viewModelScope.launch {
            val (start, end) = dateRange.value ?: return@launch
            val data = if (_timeIntervals.value.isNullOrEmpty()) {
                readRecords(HeightRecord::class, start, end)
            } else {
                _timeIntervals.value!!.flatMap { (startInterval, endInterval) ->
                    readRecords(HeightRecord::class, startInterval, endInterval)
                }
            }
            _heightRecords.value = data
        }
    }

    private fun fetchBodyFat() {
        viewModelScope.launch {
            val (start, end) = dateRange.value ?: return@launch
            val data = if (_timeIntervals.value.isNullOrEmpty()) {
                readRecords(BodyFatRecord::class, start, end)
            } else {
                _timeIntervals.value!!.flatMap { (startInterval, endInterval) ->
                    readRecords(BodyFatRecord::class, startInterval, endInterval)
                }
            }
            _bodyFatRecords.value = data
        }
    }

    private fun fetchMetabolicRate(){
        viewModelScope.launch {
            val (start, end) = dateRange.value ?: return@launch
            val data = if (_timeIntervals.value.isNullOrEmpty()) {
                readRecords(BasalMetabolicRateRecord::class, start, end)
            } else {
                _timeIntervals.value!!.flatMap { (startInterval, endInterval) ->
                    readRecords(BasalMetabolicRateRecord::class, startInterval, endInterval)
                }
            }
            _basalMetabolicRate.value = data
        }
    }


    // TODO Vital Records
    fun fetchVitalsData() {
        fetchHeartRateData()
        fetchBloodPressureData()
        fetchBloodGlucoseData()
        fetchRespiratoryRateData()
        fetchOxygenSaturationData()
        fetchBodyTemperatureData()
    }
    private fun fetchHeartRateData() {
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
    private fun fetchRespiratoryRateData() {
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
    private fun fetchBloodPressureData() {
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
    private fun fetchBloodGlucoseData() {
        viewModelScope.launch {
            val (start, end) = dateRange.value ?: return@launch
            val data = if (_timeIntervals.value.isNullOrEmpty()) {
                readRecords(BloodGlucoseRecord::class, start, end)
            } else {
                _timeIntervals.value!!.flatMap { (startInterval, endInterval) ->
                    readRecords(BloodGlucoseRecord::class, startInterval, endInterval)
                }
            }
            _bloodGlucoseRecords.value = data
        }
    }
    private fun fetchOxygenSaturationData() {
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
    private fun fetchBodyTemperatureData() {
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


    // TODO Nutrition Records
    fun fetchNutritionData() {
        fetchHydrationRecordsData()
        fetchNutritionRecordData()
    }
    private fun fetchHydrationRecordsData() {
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
    private fun fetchNutritionRecordData() {
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


    // TODO Sleep Records
    fun fetchSleepData() {
        fetchSleepSession()
    }
    private fun fetchSleepSession() {
        viewModelScope.launch {
            val (start, end) = dateRange.value ?: return@launch
            val data = if (_timeIntervals.value.isNullOrEmpty()) {
                readRecords(SleepSessionRecord::class, start, end)
            } else {
                _timeIntervals.value!!.flatMap { (startInterval, endInterval) ->
                    readRecords(SleepSessionRecord::class, startInterval, endInterval)
                }
            }
            _sleepSessionRecords.value = data
        }
    }


    // TODO READ RECORDS
    private suspend fun <T : Record> readRecords(
        recordType: KClass<T>,
        start: Instant,
        end: Instant
    ): List<T> {
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



}


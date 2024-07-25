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

    private val _dateRange = MutableLiveData<Pair<Instant, Instant>>()
    val dateRange: LiveData<Pair<Instant, Instant>> get() = _dateRange

    private val _timeIntervals = MutableLiveData<List<Pair<Instant, Instant>>>()
    val timeIntervals: LiveData<List<Pair<Instant, Instant>>> get() = _timeIntervals


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
        updateDateRange("Week")
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
                val startInterval = start.plus((i * 4).toLong(), ChronoUnit.HOURS)
                val endInterval = start.plus(((i + 1) * 4).toLong(), ChronoUnit.HOURS)
                startInterval to endInterval
            }
            _timeIntervals.value = intervals
        } else {
            _timeIntervals.value = emptyList()
        }
    }


    fun setDateRange(range: String) {
        updateDateRange(range)
        fetchStepsData()
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


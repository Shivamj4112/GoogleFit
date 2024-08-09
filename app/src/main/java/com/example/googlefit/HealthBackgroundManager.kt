package com.example.googlefit

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.BloodGlucoseRecord
import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.BodyTemperatureRecord
import androidx.health.connect.client.records.CyclingPedalingCadenceRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.OxygenSaturationRecord
import androidx.health.connect.client.records.PowerRecord
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.RespiratoryRateRecord
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
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

class HealthBackgroundManager(val context: Context) : ViewModel() {

    private val healthConnectClient by lazy { HealthConnectClient.getOrCreate(context) }

    // TODO ACTIVITY RECORDS
    private val _stepsRecords = MutableLiveData<List<StepsRecord>>()
    val stepsRecords: LiveData<List<StepsRecord>> get() = _stepsRecords

    private val _caloriesRecords = MutableLiveData<List<TotalCaloriesBurnedRecord>>()
    val caloriesRecords : LiveData<List<TotalCaloriesBurnedRecord>> get() = _caloriesRecords

    private val _distanceRecords = MutableLiveData<List<DistanceRecord>>()
    val distanceRecords: LiveData<List<DistanceRecord>> get() = _distanceRecords

    private val _speedRecords = MutableLiveData<List<SpeedRecord>>()
    val speedRecords: LiveData<List<SpeedRecord>> get() = _speedRecords

    private val _cyclingRecords = MutableLiveData<List<CyclingPedalingCadenceRecord>>()
    val cyclingRecords: LiveData<List<CyclingPedalingCadenceRecord>> get() = _cyclingRecords

    private val _powerRecords = MutableLiveData<List<PowerRecord>>()
    val powerRecords: LiveData<List<PowerRecord>> get() = _powerRecords


    // TODO VITALS RECORDS
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


    // TODO TIME RANGE
    private val _dateRange = MutableLiveData<Pair<Instant, Instant>>()
    val dateRange: LiveData<Pair<Instant, Instant>> get() = _dateRange

    private val _range = MutableLiveData("Month")
    val range: LiveData<String> = _range


    // TODO FETCH ACTIVITY DATA
     fun fetchActivityData() {
        fetchData(StepsRecord::class, _stepsRecords)
        fetchData(TotalCaloriesBurnedRecord::class, _caloriesRecords)
        fetchData(DistanceRecord::class, _distanceRecords)
        fetchData(SpeedRecord::class, _speedRecords)
        fetchData(CyclingPedalingCadenceRecord::class, _cyclingRecords)
        fetchData(PowerRecord::class, _powerRecords)
    }

    // TODO FETCH VITALS DATA
    fun fetchVitalsData() {
        fetchData(HeartRateRecord::class, _heartRateRecords)
        fetchData(RespiratoryRateRecord::class, _respiratoryRateRecords)
        fetchData(BloodPressureRecord::class, _bloodPressureRecords)
        fetchData(BloodGlucoseRecord::class, _bloodGlucoseRecords)
        fetchData(OxygenSaturationRecord::class, _oxygenSaturationRecords)
        fetchData(BodyTemperatureRecord::class, _bodyTemperatureRecords)
    }

    // TODO FETCH RECORDS
    private fun <T : Record> fetchData(recordClass: KClass<T>, liveData: MutableLiveData<List<T>>) {
        viewModelScope.launch {
            val now = Instant.now()
            val vitals = when (range.value) {
                "Month" -> now.atZone(ZoneOffset.UTC).withDayOfYear(1).truncatedTo(ChronoUnit.DAYS).toInstant()
                else -> now.atZone(ZoneOffset.UTC).withDayOfYear(1).truncatedTo(ChronoUnit.DAYS).toInstant()
            }
            _dateRange.value = vitals to now

            val (start, end) = _dateRange.value ?: return@launch
            val data = readRecords(recordClass, start, end)
            liveData.value = data
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

@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.drivetracker.ui.vehicleDetails

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.arathort.common.comments.Comment
import com.arathort.data.items.CarItem
import com.arathort.data.items.TruckItem
import com.example.drivetracker.ui.RentWheelsScreen
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlin.math.round

@Composable
fun VehicleDetailsScreen(
    viewModel: VehicleDetailsViewModel,
    navHostController: NavHostController,
    deleteItem: () -> Unit
) {
    val item by viewModel.displayedItem.collectAsState()

    val dialogState = remember { mutableStateOf(false) }
    val newPriceState = remember { mutableStateOf(false) }
    val context = LocalContext.current

    Surface(modifier = Modifier.fillMaxSize()) {
        when (item) {
            null -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Завантаження...",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            else -> {
                Column {
                    Row {
                        Button(onClick = { navHostController.navigate(RentWheelsScreen.OrderVehicles.name) }) {
                            Icon(Icons.Default.ArrowBack, "Arrow back")
                        }
                        if (viewModel.isAdmin()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(onClick = { newPriceState.value = true }) {
                                    Icon(Icons.Default.Edit, "Edit")
                                }
                            }
                        }
                    }

                    Card(modifier = Modifier.padding(5.dp, 5.dp, 20.dp, 20.dp)) {
                        Column(verticalArrangement = Arrangement.Center) {
                            Text(
                                text = "${item!!.getVehicle().brand} ${item!!.getVehicle().model}",
                                fontSize = MaterialTheme.typography.displayMedium.fontSize,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                                lineHeight = 40.sp
                            )
                            Text("Номер: ${item!!.getVehicle().registrationNumber}")
                            Text("Рейтинг: ${item!!.getRating()}★")
                            Text("Рік випуску: ${item!!.getVehicle().year}")

                            when (item) {
                                is CarItem -> {
                                    Text("Кількість місць: ${(item as CarItem).car.numberSeats}")
                                    Text("Макс. швидкість: ${round((item as CarItem).car.maxSpeed)}")
                                }

                                is TruckItem -> {
                                    Text("Вантажопідйомність: ${(item as TruckItem).truck.cargoCapacity} т")
                                }
                            }
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column {
                                    Text("Застава: ${item!!.pledge} грн")
                                    Text("${item!!.calculateRentalCost(1)} грн/день")
                                    if (viewModel.isAdmin()) {
                                        Button(onClick = deleteItem) { Text("Видалити") }
                                    } else {
                                        Button(onClick = {
                                            dialogState.value = true
                                        }) { Text("Орендувати") }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (newPriceState.value) {
        NewPriceDialog(
            onDismiss = { newPriceState.value = false },
            onSubmit = {
                viewModel.updatePrice(it)
                newPriceState.value = false
            }
        )
    }

    if (dialogState.value) {
        PopupCalendar(
            onDismiss = { dialogState.value = false },
            onConfirm = { selectedDate ->
                val success = viewModel.createAndAddRental(selectedDate)
                if (success) {
                    Toast.makeText(context, "Оренда успішна!", Toast.LENGTH_SHORT).show()
                    navHostController.navigate(RentWheelsScreen.OrderVehicles.name)
                } else {
                    Toast.makeText(
                        context,
                        "Помилка: транспорт недоступний або оплата не пройшла",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }
}

@Composable
fun PopupCalendar(
    onDismiss: () -> Unit,
    onConfirm: (LocalDate) -> Unit
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val datePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Input)
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                if (datePickerState.selectedDateMillis != null) {
                    selectedDate = convertMillisToLocalDate(datePickerState.selectedDateMillis!!)
                    if (selectedDate.isBefore(LocalDate.now())) {
                        Toast.makeText(context, "Введіть майбутню дату!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    onConfirm(selectedDate)
                }
            }) { Text("Підтвердити") }
        },
        title = { Text("Підтвердженя") },
        text = {
            Column {
                Spacer(Modifier.height(16.dp))
                DatePicker(state = datePickerState, showModeToggle = false)
            }
        },
        dismissButton = { Button(onClick = onDismiss) { Text("Назад") } }
    )
}

fun convertMillisToLocalDate(millis: Long): LocalDate {
    return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
}

@Composable
fun DisplayComment(comment: Comment) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Row(modifier = Modifier.padding(10.dp)) {
            Text(comment.authorEmail, fontSize = MaterialTheme.typography.headlineSmall.fontSize)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text("${comment.rating}★")
            }
        }
        Text(comment.text, modifier = Modifier.padding(10.dp))
    }
}

@Composable
fun NewPriceDialog(
    onDismiss: () -> Unit,
    onSubmit: (Double) -> Unit
) {
    var price by remember { mutableStateOf(TextFieldValue()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Нова ціна") },
        text = {
            TextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Введіть нову ціну") })
        },
        confirmButton = { Button(onClick = { onSubmit(price.text.toDouble()) }) { Text("Підтвердити") } },
        dismissButton = { Button(onClick = onDismiss) { Text("Скасувати") } }
    )
}
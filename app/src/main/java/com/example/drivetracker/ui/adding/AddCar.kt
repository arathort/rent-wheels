package com.example.drivetracker.ui.adding

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.drivetracker.data.items.CarItem
import com.example.drivetracker.data.entity.Car
import com.example.drivetracker.ui.RentWheelsScreen
import com.example.drivetracker.ui.order.OrderVehicleViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun AddCarScreen(
    viewModel: OrderVehicleViewModel,
    navHostController: NavHostController
){
    val context = LocalContext.current
    Surface(modifier = Modifier.fillMaxSize()) {
        Row{
            Button(onClick = { navHostController.navigate(route = RentWheelsScreen.OrderVehicles.name)}) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Arrow back")
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            var brandText by remember { mutableStateOf(TextFieldValue()) }
            Text(
                text = "Додавання авто",
                fontSize = MaterialTheme.typography.displayMedium.fontSize,
                textAlign = TextAlign.Center,
                modifier =  Modifier.fillMaxWidth(),
                lineHeight = 40.sp
            )
            OutlinedTextField(
                value = brandText,
                onValueChange = {
                    brandText = it
                },
                label={
                    Text(text = "Марка")
                },
                maxLines = 1
            )
            var modelText by remember { mutableStateOf(TextFieldValue()) }
            OutlinedTextField(
                value = modelText,
                onValueChange = {
                    modelText = it
                },
                label={
                    Text(text = "Модель")
                },
                maxLines = 1
            )

            var regNumberText by remember { mutableStateOf(TextFieldValue()) }
            OutlinedTextField(
                value = regNumberText,
                onValueChange = {
                    regNumberText = it
                },
                label={
                    Text(text = "Реєстраційний номер")
                },
                maxLines = 1
            )

            var yearText by remember { mutableStateOf(TextFieldValue()) }
            OutlinedTextField(
                value = yearText,
                onValueChange = {
                    yearText = it
                },
                label={
                    Text(text = "Рік випуску")
                },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            var numSeatsText by remember { mutableStateOf(TextFieldValue()) }
            OutlinedTextField(
                value = numSeatsText,
                onValueChange = {
                    numSeatsText = it
                },
                label={
                    Text(text = "Кількість місць")
                },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            var maxSpeedText by remember { mutableStateOf(TextFieldValue()) }
            OutlinedTextField(
                value = maxSpeedText,
                onValueChange = {
                    maxSpeedText = it
                },
                label={
                    Text(text = "Макс. швидкість")
                },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            var priceText by remember { mutableStateOf(TextFieldValue()) }
            OutlinedTextField(
                value = priceText,
                onValueChange = {
                    priceText = it
                },
                label={
                    Text(text = "Ціна за день")
                },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            var pledgeText by remember { mutableStateOf(TextFieldValue()) }
            OutlinedTextField(
                value = pledgeText,
                onValueChange = {
                    pledgeText = it
                },
                label={
                    Text(text = "Застава")
                },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            Button(onClick = {
                if(yearText.text.toInt()>LocalDate.now().year||yearText.text.toInt()<1885){
                    Toast.makeText(context, "Рік випуску не відповідає дійсності!", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                if(numSeatsText.text.toInt()<=0||maxSpeedText.text.toDouble()<=0||priceText.text.toDouble()<=0||pledgeText.text.toDouble()<=0){
                    Toast.makeText(context, "Значення мають бути більше 0!", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if(numSeatsText.text.toInt()>10){
                    Toast.makeText(context, "Надто велика кількість місць!", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if(maxSpeedText.text.toDouble()>508.73){
                    Toast.makeText(context, "Занадто велика максимальна швидкість!", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (brandText.text.isNotEmpty() && modelText.text.isNotEmpty() && yearText.text.isNotEmpty() && numSeatsText.text.isNotEmpty()) {
                    val car = Car(
                        brand = brandText.text,
                        modelText.text,
                        yearText.text.toInt(),
                        numberSeats = numSeatsText.text.toInt(),
                        maxSpeed = maxSpeedText.text.toDouble(),
                        registrationNumber = regNumberText.text
                    )
                    val carItem = CarItem(
                        car,
                        uploadDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        price = priceText.text.toDouble(),
                        pledge = pledgeText.text.toDouble()
                    )
                    viewModel.addVehicleItem(carItem)
                    navHostController.navigate(RentWheelsScreen.OrderVehicles.name)
                }
            }
            ) {
                Text(text = "Додати")
            }
        }
    }


}

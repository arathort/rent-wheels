package com.example.drivetracker.ui.commenting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.arathort.common.comments.Comment
import com.example.drivetracker.ui.RentWheelsScreen

@Composable
fun CommentScreen(
    viewModel: CommentScreenViewModel,
    navHostController: NavHostController
){
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var textFieldValue by remember { mutableStateOf(TextFieldValue()) }
            var rating by remember { mutableIntStateOf(0) }
            Text(
                text = "Дякуємо за користування",
                fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Text(text = "Напишіть відгук, якщо бажаєте")
            OutlinedTextField(
                value = textFieldValue,
                onValueChange = {
                    textFieldValue = it
                }
            )

            Row {
                repeat(5) { index ->
                    Button(onClick = { rating = index + 1 }) {
                        Text(text = "★${index + 1}")
                    }
                }
            }

            Text(text = "$rating★")

            Row {
                Button(onClick = { navHostController.navigate(RentWheelsScreen.OrderVehicles.name) }) {
                    Text(text = "Скасувати")
                }
                Button(onClick = {
                    if(textFieldValue.text!=""&& rating!=0){
                        val comment = Comment(viewModel.getEmail(),textFieldValue.text, rating)
                        if(viewModel.isCar()){
                            viewModel.updateCarWithComment(comment)
                        }
                        else{
                            viewModel.updateTruckWithComment(comment)
                        }
                        navHostController.navigate(RentWheelsScreen.MyVehicles.name)
                    }
                }) {
                    Text(text = "Підтвердити")
                }
            }
        }
    }
}


@Preview
@Composable
fun CommentScreenPreview(){
    //CommentScreen()
}
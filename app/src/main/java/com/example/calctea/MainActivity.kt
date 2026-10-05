package com.example.calctea

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.calctea.ui.theme.CalcteaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalcteaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TipCalculatorScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

/**
 * Главный экран калькулятора чаевых и скидки.
 */
@Composable
fun TipCalculatorScreen(modifier: Modifier = Modifier) {
    // Состояния для хранения введенных пользователем данных и процента чаевых
    var billAmountInput by remember { mutableStateOf("") }
    var dishesCountInput by remember { mutableStateOf("") }
    var tipPercent by remember { mutableFloatStateOf(15f) }

    // Преобразуем введенные строки в числовые значения (при ошибке или пустом вводе получаем 0)
    val billAmount = billAmountInput.toDoubleOrNull() ?: 0.0
    val dishesCount = dishesCountInput.toIntOrNull() ?: 0

    // Расчет процента скидки в зависимости от количества блюд:
    // 1-2 блюда – 3%
    // 3-5 блюд – 5%
    // 6-10 блюд – 7%
    // более 10 блюд – 10%
    val discountPercent = when {
        dishesCount in 1..2 -> 3
        dishesCount in 3..5 -> 5
        dishesCount in 6..10 -> 7
        dishesCount > 10 -> 10
        else -> 0
    }

    // Возможные варианты скидок для радиокнопок
    val discountOptions = listOf(3, 5, 7, 10)

    // Математический расчет сумм
    val tipAmount = billAmount * tipPercent / 100.0
    val discountAmount = billAmount * discountPercent / 100.0
    val totalAmount = (billAmount + tipAmount - discountAmount).coerceAtLeast(0.0)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Заголовок экрана
        Text(
            text = "Калькулятор чаевых",
            style = MaterialTheme.typography.headlineMedium
        )

        // Поле ввода суммы заказа
        OutlinedTextField(
            value = billAmountInput,
            onValueChange = { billAmountInput = it },
            label = { Text("Сумма заказа:") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        // Поле ввода количества заказанных блюд
        OutlinedTextField(
            value = dishesCountInput,
            onValueChange = { dishesCountInput = it },
            label = { Text("Количество блюд:") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        // Блок выбора процента чаевых с помощью слайдера (от 0 до 25%)
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Чаевые: ${tipPercent.toInt()}%",
                style = MaterialTheme.typography.bodyLarge
            )

            Slider(
                value = tipPercent,
                onValueChange = { tipPercent = it },
                valueRange = 0f..25f,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Блок группы радиокнопок для отображения и программного выбора скидки
        // Согласно заданию: выбор радиокнопки реализуется программно, а не пользователем.

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Скидка: $discountPercent%",
                style = MaterialTheme.typography.bodyLarge
            )

            // Строка, содержащая все радиокнопки для вариантов скидки (3%, 5%, 7%, 10%)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Проходим по каждому варианту скидки из списка discountOptions
                discountOptions.forEach { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Компонент RadioButton
                        // - selected: определяет, заполнена ли кнопка (true/false).
                        //   Она будет выбрана только если расчетный discountPercent равен текущей опции (option).
                        // - onClick: установлен в значение null.
                        //   Передача null отключает обработку нажатий пользователем, что гарантирует, 
                        //   что выбор радиокнопки происходит исключительно программно (на основе количества блюд), 

                        RadioButton(
                            selected = (discountPercent == option),
                            onClick = null 
                        )
                        // Текстовая подпись рядом с радиокнопкой (например, "3%", "5%" и т.д.)
                        Text(text = "$option%")
                    }
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))


        // Карточка с итоговыми результатами расчетов (чаевые, скидка, итого к оплате)

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Вывод рассчитанной суммы чаевых с форматированием до 2 знаков после запятой
                Text(
                    text = "Чаевые: %.2f".format(tipAmount),
                    style = MaterialTheme.typography.bodyLarge
                )
                // Вывод рассчитанной суммы скидки с форматированием до 2 знаков после запятой
                Text(
                    text = "Скидка: %.2f".format(discountAmount),
                    style = MaterialTheme.typography.bodyLarge
                )
                // Вывод итоговой суммы к оплате: Сумма заказа + Чаевые - Скидка
                // Используем полужирное начертание (FontWeight.Bold) и крупный заголовок для акцента
                Text(
                    text = "Итого к оплате: %.2f".format(totalAmount),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TipCalculatorPreview() {
    CalcteaTheme {
        TipCalculatorScreen()
    }
}
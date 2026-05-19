package com.example.practic_14.Acitivities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.example.practic_14.Classes.SendCommon;
import com.example.practic_14.ICallbackResponse;
import com.example.practic_14.R;

import java.util.regex.Pattern;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.EditText;

//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.graphics.drawable.Drawable;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.Toast;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.constraintlayout.widget.ConstraintLayout;
//import androidx.core.content.ContextCompat;
//import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    public EditText tbUserEmail; // Текстовое поле с почтой пользователя
    public Drawable BackgroundRed, Background; // Ресурсы фона текстового поля
    public Context Context; // ссылка на контекст активности
    public SendCommon SendCommon; // объект выполняющие запрос к серверу
    public String Code; // код полученный в результате ответа

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Получаем ресурсы фона для текстового поля
        BackgroundRed = ContextCompat.getDrawable(this, R.drawable.edittext_background_red);
        Background = ContextCompat.getDrawable(this, R.drawable.edittext_background);

        // Получаем текстовое поле на слое
        tbUserEmail = findViewById(R.id.user_email);

        // Инициализируем объект запроса
        SendCommon = new SendCommon(tbUserEmail, CallbackResponseCode, CallbackResponseError);

        // Запоминаем контекст активности
        Context = this;
    }

    // Метод для проверки почты пользователя на гедех
    public Boolean IsValid(String Value) {
        Pattern sPattern = Pattern.compile("A\\w{2,20}@\\w{2,10}\\.\\w{2,4}$"); // создаём гедех
        return sPattern.matcher(Value).matches(); // возвращаем результат совпадения
    }

    // Метод отправки сообщения
    public void SendMessage(View view) {
        String UserEmail = String.valueOf(tbUserEmail.getText()); // Получаем значение текстового поля

        if (!IsValid(UserEmail)) { // Проверяем действительно ли пользователь ввёл email, соответствует ли он маске
            tbUserEmail.setBackground(BackgroundRed); // Если не соответствует, меняем фон с красной рамкой
            Toast.makeText(this, "Не верно введён Email.", Toast.LENGTH_SHORT).show(); // Выводим уведомление
        } else {
            tbUserEmail.setBackground(Background); // Если соответствует меняем фон, без красной рамки
            if (SendCommon.getStatus() != AsyncTask.Status.RUNNING) // Если процесс запроса на сервер не запущен
                SendCommon.execute(); // Запускаем процесс запроса
        }
    }

    // Обработчик события, если скрыть уведомление
    DialogInterface.OnCancelListener AlertDialogCancelListner = new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialogInterface) { // при закрытии
            Intent Verification = new Intent(Context, Verification.class); // создаём новый Intent
            Verification.putExtra("Code", Code); // Передаём в интент данные, а именно КОД
            Verification.putExtra("Email", tbUserEmail.getText()); // А так-же почту пользователя
            startActivity(Verification); // Открываем новую активность
        }
    };

    // Обработчик события, если запрос не удался
    ICallbackResponse CallbackResponseError = new ICallbackResponse() {
        @Override
        public void returner(String Response) {
            Toast.makeText(Context, "Ошибка сервера", Toast.LENGTH_SHORT).show(); // Выводим сообщение об ошибке
            // Инициализируем объект запроса заного
            SendCommon = new SendCommon(tbUserEmail, CallbackResponseCode, CallbackResponseError);
        }
    };

    // Обработчик события, если запрос удался
    ICallbackResponse CallbackResponseCode = new ICallbackResponse() {
        @Override
        public void returner(String Response) {
            AlertDialog.Builder AlertDialogBuilder = new AlertDialog.Builder(Context); // Создаём уведомление
            ConstraintLayout View = (ConstraintLayout) getLayoutInflater().inflate(R.layout.check_email, null); // Получаем созданную View
            AlertDialogBuilder.setView(View); // Присваим View для уведомления
            AlertDialogBuilder.setOnCancelListener(AlertDialogCancelListner); // Назначаем обработчик события, на закрытие окна
            AlertDialog Dialog = AlertDialogBuilder.create(); // Создаём диалог
            Dialog.show(); // Отображаем пользователю
            Code = Response; // Запоминаем код
        }
    };
}
package com.bernardino.androidtts;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, View.OnClickListener{
    private TextToSpeech tts;
    private int REQUEST_TTS = 0;
    private int REQ_CODE_SPEECH_INPUT = 1;
    TextView txtSaida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btFalar = (Button)findViewById(R.id.btFalarId);
        btFalar.setOnClickListener(this);
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, REQUEST_TTS);
    }

    public void falar(String texto) {
        tts.speak(texto, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void escutar(View view) {
        Intent intent = new
                Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
//                getString(R.string.speech_prompt)
                "teste"
        );
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
//                    getString(R.string.speech_not_supported),
                    "fala não suportada",
                    Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onInit(int initStatus) {
        if (initStatus == TextToSpeech.SUCCESS) {
            if(tts.isLanguageAvailable(Locale.US) == TextToSpeech.LANG_AVAILABLE){
                tts.setLanguage(Locale.US);
            }
        } else if (initStatus == TextToSpeech.ERROR) {
            Toast.makeText(this, "Erro TTS", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View view) {
        EditText etTexto = (EditText) findViewById(R.id.etTtsId);
        String texto = etTexto .getText().toString();
        falar(texto);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TTS) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                tts = new TextToSpeech(this, this);
            }
            else {
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }

        if (requestCode == REQ_CODE_SPEECH_INPUT) {
            txtSaida = (TextView) findViewById(R.id.txtSaidaId);
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> result = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                txtSaida.setText(result.get(0));
            }
        }
    }
}

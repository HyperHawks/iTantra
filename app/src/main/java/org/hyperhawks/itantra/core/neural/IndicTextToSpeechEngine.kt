package org.hyperhawks.itantra.core.neural

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioAttributes.CONTENT_TYPE_SPEECH
import android.media.AudioAttributes.USAGE_ALARM
import android.media.AudioAttributes.USAGE_VOICE_COMMUNICATION
import android.media.AudioManager
import android.media.AudioManager.STREAM_ALARM
import android.media.AudioManager.STREAM_VOICE_CALL
import android.media.ToneGenerator
import android.media.ToneGenerator.TONE_PROP_BEEP
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.speech.tts.TextToSpeech.QUEUE_ADD
import android.speech.tts.TextToSpeech.QUEUE_FLUSH
import android.speech.tts.TextToSpeech.SUCCESS
import android.speech.tts.UtteranceProgressListener
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.hyperhawks.itantra.core.model.Language
import org.hyperhawks.itantra.core.model.Language.BENGALI
import org.hyperhawks.itantra.core.model.Language.ENGLISH
import org.hyperhawks.itantra.core.model.Language.GUJARATI
import org.hyperhawks.itantra.core.model.Language.HINDI
import org.hyperhawks.itantra.core.model.Language.KANNADA
import org.hyperhawks.itantra.core.model.Language.MALAYALAM
import org.hyperhawks.itantra.core.model.Language.MARATHI
import org.hyperhawks.itantra.core.model.Language.ODIA
import org.hyperhawks.itantra.core.model.Language.TAMIL
import org.hyperhawks.itantra.core.model.Language.TELUGU

class IndicTextToSpeechEngine(private val context: Context) : OnInitListener {

  private var textToSpeech: TextToSpeech? = null
  private var isInitialized: Boolean = false
  private val _isSpeaking: MutableStateFlow<Boolean> = MutableStateFlow(value = false)
  val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

  private val toneGenerator: ToneGenerator by lazy {
    ToneGenerator(STREAM_ALARM, 100)
  }

  init {
    textToSpeech = TextToSpeech(context, this)
  }

  override fun onInit(status: Int) {
    if (status != SUCCESS) return
    isInitialized = true
    val tts: TextToSpeech = textToSpeech ?: return

    val audioAttributes: AudioAttributes = AudioAttributes.Builder()
      .setContentType(CONTENT_TYPE_SPEECH)
      .setUsage(USAGE_VOICE_COMMUNICATION)
      .build()
    tts.setAudioAttributes(audioAttributes)

    tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
      override fun onStart(utteranceId: String?) {
        _isSpeaking.value = true
      }

      override fun onDone(utteranceId: String?) {
        _isSpeaking.value = false
      }

      override fun onError(utteranceId: String?) {
        _isSpeaking.value = false
      }
    })
  }

  fun speak(
    text: String,
    language: Language,
    isEmergencyAlert: Boolean = false,
    queue: Boolean = false
  ) {
    if (text.isBlank()) return
    val tts: TextToSpeech? = textToSpeech

    if (isEmergencyAlert) {
      try {
        toneGenerator.startTone(TONE_PROP_BEEP, 250)
      } catch (_: Exception) {
      }
    }

    if (tts == null || !isInitialized) return

    val locale: Locale = resolveLocale(language = language)
    tts.setLanguage(locale)

    val queueMode: Int = if (queue) QUEUE_ADD else QUEUE_FLUSH
    val params: Bundle = Bundle()
    if (isEmergencyAlert) {
      params.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)
      params.putFloat(TextToSpeech.Engine.KEY_PARAM_PAN, 0.0f)
    }

    val utteranceId: String = "itantra_${System.currentTimeMillis()}"
    tts.speak(text, queueMode, params, utteranceId)
  }

  fun stop() {
    val tts: TextToSpeech = textToSpeech ?: return
    tts.stop()
    _isSpeaking.value = false
  }

  fun shutdown() {
    val tts: TextToSpeech = textToSpeech ?: return
    tts.stop()
    tts.shutdown()
    textToSpeech = null
    isInitialized = false
    _isSpeaking.value = false
  }

  private fun resolveLocale(language: Language): Locale {
    return when (language) {
      HINDI -> Locale("hi", "IN")
      GUJARATI -> Locale("gu", "IN")
      MARATHI -> Locale("mr", "IN")
      KANNADA -> Locale("kn", "IN")
      MALAYALAM -> Locale("ml", "IN")
      TAMIL -> Locale("ta", "IN")
      TELUGU -> Locale("te", "IN")
      ODIA -> Locale("or", "IN")
      BENGALI -> Locale("bn", "IN")
      ENGLISH -> Locale("en", "IN")
    }
  }
}

package org.hyperhawks.itantra.core.model

enum class Language(
  val code: String,
  val displayName: String,
  val nativeName: String,
  val scriptCode: String,
  val indicAsrModel: String,
  val fastSpeechVoiceId: String
) {
  HINDI(
    code = "hi",
    displayName = "Hindi",
    nativeName = "हिन्दी",
    scriptCode = "Deva",
    indicAsrModel = "indic_asr_hi_int8.tflite",
    fastSpeechVoiceId = "fs2_melgan_hi_v1"
  ),
  GUJARATI(
    code = "gu",
    displayName = "Gujarati",
    nativeName = "ગુજરાતી",
    scriptCode = "Gujr",
    indicAsrModel = "indic_asr_gu_int8.tflite",
    fastSpeechVoiceId = "fs2_melgan_gu_v1"
  ),
  MARATHI(
    code = "mr",
    displayName = "Marathi",
    nativeName = "मराठी",
    scriptCode = "Deva",
    indicAsrModel = "indic_asr_mr_int8.tflite",
    fastSpeechVoiceId = "fs2_melgan_mr_v1"
  ),
  KANNADA(
    code = "kn",
    displayName = "Kannada",
    nativeName = "ಕನ್ನಡ",
    scriptCode = "Knda",
    indicAsrModel = "indic_asr_kn_int8.tflite",
    fastSpeechVoiceId = "fs2_melgan_kn_v1"
  ),
  MALAYALAM(
    code = "ml",
    displayName = "Malayalam",
    nativeName = "മലയാളം",
    scriptCode = "Mlym",
    indicAsrModel = "indic_asr_ml_int8.tflite",
    fastSpeechVoiceId = "fs2_melgan_ml_v1"
  ),
  TAMIL(
    code = "ta",
    displayName = "Tamil",
    nativeName = "தமிழ்",
    scriptCode = "Taml",
    indicAsrModel = "indic_asr_ta_int8.tflite",
    fastSpeechVoiceId = "fs2_melgan_ta_v1"
  ),
  TELUGU(
    code = "te",
    displayName = "Telugu",
    nativeName = "తెలుగు",
    scriptCode = "Telu",
    indicAsrModel = "indic_asr_te_int8.tflite",
    fastSpeechVoiceId = "fs2_melgan_te_v1"
  ),
  ODIA(
    code = "or",
    displayName = "Odia",
    nativeName = "ଓଡ଼ିଆ",
    scriptCode = "Orya",
    indicAsrModel = "indic_asr_or_int8.tflite",
    fastSpeechVoiceId = "fs2_melgan_or_v1"
  ),
  BENGALI(
    code = "bn",
    displayName = "Bengali",
    nativeName = "বাংলা",
    scriptCode = "Beng",
    indicAsrModel = "indic_asr_bn_int8.tflite",
    fastSpeechVoiceId = "fs2_melgan_bn_v1"
  ),
  ENGLISH(
    code = "en",
    displayName = "English (Indian)",
    nativeName = "English",
    scriptCode = "Latn",
    indicAsrModel = "indic_asr_en_int8.tflite",
    fastSpeechVoiceId = "fs2_melgan_en_v1"
  );

  companion object {
    fun fromCode(code: String): Language {
      for (lang: Language in entries) {
        if (lang.code.equals(other = code, ignoreCase = true)) return lang
      }
      return ENGLISH
    }
  }
}

package org.hyperhawks.itantra.core.neural

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

object IndicNlpTransliteration {

  private val EMERGENCY_GLOSSARY: Map<String, Map<Language, String>> = mapOf(
    "evacuate" to mapOf(
      HINDI to "तत्काल खाली करें",
      GUJARATI to "તરત જ ખાલી કરો",
      MARATHI to "त्वरित रिकामे करा",
      KANNADA to "ತಕ್ಷಣ ಖಾಲಿ ಮಾಡಿ",
      MALAYALAM to "ഉടൻ ഒഴിഞ്ഞുപോകുക",
      TAMIL to "உடனே வெளியேறுங்கள்",
      TELUGU to "వెంటనే ఖాళీ చేయండి",
      ODIA to "ତୁରନ୍ତ ଖାଲି କରନ୍ତୁ",
      BENGALI to "অবিলম্বে খালি করুন",
      ENGLISH to "Evacuate immediately"
    ),
    "flood" to mapOf(
      HINDI to "बाढ़ की चेतावनी",
      GUJARATI to "પૂરની ચેતવણી",
      MARATHI to "पुराचा इशारा",
      KANNADA to "ಪ್ರವಾಹ ಎಚ್ಚರಿಕೆ",
      MALAYALAM to "വെള്ളപ്പൊക്ക മുന്നറിയിപ്പ്",
      TAMIL to "வெள்ள எச்சரிக்கை",
      TELUGU to "వరద హెచ్చరిక",
      ODIA to "ବନ୍ୟା ଚେତାବନୀ",
      BENGALI to "বন্যা সতর্কতা",
      ENGLISH to "Flood alert"
    ),
    "medical" to mapOf(
      HINDI to "चिकित्सा सहायता की आवश्यकता",
      GUJARATI to "તબીબી સહાયની જરૂર છે",
      MARATHI to "वैद्यकीय मदतीची गरज आहे",
      KANNADA to "ವೈದ್ಯಕೀಯ ನೆರವು ಅಗತ್ಯವಿದೆ",
      MALAYALAM to "വൈദ്യസഹായം ആവശ്യമാണ്",
      TAMIL to "மருத்துவ உதவி தேவை",
      TELUGU to "వైద్య సహాయం అవసరం",
      ODIA to "ଚିକିତ୍ସା ସହାୟତା ଆବଶ୍ୟକ",
      BENGALI to "চিকিৎসা সহায়তা প্রয়োজন",
      ENGLISH to "Medical assistance needed"
    ),
    "sos" to mapOf(
      HINDI to "आपातकालीन एसओएस संकेत",
      GUJARATI to "કટોકટી એસઓએસ સિગ્નલ",
      MARATHI to "तातडीचा एसओएस सिग्नल",
      KANNADA to "ತುರ್ತು ಎಸ್‌ಒಎಸ್ ಸಂಕೇತ",
      MALAYALAM to "അടിയന്തര എസ് ഒ എസ് സിഗ്നൽ",
      TAMIL to "அவசர எஸ்ஓஎஸ் சமிக்ஞை",
      TELUGU to "అత్యవసర ఎస్ఓఎస్ సిగ్నల్",
      ODIA to "ଜରୁରୀକାଳୀନ ଏସଓଏସ ସଙ୍କେତ",
      BENGALI to "জরুরি এসওএস সংকেত",
      ENGLISH to "Emergency SOS signal"
    ),
    "safe" to mapOf(
      HINDI to "स्थिति नियंत्रण में और सुरक्षित है",
      GUJARATI to "પરિસ્થિતિ નિયંત્રણમાં અને સુરક્ષિત છે",
      MARATHI to "परिस्थिती नियंत्रणात आणि सुरक्षित आहे",
      KANNADA to "ಪರಿಸ್ಥಿತಿ ನಿಯಂತ್ರಣದಲ್ಲಿದೆ ಮತ್ತು ಸುರಕ್ಷಿತವಾಗಿದೆ",
      MALAYALAM to "സാഹചര്യം നിയന്ത്രണവിധേയവും സുരക്ഷിതവുമാണ്",
      TAMIL to "நிலைமை கட்டுப்பாட்டில் உள்ளது மற்றும் பாதுகாப்பாக உள்ளது",
      TELUGU to "పరిస్థితి అదుపులో ఉంది మరియు సురక్షితంగా ఉంది",
      ODIA to "ପରିସ୍ଥିତି ନିୟନ୍ତ୍ରଣରେ ଏବଂ ସୁରକ୍ଷିତ ଅଛି",
      BENGALI to "পরিস্থিতি নিয়ন্ত্রণে এবং নিরাপদ",
      ENGLISH to "Situation is under control and safe"
    )
  )

  fun rescoreCodeMixedText(
    inputText: String,
    targetLanguage: Language
  ): String {
    val trimmed: String = inputText.trim()
    if (trimmed.isEmpty()) return ""

    val lower: String = trimmed.lowercase()
    for ((key: String, translations: Map<Language, String>) in EMERGENCY_GLOSSARY) {
      if (lower.contains(other = key)) {
        val translated: String? = translations[targetLanguage]
        if (translated != null) return translated
      }
    }

    if (targetLanguage == ENGLISH) return trimmed
    val fallback: String? = EMERGENCY_GLOSSARY["sos"]?.get(targetLanguage)
    if (fallback != null && lower.contains("help")) return fallback
    return trimmed
  }

  fun tokenizeSentencePiece(text: String): List<String> {
    if (text.isEmpty()) return emptyList()
    val words: List<String> = text.split("\\s+".toRegex())
    val tokens: MutableList<String> = mutableListOf()
    for (word: String in words) {
      if (word.isBlank()) continue
      tokens.add(element = " $word")
    }
    return tokens
  }
}

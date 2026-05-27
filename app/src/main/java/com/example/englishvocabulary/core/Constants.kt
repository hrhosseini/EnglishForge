package com.example.englishvocabulary.core

object Constants {
    // Port 8000 is the default Python FastAPI port.
    // 10.0.2.2 is the special IP in the Android emulator that routes to the host computer's localhost.
    const val DEFAULT_EMULATOR_URL = "http://10.0.2.2:8000/"
    const val DEFAULT_PHYSICAL_URL = "http://192.168.1.100:8000/" // Example local computer network IP
    
    // Configurable endpoint saved key
    const val PREF_KEY_BASE_URL = "settings_base_url"
}

package com.builder.app.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.builder.app.domain.model.RolUsuario
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

@Singleton
class UserPreferences @Inject constructor(@ApplicationContext private val context: Context) {
    private val ROLE_KEY = stringPreferencesKey("user_role")
    private val UID_KEY = stringPreferencesKey("user_uid")
    private val NAME_KEY = stringPreferencesKey("user_name")

    val userRole: Flow<RolUsuario?> = context.dataStore.data.map { preferences ->
        preferences[ROLE_KEY]?.let { RolUsuario.valueOf(it) }
    }

    val userUid: Flow<String?> = context.dataStore.data.map { it[UID_KEY] }
    val userName: Flow<String?> = context.dataStore.data.map { it[NAME_KEY] }

    suspend fun saveUserSession(uid: String, role: RolUsuario, name: String) {
        context.dataStore.edit { preferences ->
            preferences[UID_KEY] = uid
            preferences[ROLE_KEY] = role.name
            preferences[NAME_KEY] = name
        }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}

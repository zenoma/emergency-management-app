package es.udc.emergencyapp

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class SharedPreferencesIntegrationTest {

    private val prefsName = "app_prefs"

    @After
    fun tearDown() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.getSharedPreferences(prefsName, Context.MODE_PRIVATE).edit().clear().apply()
    }

    @Test
    fun writeAndReadJwtToken_succeeds() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)

        prefs.edit().putString("jwt_token", "eyJhbGciOiJIUzI1NiJ9.test-token").apply()
        val readBack = prefs.getString("jwt_token", null)

        assertEquals("eyJhbGciOiJIUzI1NiJ9.test-token", readBack)
    }

    @Test
    fun writeAndReadUserInfo_succeeds() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)

        prefs.edit().apply {
            putString("user_name", "Ana García")
            putString("user_email", "ana@example.com")
            putString("user_role", "RESPONSIBLE")
            putString("user_dni", "12345678Z")
            putString("user_phone", "600123456")
            putLong("user_id", 42L)
            apply()
        }

        assertEquals("Ana García", prefs.getString("user_name", null))
        assertEquals("ana@example.com", prefs.getString("user_email", null))
        assertEquals("RESPONSIBLE", prefs.getString("user_role", null))
        assertEquals("12345678Z", prefs.getString("user_dni", null))
        assertEquals("600123456", prefs.getString("user_phone", null))
        assertEquals(42L, prefs.getLong("user_id", -1L))
    }

    @Test
    fun clearPrefs_removesAllData() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)

        prefs.edit().putString("jwt_token", "some-token").apply()
        assertNotNull(prefs.getString("jwt_token", null))

        prefs.edit().clear().apply()
        assertNull(prefs.getString("jwt_token", null))
    }

    @Test
    fun missingKey_returnsDefault() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)

        assertEquals("default", prefs.getString("non_existent_key", "default"))
        assertEquals(-1L, prefs.getLong("non_existent_key", -1L))
    }

    @Test
    fun overwriteValue_updatesCorrectly() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)

        prefs.edit().putString("user_role", "USER").apply()
        assertEquals("USER", prefs.getString("user_role", null))

        prefs.edit().putString("user_role", "RESPONSIBLE").apply()
        assertEquals("RESPONSIBLE", prefs.getString("user_role", null))
    }

    @Test
    fun emptyStringValue_storesAndReadsBack() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)

        prefs.edit().putString("user_name", "").apply()
        assertEquals("", prefs.getString("user_name", null))
    }

    @Test
    fun specialCharacters_storesAndReadsBackCorrectly() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)

        val special = "José María Fernández-Castaño López"
        prefs.edit().putString("user_name", special).apply()
        assertEquals(special, prefs.getString("user_name", null))
    }

    @Test
    fun removeKey_returnsDefaultAfterwards() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)

        prefs.edit().putString("jwt_token", "token-to-remove").apply()
        assertEquals("token-to-remove", prefs.getString("jwt_token", null))

        prefs.edit().remove("jwt_token").apply()
        assertNull(prefs.getString("jwt_token", null))
    }

    @Test
    fun multipleEditsInSingleTransaction_allAppliedAtomically() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)

        prefs.edit().apply {
            putString("user_name", "Miguel López")
            putString("user_email", "miguel@example.com")
            putString("user_role", "TECHNICIAN")
            putLong("user_id", 99L)
            apply()
        }

        assertEquals("Miguel López", prefs.getString("user_name", null))
        assertEquals("miguel@example.com", prefs.getString("user_email", null))
        assertEquals("TECHNICIAN", prefs.getString("user_role", null))
        assertEquals(99L, prefs.getLong("user_id", -1L))
    }

    @Test
    fun differentPrefsFile_isolatedFromAppPrefs() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appPrefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        val otherPrefs = context.getSharedPreferences("other_prefs", Context.MODE_PRIVATE)

        appPrefs.edit().putString("jwt_token", "app-token").apply()
        otherPrefs.edit().putString("jwt_token", "other-token").apply()

        assertEquals("app-token", appPrefs.getString("jwt_token", null))
        assertEquals("other-token", otherPrefs.getString("jwt_token", null))
    }
}

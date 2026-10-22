package com.unknowncoder05.escudo400

import android.database.sqlite.SQLiteConstraintException
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CallStoreTest {
    @Test fun whitelistCrudAndPersistence() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "whitelist-test.db"
        context.deleteDatabase(name)
        var store = CallStore(context, name)
        try {
            store.save("Empresa permitida", "400 123 456")
            val entry = store.allowedNumbers().single()
            assertEquals("+34400123456", entry.phone)
            assertTrue(store.contains("+34400123456"))
            try {
                store.save("Duplicado", "0034400123456")
                fail("Debe rechazar el número duplicado")
            } catch (_: SQLiteConstraintException) { /* La identidad normalizada es única. */ }
            store.save("Empresa actualizada", "400654321", entry.id)
            assertFalse(store.contains("+34400123456"))
            store.close()
            store = CallStore(context, name)
            assertEquals("Empresa actualizada", store.allowedNumbers().single().name)
            assertTrue(store.contains("+34400654321"))
            store.delete(entry.id)
            assertTrue(store.allowedNumbers().isEmpty())
        } finally { store.close(); context.deleteDatabase(name) }
    }
}

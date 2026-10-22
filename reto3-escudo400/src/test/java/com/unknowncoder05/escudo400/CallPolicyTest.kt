package com.unknowncoder05.escudo400

import org.junit.Assert.*
import org.junit.Test

class CallPolicyTest {
    @Test fun spanishFormatsHaveOneIdentity() {
        listOf("400123456", "+34 400-123-456", "0034 (400) 123 456").forEach {
            assertEquals("+34400123456", CallPolicy.normalize(it))
            assertTrue(CallPolicy.decide(it, true, false).blocked)
        }
    }
    @Test fun whitelistHasPriority() {
        assertEquals(Decision(false, Reason.WHITELIST), CallPolicy.decide("400123456", true, true))
    }
    @Test fun pausedProtectionAllowsCommercialCalls() {
        assertEquals(Decision(false, Reason.DISABLED), CallPolicy.decide("400123456", false, false))
    }
    @Test fun foreignNumbersSuffixesAndShortNumbersAreNotBlocked() {
        listOf("+14001234567", "+49400123456", "640012345", "600123400", "112", "400", "4001234567", "", null).forEach {
            assertFalse("No debe bloquear $it", CallPolicy.decide(it, true, false).blocked)
        }
    }
    @Test fun malformedNumbersAreNotAcceptedAsExceptions() {
        listOf("+34 40012345", "+34 4001234567", "++34400123456", "400123456 ext 1", "123", "+0034400123456").forEach {
            assertNull(CallPolicy.normalize(it))
        }
    }
}

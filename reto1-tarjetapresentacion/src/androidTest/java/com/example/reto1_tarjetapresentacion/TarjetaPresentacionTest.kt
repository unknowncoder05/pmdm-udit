package com.example.reto1_tarjetapresentacion

import android.content.Intent
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class TarjetaPresentacionTest {
    @get:Rule val compose = createComposeRule()

    @Test fun cardShowsProfileAndButtonInvokesAction() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        var clicked = false
        compose.setContent {
            MaterialTheme { TarjetaPresentacion(onOpenGitHub = { clicked = true }) }
        }
        compose.onNodeWithText(context.getString(R.string.profile_name)).assertIsDisplayed()
        compose.onNodeWithContentDescription(context.getString(R.string.profile_image_description))
            .assertIsDisplayed()
        compose.onNodeWithText(context.getString(R.string.github_button))
            .performScrollTo().performClick()
        compose.runOnIdle { assertTrue(clicked) }
    }

    @Test fun githubActionUsesConfiguredProfileAndViewIntent() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val url = context.getString(R.string.github_url)
        val intent = githubIntent(url)
        assertEquals(Intent.ACTION_VIEW, intent.action)
        assertEquals(url, intent.data.toString())
        assertEquals("https", intent.data?.scheme)
        assertEquals("github.com", intent.data?.host)
    }
}

package com.arestov.playlistmaker

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class MainTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun clickSearchButton_opensSearchScreen() {
        onView(withId(R.id.button_search))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.search)))

        onView(withId(R.id.button_media))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.media)))

        onView(withId(R.id.button_setting))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.settings)))
    }
}
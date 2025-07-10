package com.arestov.playlistmaker

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isNotChecked
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.allOf

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class SettingsTest {

    @get:Rule
    val activityScreen = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun settingScreenUi() {
        onView(withId(R.id.button_setting))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(
            allOf(
                withText(R.string.settings),
                isDescendantOfA(withId(R.id.toolbar_settings_screen))
            )
        ).check(matches(isDisplayed()))

        onView(withId(R.id.switcher_theme))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.dark_theme)))
            .check(matches(isNotChecked()))

        onView(withId(R.id.button_share))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.share_app)))

        onView(withId(R.id.button_support))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.write_to_support)))

        onView(withId(R.id.button_agreement))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.user_agreement)))

        onView(withContentDescription(R.string.settings))
            .perform(click())

        onView(withId(R.id.button_search))
            .check(matches(isDisplayed()))
    }
}
package com.arestov.playlistmaker

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.pressImeActionButton
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withHint
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arestov.playlistmaker.search.track.TrackViewHolder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class SearchTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun searchEditText() {
        onView(withId(R.id.button_search))
            .perform(click())

        // Проверка, что поле отображается
        onView(withId(R.id.search_view_search_screen))
            .check(matches(isDisplayed()))

        // Проверка, что установлен правильный hint
        onView(withId(R.id.search_view_search_screen))
            .check(matches(withHint(R.string.search)))

        // Ввод текста
        val testText = "Radio"
        onView(withId(R.id.search_view_search_screen))
            .perform(typeText(testText), closeSoftKeyboard())

        // Проверка, что текст введён
        onView(withId(R.id.search_view_search_screen))
            .check(matches(withText(testText)))

        onView(withId(R.id.clear_icon_search_view))
            .perform(click())

        // Проверка, что текст введён
        onView(withId(R.id.search_view_search_screen))
            .check(matches(withText("")))

    }


    @Test
    fun tracksIsDisplay() {
        onView(withId(R.id.button_search))
            .perform(click())

        val testText = "Radio"
        onView(withId(R.id.search_view_search_screen))
            .perform(typeText(testText), closeSoftKeyboard(), pressImeActionButton())

        onView(withId(R.id.search_view_search_screen))
            .check(matches(withText(testText)))

        onView(withId(R.id.recyclerView))
            .perform(RecyclerViewActions.actionOnItemAtPosition<TrackViewHolder>(0, click()));


    }
}
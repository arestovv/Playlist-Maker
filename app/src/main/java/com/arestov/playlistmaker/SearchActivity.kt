package com.arestov.playlistmaker

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import com.google.android.material.appbar.MaterialToolbar

class SearchActivity : AppCompatActivity() {
    private var text = ""
    private lateinit var inputEditText: EditText
    private lateinit var clearButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val searchContainer = findViewById<FrameLayout>(R.id.search_container_search_screen)
        inputEditText = findViewById(R.id.search_view_search_screen)
        clearButton = findViewById(R.id.clear_icon_search_view)

        //Back
        val back = findViewById<MaterialToolbar>(R.id.toolbar_search_screen)
        back.setNavigationOnClickListener {
            finish()
        }

        //Clear text
        clearButton.setOnClickListener {
            inputEditText.setText("")
            //Hide keyboard
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(clearButton.windowToken, 0)
        }

        //Click to search
        searchContainer.setOnClickListener {
            searchContainer.requestFocus()
            //Show keyboard
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(searchContainer, InputMethodManager.SHOW_IMPLICIT)
        }

        //Show button clear
        inputEditText.doOnTextChanged { text, _, _, _ ->
            clearButton.isVisible = !text.isNullOrEmpty()
        }

        //Save text after write
        inputEditText.doAfterTextChanged { input ->
            text = input.toString()
        }
    }

    //Save state
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, text)
    }

    //Restore state
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        text = savedInstanceState.getString(SEARCH_TEXT, "")
        inputEditText.setText(text)
        clearButton.isVisible = !inputEditText.text.isNullOrEmpty()

    }

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
    }
}

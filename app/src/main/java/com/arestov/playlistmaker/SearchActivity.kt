package com.arestov.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class SearchActivity : AppCompatActivity() {
    private var text = ""
    private lateinit var inputEditText: EditText
    private lateinit var clearButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val searchContainer = findViewById<LinearLayout>(R.id.search_container_search_screen)
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

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            //Show button clear
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
            }

            //Save text after write
            override fun afterTextChanged(s: Editable?) {
                text = s.toString()
            }
        }
        //Add listener
        inputEditText.addTextChangedListener(textWatcher)
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
        clearButton.visibility = clearButtonVisibility(text)
    }

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
    }
}

//Show or hide clear button
private fun clearButtonVisibility(s: CharSequence?): Int {
    return if (s.isNullOrEmpty()) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

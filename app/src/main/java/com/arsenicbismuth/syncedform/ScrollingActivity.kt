package com.arsenicbismuth.syncedform

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.DatePicker
import android.widget.EditText
import android.widget.RadioButton
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.util.*

class ScrollingActivity : AppCompatActivity(), OnDateSetListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val main = findViewById<ViewGroup>(R.id.lay_main)
        components = getChildren(main)
    }

    private fun getChildren(v: View): ArrayList<View> {

        // Anything with child(s) is a ViewGroup, end recursion if not
        if (v !is ViewGroup) {
            val viewArrayList = ArrayList<View>()
            viewArrayList.add(v)
            return viewArrayList
        }
        val result = ArrayList<View>()

        // Loop inside current group, and compile results from every child
        val vg = v
        Log.i("ChildCount", vg.childCount.toString())
        for (i in 0 until vg.childCount) {
            val child = vg.getChildAt(i)
            val viewArrayList = ArrayList<View>()
            viewArrayList.add(v)
            viewArrayList.addAll(getChildren(child)) // Recursion
            result.addAll(viewArrayList)
        }

        // Return to parent
        return result
    }// Get every EditText's tag & text.

    // Collect all input data
    private val data: MutableMap<String, String>
        private get() {
            // Collect all input data
            val result: MutableMap<String, String> = HashMap()
            for (comp in components) {
                // Get every EditText's tag & text.
                if (comp is EditText) {
                    result[comp.getTag().toString()] = comp.text.toString()
                }
            }
            return result
        }

    // Assigned to the fab (floating action button) onClick parameter.
    fun postSheet(view: View?) {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url = "https://script.google.com/macros/s/AKfycbzK1WJ5ruusJLIAUBZ_pDG_mpEpM8vXuDQTSxCKYdVEE_zkWBU/exec"

        // Collect all data to send
        val allData = data
        allData.putAll(allRadio) // Combine with radio data
        allData.putAll(allCheck) // Combine with check data

        // Request a string response from the provided URL.
        val stringRequest: StringRequest = object : StringRequest(Method.POST, url,
                Response.Listener { response ->
                    Snackbar.make(view!!, "Response: $response", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                }, Response.ErrorListener {
            Snackbar.make(view!!, "No response", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }) {
            override fun getParams(): Map<String, String> {
//                Map<String, String> params = new HashMap<>();
                return allData
            }
        }

        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    // Class variables for storing radioButton states
    private val allRadio: MutableMap<String, String> = HashMap()
    private val allCheck: MutableMap<String, String> = HashMap()

    // Assigned to every RadioButton onClick parameter.
    fun onRadioClicked(view: View) {
        // Check if button currently checked
        val checked = (view as RadioButton).isChecked

        // Tag naming format: group_pick. Ex: sex_female
        val tag = view.getTag().toString().split("_".toRegex()).toTypedArray()
        val group = tag[0]
        val pick = tag[1]

        // Put all data
        if (checked) {
            allRadio[group] = pick
        }
    }

    // Assigned to every check box onClick parameter.
    fun onCheckClicked(view: View) {
        val checked = (view as CheckBox).isChecked

        // Applies for every check, each must contains tag
        if (checked) {
            allCheck[view.getTag().toString()] = "v"
        } else {
            allCheck[view.getTag().toString()] = ""
        }
    }

    var picked: EditText? = null

    // Assigned to every date EditText onClick parameter.
    fun showDateDialog(view: View?) {
        picked = view as EditText? // Store the dialog to be picked
        val datePickerDialog = DatePickerDialog(
                this, this,
                Calendar.getInstance()[Calendar.YEAR],
                Calendar.getInstance()[Calendar.MONTH],
                Calendar.getInstance()[Calendar.DAY_OF_MONTH])
        datePickerDialog.show()
    }

    override fun onDateSet(datePicker: DatePicker, y: Int, m: Int, d: Int) {
        // If done picking date
        val date = d.toString() + "/" + (m + 1) + "/" + y
        picked!!.setText(date)
    }

    companion object {
        // Contains every component IDs inside content_scrolling.xml
        private var components = ArrayList<View>()
    }
}
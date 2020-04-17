package com.palmerchaun.matrixcalculator

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.transition.Slide
import android.transition.TransitionManager
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.NumberFormatException


class MainActivity : AppCompatActivity() {

    private var inputMatrix1 = ArrayList<ArrayList<EditText>>(5)
    private var inputMatrix2 = ArrayList<ArrayList<EditText>>(5)
    private var answerMatrix = ArrayList<ArrayList<TextView>>(5)
    private var answer = ArrayList<ArrayList<Int?>>(5)

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var spinner: Spinner = findViewById(R.id.spinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.operations,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }

        val computeBtn: Button = findViewById(R.id.computeBtn)

        computeBtn.setOnClickListener {
            calculate(spinner.selectedItem.toString())
        }

        val res = resources

        for (i in 0..4) {
            inputMatrix1.add(ArrayList<EditText>(5))
            inputMatrix2.add(ArrayList<EditText>(5))

            for (j in 0..4) {

                for (k in 1..2) {
                    var inputId = "m" + k + "r" + i + "c" + j
                    if (k == 1) {
                        inputMatrix1[i].add(findViewById(
                            res.getIdentifier(inputId, "id", packageName)
                        ))
                    } else {
                        inputMatrix2[i].add(
                            findViewById(
                                res.getIdentifier(inputId, "id", packageName)
                            )
                        )
                    }
                }
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun calculate(operation: String){
        var valid = false
        when(operation){
            "Addition" -> valid = addition()
            "Subtraction" -> valid = subtraction()
            "Matrix Multiplication" -> valid = multiplication()
            "Scalar Multiplication" -> valid = scalarMultiplication()
            "Determinant" -> {
                var num = determinant(getNumArray(inputMatrix1))
                if (num != Int.MIN_VALUE){
                    valid = true
                    answer.add(ArrayList<Int?>(5))
                    answer[0].add(num)
                }
            }


        }

        if (valid) {
            showPopUp()
        }
    }

    private fun addition(): Boolean {
        for (i in 0..4){
            answer.add(i, ArrayList<Int?>(5))

            for (j in 0..4){
                try {
                    var a: Int? = inputMatrix1[i][j].text.toString().toIntOrNull()
                    var b: Int? = inputMatrix2[i][j].text.toString().toIntOrNull()
                    if (a != null && b != null) {
                        answer[i].add(j, a + b)
                    }else{
                        answer[i].add(j, null)
                    }
                } catch(e: NumberFormatException){
                    //switch to continue if popup not showing
                    return false
                }
            }
        }

        return true
    }

    private fun subtraction(): Boolean{
        for (i in 0..4){
            answer.add(i, ArrayList<Int?>(5))

            for (j in 0..4){
                try {
                    var a: Int? = inputMatrix1[i][j].text.toString().toIntOrNull()
                    var b: Int? = inputMatrix2[i][j].text.toString().toIntOrNull()
                    if (a != null && b != null) {
                        answer[i].add(j, a - b)
                    }else{
                        answer[i].add(j, null)
                    }
                } catch(e: NumberFormatException){
                    //switch to continue if popup not showing
                    return false
                }
            }
        }

        return true
    }

    private fun multiplication(): Boolean {
        var aDim = getDimensions(getNumArray(inputMatrix1))
        var bDim = getDimensions(getNumArray(inputMatrix2))


        if (aDim[1] != bDim[0]){
            return false
        }

        for (i in 0 until aDim[0]){
            answer.add(ArrayList<Int?>(5))
            for (j in 0 until bDim[1]){
                answer[i].add(j, 0)
                for (k in 0 until aDim[1]){
                    if (inputMatrix1[i][k].text.toString() != "" && inputMatrix2[k][j].text.toString() != ""){
                        var a = inputMatrix1[i][k].text.toString().toInt()
                        var b = inputMatrix2[k][j].text.toString().toInt()
                        answer[i][j] = answer[i][j]?.plus(a * b)
                    } else{
                        continue
                    }
                }
            }
        }
        return true
    }

    private fun scalarMultiplication(): Boolean {
        var a = 0

        for (i in 0..4){
            answer.add(i, ArrayList<Int?>(5))
            for (j in 0..4){
                if (i == 0 && j == 0){
                    if (inputMatrix1[i][j].text.toString() != "") {
                        a = inputMatrix1[0][0].text.toString().toInt()
                    } else{
                        return false
                    }
                } else{
                    if (inputMatrix1[i][j].text.toString() != "") {
                        return false
                    }
                }
                if (inputMatrix2[i][j].text.toString() != ""){
                    answer[i].add(j, a * inputMatrix2[i][j].text.toString().toInt())
                }
            }
        }
        return true
    }

    private fun determinant(array: ArrayList<ArrayList<Int?>>): Int{
        var dim = getDimensions(array)
        var det = 0
        if (dim[0] != dim[1] || dim[0] == 0){
            return Int.MIN_VALUE
        } else if (dim[0] == 1){
            Log.w("MainActivity", "determinant(): rows " + dim[0])
            Log.w("MainActivity", "determinant(): columns " + dim[1])
            return array[0][0]!!
        } else if (dim[0] == 2){
            var a: Int = array[0][0]!!
            var b: Int = array[0][1]!!
            var c: Int = array[1][0]!!
            var d: Int = array[1][1]!!

            return  (a * d) - (b * c)
        } else{
            var scalar = 1
            for (i in 0 until dim[0]){
                var newArr = ArrayList<ArrayList<Int?>>(array.size - 1)
                for (j in 1 until dim[1]){
                    newArr.add(ArrayList<Int?>(array.size))
                    for (k in 0 until dim[0]){
                        if (k != i){
                            newArr[j-1].add(array[j][k])
                        }
                    }
                }
                Log.w("MainActivity", "determinant(): subarrayRows " + getDimensions(newArr)[0])

                Log.w("MainActivity", "determinant(): scalar " + scalar)
                Log.w("MainActivity", "determinant(): num " + array[0][i]!!)
                Log.w("MainActivity", "determinant(): det " + determinant(newArr))

                det +=  scalar * array[0][i]!! * determinant(newArr)

                scalar *= -1

            }
        }
        return det
    }
    private fun getDimensions(array: ArrayList<ArrayList<Int?>>): ArrayList<Int>{
        var dimensions = ArrayList<Int>(2)
        dimensions.add(0)
        dimensions.add(0)

        for (i in 0 until array.size){
            if (array[0][i] != null){
                dimensions[1] += 1
            }
            if (array[i][0] != null){
                dimensions[0] += 1
            }
        }

        return dimensions
    }

    private fun getNumArray(array: ArrayList<ArrayList<EditText>>): ArrayList<ArrayList<Int?>>{
        var arrNums = ArrayList<ArrayList<Int?>>(5)
        for (i in 0..4){
            arrNums.add(ArrayList<Int?>(5))
            for (j in 0..4){
                arrNums[i].add(array[i][j].text.toString().toIntOrNull())
            }
        }

        return arrNums
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun showPopUp(){
        val res = resources

        // Initialize a new layout inflater instance
        val inflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        // Inflate a custom view using layout inflater
        val view = inflater.inflate(R.layout.activity_pop_up_window, null)

        // Initialize a new instance of popup window
        val popupWindow = PopupWindow(
            view, // Custom view to show in popup window
            LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
            LinearLayout.LayoutParams.WRAP_CONTENT // Window height
        )

        // Set an elevation for the popup window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.elevation = 10.0F
        }


        // If API level 23 or higher then execute the code
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Create a new slide animation for popup window enter transition
            val slideIn = Slide()
            slideIn.slideEdge = Gravity.TOP
            popupWindow.enterTransition = slideIn

            // Slide animation for popup window exit transition
            val slideOut = Slide()
            slideOut.slideEdge = Gravity.RIGHT
            popupWindow.exitTransition = slideOut

        }

        for (i in 0 until answer.size) {
            answerMatrix.add(i, ArrayList<TextView>(5))

            for (j in 0 until answer[i].size) {
                var outputId = "ar" + i + "c" + j
                answerMatrix[i].add(
                    j, view.findViewById(
                        res.getIdentifier(
                            outputId, "id", packageName
                        )
                    )
                )
                if (answer[i][j] != null) {
                    answerMatrix[i][j].text = "" + answer[i][j]
                }
            }
        }

        // Get the widgets reference from custom view
        //val tv = view.findViewById<TextView>(R.id.text_view)
        val buttonPopup = view.findViewById<Button>(R.id.button_popup)

        // Set a click listener for popup's button widget
        buttonPopup.setOnClickListener {
            answer.clear()
            // Dismiss the popup window
            popupWindow.dismiss()
        }

        // Set a dismiss listener for popup window
        popupWindow.setOnDismissListener {
            Toast.makeText(applicationContext, "Popup closed", Toast.LENGTH_SHORT).show()
        }


        // Finally, show the popup window on app
        TransitionManager.beginDelayedTransition(root_layout)
        popupWindow.showAtLocation(
            root_layout, // Location to display popup window
            Gravity.CENTER, // Exact position of layout to display popup
            0, // X offset
            0 // Y offset
        )
    }
}

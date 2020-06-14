package com.fghilmany.aesencrypt

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    @ExperimentalStdlibApi
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_encrypt.setOnClickListener {

            //create Alert Dialog
            val builder = MaterialAlertDialogBuilder(this)

            builder.setTitle("Masukan teks yang akan di Enkripsi")

            // dialog message view
            val constraintLayout = getEditTextLayout(this)
            builder.setView(constraintLayout)

            val textInputLayout = constraintLayout.
                    findViewWithTag<TextInputLayout>("textInputLayoutTag")
            val textInputEditText = constraintLayout.
                    findViewWithTag<TextInputEditText>("textInputEditTextTag")

            builder.setPositiveButton("Submit"){ _, _ ->

                //key with 32 random character
                val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
                val strSecretKey = (1..32).map { kotlin.random.Random.nextInt(0, charPool.size) }
                        .map(charPool::get)
                        .joinToString("")

                //call aes class
                val aes = ChCrypto

                //encrypt process
                val chipperText = aes.aesEncrypt(textInputEditText.text.toString(), strSecretKey)

                //set view
                tv_plain_text.text = textInputEditText.text.toString()
                tv_key.text = strSecretKey
                tv_chipper_text.text = chipperText
                tv_decrypt.text = aes.aesDecrypt(chipperText, strSecretKey)

            }

            // alert dialog other buttons
            builder.setNegativeButton("No",null)
            builder.setNeutralButton("Cancel",null)

            // set dialog non cancelable
            builder.setCancelable(false)

            // finally, create the alert dialog and show it
            val dialog = builder.create()

            dialog.show()

            // initially disable the positive button
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false

            // edit text text change listener
            textInputEditText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int,
                                               p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int,
                                           p2: Int, p3: Int) {
                    if (p0.isNullOrBlank()){
                        textInputLayout.error = "Text is required."
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                                .isEnabled = false
                    }else{
                        textInputLayout.error = ""
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                                .isEnabled = true
                    }
                }
            })

        }


    }

    private fun getEditTextLayout(context: Context): ConstraintLayout {
        val constraintLayout = ConstraintLayout(context)
        val layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        constraintLayout.layoutParams = layoutParams
        constraintLayout.id = View.generateViewId()

        val textInputLayout = TextInputLayout(context)
        textInputLayout.boxBackgroundMode = TextInputLayout.BOX_BACKGROUND_OUTLINE
        layoutParams.setMargins(
                32.toDp(context),
                8.toDp(context),
                32.toDp(context),
                8.toDp(context)
        )
        textInputLayout.layoutParams = layoutParams
        textInputLayout.hint = "Input text"
        textInputLayout.id = View.generateViewId()
        textInputLayout.tag = "textInputLayoutTag"


        val textInputEditText = TextInputEditText(context)
        textInputEditText.id = View.generateViewId()
        textInputEditText.tag = "textInputEditTextTag"

        textInputLayout.addView(textInputEditText)

        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)

        constraintLayout.addView(textInputLayout)
        return constraintLayout
    }

    // extension method to convert pixels to dp
    private fun Int.toDp(context: Context):Int = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,this.toFloat(),context.resources.displayMetrics
    ).toInt()
}

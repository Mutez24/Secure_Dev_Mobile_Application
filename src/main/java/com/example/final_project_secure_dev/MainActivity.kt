package com.example.final_project_secure_dev

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Integer.parseInt
import java.security.MessageDigest

// To make it work I add the line "id 'kotlin-android-extensions'" in the file build.gradle

class MainActivity : AppCompatActivity() {
    // Create a companion object to use the native-lib file
    companion object {
        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }

    // Use to call the function SandBoxPasswordHash of the native lib
    private external fun SandBoxPasswordHash(): String

    private val mdpOfUser = SandBoxPasswordHash()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart(){
        super.onStart()

        // Create Toast start
        val toast = Toast.makeText(applicationContext, "Starting ...",
                Toast.LENGTH_SHORT)
        toast.show()

        // If Submit button is clicked, we lunch the Submitfunction()
        Submit_Button.setOnClickListener{
            submitFunction()
        }
    }

    // This function is used when the button submit is clicked
    // It verify if the user has entered the good password and a valid Id
    // If so, it lunch the account page
    private fun submitFunction(){
        // We store the value of the password enter by the user
        val mdpEntered = Password_TextBox.text.toString()
        // Here we hash the password that the user enter
        val mdpCryptedString = hashPassword(mdpEntered)
        // To know if the password is correct we compare its hash to the one we have stored

        // If the password is incorrect
        if(mdpCryptedString != mdpOfUser){
            Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show()
        }

        // We store the value of the id entered
        val idEntered = Id_TextBox.text.toString()
        // Boolean that indicates if the id is a valid
        var numeric = true
        var num = 0
        // We check if the id entered is an integer
        // It is supposed to be impossible since we put 'inputType="number"' in the xml file
        try{
            // We try to convert it, if it doesn't work it means it's not a integer
            num = parseInt(idEntered)
        } catch (e:  NumberFormatException) {
            numeric = false
            Toast.makeText(this, "Enter a numeric ID", Toast.LENGTH_SHORT).show()
        }
        // If it's an integer we check if it is below 73 since there is no greater id
        if (numeric){
            if(num>73){
                Toast.makeText(this, "Enter a correct ID", Toast.LENGTH_SHORT).show()
                numeric = false
            }
        }
        // We make sure that the number doesn't start with a zero
        if (numeric){
            if(idEntered[0]=='0'){
                Toast.makeText(this, "Enter a ID without 0 at the beginning",
                        Toast.LENGTH_SHORT).show()
                numeric = false
            }
        }
        // If the password is correct and the ID is valid we lunch the account page and create an
        // intent to send the id to the account page
        if((mdpCryptedString == mdpOfUser) && numeric){
            Toast.makeText(this, "Correct password going to accounts",
                    Toast.LENGTH_SHORT).show()
            // We lunch the account activity with an intent
            // We put the id information in the intent
            val intent = Intent(this,Accounts::class.java)
            intent.putExtra("IdOfUser", idEntered)
            intent.putExtra("mdpOfUser", mdpEntered)
            startActivity(intent)
        }
        // If the password and the id is correct we try a connection
        if((mdpCryptedString == mdpOfUser) && numeric){

        }

    }

    // Function that takes the password entered by the user then hash it
    // Takes the string password as a parameter
    // Return the password hashed
    private fun hashPassword(password: String): String{
        val mdpByte = password.toByteArray()
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val mdpCrypted = messageDigest.digest(mdpByte)
        // We use %02X to print two places (02) of Hexadecimal (X) value and store it in the string
        // st.
        val sb = StringBuffer()
        for(byte in mdpCrypted){
            var st = String.format("%02x", byte)
            sb.append(st)
        }
        val mdpCryptedString = sb.toString()
        return mdpCryptedString
    }
}
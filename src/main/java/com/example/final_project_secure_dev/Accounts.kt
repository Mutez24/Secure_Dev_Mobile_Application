package com.example.final_project_secure_dev

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_accounts.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException
import java.net.URL
import java.net.UnknownHostException
import javax.net.ssl.HttpsURLConnection

// To use coroutine I had the line :
// "'implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9'" in build gradle file

// To use HttpSUrlconnection I had the line :
// "<uses-permission android:name="android.permission.INTERNET" />" in AndroidManifest.xml

class Accounts : AppCompatActivity() {
    // Create a companion object to use the native-lib file
    companion object {
        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }
    // Use to call the functions from the native lib
    private external fun SandBoxAPI(): String

    private external fun SandBoxFileUser(): String

    private external fun SandBoxFileAccounts(): String

    // Url of the API (from the native-lib file)
    private var httpsUrl = SandBoxAPI()

    private var filenameUser = SandBoxFileUser()

    private var filenameAccounts = SandBoxFileAccounts()

    // Key to encrypt and decrypt the file that store the data in the internal storage
    // This key will correspond to the password that the user entered and will never appears in
    // plain text
    private var key = ""

    // UserNameObj is a Json Array that will store the result of the connection to the API config
    private var UserNameObj = JSONArray()
    // UserNameObj is a Json Array that will store the result of the connection to the API accounts
    private var AccountsObj = JSONArray()

    // UserId is an string that store the user ID from intent of the main page
    private var userId = ""

    // Boolean that store if the connection succeeded or not (if there is an internet connexion)
    private var connection = true

    // Boolean that store if files that store datas are already created or not
    private var fileExisted = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accounts)
    }

    override fun onStart(){
        super.onStart()
        // Create Toast start the account activity
        val toast = Toast.makeText(applicationContext,
            "Starting the account activity...", Toast.LENGTH_SHORT)
        toast.show()

        // We get the id from the main activity
        userId = (intent.extras?.get("IdOfUser")as String)
        // We get the userPassword from the main activity
        key = (intent.extras?.get("mdpOfUser")as String)
        // We create the URL of API config
        // We need to decrypt the url since it's stored encrypted
        val httpsUrlConfig = polyDecryption(httpsUrl, key) + "config"

        // Function that will connect to the url and store the content of the API in a Json array
        // It will either create the file Username_Lastname (if there is a connection) or read
        // information from the file
        tryConnection(httpsUrlConfig, filenameUser)
        // If the connection works we make a toast
        if (connection){
            val toast = Toast.makeText(applicationContext,
                "Connected", Toast.LENGTH_SHORT)
            toast.show()
        }
        // If it doesn't we make a toast
        else{
            val toast = Toast.makeText(applicationContext,
                "Not Connected", Toast.LENGTH_SHORT)
            toast.show()
        }
        // If there is no connection and no files created (meaning it's the first time you connect
        // and there is no file created since it's the first time you connect
        // We make a toast to inform the user
        if(!connection && !fileExisted){
            val toast = Toast.makeText(applicationContext,
                "It's the first time you connect, and there is no connection",
                Toast.LENGTH_SHORT)
            toast.show()
        }
        // If the connection works, or if there is no connection but files existed
        // We run the program
        if(connection || (!connection && fileExisted)){
            // We get the name and last name of the user that correspond to the id entered
            // First we need to convert the Json array to the object we need
            val jsonobject: JSONObject = UserNameObj.getJSONObject(userId.toInt()-1)
            // The index is the user ID minus one because the first index of the Json array is 0
            // and not 1 (as for the API)
            var userName = jsonobject.getString("name")
            var userLastName = jsonobject.getString("lastname")

            // We create a string that we want to show in the app
            var nameToShow = "$userName $userLastName"
            // We show the user name and last name
            Name_TextBox.text = String.format(nameToShow)

            // Function that permits to display all the accounts in the list view
            showListAccounts()

            // If refresh button is clicked, we lunch the showListAccounts() again to update the list
            Refresh_Button.setOnClickListener{
                val toast = Toast.makeText(applicationContext,
                    "Start refreshing...", Toast.LENGTH_SHORT)
                toast.show()
                showListAccounts()
                val toast2 = Toast.makeText(applicationContext,
                    "Refreshing done", Toast.LENGTH_SHORT)
                toast2.show()
                // If the connection works we make a toast
                if (connection){
                    val toast = Toast.makeText(applicationContext,
                            "Connected", Toast.LENGTH_SHORT)
                    toast.show()
                }
                // If it doesn't we make a toast
                else{
                    val toast = Toast.makeText(applicationContext,
                            "Not Connected", Toast.LENGTH_SHORT)
                    toast.show()
                }
            }
        }
    }

    // Function that try to connect to the API using coroutines, running block and jobs
    // Makes a network request is blocking the main thread, it will lead to freezing the user app
    // To ensure that we don't disrupt the user experience we use coroutines
    // We use Runblock to ensure that it will makes its jobs before continuing
    // The function takes the 2 parameters:
    //  httpsUrl: string that contain the url that we want to make connection with
    //  filename: string that contain the name of the file we want to create or read information
    //            from
    private fun tryConnection(httpsUrl: String, filename: String) =
        runBlocking {
            // When we use GlobalScope.launch, we create a top-level coroutine
            val job = GlobalScope.launch {//launch a new coroutine and keep a reference to its Job
                val url: URL
                // Then we try to make the connection
                try {
                    url = URL(httpsUrl)
                    val con = url.openConnection() as HttpsURLConnection
                    // We store the data of the url
                    // If there is no connection the error happen here
                    var stringData = con.inputStream.bufferedReader().readText()

                    // We set connection to true to indicates there is a connection
                    connection = true

                    // We store the Json object if its the name and last name in config/Id
                    // And we write a file in the internal storage to save information if offline
                    // We make sure to encrypt the data that we write in the file
                    if (filename == filenameUser){
                        val myFile = File(filesDir, filename)
                        myFile.writeText(polyEncryption(stringData, key))
                        UserNameObj = JSONArray(stringData)
                    }
                    // We store the Json array if its accounts information
                    // And we write a file in the internal storage to save information if offline
                    // We make sure to encrypt the data that we write in the file
                    if (filename == filenameAccounts){
                        val myFile = File(filesDir, filename)
                        myFile.writeText(polyEncryption(stringData, key))
                        AccountsObj = JSONArray(stringData)
                    }
                } catch (e: UnknownHostException) {
                    // We catch the exception if there is no connection or a broken url
                    e.printStackTrace()
                    // We set connection to false to indicates there is no connection
                    connection = false
                    // We get the datas from the file depending on the filename
                    // We make sure to decrypt the file's datas before we make it a Json Object
                    if (filename == filenameUser){
                        try{
                            val myFile = File(filesDir, filename)
                            var stringData  = polyDecryption(myFile.readText(), key)
                            UserNameObj = JSONArray(stringData)
                        }catch(e: FileNotFoundException){
                            e.printStackTrace()
                            fileExisted = false
                        }
                    }
                    // We make sure to decrypt the file's datas before we make it a Json Array
                    if (filename == filenameAccounts){
                        try{
                            val myFile = File(filesDir, filename)
                            var stringData  = polyDecryption(myFile.readText(), key)
                            AccountsObj = JSONArray(stringData)
                        }catch(e: FileNotFoundException){
                            e.printStackTrace()
                            fileExisted = false
                        }
                    }
                }
            }
            job.join() // wait until coroutine complete
        }

    // This function allows us to show all the accounts in the list view
    // So it first make a connection with the API url
    // Then put the datas in a good form to be shown in the list view
    // Finally display all the information in the list view
    private fun showListAccounts(){
        // Now we want to show all the accounts in the list view
        // First we get all the accounts informations with the API url
        // We need to decrypt the url since it's stored encrypted
        val httpsUrlAccounts = polyDecryption(httpsUrl, key) + "accounts/"

        // Function that will connect to the url and store the content of the API in a Json array
        tryConnection(httpsUrlAccounts, filenameAccounts)

        // Get the listView
        var mlistView = findViewById<ListView>(R.id.List_View)
        // Create a mutable list to print the string in the listview
        val informations = mutableListOf<String>()
        // Create a temporary variable to store the string we want to show in the list view
        var stringToAdd = ""

        // For every JsonObject in the Json array we store the element of the object in the mutable
        // list
        for (i in 0 until AccountsObj.length()) {
            // We create the Json object to have access to it
            val jsonobject: JSONObject = AccountsObj.getJSONObject(i)
            val id = jsonobject.getString("id")
            val title = jsonobject.getString("accountName")
            val company = jsonobject.getString("amount")
            val category = jsonobject.getString("iban")
            val currency = jsonobject.getString("currency")
            // We store all the element of the Json Object
            stringToAdd = "id: $id \ntitle: $title\ncompany: $company\ncategory: " +
                    "$category\ncurrency: $currency"
            // We add the string in the mutablelist
            informations.add(stringToAdd)
        }
        // We set the adaptater for the list view to show all the accounts
        val adapter = ArrayAdapter<String>(this,
            android.R.layout.simple_list_item_1, informations)
        mlistView.adapter = adapter
    }

    // Function that allows to encrypt a string from a key
    // It return the string encrypted
    private fun polyEncryption(toEncrypt: String, key: String): String {
        var encrypted = ""
        val size = key.length
        for (i in 0 until toEncrypt.length){
            var newAscii = toEncrypt[i].toInt() + key[i%size].toInt()
            if(newAscii>127){
                newAscii = 32 + newAscii%127
            }
            encrypted += newAscii.toChar()
        }
        return encrypted
    }

    // Function that allows to decrypt a string from a key
    // It return the string decrypted
    private fun polyDecryption(toDecrypt: String, key: String): String {
        var decrypted = ""
        val size = key.length
        for (i in 0 until toDecrypt.length){
            var oldAscii = toDecrypt[i].toInt() - key[i%size].toInt()
            if (oldAscii< 32){
                var diff = 32 - oldAscii
                oldAscii = 127 - diff
            }
            decrypted += oldAscii.toChar()
        }
        return decrypted
    }
}
# Clément MUTEZ IOS1 M1 Secure Development : Mobile applications

## Explain how you ensure user is the right one starting the app

When starting the app, the user arrive to the main page, to access the accounts page the user need to enter a password, this way only users with valid password 
can access the accounts page.

So the access to the accounts page is secured by a password but now the question is how is the password stored securely in the code.

As we do not have a database, the password need to be stored inside the code in brut text, but to secure the app I did two things, first the password isn't stored  in plain text and second the password is hidden in a another file. In fact, the password is stored encrypted with SHA-256. SHA-256 is a cryptographic hash function which means you can only hash a string in one way. It's a huge avantages when you want to encrypt a password, because even if the password is stored in brut text, once it's hashed, you can't decrypt it. The only way to decrypt a password is to use a 'rainbow table' that stored commonly used password and can decrypt a hash if it is a common password. So in the code, the password of the user is hashed with SHA-256 then stored in the code, and then we never use the password of the user in plain text. In fact when the user enter his password, to know if it's valid, we hash his input and compare it to the hashed password we have store. This way it'snot possible to know the password other than by knowing it. Finally, the hashed password is stored in a hidden C++ file. Both those techniques result in the fact that the password can't be read by inspecting the source code, nor decrypted since it is not possbile to reverse engineered SHA-256.

The password is the core element of the security of my application. Without it, not only the user can't access the second part of the application (accounts page) 
with all the information, but he cannot try to steal information from the source code since every important tokens (API url) and files content are encrypted 
with a polyencryption function that use the password as a key. So even if the polyencryption and decryption functions are in brut in the code, the key used is 
always the password of the user (but I will explain it later).

Note that since this is a school project, the password used is really simple and probably exist in the rainbow table. If you want the application to be really 
secure, it's better if the password of the user doesn't exist in those rainbow table.

For information the password of the user in the app is : 'mdp'

## How do you securely save user's data on your phone ?

In the application, if the user is not connected to the netword he still need to be able to use the application, to do so we need to store user's data and 
accounts information from the API somewhere in the app. To store the data, Android studio offers multiple option:
*App-specific storage: store files that are meant for app's use only either in a dedicated directory within an internal storage volume or external storage.Use the directories within internal storage to save sensitive information that other apps shouldn't access.
*Shared storage: store files that app intends to share with other apps
*Preferences: store private, primitive data in key-value pairs
*Databases: store structured data in a private databaxe

Since we don't want to share those informations we can't use shared storage or external storage. Since the API contain a JSON file we can't use preferences. Database weren't allowed for the project. So last things to use is the internal storage that correspond for the criteria of the application. These directories include both a dedicated location for storing persistent files, and another location for storing cache data. The system prevents other apps from accessing these locations and on Android 10 (API level 29) and higher, these locations are encrypted. These characteristics make these locations a good place to store sensitive data that only your app itself can access.

The file cannot be found inside the app or the root project but while running the emulator you can find it in data->data->com.example.td3_secure_dev->files->. So it's important to encrypt the data stored in the file so nobody can access and read those informations. To do so, when we connect to the API and want to make a copy of the information retrieved, we first encrypt those information before storing it in the internal storage. Then if there is no network connection we use the function PolyDecription to decrypt the data. 

Polyencryption/decryption encrypt/decrypt a string by changing the ascii values of every characters of the string depending on a key. So even if someone has access to the code, he needs to have the right key to decrypt the datas. So obviously the key to encrypt data is not stored in plain text in the code. In fact, we use the password input by the user from the main page as a key to encrypt file's data, that way the key is never stored in brut and nobody can access it. Note that if the user enter a wrong password, files will not be encrypted with a wrong key since the user needs to enter a good password to have access to the accounts page that lunch all the procedures to encrypt or decrypt files.

So to securely svae user's data, we save the datas in files that are in the internal storage. Thos files are encrypted and decrypted with functions that take the password of the user as a parameter. This way is is not possible to decrypt the datas except if you know the password.

## How did you hide the API url ?

## Screenshots of your application 

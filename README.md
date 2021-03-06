# Clément MUTEZ IOS1 M1 Secure Development : Mobile applications

## Explain how you ensure user is the right one starting the app

When starting the app, the user arrive to the main page, to access the accounts page the user need to enter a password, this way only users with valid password 
can access the accounts page.

So the access to the accounts page is secured by a password but now the question is how is the password stored securely in the code.

As we do not have a database, the password need to be stored inside the code in brut text, but to secure the app I did two things, first the password isn't stored 
in plain text and second the password is hidden in a another file. In fact, the password is stored encrypted with SHA-256. SHA-256 is a cryptographic hash function 
which means you can only hash a string in one way. It's a huge avantages when you want to encrypt a password, because even if the password is stored in brut text, 
once it's hashed, you can't decrypt it. The only way to decrypt a password is to use a 'rainbow table' that stored commonly used password and can decrypt a hash if 
it is a common password. So in the code, the password of the user is hashed with SHA-256 then stored in the code, and then we never use the password of the user 
in plain text. In fact when the user enter his password, to know if it's valid, we hash his input and compare it to the hashed password we have store. This way it's
not possible to know the password other than by knowing it. Finally, the hashed password is stored in a hidden C++ file. Both those techniques result in the fact 
that the password can't be read by inspecting the source code, nor decrypted since it is not possbile to reverse engineered SHA-256.

The password is the core element of the security of my application. Without it, not only the user can't access the second part of the application (accounts page) 
with all the information, but he cannot try to steal information from the source code since every important tokens (API url) and files content are encrypted 
with a polyencryption function that use the password as a key. So even if the polyencryption and decryption functions are in brut in the code, the key used is 
always the password of the user (but I will explain it later).

Note that since this is a school project, the password used is really simple and probably exist in the rainbow table. If you want the application to be really 
secure, it's better if the password of the user doesn't exist in those rainbow table.

For information the password of the user in the app is : 'mdp'

## How do you securely save user's data on your phone ?

In the application, if the user is not connected to the netword he still need to be able to use the application, to do so we need to store user's data and 
accounts information somewhere. I choose to store those data in the internal storage of the application.

## How did you hide the API url ?

## Screenshots of your application 

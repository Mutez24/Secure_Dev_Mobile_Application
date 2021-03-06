#include <jni.h>
#include <string>

// Function that store the Url of the API
// We first encrypt the url with the polydecryption function and the password of the user as a key
extern "C" JNIEXPORT jstring JNICALL
Java_com_example_final_1project_1secure_1dev_Accounts_SandBoxAPI(
        // Parameters fo the function
        JNIEnv* env,
        jobject /* this */) {
    std::string httpsUrl = "vy\u0085~xK=4G>6A@kBD;t@6v?5A?<AC5B@=?{ttyf\u0081w3z}4}ogspf\u007Fy4";
    return env->NewStringUTF(httpsUrl.c_str());
}

// Function that store the name of the file that contain user information
extern "C" JNIEXPORT jstring JNICALL
Java_com_example_final_1project_1secure_1dev_Accounts_SandBoxFileUser(
        // Parameters fo the function
        JNIEnv* env,
        jobject /* this */) {
    std::string filename = "Username_Lastname.txt";
    return env->NewStringUTF(filename.c_str());
}

// Function that store the name of the file that contain account information
extern "C" JNIEXPORT jstring JNICALL
Java_com_example_final_1project_1secure_1dev_Accounts_SandBoxFileAccounts(
        // Parameters fo the function
        JNIEnv* env,
        jobject /* this */) {
    std::string filename = "Account_data.txt";
    return env->NewStringUTF(filename.c_str());
}
// Function that store the hashed password of the API
// We encoded the password one time, then get it and store here
// So the password will be store directly in the code but in hexadecimal
// Hashing permits to encode only in one direction, so even if someone get the hashed password
// he couldn't know what the password is
// I enter the correct password  then it gives me the hexadecimal hashed string
extern "C" JNIEXPORT jstring JNICALL
Java_com_example_final_1project_1secure_1dev_MainActivity_SandBoxPasswordHash(
        // Parameters fo the function
        JNIEnv* env,
        jobject /* this */) {
    std::string password = "f4f263e439cf40925e6a412387a9472a6773c2580212a4fb50d224d3a817de17";
    return env->NewStringUTF(password.c_str());
}

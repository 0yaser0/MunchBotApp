package com.munchbot.munchbot.ui.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser

@Suppress("DEPRECATION")
class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> get() = _user

    private val _authError = MutableLiveData<String?>()
    val authError: LiveData<String?> get() = _authError

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _toastMessage

    private val _navigateToLogin = MutableLiveData<Boolean>()
    val navigateToLogin: LiveData<Boolean> get() = _navigateToLogin

    fun logIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        _user.value = user
                    } else {
                        _authError.value = "Please verify your email address."
                        auth.signOut()
                    }
                } else {
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthInvalidUserException) {
                        _authError.value = "Invalid email Or password."
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        _authError.value = "Invalid email Or password."
                    } catch (e: Exception) {
                        _authError.value = "Login failed. Please try again."
                    }
                }
            }
    }

    fun signUp(email: String, password: String) {
        auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val signInMethods = task.result?.signInMethods
                    if (signInMethods.isNullOrEmpty()) {
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { createTask ->
                                if (createTask.isSuccessful) {
                                    auth.currentUser?.sendEmailVerification()
                                        ?.addOnCompleteListener { verifyTask ->
                                            if (verifyTask.isSuccessful) {
                                                _toastMessage.value = "Verification email sent. Please check your inbox."
                                                _navigateToLogin.value = true
                                            } else {
                                                _toastMessage.value = "Failed to send verification email."
                                                Log.e("SignUpStep1", "Verification email failed: ${verifyTask.exception?.message}")
                                            }
                                        }
                                } else {
                                    val exception = createTask.exception
                                    if (exception is FirebaseAuthUserCollisionException) {
                                        _toastMessage.value = "Email already registered. Please try another email."
                                    } else {
                                        _toastMessage.value = "Failed to create account: ${exception?.message}"
                                        Log.e("SignUpStep1", "Account creation failed: ${exception?.message}")
                                    }
                                }
                            }
                    } else {
                        auth.currentUser?.reload()?.addOnCompleteListener { reloadTask ->
                            if (reloadTask.isSuccessful) {
                                if (auth.currentUser?.isEmailVerified == true) {
                                    _toastMessage.value = "Email already registered. Redirecting to login page."
                                    _navigateToLogin.value = true
                                } else {
                                    _toastMessage.value = "Email already registered, but not verified. Please verify your email before logging in."
                                }
                            } else {
                                Log.e("SignUpStep1", "Failed to reload user: ${reloadTask.exception?.message}")
                            }
                        }
                    }
                } else {
                    _toastMessage.value = "Failed to check email."
                    Log.e("SignUpStep1", "Email check failed: ${task.exception?.message}")
                }
            }
    }

    fun signOut() {
        auth.signOut()
        _user.value = null
    }
}
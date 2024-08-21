package com.munchbot.munchbot.ui.main_view.auth

import android.os.Handler
import android.os.Looper
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

    private val _showVerificationAlert = MutableLiveData<Boolean>()
    val showVerificationAlert: LiveData<Boolean> get() = _showVerificationAlert

    private val _resendButtonState = MutableLiveData(ButtonState.UNCLICKABLE)
    val resendButtonState: LiveData<ButtonState> get() = _resendButtonState

    private val handler = Handler(Looper.getMainLooper())
    private var resendButtonRunnable: Runnable? = null

    enum class ButtonState {
        CLICKABLE, UNCLICKABLE
    }

    fun updateResendButtonState(state: ButtonState) {
        _resendButtonState.value = state
    }

    fun signUp(email: String, password: String) {
        auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val signInMethods = task.result?.signInMethods
                    if (signInMethods.isNullOrEmpty()) {
                        createUser(email, password)
                    } else {
                        handleExistingEmail(email)
                    }
                } else {
                    _toastMessage.value = "Failed to check email."
                    Log.e("SignUpStep1", "Email check failed: ${task.exception?.message}")
                }
            }
    }

    private fun createUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { createTask ->
                if (createTask.isSuccessful) {
                    auth.currentUser?.sendEmailVerification()
                        ?.addOnCompleteListener { verifyTask ->
                            if (verifyTask.isSuccessful) {
                                _showVerificationAlert.value = true
                                startResendButtonTimer()
                            } else {
                                _toastMessage.value = "Failed to send verification email."
                                Log.e("SignUpStep1", "Verification email failed: ${verifyTask.exception?.message}")
                            }
                        }
                } else {
                    val exception = createTask.exception
                    if (exception is FirebaseAuthUserCollisionException) {
                        handleExistingEmail(email)
                    } else {
                        _toastMessage.value = "Failed to create account: ${exception?.message}"
                        Log.e("SignUpStep1", "Account creation failed: ${exception?.message}")
                    }
                }
            }
    }

    private fun handleExistingEmail(email: String) {
        auth.signInWithEmailAndPassword(email, "dummyPassword") // Use dummy password for existing email
            .addOnCompleteListener { signInTask ->
                if (signInTask.isSuccessful) {
                    auth.currentUser?.reload()?.addOnCompleteListener { reloadTask ->
                        if (reloadTask.isSuccessful) {
                            if (auth.currentUser?.isEmailVerified == true) {
                                _toastMessage.value = "Email already registered. Redirecting to login page."
                                _navigateToLogin.value = true
                            } else {
                                _toastMessage.value = "Email already registered, but not verified. Please verify your email before logging in."
                                auth.currentUser?.sendEmailVerification()
                            }
                        } else {
                            Log.e("SignUpStep1", "Failed to reload user: ${reloadTask.exception?.message}")
                        }
                    }
                } else {
                    Log.e("SignUpStep1", "Sign-in failed: ${signInTask.exception?.message}")
                }
            }
    }


    private fun startResendButtonTimer() {
        _resendButtonState.value = ButtonState.UNCLICKABLE
        resendButtonRunnable = Runnable {
            _resendButtonState.value = ButtonState.CLICKABLE
        }
        handler.postDelayed(resendButtonRunnable!!, 60000) // 1 minute delay
    }

    fun resendVerificationEmail() {
        if (_resendButtonState.value == ButtonState.CLICKABLE) {
            auth.currentUser?.sendEmailVerification()
                ?.addOnCompleteListener { verifyTask ->
                    if (verifyTask.isSuccessful) {
                        _toastMessage.value = "Verification email sent. Please check your inbox."
                    } else {
                        _toastMessage.value = "Failed to resend verification email."
                        Log.e("SignUpStep1", "Resend verification email failed: ${verifyTask.exception?.message}")
                    }
                }
        } else {
            _toastMessage.value = "Please wait before resending the verification email."
        }
    }

    fun checkEmailVerification(callback: (Boolean) -> Unit) {
        auth.currentUser?.reload()?.addOnCompleteListener { reloadTask ->
            if (reloadTask.isSuccessful) {
                val isVerified = auth.currentUser?.isEmailVerified == true
                callback(isVerified)
            } else {
                Log.e("AuthViewModel", "Failed to reload user: ${reloadTask.exception?.message}")
                callback(false)
            }
        }
    }



    fun signOut() {
        auth.signOut()
        _user.value = null
    }

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
}
package com.dmdev.fossilvaultanda.authentication.data

import com.dmdev.fossilvaultanda.authentication.domain.Authenticable
import com.dmdev.fossilvaultanda.authentication.domain.AuthenticationState
import com.dmdev.fossilvaultanda.authentication.domain.UserProfile
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidFBAuthentication @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : Authenticable {
    
    private val _profileSubject = MutableStateFlow<UserProfile?>(null)
    override val profileSubject: StateFlow<UserProfile?> = _profileSubject.asStateFlow()
    
    private val _authenticationState = MutableStateFlow(AuthenticationState.UNAUTHENTICATED)
    override val authenticationState: StateFlow<AuthenticationState> = _authenticationState.asStateFlow()
    
    private var currentUser: FirebaseUser? = null
    
    init {
        setupAuthStateListener()
    }
    
    private fun setupAuthStateListener() {
        firebaseAuth.addAuthStateListener { auth ->
            val user = auth.currentUser
            currentUser = user
            
            if (user != null) {
                val profile = UserProfile(
                    userId = user.uid,
                    email = user.email ?: "",
                    fullName = user.displayName,
                    username = user.displayName
                )
                
                _profileSubject.value = profile
                _authenticationState.value = if (user.isAnonymous) {
                    AuthenticationState.LOCAL_USER
                } else {
                    AuthenticationState.AUTHENTICATED
                }
            } else {
                _profileSubject.value = null
                _authenticationState.value = AuthenticationState.UNAUTHENTICATED
            }
        }
    }
    
    override suspend fun signInWith(email: String, password: String) {
        try {
            _authenticationState.value = AuthenticationState.AUTHENTICATING
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            
            if (user != null) {
                val profile = UserProfile(
                    userId = user.uid,
                    email = user.email ?: "",
                    fullName = user.displayName,
                    username = user.displayName
                )
                
                _profileSubject.value = profile
                _authenticationState.value = AuthenticationState.AUTHENTICATED
            }
        } catch (error: Exception) {
            _authenticationState.value = AuthenticationState.UNAUTHENTICATED
            throw error
        }
    }
    
    override suspend fun signUpWith(email: String, password: String) {
        try {
            _authenticationState.value = AuthenticationState.AUTHENTICATING
            val result = if (_authenticationState.value == AuthenticationState.LOCAL_USER) {
                signUpFromAnonymous(email, password)
            } else {
                firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            }
            
            val user = result.user
            if (user != null) {
                val profile = UserProfile(
                    userId = user.uid,
                    email = user.email ?: "",
                    fullName = user.displayName,
                    username = user.displayName
                )
                
                _profileSubject.value = profile
                _authenticationState.value = AuthenticationState.AUTHENTICATED
            }
        } catch (error: Exception) {
            if (_authenticationState.value != AuthenticationState.LOCAL_USER) {
                _authenticationState.value = AuthenticationState.UNAUTHENTICATED
            }
            throw error
        }
    }
    
    private suspend fun signUpFromAnonymous(email: String, password: String) = 
        currentUser?.let { user ->
            val credential = EmailAuthProvider.getCredential(email, password)
            val result = user.linkWithCredential(credential).await()
            result
        } ?: throw IllegalStateException("Unable to convert anonymous user")
    
    override suspend fun signInAsLocalUser() {
        try {
            val result = firebaseAuth.signInAnonymously().await()
            val user = result.user
            
            if (user != null) {
                val profile = UserProfile(
                    userId = user.uid,
                    email = user.email ?: "",
                    fullName = user.displayName,
                    username = user.displayName
                )
                
                _profileSubject.value = profile
                _authenticationState.value = AuthenticationState.LOCAL_USER
            }
        } catch (error: Exception) {
            _authenticationState.value = AuthenticationState.UNAUTHENTICATED
            throw error
        }
    }
    
    override fun signOut() {
        firebaseAuth.signOut()
    }
    
    override suspend fun deleteAccount() {
        currentUser?.delete()?.await()
    }
    
    override fun isLocalUser(): Boolean {
        return _authenticationState.value == AuthenticationState.LOCAL_USER
    }
    
    
    override fun updateProfile(id: String, email: String, username: String, location: String) {
        currentUser?.updateProfile(
            com.google.firebase.auth.UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build()
        )?.addOnSuccessListener {
            val profile = UserProfile(
                userId = id,
                email = email,
                username = username,
                location = location
            )
            _profileSubject.value = profile
        }
    }
}
package com.dmdev.fossilvaultanda.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmdev.fossilvaultanda.data.models.Specimen
import com.dmdev.fossilvaultanda.data.repository.interfaces.DatabaseManaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: DatabaseManaging
) : ViewModel() {
    
    init {
        Log.d("HomeViewModel", "HomeViewModel initialized")
        // Force a manual check
        viewModelScope.launch {
            try {
                val allSpecimens = repository.getAllSpecimens()
                Log.d("HomeViewModel", "Manual query result: ${allSpecimens.size} specimens")
                
                // Also test a direct Firestore query to see raw data
                testDirectFirestoreQuery()
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Manual query failed", e)
            }
        }
    }
    
    private suspend fun testDirectFirestoreQuery() {
        try {
            // Get direct access to Firestore to test raw queries
            val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
            firestore.collection("specimens")
                .limit(5)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    Log.d("HomeViewModel", "Direct query found ${querySnapshot.documents.size} documents")
                    for (doc in querySnapshot.documents) {
                        Log.d("HomeViewModel", "Doc ID: ${doc.id}")
                        Log.d("HomeViewModel", "Doc data: ${doc.data}")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("HomeViewModel", "Direct query failed", e)
                }
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Error in direct query", e)
        }
    }
    
    val specimens: StateFlow<List<Specimen>> = repository.specimens
        .onEach { specimens ->
            Log.d("HomeViewModel", "Specimens updated: ${specimens.size}")
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
package com.dmdev.fossilvaultanda.data.models

sealed class DataException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class ValidationException(message: String) : DataException(message)
    class NetworkException(message: String, cause: Throwable?) : DataException(message, cause)
    class StorageException(message: String, cause: Throwable?) : DataException(message, cause)
    class AuthenticationException(message: String) : DataException(message)
    class FirestoreException(message: String, cause: Throwable?) : DataException(message, cause)
    class DuplicateInventoryIdException(message: String = "A specimen with this inventory ID already exists") : DataException(message)
}
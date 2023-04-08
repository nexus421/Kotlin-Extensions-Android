package bayern.kickner.kotlin_extensions_android.uri

enum class Path {
    Download, Documents
}

enum class UriExtensionsError {
    InsertionFailed,
    InputErrorProviderCrashed,
    FileNotFound,
    InvalidColumnIndex,
    WriteFailed
}
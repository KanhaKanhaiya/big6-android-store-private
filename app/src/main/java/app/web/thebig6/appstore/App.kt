package app.web.thebig6.appstore

data class App (
    val name: String,
    val oneLineDescription: String,
    val packageName: String,
    val version: String,
    val versionCode: Int,
    val whatsNew: String,
    val screenshots: ArrayList<String>,
    val updatedOn: String,
    val size: String,
    val description: String,
    val icon: String,
    val isOpenable: Boolean,
    val checksum: String,
    val compatibleWith: String
)
package app.web.thebig6.appstore

data class App(
    val name: String = "",
    val oneLineDescription: String = "",
    val packageName: String = "",
    val version: String = "",
    val versionCode: Int = 0,
    val whatsNew: String = "",
    val screenshots: ArrayList<String>? = null,
    val updatedOn: String = "",
    val size: String = "",
    val description: String = "",
    val icon: String = "",
    val isOpenable: Boolean = false,
    val checksum: String = "",
    val compatibleWith: String = ""
)
package pet.itpuppy.fluffyisles.utils

object FormatCodes {
    private val CODES = Regex("§.")

    fun strip(text: String): String = CODES.replace(text, "")
}
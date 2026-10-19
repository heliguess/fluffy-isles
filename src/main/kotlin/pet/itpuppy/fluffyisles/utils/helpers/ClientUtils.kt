package pet.itpuppy.fluffyisles.utils.helpers

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen

object ClientUtils {
    val self get() = Minecraft.getInstance()
    val connection get() = self.connection
    val keyboardHandler get() = self.keyboardHandler
    val screen get() = self.screen
    val font get() = self.font
    val window get() = self.window

    val level get() = self.level
    val player get() = self.player
    val user get() = self.user

    val gui get() = self.gui
    val chat get() = gui.chat
    val bossOverlay get() = gui.bossOverlay

    fun execute(callback: Runnable) { self.execute(callback) }
    fun setScreenAndShow(screen: Screen) { self.setScreenAndShow(screen) }
}
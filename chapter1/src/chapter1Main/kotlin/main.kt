package chapter1

fun main() {
    val game = Game()
    val result = game.initialize()

    if (result) {
        game.runloop()
    }

    game.shutdown()

    return
}

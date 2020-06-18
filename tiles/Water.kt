package tiles

import java.lang.Long.parseLong

class Water: Tile {
    override val name: String = "Water"

    override val colour: Int = parseLong("000000ff", 16).toInt()

    override fun copy(): Tile {
        return Water()
    }

    override fun toString(): String {
        return "\u25AB"
    }

}
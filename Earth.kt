package com.example.demo

import java.lang.Long

class Earth: Tile {
    override val name: String = "Earth"

    override val colour: Int = Long.parseLong("00ffff00", 16).toInt()

    override fun copy(): Tile {
        return Earth()
    }

    override fun toString(): String {
        return "\u25A0"
    }
}
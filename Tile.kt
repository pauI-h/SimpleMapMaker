package com.example.demo

interface Tile {
    val name: String;
    val colour: Int

    fun copy(): Tile
}
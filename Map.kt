package com.example.demo

import javafx.scene.image.*
import java.awt.image.BufferedImage
import java.lang.reflect.WildcardType
import java.util.*

class World_Map(height: Int, width: Int, start_water_percentage: Float) {
    private val random = Random()
    private var map: Array<Array<Tile>> = Array(height) {Array(width) {
        if(random.nextFloat()>start_water_percentage){
            Earth()
        } else {
            Water()
        }
    } }
    val HEIGHT = height;
    val WIDTH = width;

    private var tile_map: MutableMap<Int, Tile> = mutableMapOf<Int, Tile>()

    init {
        tile_map[Water().colour] = Water()
    }

    fun setTile(row: Int, column: Int, tile: Tile){
        map[row][column] = tile.copy()
    }

    fun getTile(row: Int, column: Int): Tile{
        return try {
            map[row][column].copy();
        } catch (IE: IndexOutOfBoundsException) {
            Water()
        }
    }

    fun toImage(image: Image): Image{
        val pixel_reader = image.pixelReader;
        val writable_image = WritableImage(pixel_reader, HEIGHT, WIDTH)
        val writer = writable_image.pixelWriter;
        for (row in 0..writable_image.height.toInt()){
            for (column in 0..writable_image.width.toInt()){
                writer.setArgb(column, row, map[row][column].colour)
            }
        }
        return writable_image
    }

    fun loadFromImage(image: Image){
        val reader = image.pixelReader;
        for (row in 0..image.height.toInt()){
            for (column in 0..image.width.toInt()){
                val value = reader.getArgb(column, row)
                val temp = tile_map[value]
                if (temp == null) {
                    map[row][column] = Water()
                } else {
                    map[row][column] = temp.copy()
                }
            }
        }
    }

    override fun toString(): String {
        var str = ""
        for (row in map){
            str+=""
            for (tile in row){
                str += "$tile"
            }
            str += "\n"
        }
        return str;
    }

    fun addTiles(a: Tile, b: Tile): Tile{
        if (a is Water && b is Water){
            return Water();
        } else {
            return Earth();
        }
    }

    fun add(other: World_Map): World_Map{
        if (other.WIDTH != this.WIDTH || other.HEIGHT!=this.HEIGHT){
            throw IllegalArgumentException("Both maps must have the same dimensions")
        }
        val new_map = World_Map(HEIGHT, WIDTH, 0F)
        for (row in 0 until HEIGHT){
            for (column in 0 until HEIGHT){
                new_map.setTile(row, column, addTiles(this.getTile(row, column), other.getTile(row, column)))
            }
        }
        return new_map
    }
}
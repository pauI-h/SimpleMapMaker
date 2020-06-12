package com.example.demo

import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

open class ClumperCore(var water_prob: Float, var shape:Ellipse) {
    private val random = Random()
    var change_prob = 1F
    var water_count = 0
    var current_tiles = 0

    fun clumper(map: World_Map, row: Int, column: Int){
        if (row !in 0 until map.HEIGHT || column !in 0 until map.WIDTH){
            return;
        }
        current_tiles++
        val tile_is_water: Boolean;
        try {
            if (map.getTile(row, column) is Water) {
                tile_is_water = true
                water_count++
            } else {
                tile_is_water = false
            }
            val predicted_water = water_prob*current_tiles
            if (water_count > predicted_water
                    && random.nextFloat()<change_prob  ) {
                map.setTile(row, column, Earth())
                if (tile_is_water) {
                    water_count--
                }
            } else if (!shape.containsPoint(column, row)
                    && water_count < predicted_water
                    && random.nextFloat()<change_prob) {
                map.setTile(row, column, Water())
                if (!tile_is_water) {
                    water_count++
                }
            }
        } catch (IE: ArrayIndexOutOfBoundsException){
            current_tiles--
        }
    }
}
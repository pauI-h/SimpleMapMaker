package com.example.demo

import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.system.exitProcess

abstract class SpiralMovement {
    var current_tiles = 0
    var row: Int = 0
    var column: Int = 0
    var moved_current_dir = 0
    var current_row_change = 0
    var current_column_change = 1
    var to_move_current_dir = 1
    var num_current_to_move = 0

    open fun applyOnce(map: World_Map){
        row = map.HEIGHT/2
        column = map.WIDTH/2


        while (row<map.HEIGHT || column<map.WIDTH){
            if ((row<map.HEIGHT && row>-1) && (column<map.WIDTH && column>-1)) {
                current_tiles++;

                function(map)
            }

            row += current_row_change
            column += current_column_change
            moved_current_dir++;
            if (moved_current_dir >= to_move_current_dir) {
                moved_current_dir = 0
                val temp_row = current_row_change
                val temp_column = current_column_change
                current_row_change = -1 * temp_column
                current_column_change = temp_row
                num_current_to_move++;
            }
            if (num_current_to_move >= 2) {
                num_current_to_move = 0;
                to_move_current_dir++
            }
        }

        current_tiles = 0
        row = 0
        column = 0
        moved_current_dir = 0
        current_row_change = 0
        current_column_change = 1
        to_move_current_dir = 1
        num_current_to_move = 0
    }

    open fun applyNRounds(map: World_Map, rounds: Int){
        for (i in 0 until rounds){
            applyOnce(map)
        }
    }

    abstract fun function(map: World_Map)
}
package com.example.demo

abstract class SpiralMovement {

    open fun applyOnce(map: World_Map){
        var row = map.HEIGHT/2
        var column = map.WIDTH/2
        var current_tiles = 0
        var moved_current_dir = 0
        var current_row_change = 0
        var current_column_change = 1
        var to_move_current_dir = 1
        var num_current_to_move = 0


        while (row<map.HEIGHT || column<map.WIDTH){
            if ((row<map.HEIGHT && row>-1) && (column<map.WIDTH && column>-1)) {
                current_tiles++;

                function(map, row, column)
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

    open fun applyNRoundsConcurrently(map: World_Map, rounds: Int, wait: Double){
        val threads = Array(rounds){Thread{applyOnce(map)}}

        for (thread in threads){
            thread.start()
            Thread.sleep((((map.HEIGHT*map.WIDTH)-((map.HEIGHT-7)*(map.WIDTH-7))).toDouble()*wait).toLong())
        }

        while (threads[rounds-1].isAlive){
            Thread.sleep(10)
            Thread.yield()
        }

        for (thread in threads){
            thread.interrupt()
        }
    }

    abstract fun function(map: World_Map, row: Int, column: Int)
}

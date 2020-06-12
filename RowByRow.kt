package com.example.demo

import java.lang.Thread.yield
import kotlin.concurrent.thread

abstract class RowByRow {

    fun applyOnce(map: World_Map){
        for (row in 0 until map.HEIGHT){
            for (column in 0 until map.WIDTH){
                function(map, row, column)
            }
        }
    }

    fun applyNRounds(map: World_Map, rounds: Int){
        for (i in 0 until rounds){
            applyOnce(map)
        }
    }

    fun applyNRoundsConcurrently(map: World_Map, rounds: Int, wait: Double){
        val threads = Array(rounds){Thread{applyOnce(map)}}

        for (thread in threads){
            thread.start()
            Thread.sleep(((map.WIDTH*12).toDouble()*wait).toLong())
        }

        while (threads[rounds-1].isAlive){
            Thread.sleep(10)
            yield()
        }

        for (thread in threads){
            thread.interrupt()
        }
    }

    abstract fun function(map: World_Map, row: Int, column: Int)
}
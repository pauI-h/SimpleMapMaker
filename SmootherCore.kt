package com.example.demo

import java.lang.Thread.yield
import kotlin.math.min

open class SmootherCore(var square_size: Int, var threaded: Boolean) {

    fun changeTile(map: World_Map, row: Int, column: Int){
        map.setTile(row, column, smoother(TileSquare(map, arrayOf(row, column), square_size)))
    }

    private fun smoother(tiles: TileSquare): Tile{
        tiles.setCenterTile(Water())

        if (tiles.countTile(Water())<min(3*tiles.size, tiles.area/4)){ //n^2
            return Earth()
        }

        //Check for lake
        val target = ((tiles.size)/2).pow(2) //Maybe change this back to size/2 ^ 2
        val square_width = (tiles.size)/2
        for (row in 0..tiles.size/2){
            for (column in 0..tiles.size/2){
                if (tiles.countTilesInArea(Water(), row, row+square_width, column,
                                column+square_width)
                    == target){
                    return Water()
                }
            }
        }

//        for (row in 0 until tiles.size){ //Removes options that cannot be rivers
//            if (tiles.countTilesInArea(Water(), row, row, 0, tiles.size-1) < 3){
//                return Earth()
//            }
//        }
//
//        //Check for river
//        val threads = arrayOf(RiverChecker(true, tiles, threaded),
//                RiverChecker(false, tiles, threaded))
//
//        for (thread in threads){
//            thread.start()
//        }
//
//        var done = false
//        while(!done){
//            done = true
//            for (thread in threads){
//                if (!thread.is_finished){
//                    done = false
//                }
//                if (thread.is_finished && !thread.is_river){
//                    break;
//                }
//            }
//            yield()
//        }
//
//        for (thread in threads){
//            thread.interrupt()
//        }
//
//        if (threads[0].is_river && threads[1].is_river){
//            return Water()
//        }

        return Earth()
    }
}
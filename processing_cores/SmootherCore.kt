package processing_cores

import WorldMap
import pow
import tiles.Earth
import tiles.Tile
import tiles.Water

open class SmootherCore(var square_size: Int, var threaded: Boolean) {

    fun changeTile(map: WorldMap, row: Int, column: Int){
        map.setTile(row, column, smoother(WorldMap(map, arrayOf(row, column), square_size)))
    }

    private fun smoother(tiles: WorldMap): Tile {

        tiles.setCenterTile(Water())

        if (tiles.countTile(Water())<tiles.getArea()/4){ //n^2
            return Earth()
        }

        val target = ((tiles.getArea())/2).pow(2) //Maybe change this back to size/2 ^ 2
        val square_width = (tiles.WIDTH)/2
        val square_height = tiles.HEIGHT/2
        for (row in 0..tiles.HEIGHT/2) {
            for (column in 0..tiles.WIDTH/2) {
                if (tiles.countTilesInArea(Water(), row, row + square_height, column,
                                column + square_width)
                        == target) {
                    return Water()
                }
            }
        }

        //Check for lake
//        for (row in 0 until tiles.size){ //Removes options that cannot be rivers
//            if (tiles.countTilesInArea(Water(), row, row, 0, tiles.size-1) < 3){
//                return Earth()
//            }
//        }

        //Check for river
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
//            println("Is river")
//            return Water()
//        }

        return Earth()
    }
}
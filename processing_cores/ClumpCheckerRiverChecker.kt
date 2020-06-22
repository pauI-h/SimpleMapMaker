package processing_cores

import WorldMap
import map_tidying.ClumpChecker
import tiles.Water

class ClumpCheckerRiverChecker(var search_up: Boolean, var tiles: WorldMap): Thread() {
    var is_river = false;
    var is_finished = false;
    var is_running = false;
    var is_interupted = false;

    override fun run() {
        is_running = true

        val middle_row = (tiles.HEIGHT-1)/2

        if (search_up){
            val map = WorldMap(tiles, 0, middle_row, 0, tiles.WIDTH)
            val checker = ClumpChecker(map, Water())
            checker.setTileLake(middle_row, (tiles.WIDTH-1)/2)
            for (i in 0..tiles.WIDTH){
                if (checker.lakeMapValue(0, i) == null){
                    continue
                }
                if (checker.lakeMapValue(0, i)!!){
                    is_river = true
                    break
                }
            }
        } else {
            val map = WorldMap(tiles, middle_row, tiles.HEIGHT, 0, tiles.WIDTH)
            val checker = ClumpChecker(map, Water())
            checker.setTileLake(middle_row, (tiles.WIDTH-1)/2)
            for (i in 0..tiles.WIDTH){
                if (checker.lakeMapValue(tiles.HEIGHT, i)!!){
                    is_river = true
                    break
                }
            }
        }

        is_finished = true
        is_running = false
    }
}
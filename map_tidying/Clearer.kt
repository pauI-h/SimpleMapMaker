package map_tidying

import movement.RowByRow
import WorldMap
import tiles.Tile

class Clearer(val clump_checker: ClumpChecker, val set_to: Tile): RowByRow() {

    override fun function(map: WorldMap, row: Int, column: Int) {
        if (!clump_checker.isTileLake(row, column)){
            map.setTile(row, column, set_to.copy())
        }
    }
}
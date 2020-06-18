package map_tidying

import movement.RowByRow
import WorldMap

class Scanner(val clump_checker: ClumpChecker): RowByRow() {
    override fun function(map: WorldMap, row: Int, column: Int) {
        clump_checker.isTileLake(row, column)
    }
}
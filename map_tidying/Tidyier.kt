package map_tidying

import WorldMap
import movement.Applyable
import tiles.Tile
import tornadofx.*

class Tidyier(val tile_from: Tile, val tile_to: Tile): Applyable {

    override fun applyOnce(map: WorldMap) {
        val clump_checker = ClumpChecker(map, tile_from, false)
        val scanner = Scanner(clump_checker)
        val tidyier = Clearer(clump_checker, tile_to)

        println(clump_checker.isTileLake(3,2))
        scanner.applyOnce(map)
        tidyier.applyOnce(map)
    }

    override fun applyNRoundsConcurrently(map: WorldMap, rounds: Int, wait: Double) {
        warning("This does not save time compared to applyNRounds")
        applyNRounds(map, rounds)
    }

}
package map_tidying

import WorldMap
import movement.Applyable
import tiles.Tile

class Tidyier(val tile_from: Tile, val tile_to: Tile): Applyable {

    private var allow_edges = false
    private var square_size = 5

    constructor(tile_from: Tile, tile_to: Tile, allow_edges: Boolean): this(tile_from, tile_to){
        this.allow_edges = allow_edges
    }

    constructor(tile_from: Tile, tile_to: Tile, square_size: Int): this(tile_from, tile_to){
        setSquareSize(square_size)
    }

    constructor(tile_from: Tile, tile_to: Tile, allow_edges: Boolean, square_size: Int):
            this(tile_from, tile_to, allow_edges){
        setSquareSize(square_size)
    }


    override fun applyOnce(map: WorldMap) {
        val clump_checker = ClumpChecker(map, tile_from, false, 7)
        val scanner = Scanner(clump_checker)
        val tidyier = Clearer(clump_checker, tile_to)

        scanner.applyOnce(map)
        tidyier.applyOnce(map)
    }

    override fun applyNRoundsConcurrently(map: WorldMap, rounds: Int, wait: Double) {
        applyNRounds(map, rounds)
    }

    private fun setSquareSize(square_size: Int){
        if (square_size%2 != 1){
            throw IllegalArgumentException("Size should be odd")
        }

        this.square_size = square_size
    }

}
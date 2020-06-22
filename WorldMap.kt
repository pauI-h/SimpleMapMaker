import tiles.Earth
import tiles.Tile
import tiles.Water
import java.io.File
import java.util.*

class WorldMap(height: Int, width: Int, start_water_percentage: Float) {
    private val random = Random()
    private var map: Array<Array<Tile>> = Array(height) {Array(width) {
        if(random.nextFloat()>start_water_percentage){
            Earth()
        } else {
            Water()
        }
    } }
    val HEIGHT = height;
    val WIDTH = width;

    private var tile_map: MutableMap<Int, Tile> = mutableMapOf<Int, Tile>()

    constructor(file: File, tile_list: Array<Tile>):
            this(file.readLines().size, file.readLines()[0].length, 0F){
        loadFromFile(file, tile_list)
    }

    constructor(map: WorldMap, min_row: Int, max_row: Int, min_column: Int, max_column: Int):
            this(1+max_row-min_row, 1+max_column-min_column, 0F){

        for (row in min_row..max_row){
            for (column in min_column..max_column){
                setTile(row, column, map.getTile(row, column))
            }
        }
    }

    constructor(map: WorldMap, center: Array<Int>, size: Int): this(size, size, 0F){
        if (center.size != 2){
            throw IllegalArgumentException("Center was expected to have size 2 not ${center.size}")
        }

        if (size%2 != 1){
            throw IllegalArgumentException("Size should be odd")
        }

        this.map = Array(size){
            val row = it;
            return@Array Array(size){map.getTile(row-(size/2)+center[0], it-(size/2)+center[1]).copy()}
        }
    }

    init {
        tile_map[Water().colour] = Water()
    }

    fun setTile(row: Int, column: Int, tile: Tile){
        map[row][column] = tile.copy()
    }

    fun getTile(row: Int, column: Int): Tile {
        return try {
            map[row][column].copy();
        } catch (IE: IndexOutOfBoundsException) {
            Water()
        }
    }

    fun toFile(file: File){
        var start = System.currentTimeMillis()
        val t = this.toString()
        println(System.currentTimeMillis()-start)
        start = System.currentTimeMillis()
        file.writeText(t)
        println(System.currentTimeMillis()-start)
    }

    fun loadFromFile(file: File, tile_list: Array<Tile>){
        val lines = file.readLines()
        var row = 0
        for (line in lines){
            var column = 0
            for (char in line){
                for (tile in tile_list){
                    if (tile.toString() == char.toString()){
                        setTile(row, column, tile.copy())
                    }
                }
                column++
            }
            row++
        }
    }

    override fun toString(): String {
        var str = ""
        for (row in 0 until HEIGHT){
            str+=lineToString(row)
        }
        str.trim()
        return str;
    }

    private fun lineToString(row: Int): String{
        val row_tiles = map[row]
        var str = ""
        for (tile in row_tiles){
            str += "$tile"
        }
        str += "\n"
        return str
    }

    fun addTiles(a: Tile, b: Tile): Tile {
        if (a is Water && b is Water){
            return Water();
        } else {
            return Earth();
        }
    }

    fun add(other: WorldMap): WorldMap {
        if (other.WIDTH != this.WIDTH || other.HEIGHT!=this.HEIGHT){
            throw IllegalArgumentException("Both maps must have the same dimensions")
        }
        val new_map = WorldMap(HEIGHT, WIDTH, 0F)
        for (row in 0 until HEIGHT){
            for (column in 0 until HEIGHT){
                new_map.setTile(row, column, addTiles(this.getTile(row, column), other.getTile(row, column)))
            }
        }
        return new_map
    }

    fun isOnMap(row: Int, column: Int): Boolean{
        return !( (row>=HEIGHT || row<0) || (column>=WIDTH || column<0) )
    }

    fun setCenterTile(tile: Tile){
        setTile(HEIGHT/2,WIDTH/2, tile)
    }

    fun countTile(tile: Tile): Int{
        return countTilesInArea(tile, 0, HEIGHT-1, 0, WIDTH-1)
    }

    fun countTilesInArea(tile: Tile, min_row: Int, max_row: Int, min_column: Int, max_column: Int): Int{
        if (min_row > max_row){
            throw IllegalArgumentException("min_row must be smaller than max_row")
        }

        if (min_column > max_column){
            throw IllegalArgumentException("min_column must be smaller than max_column")
        }

        if (min_row<0 || min_column<0){
            throw IllegalArgumentException("Minimum value must not be less that 0")
        }

        if (max_column>=WIDTH){
            throw IllegalArgumentException("Max column ($max_column) must be less than the width ($WIDTH)")
        }

        if (max_row>=HEIGHT){
            throw IllegalArgumentException("Max row ($max_row) must be less than the height ($HEIGHT)")
        }

        var num = 0

        if (true){//max_row-min_row<=5 || max_column-min_column<=5) {
            for (row in min_row..max_row) {
                for (column in min_column..max_column) {
                    if (tile::class == getTile(row, column)::class) {
                        if (tile is Water && getTile(row, column) !is Water) {
                            throw IllegalArgumentException("Is counting not water as water")
                        }
                        num++
                    }
                }
            }
        }

        else {
            val threads = Array(max_row-min_row){
                CounterThread(tile, it + min_row,
                        min_column, max_column, this)
            }
            for (thread in threads){
                thread.start()
            }
            var done = false
            while (!done){
                done = true
                for (thread in threads){
                    done = done && thread.finished
                }
            }
            for (thread in threads){
                num += thread.count
            }
        }

        return num
    }

    fun countTilesInRow(tile: Tile, row: Int): Int{
        return countTilesInStrip(tile, row, 0, WIDTH-1)
    }

    private fun countTilesInStrip(tile: Tile, row: Int, min_column: Int, max_column: Int): Int{
        var count = 0
        for (column in min_column..max_column){
            if (tile::class == map[row][column]::class){
                count++
            }
        }
        return count
    }

    private class CounterThread(val tile: Tile, val row: Int, val min_column: Int, val max_column: Int,
                                val tiles: WorldMap): Thread(){
        var count = 0
        var finished = false

        override fun run() {
            count = tiles.countTilesInStrip(tile, row, min_column, max_column)
            finished = true
        }
    }

    fun getArea(): Int{
        return HEIGHT*WIDTH
    }

    fun copy(): WorldMap{
        return WorldMap(this, 0, HEIGHT, 0, WIDTH)
    }
}

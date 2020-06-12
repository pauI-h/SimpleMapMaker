package com.example.demo

class TileSquare(map: World_Map, center: Array<Int>, val size: Int) {
    val area = size.pow(2)
    var content: Array<Array<Tile>>
    init {
        if (center.size != 2){
            throw IllegalArgumentException("Center was expected to have size 2 not ${center.size}")
        }

        if (size%2 != 1){
            throw IllegalArgumentException("Size should be odd")
        }

        content = Array(size){
            val row = it;
            return@Array Array(size){map.getTile(row+(center[0]/2), it+(center[1]/2)).copy()}
        }
    }

    fun getCenterTile(): Tile{
        return content[size/2][size/2]
    }

    fun getTile(row: Int, column: Int): Tile{
        return content[row][column].copy()
    }

    fun setTile(row: Int, column: Int, tile: Tile){
        content[row][column] = tile.copy()
    }

    fun setCenterTile(tile: Tile){
        setTile(size/2,size/2, tile)
    }

    fun countTile(tile: Tile): Int{
        return countTilesInArea(tile, 0, size-1, 0, size-1)
    }

    fun countTilesInArea(tile: Tile, min_row: Int, max_row: Int, min_column: Int, max_column: Int): Int{
        if (min_row > max_row){
            throw IllegalArgumentException("min_row must be smaller than max_row")
        }

        if (min_column > max_column){
            throw IllegalArgumentException("min_column must be smaller than max_column")
        }

        if (min_row<0 || min_column<0){
            throw IllegalArgumentException("minimum value must not be less that 0")
        }

        if (max_row>=size || max_column>=size){
            throw IllegalArgumentException("max value ($max_column) must be less than the size ($size)")
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
            val threads = Array(max_row-min_row){CounterThread(tile, it+min_row,
                    min_column, max_column, this)}
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
        return countTilesInStrip(tile, row, 0, size-1)
    }

    private fun countTilesInStrip(tile: Tile, row: Int, min_column: Int, max_column: Int): Int{
        var count = 0
        for (column in min_column..max_column){
            if (tile::class == content[row][column]){
                count++
            }
        }
        return count
    }

    private class CounterThread(val tile: Tile, val row: Int, val min_column: Int, val max_column: Int,
                                val tiles: TileSquare): Thread(){
        var count = 0
        var finished = false

        override fun run() {
            count = tiles.countTilesInStrip(tile, row, min_column, max_column)
            finished = true
        }
    }

    override fun toString(): String {
        var temp = ""
        for (row in 0 until size){
            for (column in 0 until size){
                temp+=content[row][column].toString()
            }
            temp+="\n"
        }
        return temp
    }
}

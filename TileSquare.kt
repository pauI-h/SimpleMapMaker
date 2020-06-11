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

    fun countTile(tile: Tile): Int{
        return countTilesInArea(tile, 0, size-1, 0, size-1)
    }

    fun getTile(row: Int, column: Int): Tile{
        return content[row][column].copy()
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

        for (row in min_row..max_row){
            for (column in min_column..max_column){
                if (tile::class == getTile(row, column)::class){
                    if (tile is Water && getTile(row, column) !is Water){
                        throw IllegalArgumentException("Is counting not water as water")
                    }
                    num++
                }
            }
        }

        if (getCenterTile()::class == tile::class){
            num--
        }

        return num
    }

    fun setTile(row: Int, column: Int, tile: Tile){
        content[row][column] = tile.copy()
    }

    fun setCenterTile(tile: Tile){
        setTile(size/2,size/2, tile)
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
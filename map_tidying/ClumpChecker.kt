package map_tidying

import WorldMap
import tiles.Tile
import kotlin.math.abs

class ClumpChecker (val map: WorldMap, val tile: Tile) {

    private var lake_map: MutableMap<List<Int>, Boolean> = mutableMapOf()
    private var being_checked: MutableList<List<Int>> = mutableListOf()
    private var allow_edges: Boolean = false

    constructor(map: WorldMap, tile: Tile, allow_edges: Boolean): this(map, tile){
        this.allow_edges = allow_edges
    }

    fun isTileLake(row: Int, column: Int): Boolean{
        val temp = listOf(row, column)

        //println(lake_map.size.toFloat()/(1024F.pow(2))) //Gives the fraction completed

        being_checked.add(temp)
        if (lake_map.containsKey(temp)){
            being_checked.remove(temp)
            return lake_map[temp]!!; //This can't return null as the key is guaranteed
        }

        if (map.getTile(row, column)::class != tile::class){
            lake_map[temp] = false
            being_checked.remove(temp)
            return false
        }

        val square = WorldMap(map, arrayOf(row, column), 5)

        if (square.countTile(tile) == square.getArea()){
            setTileLake(row, column)
            being_checked.remove(temp)
            return true
        }

        val changes = arrayOf(arrayOf(1,0), arrayOf(-1,0), arrayOf(0,1), arrayOf(0,-1))

        for (change in changes){
            val row_check = row+change[0]
            val column_check = column+change[1]
            if (abs(row_check-row)+abs(column_check-column) != 1){
                println("Row: $row -> $row_check")
                println("Column: $column -> $column_check")
                throw IllegalArgumentException("An invalid square is being checked")
            }
            if (!map.isOnMap(row_check, column_check) && !allow_edges){
                continue
            }
            if (being_checked.contains(listOf(row_check,column_check))){ //Stops infinite loops
                continue;
            }
            else if (map.getTile(row_check, column_check)::class == tile::class){
                if (isTileLake(row_check, column_check)) {
                    setTileLake(row, column)
                    being_checked.remove(temp)
                    return true
                }
            }
        }

        lake_map[temp] = false
        being_checked.remove(temp)
        return false
    }

    private fun setTileLake(row: Int, column: Int){
        val temp = listOf(row, column)
        lake_map[temp] = true

        val changes = arrayOf(arrayOf(1,0), arrayOf(-1,0), arrayOf(0,1), arrayOf(0,-1))

        for (change in changes){
            val row_check = row+change[0]
            val column_check = column+change[1]

            if (abs(row_check-row)+abs(column_check-column) != 1){
                println("Row: $row -> $row_check")
                println("Column: $column -> $column_check")
                throw IllegalArgumentException("An invalid square is being checked")
            }
            if (!map.isOnMap(row_check, column_check) && !allow_edges){
                continue
            }

            else if (map.getTile(row_check, column_check)::class == tile::class){
                val t = listOf(row_check, column_check)
                if (lake_map.containsKey(t)){
                    if (lake_map[t]!!){}
                    else {
                        setTileLake(row_check, column_check)
                    }
                }
            }
        }
    }
}
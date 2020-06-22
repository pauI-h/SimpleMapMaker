package processing_cores

import WorldMap
import tiles.Water
import java.util.*


class RiverChecker(var search_up: Boolean, var tiles: WorldMap, var threaded: Boolean = true): Thread() {
    var is_river = false;
    var is_finished = false;
    var is_running = false;
    var tile_thread_map: MutableMap<List<Int>, RiverChecker> = mutableMapOf()
    var is_interupted = false;

    var row = tiles.HEIGHT/2
    var column = tiles.WIDTH/2


    private constructor(search_up: Boolean, tiles: WorldMap, row: Int, column: Int,
                        tile_thread_map: MutableMap<List<Int>, RiverChecker>): this(search_up, tiles){
        this.search_up = search_up
        this.tiles = tiles
        this.row = row
        this.row = column
        this.tile_thread_map = tile_thread_map
    }

    override fun run() {
        is_running = true
        is_interupted = false
        is_finished = false
        if (threaded) {
            if (search_up) {
                is_river = searchUp(tiles, row, column)
            } else {
                is_river = searchDown(tiles, row, column)
            }
        } else {
            if (search_up) {
                is_river = searchUpStack(tiles, row, column)
            } else {
                is_river = searchDownStack(tiles, row, column)
            }
        }
        is_running = false
        is_finished = true
    }

    private fun search(tiles: WorldMap, row: Int, column: Int, row_difference: Int): Boolean{
        if (row_difference == 0){
            throw IllegalArgumentException("Row difference cannot be 0")
        }

        //These two are the base cases
        if (row < 0 && row_difference<0){
            return true
        }

        if (row >= tiles.getArea() && row_difference>0){
            return true
        }

        if (!rowCheck(tiles, row, column)){
            return false
        }

        val columns: MutableList<Int> = getColumnList(tiles, row, column).toMutableList() //Reduces pass by reference errors \u1F91E

        val next_row = row+row_difference

        if (columns.size == 1){ //Reduces thread overhead
            return search(tiles, next_row, columns[0], row_difference)
        } else {

            var result = false
            val threads = Array(columns.size)
                        { RiverChecker(true, tiles, next_row, columns[it], tile_thread_map) }

            for (i in 0 until columns.size) { //Stops tiles being checked multiple times
                if (tile_thread_map.containsKey(listOf(next_row, columns[i]))) {
                    threads[i] = tile_thread_map[listOf(next_row, columns[i])]!!
                } else {
                    tile_thread_map[listOf(row + row_difference, columns[i])] = threads[i]
                }
            }


            for (pos in threads.indices) {

                var count = 0
                while (true){
                    var tried = false
                    var thread = threads[pos]
                    if (!thread.is_running && !thread.is_finished) {
                        try {
                            if (thread.is_interupted || thread.state == State.RUNNABLE){ //Reduces some exceptions
                                thread.run()
                                tried = true
                                continue
                            }
                            thread.start()

                            break
                        } catch (ITSE: IllegalThreadStateException) {
                            count++
                            if (count>=5 || tried){
                                println(thread.state)
                            }
                        }
                    }
                }
            }

            var done = false

            while (!done && !is_interupted) {
                done = true
                for (thread in threads) {
                    if (thread.is_running) {
                        done = false
                    }
                    if (thread.is_finished && thread.is_river) {
                        done = true
                        result = true;
                        break;
                    }
                }
                yield()
            }

            if (is_interupted) {
                for (thread in threads) {
                    if (thread.is_finished && thread.is_river){
                        return true
                    }
                    thread.interrupt()
                }
                return result;
            }

            if (result) {
                for (thread in threads) {
                    thread.is_finished = true
                    thread.interrupt()
                }
            }

            for (thread in threads) {
                if (thread.is_river) {
                    result = true
                }
            }

            return result
        }

    }

    private fun searchUp(tiles: WorldMap, row: Int, column: Int): Boolean {
        return search(tiles, row, column, -1)
    }

    private fun searchDown(tiles: WorldMap, row: Int, column: Int): Boolean {
        return search(tiles, row, column, +1)
    }

    private fun searchUpStack(tiles: WorldMap, row: Int, column: Int): Boolean{
        if (row < 0){
            return true
        }

        if (!rowCheck(tiles, row, column)){
            return false
        }

        val stack = Stack<Array<Int>>()

        var columns: MutableList<Int> = getColumnList(tiles, row, column)

        for (i in columns){
            stack.add(arrayOf(row, i))
        }

        while (!stack.isEmpty()){
            val current_position = stack.pop()
            if (current_position[0] < 0){
                return true
            }

            if (!rowCheck(tiles, current_position[0], current_position[1])){
                continue
            }

            columns= getColumnList(tiles, current_position[0], current_position[1])

            for (i in columns){
                stack.add(arrayOf(current_position[0]-1, i))
            }

        }

        return false
    }

    private fun searchDownStack(tiles: WorldMap, row: Int, column: Int): Boolean {
        if (row >= tiles.HEIGHT){
            return true
        }

        if (!rowCheck(tiles, row, column)){
            return false
        }

        val stack = Stack<Array<Int>>()

        var columns: MutableList<Int> = getColumnList(tiles, row, column)

        for (i in columns){
            stack.add(arrayOf(row, i))
        }

        while (!stack.isEmpty()){
            val current_position = stack.pop()
            if (current_position[0] >= tiles.HEIGHT){
                return true
            }

            if (!rowCheck(tiles, current_position[0], current_position[1])){
                continue
            }

            columns= getColumnList(tiles, current_position[0], current_position[1])

            for (i in columns){
                stack.add(arrayOf(current_position[0]+1, i))
            }

        }

        return false
    }

    private fun rowCheck(tiles: WorldMap, row: Int, column: Int): Boolean{
        if (tiles.getTile(row, column)::class != Water()::class){
            return false
        }

        if (is_interupted){
            return false
        }

        if ((tiles.getTile(row, column-1)::class != Water()::class || column<1)&& //Checks left tile
                (tiles.getTile(row, column+1)::class != Water()::class || column>tiles.WIDTH-1)){ //Checks right tile
            return false
        }

        return true
    }

    private fun getColumnList(tiles: WorldMap, row: Int, column: Int): MutableList<Int>{
        val columns: MutableList<Int> = MutableList(0) {it} //Blank list of Ints

        try {
            if (tiles.countTilesInArea(Water(), row, row, column - 1, column + 1) == 3) {
                columns.add(column)
            }
        } catch (IE: ArrayIndexOutOfBoundsException){}


        try {
            if (tiles.countTilesInArea(Water(), row, row, column - 2, column) == 3) {
                columns.add(column - 1)
            }
        } catch (IE: ArrayIndexOutOfBoundsException){}


        try {
            if (tiles.countTilesInArea(Water(), row, row, column, column + 2) == 3) {
                columns.add(column + 1)
            }
        } catch (IE: ArrayIndexOutOfBoundsException){}

        return columns.toMutableList() //Reduces pass by reference issues
    }

    override fun interrupt() {
        is_finished = true
        is_interupted = true
        is_running = false
        super.interrupt()
    }
}
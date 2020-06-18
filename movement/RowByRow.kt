package movement

import WorldMap
import java.lang.Thread.yield

abstract class RowByRow: Applyable {

    override fun applyOnce(map: WorldMap){
        for (row in 0 until map.HEIGHT){
            for (column in 0 until map.WIDTH){
                function(map, row, column)
            }
        }
    }

    override fun applyNRoundsConcurrently(map: WorldMap, rounds: Int, wait: Double){
        if (rounds == 0){
            return
        }
        val threads = Array(rounds){Thread{applyOnce(map)}}

        for (thread in threads){
            thread.start()
            Thread.sleep(((map.WIDTH*12).toDouble()*wait).toLong())
        }

        while (threads[rounds-1].isAlive){
            Thread.sleep(10)
            yield()
        }

        for (thread in threads){
            thread.interrupt()
        }
    }

    abstract fun function(map: WorldMap, row: Int, column: Int)
}
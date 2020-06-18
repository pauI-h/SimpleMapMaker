package movement

import WorldMap

interface Applyable {

    fun applyOnce(map: WorldMap)

    fun applyNRounds(map: WorldMap, rounds: Int){
        for (i in 0 until rounds){
            applyOnce(map)
        }
    }

    fun applyNRoundsConcurrently(map: WorldMap, rounds: Int, wait: Double)
}
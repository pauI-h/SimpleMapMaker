import processing_cores.ClumpCheckerRiverChecker
import processing_cores.RiverChecker
import java.lang.Thread.yield

class RiverCheckSpeedCheck {

    val river_speed_map: MutableMap<Float, MutableList<Long>> = mutableMapOf()
    val clump_speed_map: MutableMap<Float, MutableList<Long>> = mutableMapOf()

    fun main(){
        for (i in 0 .. 10){
            val prob = i.toFloat()/10

            river_speed_map[prob] = mutableListOf()
            clump_speed_map[prob] = mutableListOf()

            for (j in 0 until 200){
                val map = WorldMap(11, 11, prob)
                river_speed_map[prob]?.add(runRiverChecker(map.copy()))
                clump_speed_map[prob]?.add(runClumpRiverChecker(map.copy()))
            }

            println("$prob: ${mean(river_speed_map[prob]!!)},  ${mean(clump_speed_map[prob]!!)}")

        }
    }

    fun runRiverChecker(map: WorldMap): Long{
        val start = System.currentTimeMillis()
        val river_check = RiverChecker(true, map)
        river_check.start()
        while(!river_check.is_finished){
            yield()
        }
        return System.currentTimeMillis()-start
    }

    fun runClumpRiverChecker(map: WorldMap): Long{
        val start = System.currentTimeMillis()
        val river_check = ClumpCheckerRiverChecker(true, map)
        river_check.start()
        while(!river_check.is_finished){
            yield()
        }
        return System.currentTimeMillis()-start
    }

    fun mean(list: List<Long>): Double{
        var sum = 0L
        for (i in list){
            sum += i
        }
        return sum.toDouble()/list.size.toDouble()
    }

}
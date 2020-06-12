package com.example.demo

import java.io.File
import kotlin.math.PI
import kotlin.math.pow
import kotlin.system.exitProcess

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val temp = Code()
            temp.run(1000,1000,0.8F,0.5F, 0.9F, 0.2F)
        }
    }
}

class Code(){

    private var smoother = SpiralSmoother(11, true)
    private val smoother_delay = 1.5
    private lateinit var automata: RowAutomata;
    private lateinit var clumper: SpiralClumper;

    fun run(height: Int, width: Int, water_start_prob: Float, water_prob: Float, max_prob: Float, min_prob: Float) {
        val map = World_Map(height, width, water_start_prob)
        automata = RowAutomata(water_prob, max_prob, min_prob, 11)
        val spiral_automata = SpiralAutomata(water_prob, max_prob, min_prob, 7)
        clumper = SpiralClumper(water_prob*1.2F, Ellipse(map.HEIGHT/3, map.WIDTH/4,
                map.HEIGHT/2, map.WIDTH/2, (PI/3F).toFloat()));

        //Speed testing stuff hidden here

        {
//            var norm_start = System.currentTimeMillis()
//            println("Normal automata start")
//            automata.applyNRounds(map, 3)
//            println("Normal ${System.currentTimeMillis() - norm_start}")
//
//            var concurrent_start = System.currentTimeMillis()
//            println("Concurrent start")
//            automata.applyNRoundsConcurrently(map, 3, 0.01)
//            println("Concurrent ${System.currentTimeMillis() - concurrent_start}")
//
//
//            norm_start = System.currentTimeMillis()
//            println("Normal smoother start")
//            smoother.applyNRounds(map, 3)
//            println("Normal ${System.currentTimeMillis() - norm_start}")
//
//            concurrent_start = System.currentTimeMillis()
//            println("Concurrent start")
//            smoother.setThreaded(true)
//            smoother.applyNRounds(map, 3)
//            println("Concurrent ${System.currentTimeMillis() - concurrent_start}")
//
//            exitProcess(-2)
        }

        seeder(map, water_start_prob, max_prob)
        clumper.water_prob = water_prob
        automataThenSmooth(map, 10, 2)


        for (i in 0 until 5){
            clumpThenRun(map, 2, 10, 1)
        }

        clumper.applyNRounds(map, 3)

        automata.applyNRounds(map, 10)

        spiral_automata.applyNRounds(map, 4)

        smoother.applyNRoundsConcurrently(map, 3, smoother_delay)

        val water_ring = SpiralClumper(1F, Ellipse(map.HEIGHT/2, map.WIDTH/2,
                map.HEIGHT/2, map.WIDTH/2, 0F))
        water_ring.clumperCore.change_prob=1F

        water_ring.applyOnce(map)

        clumpThenRun(map, 3, 8, 3)

        clumper.setWaterProb(min_prob)
        clumper.applyNRounds(map, 2)

        clumper.setWaterProb(1F)
        clumper.applyNRounds(map, 1)

        smoother.applyNRoundsConcurrently(map, 3, smoother_delay)

//        File("Map1.txt").writeText(map.toString())

        automata.applyOnce(map)

        clumper.setWaterProb(water_prob)
        clumper.applyNRounds(map, 2)

        clumper.setWaterProb(1F)
        clumper.applyNRounds(map, 1)

        File("Map2.txt").writeText(map.toString())

        smoother.applyNRoundsConcurrently(map, 2, smoother_delay)

        File("Map.txt").writeText(map.toString())
    }

    private fun seeder(map: World_Map, water_prob: Float, max_prob: Float){
        for (row in arrayOf(0, map.HEIGHT)){
            for (column in arrayOf(0, map.WIDTH)){
                clumper.water_prob = water_prob
                clumper.changeCenter(row, column)
                clumper.applyOnce(map)
                automataThenSmooth(map, 10, 4)
            }
        }
        clumper.changeCenter(map.HEIGHT/2, map.WIDTH/2)

    }

    private fun automataThenSmooth(map: World_Map, automata_rounds: Int, smoother_rounds: Int){
        automata.applyNRoundsConcurrently(map, automata_rounds, 0.05)
        smoother.applyNRoundsConcurrently(map, smoother_rounds, smoother_delay)
    }

    private fun clumpThenRun(map: World_Map, clumper_rounds: Int, automata_rounds: Int, smoother_rounds: Int){
        clumper.applyNRounds(map, clumper_rounds)
        automataThenSmooth(map, automata_rounds, smoother_rounds)
    }
}

fun Int.pow(n: Int): Int {
    return this.toFloat().pow(n).toInt()
}
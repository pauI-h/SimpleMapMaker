package com.example.demo

class SpiralAutomata(var water_prob: Float, var max_prob: Float, var min_prob: Float, var square_size: Int):
        SpiralMovement() {

    private val automata: AutomataCore = AutomataCore(water_prob, max_prob, min_prob, square_size)

    override fun function(map: World_Map, row: Int, column: Int) {
        automata.changeTile(map, row, column)
    }

}

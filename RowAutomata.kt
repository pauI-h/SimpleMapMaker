import movement.RowByRow
import processing_cores.AutomataCore

class RowAutomata(water_prob: Float, max_prob: Float, min_prob: Float, square_size: Int): RowByRow() {

    private val automata: AutomataCore = AutomataCore(water_prob, max_prob, min_prob, square_size)

    override fun function(map: WorldMap, row: Int, column: Int) {
       automata.changeTile(map, row, column)
    }
}
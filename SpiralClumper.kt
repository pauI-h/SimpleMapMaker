import movement.SpiralMovement
import processing_cores.ClumperCore
import shapes.Ellipse

class SpiralClumper(var water_prob: Float,
                    shape: Ellipse):
        SpiralMovement() {

    val clumperCore = ClumperCore(water_prob, shape)

    override fun applyOnce(map: World_Map){
        clumperCore.water_count = 0
        clumperCore.current_tiles = 0
        super.applyOnce(map)
    }

    override fun function(map: World_Map, row: Int, column: Int) {
        clumperCore.clumper(map, row, column)
    }

    fun setWaterProb(water_prob: Float){
        clumperCore.water_prob = water_prob
    }

    fun changeShape(shape: Ellipse){
        clumperCore.shape = shape;
    }

    fun changeCenter(row: Int, column: Int){
        clumperCore.shape.center_row = row
        clumperCore.shape.center_column = column
    }
}
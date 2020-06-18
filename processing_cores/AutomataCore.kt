package processing_cores

import WorldMap
import tiles.Earth
import tiles.Tile
import tiles.Water
import java.lang.NumberFormatException
import java.util.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

open class AutomataCore(var water_prob: Float, var max_prob: Float, var min_prob: Float, private var square_size: Int) {
    private val random = Random()

    open fun changeTile(map: WorldMap, row: Int, column: Int){
        val prob: Float = water_prob
        val square = WorldMap(map, arrayOf(row, column), square_size)
        map.setTile(row, column, automata(square, prob, max_prob, min_prob))
    }

    fun automata(tiles: WorldMap, water_prob: Float, max_prob: Float, min_prob: Float): Tile {

        val start_time = System.currentTimeMillis()

        val num_tiles = tiles.getArea()

        val water_count = tiles.countTile(Water())

        val prob = getProb(water_count, water_prob, max_prob, min_prob, num_tiles)

        return if (random.nextFloat() < prob) {
            Water()
        } else {
            Earth()
        }


    }

    private fun getProb(water_count: Int, water_prob: Float, max_prob: Float, min_prob: Float, num_tiles: Int): Float{

        if (water_prob>1 || water_prob<0){
            throw java.lang.IllegalArgumentException("Water_prob must be between 1 and 0")
        }
        if (max_prob<water_prob){
            throw IllegalArgumentException("max_prob must be greater than or equal to water_prob")
        }
        if (max_prob<0 || max_prob>1){
            throw java.lang.IllegalArgumentException("max_prob must be between 1 and 0")
        }

        if (min_prob>water_prob){
            throw IllegalArgumentException("min_prob must be less than or equal to water_prob")
        }

        if (min_prob<0){
            throw IllegalArgumentException("min_prob must be greater than 0")
        }

        val max: Float = min(water_prob-min_prob, max_prob-water_prob)
        val min: Float = max(min_prob-water_prob, water_prob-max_prob)
        val grad: Float = (max-min)/num_tiles.toFloat()
        val add = (grad*water_count)+min

        val final = add+water_prob;
        if(final>max_prob || (final+(1/(10F.pow(8))))<min_prob ){ //The adition fixs stupid errors with fraction rep
            throw NumberFormatException("prob should be between $min_prob and $max_prob but $final was returned instead")
        }
        return final
    }
}
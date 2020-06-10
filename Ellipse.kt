package com.example.demo

import kotlin.math.*


class Ellipse(val longest_side: Int, val shortest_side: Int,
              var center_row: Int, var center_column: Int,
              private val rotation_from_vertical: Float): Shape {

    override fun containsPoint(x: Int, y: Int): Boolean {
        val x_use = x-center_column
        val y_use = y-center_row
        val rotated_point = rotatePoint(x_use,y_use)
        return (rotated_point[1].toFloat()/longest_side.toFloat()).pow(2) +
                (rotated_point[0].toFloat()/shortest_side.toFloat()).pow(2) <= 1
    }

    private fun rotatePoint(x: Int, y: Int): Array<Int>{
        val alpha = atan(x.toFloat()/y.toFloat())-rotation_from_vertical
        val mag = sqrt(x.toFloat().pow(2)+y.toFloat().pow(2))
        val final_x = -1*mag*sin(alpha)
        val final_y = mag*cos(alpha)
        val final_point = arrayOf(final_x.toInt(), final_y.toInt())
        if (!checkRotation(arrayOf(x,y), final_point)){
            println("[$x, $y] , [${final_point[0]}, ${final_point[1]}]")
            throw Exception("The rotated point should have the same magnitude but did not")
            //This is to catch my errors, not to show to users
        }
        return final_point
    }

    private fun rotatePoint(point: Array<Int>): Array<Int>{
        return rotatePoint(point[0], point[1])
    }

    private fun checkRotation(orig_point: Array<Int>, final_point: Array<Int>): Boolean{
        return abs(sqrt(orig_point[0].toFloat().pow(2) + orig_point[1].toFloat().pow(2)) -
                sqrt(final_point[0].toFloat().pow(2) + final_point[1].toFloat().pow(2))) <= 2
    }
}
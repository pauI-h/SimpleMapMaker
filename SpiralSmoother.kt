package com.example.demo

class SpiralSmoother(square_size: Int, private var threaded: Boolean): SpiralMovement() {

    private val smoother = SmootherCore(square_size, threaded)

    override fun function(map: World_Map, row: Int, column: Int) {
        smoother.changeTile(map, row, column)
    }

    fun setThreaded(threaded: Boolean){
        this.threaded = threaded
        smoother.threaded = threaded
    }

}
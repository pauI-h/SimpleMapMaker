package com.example.demo

interface Shape {
    fun containsPoint(x: Int, y: Int): Boolean

    fun containsPoint(point: Array<Int>): Boolean{
        return containsPoint(point[0], point[1])
    }
}
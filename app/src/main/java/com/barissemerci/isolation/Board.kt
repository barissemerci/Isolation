package com.barissemerci.isolation

import android.content.res.ColorStateList
import android.graphics.Color.green
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import java.lang.Float.max
import java.lang.Float.min


class Board : AppCompatActivity() {
    var human = "H"
    var ai = "A"
    var currentPLayer = human //su an oyun sirasinin hangi oyuncuda oldugunu tutar
    var legalMovesForHuman: MutableList<Int> = mutableListOf() //siradaki hamle icin insanin yapabilecegi hamlelerin listesi
    var legalMovesForAi: MutableList<Int> = mutableListOf() //siradaki hamle icin yapay zekanin yapabilecegi hamlelerin listesi
    var tempLegalMovesForHuman: MutableList<Int> = mutableListOf() //minimax olusturulmasi asamasinda yapilabilecek hareketleri tutan liste
    var tempLegalMovesForAi: MutableList<Int> = mutableListOf() //minimax olusturulmasi asamasinda yapilabilecek hareketleri tutan liste
    var temp2LegalMovesForHuman: MutableList<Int> = mutableListOf() //minimax olusturulmasi asamasinda yapilabilecek hareketleri tutan liste
    var temp2LegalMovesForAi: MutableList<Int> = mutableListOf() //minimax olusturulmasi asamasinda yapilabilecek hareketleri tutan liste


    var chooseHeuristic = 0//heuristic fonksiyon secici
    var firstMoveHuman = 1 //insanin ilk hamlesini yapmasini saglar. ilk hamleden sonra 0 degerini alir
    var lastMoveX = 0 //oynanan son hareketin x konumunu tutar
    var tempX = 0 //yapilabilecek hareketlerin belirlenmesi icin gerekli
    var tempY = 0 //yapilabilecek hareketlerin belirlenmesi icin gerekli
    var lastMoveY = 0 //oynanan son hareketin y konumunu tutar
    var ray = arrayOf( //yön dizisi
        arrayOf(0, 1),
        arrayOf(1, 1),
        arrayOf(1, 0),
        arrayOf(1, -1),
        arrayOf(0, -1),
        arrayOf(-1, -1),
        arrayOf(-1, 0),
        arrayOf(-1, 1)
    )
    var size = 3 //matrisin boyutu
    var board = Array(size) { Array(size) { 0 } } //oyun tahtasi
    var tempBoard = Array(size) { Array(size) { 0 } } //minmax ile degisen tahta matrisi
    var buttons = arrayOf( //tiklanacak butonların dizisi
        R.id.imageButton1, R.id.imageButton2, R.id.imageButton3,
        R.id.imageButton4, R.id.imageButton5, R.id.imageButton6,
        R.id.imageButton7, R.id.imageButton8, R.id.imageButton9
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)

    }


    fun movement(view: View) {


        if (currentPLayer == human) { //oynama sırasi insanda ise

            var tag = view.getTag().toString().toInt() // basılan butonun hangisi oldugunu bulur
            var row = tag / size //tag e göre matriste yerini bulur
            var column = tag % size
            if (board[row][column] == 1) { //daha önce bu noktaya hareket edilmiş mi
                val textView: TextView = findViewById(R.id.textView) as TextView
                textView.text = "Buraya daha önce tıklandı"
            } else {
                if (firstMoveHuman == 1) {//insanın ilk hamlesiyse

                    board[row][column] = 1
                    val button = findViewById<ImageButton>(buttons[tag])
                    button.backgroundTintList = //butonu mavi yap
                        ColorStateList.valueOf(resources.getColor(R.color.blue))
                    currentPLayer = ai //sıra yapay zekaya geçti
                    lastMoveX = row
                    lastMoveY = column
                    for (i in 0..(size * size) - 1) { //insanın hamlesinden sonra yapılabilecek hamleleri bul
                        if (i != tag) {
                            legalMovesForAi.add(i)
                            legalMovesForHuman.add(i)
                            temp2LegalMovesForAi.add(i)
                            temp2LegalMovesForHuman.add(i)
                        }
                    }
                    firstMoveHuman = 0

                } else if (legalMovesForHuman.contains(tag)) { //insanın ikinci hamlesiyse

                    var tag = view.getTag().toString().toInt()
                    var row = tag / size
                    var column = tag % size
                    board[row][column] = 1
                    val button = findViewById<ImageButton>(buttons[tag])
                    button.backgroundTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.blue))
                    currentPLayer = ai
                    for (i in 0..7) {// yapay zekanın yapabileceği hamleleri bul
                        for (j in 1..size - 1) {
                            tempX = lastMoveX + (ray[i][0] * j)
                            tempY = lastMoveY + (ray[i][1] * j)
                            if (tempX >= 0 && tempY >= 0 && tempX < size && tempY < size) {
                                if (board[tempX][tempY] == 0) {
                                    legalMovesForAi.add((tempX * size) + tempY)

                                } else {
                                    break
                                }

                            }
                        }

                    }
                    lastMoveX = row
                    lastMoveY = column
                }
                if (legalMovesForAi.size == 0) { //yapay zekanın yapabileceği hareket kalmadıysa oyun bitti
                    val textView: TextView = findViewById(R.id.textView) as TextView
                    textView.text = "Oyun bitti"
                } else { //yapay zekanın yapabileceğ hareket varsa
                    nextMove()
                }


            }


        }


    }


    fun nextMove() {
        var bestScore = Float.NEGATIVE_INFINITY
        var moveX = 0
        var moveY = 0
        var row = 0
        var column = 0




        for (i in 0..legalMovesForAi.size - 1) { //yapay zekanın hamleleri arasından
            var tag = legalMovesForAi[i] //yapabileceğ hamleleri tek tek al
            row = tag / size
            column = tag % size
            for (i in 0..size - 1) {
                for (j in 0..size - 1) { //yedek tahtaya asıl tahtayı kopyala
                    tempBoard[i][j] = board[i][j]
                }
            }

            tempBoard[row][column] = 1 //yedek tahtanın o degerini 1 yap
            temp2LegalMovesForHuman.remove(tag) //insan ve yapay zekanın yapabilecek hamlelerinin arasindan o hamleyi cikar
            temp2LegalMovesForAi.clear() //temp2LegalMovesForAi tekrar olusturulacağı icin temizleniyor
            for (i in 0..7) { //yapay zekanın son hareketine göre gidebileceği yerler hesaplanıyor
                for (j in 1..size - 1) {
                    tempX = row + (ray[i][0] * j)
                    tempY = column + (ray[i][1] * j)
                    if (tempX >= 0 && tempY >= 0 && tempX < size && tempY < size) {
                        if (tempBoard[tempX][tempY] == 0) {
                            temp2LegalMovesForAi.add((tempX * size) + tempY)
                        } else {
                            break
                        }
                    }
                }
            }


            var score = minimax(tempBoard, 4, false) //minimax fonksiyonuna gönderiliyor
            temp2LegalMovesForAi.add(tag)//fonksiyondan cıkınca degerler tekrar listeye ekleniyor
            temp2LegalMovesForHuman.add(tag)
            if (score > bestScore) { //en iyi skoru bulmak icin karsilastirma yapiliyor
                bestScore = score.toFloat()
                moveX = row
                moveY = column
            }
        }

        legalMovesForHuman.clear() //insanın yapabileceği hamleler tekrar hesaplanacağı için temizleniyor
        board[moveX][moveY] = 1
        var tag = moveX * size + moveY
        val button = findViewById<ImageButton>(buttons[tag])
        button.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.red)) //hareket kırmızı yapılıyor

        currentPLayer = human
        for (i in 0..7) {//insanın yapabileceği hamleler hesaplanıyor
            for (j in 1..size - 1) {
                tempX = lastMoveX + (ray[i][0] * j)
                tempY = lastMoveY + (ray[i][1] * j)
                if (tempX >= 0 && tempY >= 0 && tempX < size && tempY < size) {
                    if (board[tempX][tempY] == 0) {
                        legalMovesForHuman.add((tempX * size) + tempY)
                    } else {
                        break
                    }
                }
            }
        }
        lastMoveX = moveX
        lastMoveY = moveY
        legalMovesForAi.clear()



        if (legalMovesForHuman.size == 0) {//insanın yapabileceği hamleler kalmamışsa oyun biter
            val textView: TextView = findViewById(R.id.textView) as TextView
            textView.text = "Oyun bitti"
        }


    }

    private fun checkWinner(): Int { //kazanan var mı diye kontrol ediyor
        var winner = 0
        if (currentPLayer == ai && legalMovesForAi.size == 0) {
            winner = -1
        } else if (currentPLayer == human && legalMovesForHuman.size == 0) {
            winner = 1
        }
        return winner
    }

//heuristic fonksiyonlar
    private fun simple(): Int {
        return tempLegalMovesForAi.size
    }

    private fun defensive(): Int {
        return (tempLegalMovesForAi.size * 2) - tempLegalMovesForHuman.size
    }

    private fun offensive(): Int {
        return tempLegalMovesForAi.size - (tempLegalMovesForHuman.size * 2)
    }


    private fun changeLegalMoves(row: Int, column: Int, kontrol: Boolean) {

//siradaki hareket icin yapilabilecek hareketleri hesaplıyor
        tempBoard[row][column] = 1

        if (kontrol == true) {
            tempLegalMovesForHuman.clear()
            for (i in 0..7) {
                for (j in 1..size - 1) {
                    tempX = row + (ray[i][0] * j)
                    tempY = column + (ray[i][1] * j)
                    if (tempX >= 0 && tempY >= 0 && tempX < size && tempY < size) {
                        if (tempBoard[tempX][tempY] == 0) {
                            tempLegalMovesForHuman.add((tempX * size) + tempY)

                        } else {
                            break
                        }
                    }
                }
            }
        } else {
            tempLegalMovesForAi.clear()
            for (i in 0..7) {
                for (j in 1..size - 1) {
                    tempX = row + (ray[i][0] * j)
                    tempY = column + (ray[i][1] * j)
                    if (tempX >= 0 && tempY >= 0 && tempX < size && tempY < size) {
                        if (tempBoard[tempX][tempY] == 0) {
                            tempLegalMovesForAi.add((tempX * size) + tempY)
                        } else {
                            break
                        }
                    }
                }
            }
        }
    }


    private fun minimax(m: Array<Array<Int>>, depth: Int, isMaximizing: Boolean): Int {
        var result = checkWinner()
        var score = 0
        if (result != 0 || depth == 0) {//recursive'dan çıkış şartı
            if (chooseHeuristic == 0) {
                return simple()
            } else if (chooseHeuristic == 1) {
                return defensive()
            } else if (chooseHeuristic == 2) {
                return offensive()
            }
        }
        if (isMaximizing) { //minmaxta yapay zekanın hareket sırası ise
            var bestScore = Float.NEGATIVE_INFINITY
            for (i in 0..temp2LegalMovesForAi.size - 1) {
                var tag = temp2LegalMovesForAi[i] //bu hareket yapilirsa
                var row = tag / size
                var column = tag % size
                temp2LegalMovesForAi.remove(tag)
                temp2LegalMovesForHuman.remove(tag)
                changeLegalMoves(row, column, false) //uygun hareketleri hesapla
                tempBoard[row][column] = 0
                score = minimax(m, depth - 1, false) //minimaxa gönder
                temp2LegalMovesForAi.add(i, tag)
                temp2LegalMovesForHuman.add(i, tag)
                bestScore = max(score.toFloat(), bestScore) //maximumu bul
            }
            return bestScore.toInt() //en iyi skoru dön
        } else { //minmaxta insanın hareket sırası ise
            var bestScore = Float.POSITIVE_INFINITY
            var moveX = 0
            var moveY = 0
            for (i in 0..temp2LegalMovesForHuman.size - 1) {
                var tag = temp2LegalMovesForHuman[i] //bu hareket yapılırsa
                var row = tag / size
                var column = tag % size
                temp2LegalMovesForAi.remove(tag)
                temp2LegalMovesForHuman.remove(tag)
                changeLegalMoves(row, column, true)//uygun hareketleri hesapla

                score = minimax(m, depth - 1, true)//minimaxa gönder
                tempBoard[row][column] = 0
                temp2LegalMovesForHuman.add(i, tag)

                temp2LegalMovesForAi.add(i, tag)

                bestScore = min(score.toFloat(), bestScore)//minimumu bul
            }

            return bestScore.toInt() //en iyi skoru dön
        }
    }


    fun randomComputerMove() { //rastgele hareket yap
        var row = (0..size - 1).random()
        var column = (0..size - 1).random()
        var tag = (row * size) + column
        while (!legalMovesForAi.contains(tag)) { //hareket uygun mu
            row = (0..size - 1).random()
            column = (0..size - 1).random()
            tag = (row * size) + column
        }
        board[row][column] = 1
        val button = findViewById<ImageButton>(buttons[tag])
        button.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.purple_200))
        currentPLayer = human
        for (i in 0..7) {
            for (j in 1..size - 1) {
                tempX = lastMoveX + (ray[i][0] * j)
                tempY = lastMoveY + (ray[i][1] * j)
                if (tempX >= 0 && tempY >= 0 && tempX < size && tempY < size) {
                    if (board[tempX][tempY] == 0) {
                        legalMovesForHuman.add((tempX * size) + tempY)
                    } else {
                        break
                    }
                }
            }
        }
        lastMoveX = row
        lastMoveY = column
        legalMovesForAi.clear()
    }
}
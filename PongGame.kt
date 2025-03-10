package my.poject2.pinpong

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class PongGame (context: Context,attrs:AttributeSet? = null): View(context,attrs) {
    private var paddleWidth = 200f  // Ширина ракетки
    private var paddleHeight = 30f  // Высота ракетки
    private var ballRadius = 20f    // Радиус мяча

    private var paddle1X = 0f   // Позиция X верхней ракетки
    private var paddle2X = 0f   // Позиция X нижней ракетки
    private var paddle1Y = 0f     // Позиция Y верхней ракетки
    private var paddle2Y = 0f   // Позиция Y нижней ракетки

    private var ballX = 0f     // Позиция X мяча
    private var ballY = 0f    // Позиция Y мяча

    private var ballSpeedX = 10f   // Скорость мяча по оси X
    private var ballSpeedY = 10f   // Скорость мяча по оси Y

    private var screenWidth = 0   // Ширина экрана
    private var screenHeight = 0   // Высота экрана

    private var player1Score = 0    // Счёт игрока 1
    private var player2Score = 0    // Счёт игрока 2

    private val paint = Paint()    // Кисть для рисования объектов

    init {
        paint.color = Color.WHITE  // Цвет кисти — белый
        paint.style = Paint.Style.FILL  // Стиль кисти — заливка
        paint.textSize=50f  //Размер текста для счёта
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        screenWidth = w   // Сохраняем ширину экрана
        screenHeight = h   // Сохраняем высоту экрана


        // Начальные позиции ракеток
        paddle1X = (screenWidth - paddleWidth) / 2f
        paddle2X = (screenWidth - paddleWidth) / 2f
        paddle1Y = 50f
        paddle2Y = screenHeight - paddleHeight - 50f

        // Начальная позиция мяча
        ballX = screenWidth / 2f
        ballY = screenHeight / 2f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Очистка экрана
        canvas.drawColor(Color.BLACK)

        // Двигаем верхнюю ракетку
        movePaddle1()

// Отрисовка ракеток
        canvas.drawRect(paddle1X, paddle1Y, paddle1X + paddleWidth, paddle1Y + paddleHeight, paint)
        canvas.drawRect(paddle2X, paddle2Y, paddle2X + paddleWidth, paddle2Y + paddleHeight, paint)

        // Отрисовка мяча
        canvas.drawCircle(ballX, ballY, ballRadius, paint)

        // Отрисовка счёта
        canvas.drawText("Player 1: $player1Score", 50f, 100f, paint)
        canvas.drawText("Player 2: $player2Score", screenWidth - 400f, 100f, paint)

// Обновление позиции мяча
        updateBallPosition()

        // Проверка столкновений
        checkCollisions()

        // Перерисовка
        invalidate()
    }
    private fun movePaddle1() {
        // Простое движение верхней ракетки за мячом
        if (paddle1X + paddleWidth / 2 < ballX) {
            paddle1X += 10f // Двигаем вправо
        } else {
            paddle1X -= 10f // Двигаем влево
        }

        // Ограничиваем движение ракетки в пределах экрана
        paddle1X = paddle1X.coerceIn(0f, screenWidth - paddleWidth)
    }

    private fun updateBallPosition() {
        ballX += ballSpeedX  // Движение по горизонтали
        ballY += ballSpeedY  // Движение по вертикали

        // Отскок от левой и правой стен
        if (ballX - ballRadius < 0 || ballX + ballRadius > screenWidth) {
            ballSpeedX = -ballSpeedX   // Меняем направление по оси X
        }

        // Отскок от верхней ракетки
        if (ballY - ballRadius < paddle1Y + paddleHeight && ballY + ballRadius >paddle1Y && ballX > paddle1X && ballX<paddle1X + paddleWidth) {
            ballSpeedY = -ballSpeedY   // Меняем направление по оси Y
        }

        // Отскок от нижней ракетки
        if (ballY + ballRadius > paddle2Y && ballY - ballRadius < paddle2Y + paddleHeight && ballX > paddle2X && ballX < paddle2X + paddleWidth) {
            ballSpeedY = -ballSpeedY  // Меняем направление по оси Y
        }

        // Проверка выхода за пределы экрана (гол)
        if (ballY - ballRadius < 0) {
            player2Score++
            resetBall()
        }
        if (ballY + ballRadius > screenHeight) {
            player1Score++
            resetBall()
        }
    }

    private fun resetBall() {
        ballX = screenWidth / 2f
        ballY = screenHeight / 2f
        ballSpeedX = if (Math.random() < 0.5) 10f else -10f
        ballSpeedY = if (Math.random() < 0.5) 10f else -10f

    }

    private fun checkCollisions() {
        // Столкновение с верхней ракеткой
        if (ballY - ballRadius < paddle1Y + paddleHeight && ballX > paddle1X && ballX < paddle1X + paddleWidth) {
            ballSpeedY = -ballSpeedY
        }
        // Столкновение с нижней ракеткой
        if (ballY + ballRadius > paddle2Y && ballX > paddle2X && ballX < paddle2X + paddleWidth) {
            ballSpeedY = -ballSpeedY
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
// Управление нижней ракеткой
                paddle2X = event.x - paddleWidth / 2
                paddle2X = paddle2X.coerceIn(0f, screenWidth - paddleWidth)
                invalidate()// Перерисовываем экран
            }
        }
        return true
    }
}
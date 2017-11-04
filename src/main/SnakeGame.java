package main;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import static javax.swing.JSplitPane.BOTTOM;

public class SnakeGame
{
    final String TITLE_OF_PROGRAMM = "Игра \"Змейка\""; //заголовок JFrame
    String GAME_OVER_MSG = "ВЫ ПРОИГРАЛИ!"; //сообщение о проигрыше
    final int POINT_RADIUS = 20; // in pix
    final int FIELD_WIDTH = 30; // in point
    final int FIELD_HEIGHT = 20;
    final int FIELD_DX = 6;
    final int FIELD_DY = 28;
    final int START_LOCATION = 200;
    final int START_SNAKE_SIZE = 6; //первоначальный размер змейки
    final int START_SNAKE_X = 10;
    final int START_SNAKE_Y = 10;
    final int SHOW_DELAY = 150;
    final int LEFT = 37;
    final int UP = 38;
    final int RIGHT = 39;
    final int DOWN = 40;
    final int START_DIRECTION = RIGHT;
    final Color DEFAULT_COLOR = Color.black;
    final Color FOOD_COLOR = Color.green;
    Snake snake;
    Food food;
    JFrame frame;
    Canvas canvasPanel;
    Random random = new Random();
    boolean gameOver = false;
    
    public static void main(String[] args)
    {
        new SnakeGame().go();
    }
    
    /*
     * Для отрисовки элементов в окне
     */
    public class Canvas extends JPanel 
    {
        @Override
        public void paint(Graphics g)
        {
            super.paint(g);
            snake.paint(g);
            food.paint(g);
            if(gameOver)
            {
                g.setColor(Color.RED);
                g.setFont(new Font("Times New Roman", Font.BOLD, 40));
                FontMetrics fm = g.getFontMetrics();
                g.drawString(GAME_OVER_MSG, (FIELD_WIDTH * 
                        POINT_RADIUS + FIELD_DX - fm.stringWidth(GAME_OVER_MSG))/2, 
                        (FIELD_HEIGHT * POINT_RADIUS + FIELD_DY)/2);
                JButton repeat = new JButton("<html><h2><font \n" +
"                                   color=\\\"red\"\\\">Сыграть заново");
                repeat.setBackground(Color.BLUE);
                repeat.setSize(200, 100);
                
                //frame.add(repeat);
                
                repeat.addActionListener(new ActionListener() 
                {
                    public void actionPerformed(ActionEvent e) 
                    {
                        new SnakeGame().go();
                    }
                });
            }
        }
    }
    
    /*
     * точка
     */
    class Point
    {
        int x, y;
        Color color = DEFAULT_COLOR;
        
        public Point(int x, int y)
        {
            this.setXY(x, y);
        }
        
        void paint(Graphics g) 
        {
            g.setColor(color);
            g.fillOval(x * POINT_RADIUS, y * POINT_RADIUS, POINT_RADIUS, POINT_RADIUS);
        }
        
        int getX() 
        { 
            return x; 
        }
        
        int getY() 
        { 
            return y; 
        }
        
        void setXY(int x, int y)
        {
            this.x = x;
            this.y = y;
        }
    }
    
    /*
     *метод для запуска игры
     */
    private void go()
    {
        System.out.println("Запуск игры!");
        frame = new JFrame(TITLE_OF_PROGRAMM + " : " + START_SNAKE_SIZE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(FIELD_WIDTH * POINT_RADIUS + FIELD_DX, 
                FIELD_HEIGHT * POINT_RADIUS + FIELD_DY); // размер окна
        frame.setLocation(START_LOCATION, START_LOCATION); //Расположение окна
        frame.setResizable(false); //нельзя изменить размер JFrame
        
        canvasPanel = new Canvas();
        canvasPanel.setBackground(Color.WHITE);
        
        frame.getContentPane().add(BorderLayout.CENTER, canvasPanel);
        frame.addKeyListener(new KeyAdapter()
        {
           public void keyPressed(KeyEvent e) 
           {
               snake.setDirection(e.getKeyCode());
           }
        });
        
        frame.setVisible(true);
        
        snake = new Snake(START_SNAKE_X, START_SNAKE_Y, START_SNAKE_SIZE, START_DIRECTION);
        food = new Food();
 
        while (!gameOver)
        {
            snake.move();
            if (food.isEaten()) 
            {
                food.next();
            }
            canvasPanel.repaint();
            try 
            {
                Thread.sleep(SHOW_DELAY);
            } 
            catch (InterruptedException e) 
            { 
                e.printStackTrace();
            }
        }
    }
    
    /*
     * Змейка
     */
    class Snake
    {
        ArrayList<Point> snake = new ArrayList<Point>();
        int direction;
        
        public Snake(int x, int y, int length, int direction) 
        {
            for(int i = 0; i < length; i++)
            {
                Point point = new Point(x-i, y);
                snake.add(point);
            }
            this.direction = direction;
        }
        
        boolean isInsideSnake(int x, int y) 
        {
            for (Point point : snake) 
            {
                if ((point.getX() == x) && (point.getY() == y)) 
                {
                    return true;
                }
            }
            return false;
        }
 
        boolean isFood(Point food) 
        {
            return ((snake.get(0).getX() == food.getX()) 
                    && (snake.get(0).getY() == food.getY()));
        }
         
        void move()
        {
            int x = snake.get(0).getX();
            int y = snake.get(0).getY();
            if (direction == LEFT)
            { 
                x--; 
            }
            if (direction == RIGHT) 
            { 
                x++; 
            }
            if (direction == UP) 
            { 
                y--; 
            }
            if (direction == DOWN) 
            { 
                y++;
            }
            if (x > FIELD_WIDTH - 1) 
            { 
                x = 0; 
            }
            if (x < 0) 
            { 
                x = FIELD_WIDTH - 1; 
            }
            if (y > FIELD_HEIGHT - 1) 
            { 
                y = 0;
            }
            if (y < 0)
            { 
                y = FIELD_HEIGHT - 1; 
            }
            gameOver = isInsideSnake(x, y); // Проверка на столкновени
            snake.add(0, new Point(x, y));
            if (isFood(food)) 
            {
                food.eat();
                frame.setTitle(TITLE_OF_PROGRAMM + " : " + snake.size());
            } 
            else 
            {
                snake.remove(snake.size() - 1);
            }
        }
         
        void setDirection(int direction) 
        {
            if ((direction >= LEFT) && (direction <= DOWN)) 
            {
                if (Math.abs(this.direction - direction) != 2)
                {
                    this.direction = direction;
                }
            }
        }
 
        void paint(Graphics g) 
        {
            for(Point point : snake)
            {
                point.paint(g);
            }
        }
        
    }
    
    /*
     * Еда для змеи
     */
    class Food extends Point
    {
        public Food() 
        {
            super(-1, -1);
            this.color = FOOD_COLOR;
        }
        
        void eat() 
        {
            this.setXY(-1, -1);
        }
        
        boolean isEaten() 
        {
            return this.getX() == -1;
        }
        
        void next() 
        {
            int x, y;
            do 
            {
                x = random.nextInt(FIELD_WIDTH);
                y = random.nextInt(FIELD_HEIGHT);
            } while (snake.isInsideSnake(x, y));
            this.setXY(x, y);
        }
    }
}
package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Scanner;

public class MemoryGame extends JFrame implements ActionListener {

    // zmienne
    private JButton[] buttons;
    private ImageIcon[] icons;
    private int[] randomNumbers;
    private int firstButton, secondButton;
    private int moves;
    private Timer timer;
    private File statsFile;

    // okno gry
    public MemoryGame() {
        super("Memory Game");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // zdjęcia 
        icons = new ImageIcon[10];
        for (int i = 0; i < 10; i++) {
            icons[i] = new ImageIcon("icon" + i + ".jpg");
        }

        
        randomNumbers = new int[20];
        for (int i = 0; i < 20; i++) {
            randomNumbers[i] = i % 10;
        }
        shuffle(randomNumbers);

        // przyciski
        buttons = new JButton[20];
        JPanel panel = new JPanel(new GridLayout(4, 5));
        for (int i = 0; i < 20; i++) {
            buttons[i] = new JButton();
            buttons[i].setActionCommand(Integer.toString(i));
            buttons[i].addActionListener(this);
            panel.add(buttons[i]);
        }

        // timer
        timer = new Timer(700, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                buttons[firstButton].setIcon(null);
                buttons[secondButton].setIcon(null);
                firstButton = -1;
                secondButton = -1;
                timer.stop();
            }
        });

        add(panel, BorderLayout.CENTER);
        setVisible(true);

        // zmienianie rozmiaru okna
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                // nowe wymiary po zmianie
                int width = getContentPane().getWidth();
                int height = getContentPane().getHeight();

                // skalowanie zdjec
                for (int i = 0; i < 10; i++) {
                    Image img = icons[i].getImage();
                    Image scaledImg = img.getScaledInstance(width/5, height/4, Image.SCALE_SMOOTH);
                    icons[i] = new ImageIcon(scaledImg);
                }

                for (int i = 0; i < 20; i++) {
                    if (buttons[i].getIcon() != null) {
                        buttons[i].setIcon(icons[randomNumbers[i]]);
                    }  
                }
            }
        });

        // plik z wynikami
        statsFile = new File("memorygame_stats.txt");
        if (!statsFile.exists()) {
            try {
                statsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // mieszanie 
    private void shuffle(int[] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int index = (int) (Math.random() * (i + 1));
            int temp = array[i];
            array[i] = array[index];
            array[index] = temp;
        }
    }

    // klikniecie myszka
    public void actionPerformed(ActionEvent e) {
        int index = Integer.parseInt(e.getActionCommand());
    
        // odkrywanie przyciskow
        if (buttons[index].getIcon() != null || timer.isRunning() || firstButton == index) {
            return;
        }
    
        buttons[index].setIcon(icons[randomNumbers[index]]);
    
        if (firstButton == -1) {
            firstButton = index;
        } else {
            secondButton = index;
            moves++;
    
            // sprawdza czy ikony takie same
            if (randomNumbers[firstButton] == randomNumbers[secondButton]) {
                buttons[firstButton].setEnabled(false);
                buttons[secondButton].setEnabled(false);
                firstButton = -1;
                secondButton = -1;
    
                // czy skonczona
                for (int i = 0; i < 20; i++) {
                    if (buttons[i].isEnabled()) {
                        return;
                    }
                }
    
                // komunikat
                JOptionPane.showMessageDialog(this, "You won in " + moves + " moves!");
    
                // zapis wyniku
                try {
                    FileWriter writer = new FileWriter(statsFile, true);
                    writer.write(moves + "\n");
                    writer.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
    
                resetGame();
            } else {
                timer.start();
            }
        }
    }
    // resetowanie gry
    private void resetGame() {
        shuffle(randomNumbers);
        for (int i = 0; i < 20; i++) {
            buttons[i].setIcon(null);
            buttons[i].setEnabled(true);
        }
        firstButton = -1;
        secondButton = -1;
        moves = 0;
    
        // wyświetlanie statystyk
        try {
            Scanner scanner = new Scanner(statsFile);
            int gamesPlayed = 0;
            int bestResult = Integer.MAX_VALUE;
            int totalMoves = 0;
            while (scanner.hasNext()) {
                int result = Integer.parseInt(scanner.nextLine());
                gamesPlayed++;
                totalMoves += result;
                if (result < bestResult) {
                    bestResult = result;
                }
            }
            scanner.close();
            double averageMoves = (double) totalMoves / gamesPlayed;
            JOptionPane.showMessageDialog(this, "Games played: " + gamesPlayed +
                    "\nBest result: " + (bestResult == Integer.MAX_VALUE ? "none" : bestResult) +
                    "\nAverage moves: " + String.format("%.2f", averageMoves));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new MemoryGame();
    }
}



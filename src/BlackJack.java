import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;//storing cards in players hand
import java.util.Random;// shuffling the deck of cards
import javax.swing.*;// for GUI

public class BlackJack {
    private class Card {// create a class to represent the card
        String value;// each card has a value and a type
        String type;

        Card(String value, String type){// create a constructor and parse in a value and a type
            this.value = value;
            this.type = type;
        }    
        // overload the toString method to convert memory to String representation for cards
        public String toString(){
            return value + "-" + type;// since our card is named e.g. 2-C indicating 2 of clubs

        }
        public int getValue(){
            if ("AJQK".contains(value)){ 
                if (value == "A") {
                    return 11; //set A to 11 at the start change it later;
                }
                return 10;// for picture cards
            }
            return Integer.parseInt(value);// for number cards
        }
        public boolean isAce(){// FOR THE HIDDEN CARD
            return value == "A";
        }

        public String getImagePath(){ // create a new method to define the image path 
            return "./cards/" + toString() + ".png";
        }
    }
    ArrayList<Card> deck;//create an ArrayList to store all of the cards. in start game buildDeck()
    Random random = new Random();//for shuffling the deck
    //dealer
    Card hiddenCard;
    ArrayList<Card> dealerHand;
    int dealerSum;
    int dealerAceCount;
    // player
    ArrayList<Card> playerHand;
    int playerSum;
    int playerAceCount;
    //GUI
    int boardWidth = 600;
    int boardHeight = 600;

    int cardWidth = 100; // ratio must be 1:1.4
    int cardHeight = 140;

    JFrame frame = new JFrame("BlackJack");
    JPanel gamePanel = new JPanel(){// to draw cards on the Jpanel we have to override the method
            @Override
            public void paintComponent(Graphics g) {// take a graphics object g. in this method is where we are gg to do all the drawing
                super.paintComponent(g);

                try {// try catch block just in case the images do not load
                    // draw hidden card for dealer
                    Image hiddenCardImage = new ImageIcon(getClass().getResource("./cards/BACK.png")).getImage();
                    if (!stayButton.isEnabled()){// if the stay button is not emabled hiddern card is replaced with the card that has the same value as the hidden card
                        hiddenCardImage = new ImageIcon(getClass().getResource(hiddenCard.getImagePath())).getImage();// replace the hidden card image
                    }
                    g.drawImage(hiddenCardImage, 20, 20, cardWidth, cardHeight, null); // 20 px right and down
                    
                    // draw rest of the cards int the dealers hand
                    for (int i = 0; i < dealerHand.size(); i++){
                        Card card = dealerHand.get(i);
                        Image cardImage = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                        g.drawImage(cardImage, cardWidth + 25 +(cardWidth + 5)*i, 20,cardWidth, cardHeight, null);
                        };
                    

                    // draw players card
                    for (int i = 0; i < playerHand.size(); i++){
                        Card card = playerHand.get(i);
                        Image cardImage = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                        g.drawImage(cardImage, 20 + (cardWidth + 5)*i,320,cardWidth, cardHeight, null);


                    }
                    if (!stayButton.isEnabled()){// find the total of both dealer and player
                        dealerSum = reduceDealerAce();
                        playerSum = reducePlayerAce();
                        System.out.println("Stand:");
                        System.out.println(dealerSum);
                        System.out.println(playerSum);


                        String message = "";// write all the different outcomes
                        if (playerSum > 21) {
                            message = "You Lose";
                        }

                        else if (dealerSum > 21) {
                            message = "You Win";
                        }
                        else if (dealerSum == playerSum){
                            message = "Draw";
                        }
                        else if (dealerSum > playerSum){
                            message = "You Lose";
                        }
                        else if (dealerSum < playerSum){
                            message = "You Win";
                        }

                        g.setFont(new Font("Arial", Font.PLAIN, 45));
                        g.setColor(Color.white);
                        g.drawString(message, 200 ,250);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    };

    JPanel buttonPanel = new JPanel();
    JButton hitButton = new JButton("Hit");
    JButton stayButton = new JButton("Stand");


    BlackJack() {//create constructor
        startGame();

        frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);// open in the center of the screen instead of top left
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// close window when you hit the x key on the top left

        gamePanel.setLayout(new BorderLayout());
        gamePanel.setBackground(new Color(110, 0, 108));
        frame.add(gamePanel);

        hitButton.setFocusable(false);
        buttonPanel.add(hitButton);
        stayButton.setFocusable(false);
        buttonPanel.add(stayButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        hitButton.addActionListener(new ActionListener() {// when hit button in pressed its going to call the function
            public void actionPerformed(ActionEvent e ){
                Card card = deck.remove(deck.size()-1);// draw another card
                playerSum += card.getValue(); // add value to player sum
                playerAceCount += card.isAce()? 1 : 0;// check if its an ace
                playerHand.add(card);// add it to players hand
                if (reducePlayerAce() > 21 ){
                    hitButton.setEnabled(false);// make the button unclickable
                }
                gamePanel.repaint();//going to call the paint component.
            }
        });

        stayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                hitButton.setEnabled(false);// once a player has pressed stay both buttons must not be pressed again
                stayButton.setEnabled(false);
                while(dealerSum < 17){// if dealer sum < 17 dealer must hit 
                    Card card = deck.remove(deck.size()-1);
                    dealerSum += card.getValue();
                    dealerAceCount += card.isAce() ? 1 : 0;
                    dealerHand.add(card);
                }
                gamePanel.repaint();
            }
        });
        gamePanel.repaint();//going to call the paint component. it will update the images in the game panel

    }
    
    public void startGame(){ // define the start game function, create deck of cards and shuffle and assign dealer and player 2 cards
        buildDeck();//create the deck
        shuffleDeck();//shuffle deck

        //dealer
        dealerHand = new ArrayList<Card>();
        dealerAceCount = 0;
        dealerSum = 0;
        hiddenCard = deck.remove(deck.size()-1);//remove card at last index
        dealerSum += hiddenCard.getValue();//need to create a getValue method which calc the total points of the dealer int the Card class
        dealerAceCount += hiddenCard.isAce() ? 1 : 0;// return 1 otherwise return 0 for java turnary operator syntax
        

        Card card = deck.remove(deck.size()-1);
        dealerSum += card.getValue();
        dealerAceCount += card.isAce() ? 1 : 0;// return 1 otherwise return 0 for java turnary operator syntax
        dealerHand.add(card);

        System.out.println("Dealer:");
        System.out.println(hiddenCard);// prints out the hidden card for dealer 
        System.out.println(dealerHand);// prints out the dealers open card
        System.out.println(dealerSum);// prints out the dealers sum total points
        System.out.println(dealerAceCount);// prints out the total number of aces the dealer has

        // Player
        playerHand = new ArrayList<Card>();
        playerSum = 0;
        playerAceCount = 0;

        for (int i = 0; i < 2; i++){// deal the first two cards by running for loop twice
            card = deck.remove(deck.size()-1); // remove 1 card from the size of the shoe 
            playerSum += card.getValue(); // get the value of the cards
            playerAceCount += card.isAce() ? 1 : 0;// return 1 otherwise return 0 for java turnary operator syntax
            playerHand.add(card);// add both cards to the hand of player
        }
        System.out.println("Player:"); 
        System.out.println(playerHand);// prints out the dealers open card
        System.out.println(playerSum);// prints out the dealers sum total points
        System.out.println(playerAceCount);// prints out the total number of aces the dealer


    }

    public void buildDeck(){//create the fn for buildDeck
        deck = new ArrayList<Card>();//list out all types and values so we can use for loop to iterate the type and value for each combinations
        String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        String[] types = {"C", "D", "H", "S"};
        //iterate through each type and then iterate through each value for each specfic type to create all combinations of possible cards
        for (int i=0; i< types.length; i++){
            for (int j=0; j< values.length; j++){
                Card card = new Card(values[j], types[i]);
                deck.add(card);            
            }
        }
        System.out.println("BUILD DECK");
        System.out.println(deck);// will return Object Type and memory address but i want to  convert it to string representation of the card
    }

    public void shuffleDeck(){//iterate thru each card then select a random card in our deck and replace the original card
        for (int i = 0; i< deck.size(); i++){
            int j = random.nextInt(deck.size());// give us a random integer from 0 to 51
            Card currCard = deck.get(i);
            Card randomCard = deck.get(j);
            deck.set(i, randomCard); // replace both cards
            deck.set(j, currCard);
        }
        System.out.println("AFTER SHUFFLE");
        System.out.println(deck);      
    }
    public int reducePlayerAce(){ // turn "A" from 11 to 1 if conditions are fufilled for player
        while (playerSum > 21 && playerAceCount > 0){
            playerSum -= 10;
            playerAceCount -= 1;
        }
        return playerSum;
    }
    public int reduceDealerAce(){ // turn "A" from 11 to 1 if conditions are fufilled
        while (dealerSum > 21 && dealerAceCount > 0){
            dealerSum -= 10;
            dealerAceCount -= 1;
        }
        return dealerSum;
    }
}

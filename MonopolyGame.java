package src;

import Buildings.*;
import Resources.GameProperties;
import Cards.*;
import Squares.*;

import java.io.IOException;
import java.util.*;

public class MonopolyGame {

    public MonopolyGame() throws IOException {
    }

    private ArrayList<Cards> cards = new ArrayList<>();//Chance cards arraylist
    private BankAccount bank = new BankAccount(); //Bank Account
    private Dice dice = new Dice();
    private ArrayList<Player> remainingPlayers = new ArrayList<>();//The players who do not bankrupt
    private ArrayList<Player> players = new ArrayList<>();

    private Board board = new Board();

    private GameProperties props = new GameProperties();//Game constants
    private int counter = props.getCycleCount();//how many tour game continue
    private int control, key = 0;
    private Random randomGenerator = new Random();
    private boolean anyBankruptOccur = false;


    private Map<Integer, String> purpleColor = new LinkedHashMap<>();
    private Map<Integer, String> orangeColor = new LinkedHashMap<>();
    private Map<Integer, String> blueColor = new LinkedHashMap<>();
    private Map<Integer, String> yellowColor = new LinkedHashMap<>();

    public void play() throws IOException {//Play function is main function of monopoly game

        createPlayers();
        createCards();
        determinePlayingOrder();
        while (controlFinish()) {//till the game ends...
            for (int j = 0; j < remainingPlayers.size(); j++) { //one cycle
                if (!controlFinish()) {//control the finish of game
                    break;
                }
                if (remainingPlayers.get(j).getJailCounter() > 0) {//If player is in jail it can not take action
                    remainingPlayers.get(j).setJailCounter(remainingPlayers.get(j).getJailCounter() - 1);
                } else if (remainingPlayers.get(j).getJailCounter() == 0) {//Else continue

                    System.out.println("-----------------------------------------------------------------------------------------");
                    System.out.println("Cycle counter is : " + (props.getCycleCount() - counter + 1));
                    System.out.println("Turn is player: " + remainingPlayers.get(j).getName());
                    System.out.println("Its turn counter is: " + (remainingPlayers.get(j).getTurnCounter() + 1));
                    System.out.println("Its current location is: " + remainingPlayers.get(j).getCurrentSquare().getSquareNo());
                    System.out.println("Name of squares: " + remainingPlayers.get(j).getCurrentSquare().getName());
                    System.out.println("Dices is shaking....");
                    int dice_Sum;
                    dice_Sum = throwDice();
                    key = dice_Sum;
                    if (dice.getControlDiceSameness()) {//control player throw double dice
                        Player player = remainingPlayers.get(j);
                        move(dice_Sum, remainingPlayers.get(j));
                        System.out.println();
                        System.out.println("Its new location is : " + remainingPlayers.get(j).getCurrentSquare().getSquareNo());
                        takeActionAccordingToSquare(remainingPlayers.get(j), remainingPlayers.get(j).getCurrentSquare());
                        control++;
                        if (!remainingPlayers.contains(player)) {//If player bankrupt it leave from the game
                            break;
                        }
                        if (control == 1 && !(remainingPlayers.get(j).getCurrentSquare() instanceof Jail)) {
                            System.out.println("One more time throw!!");
                            dice_Sum = throwDice();
                            key = dice_Sum;
                            move(dice_Sum, remainingPlayers.get(j));
                            takeActionAccordingToSquare(remainingPlayers.get(j), remainingPlayers.get(j).getCurrentSquare());
                        }
                    } else { //Player throw different dice
                        key = dice_Sum;
                        move(dice_Sum, remainingPlayers.get(j));
                        System.out.println();
                        System.out.println("Its new location is : " + remainingPlayers.get(j).getCurrentSquare().getSquareNo());
                        takeActionAccordingToSquare(remainingPlayers.get(j), remainingPlayers.get(j).getCurrentSquare());
                    }
                    control = 0;


                }

                if (anyBankruptOccur) {//If any bankrupt occur
                    j--;
                    anyBankruptOccur = false;
                }
            }

            if (remainingPlayers.size() > 1) {//Every end of the cycle
                sortingObjectsToBalance(remainingPlayers);
                System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                System.out.println("End of the cycle");
                for (int i = 0; i < remainingPlayers.size(); i++) {
                    System.out.println("Player " + remainingPlayers.get(i).getName() + " has balance : " + remainingPlayers.get(i).getCurrentBalance()
                            + ",has " + remainingPlayers.get(i).getCurrentHomes() + " homes ," + remainingPlayers.get(i).getCurrentHotels()
                            + " hotels ," + remainingPlayers.get(i).getCurrentMalls() + " malls ," + remainingPlayers.get(i).getCurrentSkyscrapers()
                            + " skyscrappers.Its location is : " + remainingPlayers.get(i).getCurrentSquare().getSquareNo());
                }
                System.out.println("Bank total cash is " + bank.getTotalCash() + " dolar.");
                System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                counter--;
            }
        }

    }

    public void takeActionAccordingToSquare(Player player, Square square) throws IOException {//Take action according to square type
        Scanner sc = new Scanner(System.in);
        if (square instanceof Avenue) {//If any owner of the square
            if(((Avenue) square).getOwner() != null) {
            player.setCurrentBalance(player.getCurrentBalance()-((Avenue) square).getRentalFee());
            ((Avenue) square).getOwner().setCurrentBalance(((Avenue) square).getOwner().getCurrentBalance()+((Avenue) square).getRentalFee());
            System.out.println("The owner of this square is : " + ((Avenue) square).getOwner().getName());
            System.out.println("Player " + player.getName() + " is paid to "+ "player "+ ((Avenue) square).getOwner().getName()+".");
            }
            else {
                if (key >= props.purchaseControlKey()) {
                    System.out.println("This is " + square.getName() + ".It is " +
                            ((Avenue) square).getPurchasePrice() + " dolar!");
                    int input = (int)(Math.random()*(2));

                    if (input == 1) {//If user want to buy

                        if (player.getCurrentBalance() > ((Avenue) square).getPurchasePrice()) {
                            System.out.println("Player purchase the "+ square.getName());
                            ((Avenue) square).setOwner(player);
                            player.setCurrentBalance(player.getCurrentBalance() - ((Avenue) square).getPurchasePrice());
                            bank.setTotalCash(((Avenue) square).getPurchasePrice() + bank.getTotalCash());
                        }
                        else {
                            System.out.println("Your balance is not enough so go on");
                        }
                    }
                    else{
                        System.out.println("Player does not want to buy square");
                    }
                }

                else{
                    System.out.println("This square is purchasable square but your dice number does not bigger than 8 :( So you dont have any action");
                }
            }
            System.out.println("Player "+ player.getName() +"'s balance is : " + player.getCurrentBalance());

        }
        else if (square instanceof ChanceSquare) {
            System.out.println("This is chance square... Your cards is selecting....");
            cardsGenerator(player);
            controlBankrupt(player);
        }
        else if (square instanceof ChestSquare) {
            System.out.println("This is" + square.getName() + "\nYou earn " + ((ChestSquare) square).getChest() + " " + " dolar !");
            player.setCurrentBalance(player.getCurrentBalance() + ((ChestSquare) square).getChest());
            bank.setTotalCash(bank.getTotalCash() - ((ChestSquare) square).getChest());
        }
        else if (square instanceof ElectricTax) {
            System.out.println("This location is " + " " + square.getName() + " " + " .You must pay " + " " + ((ElectricTax) square).getTax() + " " + " electric tax !");
            player.setCurrentBalance(player.getCurrentBalance() - ((ElectricTax) square).getTax());
            bank.setTotalCash(bank.getTotalCash() + ((ElectricTax) square).getTax());
            controlBankrupt(player);
        }
        else if (square instanceof Jail) {
            player.setJailCounter(1);
            System.out.println("You are in Jail and you must wait for one tour!!");
        }

        else if (square instanceof Place) {//If square is place and if player have all color of square then it can build buildings
            if(((Place) square).getOwner() != null && ((Place) square).getOwner() != player) {
                player.setCurrentBalance(player.getCurrentBalance()-((Place) square).getRentalFee());
                ((Place) square).getOwner().setCurrentBalance(((Place) square).getOwner().getCurrentBalance()+((Place) square).getRentalFee());
                System.out.println("This square owner is :" + ((Place) square).getOwner().getName());
                System.out.println("You must pay to owner " + ((Place) square).getRentalFee());

                for (int i = 0; i < square.buildings.size() ; i++) { // If square has buildings players except owner must pay rent to owner.
                    if(square.buildings.get(i) instanceof Homes){
                        System.out.println("Player " + player.getName() + " pay " + ((Homes) square.buildings.get(i)).getRentalFee() +
                        " dolar to owner of home : " + ((Place) square).getOwner().getName());
                        player.setCurrentBalance(player.getCurrentBalance()-((Homes) square.buildings.get(i)).getRentalFee());
                        ((Place) square).getOwner().setCurrentBalance(((Place) square).getOwner().getCurrentBalance()+((Homes) square.buildings.get(i)).getRentalFee());
                    }
                    else if(square.buildings.get(i) instanceof Hotels){
                        System.out.println("Player " + player.getName() + " pay " + ((Hotels) square.buildings.get(i)).getRentalFee() +
                                " dolar to owner of home : " + ((Place) square).getOwner().getName());
                        player.setCurrentBalance(player.getCurrentBalance()-((Hotels) square.buildings.get(i)).getRentalFee());
                        ((Place) square).getOwner().setCurrentBalance(((Place) square).getOwner().getCurrentBalance()+((Hotels) square.buildings.get(i)).getRentalFee());
                    }
                    else if(square.buildings.get(i) instanceof Malls){
                        System.out.println("Player " + player.getName() + " pay " + ((Malls) square.buildings.get(i)).getRentalFee() +
                                " dolar to owner of home : " + ((Place) square).getOwner().getName());
                        player.setCurrentBalance(player.getCurrentBalance()-((Malls) square.buildings.get(i)).getRentalFee());
                        ((Place) square).getOwner().setCurrentBalance(((Place) square).getOwner().getCurrentBalance()+((Malls) square.buildings.get(i)).getRentalFee());
                    }
                    else if(square.buildings.get(i) instanceof Skyscrapers){
                        System.out.println("Player " + player.getName() + " pay " + ((Skyscrapers) square.buildings.get(i)).getRentalFee() +
                                " dolar to owner of home : " + ((Place) square).getOwner().getName());
                        player.setCurrentBalance(player.getCurrentBalance()-((Skyscrapers) square.buildings.get(i)).getRentalFee());
                        ((Place) square).getOwner().setCurrentBalance(((Place) square).getOwner().getCurrentBalance()+((Skyscrapers) square.buildings.get(i)).getRentalFee());
                    }
                }
                controlBankrupt(player);
            }
            else if(((Place) square).getOwner() == player){//If the owner of this square checks whether can build or not
                purchaseBuilding(player,square);
            }
            else {
                if (key >= props.purchaseControlKey()) {//dice total must be greater than from purchase control key
                    System.out.println("This is " + square.getName() + " it is " +
                            ((Place) square).getPurchasePrice() +
                            "tl");
                    int input = (int)(Math.random()*(2));
                    if (input == 1) {
                        if (player.getCurrentBalance() > ((Place) square).getPurchasePrice()) {
                            ((Place) square).setOwner(player);
                            player.setCurrentBalance(player.getCurrentBalance() - ((Place) square).getPurchasePrice());
                            System.out.println("Player purchase the square "+ square.getName());
                        } else {
                            System.out.println("Your balance is not enough so go on");
                        }
                    }
                    else{
                        System.out.println("Player does not want to buy square");
                    }
                }
                else {
                    System.out.println("This square is purchasable square but your dice number does not bigger than 8 :( So you dont have any action");
                }
            }
        }
        else if (square instanceof StartSquare) {
        }
        else if (square instanceof TrainStation) {
            if(((TrainStation) square).getOwner() != null) {
                player.setCurrentBalance(player.getCurrentBalance()-((TrainStation) square).getRentalFee());
                ((TrainStation) square).getOwner().setCurrentBalance(((TrainStation) square).getOwner().getCurrentBalance()+((TrainStation) square).getRentalFee());
                controlBankrupt(player);
            }
            else {
                if (key >= props.purchaseControlKey()) {
                    System.out.println("This is " + square.getName() + "it is " +
                            ((TrainStation) square).getPurchasePrice() +
                            "tl");
                    int input = (int)(Math.random()*(2));
                    if (input == 1) {
                        if (player.getCurrentBalance() > ((TrainStation) square).getPurchasePrice()) {
                            ((TrainStation) square).setOwner(player);
                            player.setCurrentBalance(player.getCurrentBalance() - ((TrainStation) square).getPurchasePrice());
                            bank.setTotalCash(bank.getTotalCash() + ((TrainStation) square).getPurchasePrice());
                        } else {
                            System.out.println("Your balance is not enough!!");
                        }
                    }
                    else{
                        System.out.println("Player does not want to buy square");
                    }
                }
                else {
                    System.out.println("This square is purchasable square but your dice number does not bigger than 8 :( So you dont have any action");
                }
            }
        }
        else if (square instanceof WaterTax) {
            System.out.println("This location is " + " " + square.getName() + " " + " .You must pay " + ((WaterTax) square).getTax() + " " + "water tax !");
            player.setCurrentBalance(player.getCurrentBalance() - ((WaterTax) square).getTax());
            bank.setTotalCash(bank.getTotalCash() + ((WaterTax) square).getTax());
            controlBankrupt(player);
        }
    }

    public void purchaseBuilding(Player player,Square square) throws IOException {
        if(controlUserCanBuild(player,square)){

            int input = (int)(Math.random()*(5) +1);

            switch (input){
                case 1:
                    if(player.getCurrentBalance() >= props.homeSalePrice())
                        createHome(player,square);
                    break;
                case 2:
                    if(player.getCurrentBalance() >= props.hotelSalePrice())
                        createHotel(player,square);
                    break;
                case 3:
                    if(player.getCurrentBalance() >= props.mallSalePrice())
                        createMall(player,square);
                    break;
                case 4:
                    if(player.getCurrentBalance() >= props.skyscrapperSalePrice())
                        createSkyscraper(player,square);
                    break;
            }
        }
        else {
            System.out.println("You can not have all color of "+ ((Place)square).getColor());
        }
    }
    public void move(int moveNum, Player player) throws IOException {//src.Player moves sum of dice next square
        int newLocation;
        if (moveNum + player.getCurrentSquare().getSquareNo() >= props.getNumberOfSquare()) {
            //When player complete the tour it gain money that determined by game constants
            player.setCurrentBalance(player.getCurrentBalance() + props.getMoneyGivenPerTour());
            newLocation = player.getCurrentSquare().getSquareNo() + moveNum - props.getNumberOfSquare();
            player.setCurrentSquare(board.squares.get(newLocation));
            bank.setTotalCash(bank.getTotalCash() - props.getMoneyGivenPerTour());
        } else {//move only
            newLocation = player.getCurrentSquare().getSquareNo() + moveNum;
            player.setCurrentSquare(board.squares.get(newLocation));
        }
    }

    public boolean controlFinish() {//Control the finishes of tour number determined by game constants
        if (counter == 0 || remainingPlayers.size() == 1) {
            System.out.println("The game is over ");
            for (int i = 0; i < remainingPlayers.size(); i++) {
                System.out.print("Player " + remainingPlayers.get(i).getName() + " has current balance : " + remainingPlayers.get(i).getCurrentBalance());
                System.out.println(" and its location is : " + remainingPlayers.get(i).getCurrentSquare().getSquareNo());
            }
            return false;
        } else {
            return true;
        }
    }

    public void createPlayers() throws IOException {//Create number of players

        Scanner input = new Scanner(System.in);
        Player player;
        for (int i = 1; i <= props.getNumberOfPlayer(); i++) {

            System.out.println("Please enter name of " + i + "'th simulated player : ");
            player = new Player(input.next());

            player.setCurrentBalance(props.getMoneyGivenBeginningOfGame());
            player.setCurrentSquare(board.squares.get(0)); //each user start from 0
            System.out.println(player.getName() + " is shaking dices... ");
            player.setDiceSum(dice.getSumOfDice());
            remainingPlayers.add(player);
            players.add(player);
        }
    }

    public void determinePlayingOrder() {
        sortingObjects(remainingPlayers);
        for (int j = 0; j < remainingPlayers.size(); j++) {
            remainingPlayers.get(j).setTurnCounter(j);
            System.out.println(remainingPlayers.get(j).getName() + " " + remainingPlayers.get(j).getDiceSum());
        }
    }

    public void sortingObjects(ArrayList<Player> list) {
        Collections.sort(list, new Comparator<Player>() {
            @Override
            public int compare(Player z1, Player z2) {
                if (z1.getDiceSum() > z2.getDiceSum())
                    return -1;
                if (z1.getDiceSum() < z2.getDiceSum())
                    return 1;
                return 0;
            }
        });
    }

    public void sortingObjectsToBalance(ArrayList<Player> list) {
        Collections.sort(list, new Comparator<Player>() {
            @Override
            public int compare(Player z1, Player z2) {
                if (z1.getCurrentBalance() > z2.getCurrentBalance())
                    return -1;
                if (z1.getCurrentBalance() < z2.getCurrentBalance())
                    return 1;
                return 0;
            }
        });
    }

    public int throwDice() {
        int diceSum = dice.getSumOfDice();
        return diceSum;
    }

    public void createCards() throws IOException {
        for (int i = 0; i < 5; i++) {
            Prize prize = new Prize();
            cards.add(prize);
            Penalty penalty = new Penalty();
            cards.add(penalty);
        }
    }

    public void cardsGenerator(Player player) {

        Scanner input = new Scanner(System.in);
        int randomInput;
        int index = randomGenerator.nextInt(cards.size());
        Collections.shuffle(cards);
        Cards item = cards.get(index);
        int turn = (int) (Math.random() * 5);
        if (item instanceof Prize) {
            if (turn == 0) {
                player.setCurrentBalance(player.getCurrentBalance() + ((Prize) item).getDolarTwenty());
                bank.setTotalCash(bank.getTotalCash() - ((Prize) item).getDolarTwenty());
                System.out.println(player.getName() + " " + ", you win 20 Dolar!!");
            } else if (turn == 1) {
                player.setCurrentBalance(player.getCurrentBalance() + ((Prize) item).getDolarFifthy());
                bank.setTotalCash(bank.getTotalCash() - ((Prize) item).getDolarFifthy());
                System.out.println(player.getName() + " " + ", you win 50 Dolar!!");

            } else if (turn == 2) {
                randomInput = (int) (Math.random() * (remainingPlayers.size()));
                while (remainingPlayers.get(randomInput) == player){
                    randomInput = (int) (Math.random() * (remainingPlayers.size()));
                }
                remainingPlayers.get(randomInput).setCurrentSquare(board.squares.get(0));
                System.out.println(remainingPlayers.get(randomInput).getName() + " is sended to the start square by player" + player.getName() + ".");
            } else if (turn == 3) {
                randomInput = (int) (Math.random() * (remainingPlayers.size()));
                while (remainingPlayers.get(randomInput) == player){
                    randomInput = (int) (Math.random() * (remainingPlayers.size()));
                }
                System.out.println("Player "+ remainingPlayers.get(randomInput).getName()+" is sended to jail by " + player.getName());
                remainingPlayers.get(randomInput).setCurrentSquare(board.squares.get(((Prize) item).getJailIndex()));
                remainingPlayers.get(randomInput).setJailCounter(1);

            } else if (turn == 4) {
                player.setCurrentBalance(player.getCurrentBalance() + ((Prize) item).getDolarHundred());
                System.out.println(player.getName() + " " + ",you win 100 Dolar!!");
                bank.setTotalCash(bank.getTotalCash() - 100);
            }
        }
        else if (item instanceof Penalty) {
            if (turn == 0) {
                player.setCurrentBalance(player.getCurrentBalance() - ((Penalty) item).payTwenty());
                bank.setTotalCash(bank.getTotalCash() + ((Penalty) item).payTwenty());
                System.out.println(player.getName() + " " + ",you lost 20 Dolar!!");
            } else if (turn == 1) {
                player.setCurrentBalance(player.getCurrentBalance() - ((Penalty) item).payFifty());
                bank.setTotalCash(bank.getTotalCash() + ((Penalty) item).payFifty());
                System.out.println(player.getName() + " " + ",you lost 50 Dolar!!");
            } else if (turn == 2) {
                System.out.println("Player " + player.getName() + " you have to go start square!!");
                player.setCurrentSquare(board.squares.get(0));
            } else if (turn == 3) {
                player.setCurrentSquare(board.squares.get(((Penalty) item).getJailIndex()));
                System.out.println("Player " + player.getName() + " " + " must wait in Jail for one tour!!");
                player.setJailCounter(1);
            } else if (turn == 4) {
                player.setCurrentBalance(player.getCurrentBalance() - ((Penalty) item).payHundred());
                bank.setTotalCash(bank.getTotalCash() + ((Penalty) item).payHundred());
                System.out.println(player.getName() + " " + " ,you lost 100 Dolar!!");
            }
        }
    }

    public void controlBankrupt(Player player) {
        if (player.getCurrentBalance() <= 0) {
            System.out.println("\n Opps ! Player " + player.getName() + " bankrupt !");
            player.setCurrentBalance(0);
            System.out.println("Player " + player.getName() + "'s balance is : " + player.getCurrentBalance());
            remainingPlayers.remove(player);
            anyBankruptOccur = true;
        } else {
            System.out.println("Player " + player.getName() + "'s balance is : " + player.getCurrentBalance());
        }
        if (remainingPlayers.size() == 1) {
            System.out.println("*************************************************");
            System.out.println("*************************************************");
            System.out.println("The winner of the game is Player : " + remainingPlayers.get(0).getName() + " Congratulations !!!!");
            System.out.println("*************************************************");
            System.out.println("*************************************************");
        }

    }

    public void addSquareToColorMap() {
        blueColor.put(17, null);
        blueColor.put(21, null);
        blueColor.put(28, null);
        blueColor.put(38, null);

        orangeColor.put(14, null);
        orangeColor.put(22, null);
        orangeColor.put(35, null);

        purpleColor.put(31, null);
        purpleColor.put(25, null);
        purpleColor.put(19, null);
        purpleColor.put(26, null);
        purpleColor.put(29, null);
        purpleColor.put(36, null);

        yellowColor.put(18, null);
        yellowColor.put(24, null);
        yellowColor.put(27, null);
        yellowColor.put(32, null);
        yellowColor.put(37, null);
        yellowColor.put(39, null);

    }

    public boolean controlUserCanBuild(Player player, Square square) {
        addSquareToColorMap();
        switch (((Place) square).getColor()) {

            case "purple":
                return arePlayerHaveAllColor(purpleColor, 19, player);
            case "orange":
                return arePlayerHaveAllColor(orangeColor, 14, player);
            case "yellow":
                return arePlayerHaveAllColor(yellowColor, 18, player);
            case "blue":
                return arePlayerHaveAllColor(blueColor, 17, player);
        }
        return false;
    }

    public boolean arePlayerHaveAllColor(Map<Integer, String> map, int index, Player player) {

        ArrayList<String> temp = new ArrayList<String>();

        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            temp.add(entry.getValue());
        }
        return areSame(temp.toArray(String[]::new));
    }

    private static boolean areSame(String[] arr) {
        // Put all array elements in a HashSet
        Set<String> s = new HashSet<>(Arrays.asList(arr));
        return (s.size() == 1);
    }

    public void createHome(Player player, Square square) throws IOException {
        BuildingsForSale home = new Homes(props.homeSalePrice(), props.homeRentAmount());
        home.setOwner(player);
        square.buildings.add(home);
        setColorMap(player,square);
        player.setCurrentHomes(player.getCurrentHomes()+1);

    }

    public void createHotel(Player player, Square square) throws IOException {
        BuildingsForSale hotel = new Hotels(props.hotelSalePrice(), props.hotelRentAmount());
        hotel.setOwner(player);
        square.buildings.add(hotel);
        setColorMap(player,square);
        player.setCurrentHotels(1);
    }

    public void createMall(Player player, Square square) throws IOException {
        BuildingsForSale mall = new Malls(props.mallSalePrice(), props.mallRentAmount());
        mall.setOwner(player);
        square.buildings.add(mall);
        setColorMap(player,square);
        player.setCurrentMalls(1);
    }

    public void createSkyscraper(Player player, Square square) throws IOException {
        BuildingsForSale skyscraper = new Skyscrapers(props.skyscrapperSalePrice(), props.skyscrapperRentAmount());
        skyscraper.setOwner(player);
        square.buildings.add(skyscraper);
        setColorMap(player,square);
        player.setCurrentSkyscrapers(1);
    }

    public void setColorMap(Player player,Square square){
        if(purpleColor.containsKey(square.getSquareNo()))
            purpleColor.replace(square.getSquareNo(),player.getName());
        else if(yellowColor.containsKey(square.getSquareNo()))
            yellowColor.replace(square.getSquareNo(),player.getName());
        else if(blueColor.containsKey(square.getSquareNo()))
            blueColor.replace(square.getSquareNo(),player.getName());
        else if(orangeColor.containsKey(square.getSquareNo()))
            orangeColor.replace(square.getSquareNo(),player.getName());
    }
}



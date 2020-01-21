# MonopolyGame
Monopoly is a board game representing business people and property deeds. Required players minimum 2, maximum 8 players. Main purposes are buy valuable properties or businesses and raise money from players properties. Players who go bankrupt are disqualified. The last player wins.

# Monopoly game contains :

There are 40 square that include buildings, owner, squareNo.
At the beginning a certain amount of money as balance is given to the players.
Players are moving by throwing dices.
Players pay tax in the tax squares or square that have owner.
When square has owner , owner take rent from players who moves that square.
Also if a player build a building , it take rent from players who moves that square.
Place squares have color.
If any player has all squares of any color , player can build home, hotel ,mall or sky
scrapper.
Players are earn amount of money for each pass-over of start point.
When the player have 0 balance or subzero player bankrupt.
Each cycle we will see list of remain players. The game will remain until the last
player stands or the cycle counter is equals max iteration of game.

# System constraints:
We only run the program via the command line. And we used the JDK tools. We did not use
GUI or any visual tools. We use Properties file class to read inputs from file (with extension
.properties) game.properties .

# Terminology of Classes and Files:

Monopoly Game : This class is a class where game functions are used and rounds are
returned.
Dice : The item is used to get random numbers between 1-6.
Square : Abstract super class of all type of squares .
TaxSquare : Players who come to this square have to pay taxes to the bank.
WaterTax : Players who come to this square have to pay water taxes to the bank.
ElectricTax: Players who come to this square have to pay electric taxes to the bank.
ChanceSquare: Players who come to this square can choose a card from Cards and take an
action by this way.
Place: Players who come to this square can rent or buy field.
Avenue: Players who come to this square can rent or buy an avenue.
TrainStation: Players who came to this square can rent or buy an train station.
Jail: Players who come to this square go to jail and stay here for a tour.
Player : This contains simulated users.
Board : A playground with these features and squares.
BankAccount : This class keeps total count of money.
Purchasable: This class represent salable squares for gamers.
Unpurchasable: This class represent unsalable squares for gamers.
Cards: This class is only available when the player comes over the square of chance. There
are two types of randomly shuffled cards. These are penalty and power.
Penalty: The player is subject to various penalties in this class.
Prize: The player wins various prizes in this class.
Homes , Hotels, Malls ,Skyscrappers : Are buildings which user can build.
GameProperties : This class contains the properties of the game and allows you to call these
properties through the Properties object.

# JUnit Tests

We add junit test for our implementation.
BankAccount Test : Test the bank in the simulation.
Monopoly Game Test: Test the main class of project.
Penalty Test: Test the penalty cards.
Prize Test: Test the prize cards.

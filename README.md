# Tic Tac Toe

## A. Details
- Name of the project: Tic Tac Toe
- Name : Karan Shetty
- BITS ID : 2018A7PS0111G
- Email : f20180111@goa.bits-pilani.ac.in

## B. Description and Bugs:
- The app allows a user to play multiplayer Tic Tac Toe with another player online or single player Tic Tac Toe versus a random computer agent.
- The app also keeps a track of game outcomes for all users and displays it on login. 
- To access the app users must first login with valid E-mail ids.
- Pressing the back button in the GameFragment opens a dialog that confirms if the user wants to forfeit the game. 
- A "log out" action bar menu is shown on both the dashboard and the game fragments. Clicking it logs the user out and show the LoginFragment. This click is handled in the MainActivity.
- Bugs:
    - No known bugs of yet.

## C. Task Completion
### Task 1
- I have correctly implemented the sign-in screen with a button which both allows a new user to register into our Firebase Auth Database, or an existing user to log into the app and retrieve their score from the database.
Logging in is done using Firebase Auth and email id and a corresponding password.
- I have also included offline Firebase capabilities so that a user can log in offline and play single player games even if they are not connected to the internet.
- On logging in, the user is shown a dashboard fragment, where they can see ongoing games by different users and decide to join a two player game.
- Apart from this, they have a floating action button in order to play new games - Either a single player game with the computer's random player, or start a new double player game and wait for some other user to join from their device.

### Task 2
- If a user selects a new one-player game, they are directed to a game board where they have to make the first move and the UI waits for the user to mark a cell as 'X'.
- Then the UI marks any other cell as 'O', and they keep playing until either the user wins, looses or the game is tied due to no space on the board.
- On each of these cases, a Toast is shown to the user, and they are taken back to the dashboard fragment.
- On starting the game, the user has an option to click on the back button, and hence forfeit the game, incrementing their loss by one.

### Task 3
- In a double player game, the user who made the game has to wait until another user joins the game and then they can play against each other.
- I have used Firebase for task 3, and are adding new games to a new Games List. I are also storing a new OpenGames List, in which I only add open games, and I delete a game once another user joins the game.
- After every move a user makes, the app checks if the game has ended. If so, it shows the appropriate toast to each user and takes them back to the dashboard fragment.
- This is done by using LiveData Listeners on the Games.
- The users' score is directly uploaded to the firebase and the dashboard fragment gets their corresponding score using a One Time Data Listener with firebase.
- For the open games list, I have used a Persistent Data Listener with Firebase's RT database. This is because at any time, new games can be added by different users on different devices, and I need to be able to see them all.

## D. How To Run
- The app should run out of the box, Realtime database from Firebase and Firebase auth are used for the backend with a project that is configured already. Config files have also been provided at appropriate places.
- If you wish to create a new Firebase project, please ensure that the realtime database is hosted on the singapore server and config files are placed in the appropriate directories (app/).

## E. Testing & Accessibility
- Testing
- Accessibility scanner was run which pointed out that labels Ire not set for some of the buttons.
- Monkey

## F. Hours Taken
16

## G. Difficulty
9

## References:
https://firebase.google.com/docs/auth/android/password-auth
Other firebase docs
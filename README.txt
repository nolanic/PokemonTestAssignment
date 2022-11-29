developer: Eugeniu Bondari    developer.eugen@gmail.com

For this test assignment I used Kotlin, JetPack Compose with MVVM, retrofit with okhttp, coroutines.
The user data is just some static data that includes a nickname and available balance (availableFunds). 
Wasn't sure if I was supposed to create a login screen for this project, so I took the easy way.

The pokemon search screen (MainActivity) does not display all the data provided by the API, I randomly picked the pieces that I consider most relevant.
It also displays an image of the pokemon (if it's available).

The purchase button (Buy for x$) is placed at the bottom of the content.
If the content is too long, you might have to scroll to the bottom to reach the purchase button.
On button click the cost of the pokemon is extracted from available balance.
Click it often enough and at some point "Not enough money" message will be displayed.

In case if it's not obvious:
The search gets triggered 2 seconds after the user stopped typing in the search bar.
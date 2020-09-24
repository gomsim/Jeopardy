A friend's birthday was eminent, upon which I'd promised to hold a Jeopardy session, so I made this implementation.

Most people have probably heard of Jeopardy. It's a sort of quiz game. A grid of "cards" is laid out, each column representing a category and each row representing a level of difficulty. Underneath each card is a statement. Upon choosing a card the underlying statement is shown. The player must guess the question for which the statement is the answer. Like in the gif below, the statement is (in Swedish) "neither an ent, nor a tree" for which the correct response is "What is a huorn" (Lord of the Rings trivia).

The game draws the contents of the cards from any file "*.game" in the games-directory. As many or as few categories as well as questions per category can be added. I's all determined by the game file.

Starting up Jeopardy while connected to a second monitor in expanded mode, a second GUI for the game will be rendered on the second monitor or TV. The purpose of this is: 
1. The game host can sit by the screen and watch and interact with the players instead of watching the TV like the others.
2. The players will not have to see your mouse cursor. 

Main screen with twinkling stars:

![MainScreen](https://j.gifs.com/MwGoJR.gif)


Turning a card:

![QuestionScreen](https://j.gifs.com/D1vP85.gif)

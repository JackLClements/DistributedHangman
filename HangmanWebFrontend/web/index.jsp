<%-- 
    Document   : index
    Created on : 09-Jul-2017, 20:35:09
    Author     : Jack L. Clements
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <script type="text/javascript" src="javascriptFunctions.js"></script>
        <title>Hangman Game</title>
    </head>
    <body>
        <h1>Hangman Game</h1>
        <!-- jQuery -->
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"/></script>

    <input type="button" value="connect" id="somebutton" onclick="connect()"/>
    <input type="button" value="update" id="somebutton2" onclick="update()"/>
    <form>
        <table>
            <tr>
                <td>Room:</td>
                <td> <select id="room">
                        <option value="0">New Game</option>
                    </select></td>
            </tr>
            <tr>
                <td><input type="button" onclick="chooseRoom();" value="choose room" /></td>
            </tr>
        </table>
    </form>
    <h2>Rules</h2>
    <p>The rules are simple. Press connect to connect to the server, select a room and alongside other players, attempt to solve a game of hangman. Once a game has been solved, the game is over and you are disconnected from the server.</p>
    <li>
    <ul>You can fetch updates from the server by pressing update</ul>
    <ul>Press connect to connect/re-connect to the server</ul>
    <ul>Once a game is complete you are disconnected from the server</ul>
    <ul>15 incorrect guesses are allowed before Game Over</ul>
    </li>
    
    <form>
        <table>
            <tr>
                <td>Your guess:</td>
                <td><input type="text" id="textbox" name="textbox"/></td>
            </tr>
            <tr>
                <td><input type="button" onclick="postGuess();" value="guess" /></td>
            </tr>
        </table>
    </form>

    <div id="responses">
        <ul id="test">

        </ul>

    </div>

</body>
</html>

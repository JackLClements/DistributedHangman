var req;
function connect() { 
    var url = "connect?action=choice"; //url for servlet used to complete action in web.xml
    req = new XMLHttpRequest();
    req.open("GET", url, true);
    req.onreadystatechange = callback2;
    req.send(null);
}

function update() {
    var url = "connect?action=getUpdates"; //url for servlet used to complete action in web.xml
    req = new XMLHttpRequest();
    req.open("GET", url, true);
    req.onreadystatechange = callback;
    req.send(null);
}

function chooseRoom() { 
    var url = "connect?action=chooseRoom&roomid=" + document.getElementById("room").value; //url for servlet used to complete action in web.xml
    req = new XMLHttpRequest();
    req.open("POST", url, true);
    req.onreadystatechange = callback;
    req.send(null);
    update();
}

function postGuess() { 
    var url = "connect?action=guess&character=" + document.getElementById("textbox").value; //url for servlet used to complete action in web.xml
    req = new XMLHttpRequest();
    req.open("POST", url, true);
    req.onreadystatechange = callback;
    req.send(null);
    //update();
}

function callback() {
    if (req.readyState == 4) {
        if (req.status == 200) {
            parseMessages(req.responseText);
        }
    }
    /* Can't get this to work, using JS/AJAX manually
     return function(){
     $.ajax({
     type: 'GET',
     url: 'connect',
     success:function(text){
     parseMessages(text);
     }
     });
     };*/
}

function callback2() {
    if (req.readyState == 4) {
        if (req.status == 200) {
            parseRooms(req.responseText);
        }
    }
}

function parseMessages(responseText) {
    document.getElementById("test").innerHTML = responseText.toString();
}

function parseRooms(responseText) {
    var select = document.getElementById("room");
    for (i = 0; i < responseText; i++){
        var newElement = document.createElement("option");
        newElement.textContent = i+1;
        newElement.value = i+1;
        select.appendChild(newElement);
    }
}
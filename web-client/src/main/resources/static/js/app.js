var stompClient = null;

var connect = function () {
    var destination = "published.transactions";
    stompClient = Stomp.client("ws://localhost:61614/stomp");
    stompClient.debug = null;

    // the client is notified when it is connected to the server.
    var onConnect = function(frame) {
        clearTables();
        displayConnectionStatus(stompClient.connected);
        stompClient.subscribe(destination, function(message) {
            appendTransaction(JSON.parse(message.body));
        });
    };

    var onDisconnect = function(){
        displayConnectionStatus(stompClient.connected);
    };

    // guest - default ActiveMQ credentials
    stompClient.connect("guest", "guest", onConnect, onDisconnect);
}

var clearTables = function () {
    $("#transactions > tbody").html("");
    $("#deposits > tbody").html("");
    $("#withdrawals > tbody").html("");
    $("#transfers > tbody").html("");
}

var appendTransaction = function (message) {
    appendTransactionToTable(message, "transaction", formatRowGeneral);

    if(typeof(message.type) !== 'undefined'){
        appendTransactionToTable(message, message.type.toLowerCase(), formatRowCustom);
    }
}

var appendTransactionToTable = function (message, tableName, formatRow) {
    var table = document.getElementById(tableName + "s");
    formatRow(table, message);

    // keep 20 latest records, +1 is to account header
    while(table.rows.length > 21) {
       table.deleteRow(table.rows.length -1);
    }
}

var formatRowGeneral = function (table, message){
    var tbody = table.getElementsByTagName('tbody')[0];
    var row = tbody.insertRow(0);
    var cell = row.insertCell(-1);
    cell.innerHTML = message.id;
    cell = row.insertCell(-1);
    cell.innerHTML = message.type;
    cell = row.insertCell(-1);
    cell.innerHTML = message.timestamp;

}

var formatRowCustom = function (table, message){
    var tbody = table.getElementsByTagName('tbody')[0];
    var row = tbody.insertRow(0);
    var cell = row.insertCell(-1);
    cell.innerHTML = message.id;
    cell = row.insertCell(-1);
    cell.innerHTML = message.timestamp;

    var addedFields = new Set();
    for(var i = 2; i < table.rows[0].cells.length; i++){
        var key = table.rows[0].cells[i].innerHTML;
        cell = row.insertCell(-1);
        cell.innerHTML = message.fields[key];
        addedFields.add(key);
    }

    for (var property in message.fields) {
        if (message.fields.hasOwnProperty(property) && !addedFields.has(property)) {
            cell = table.rows[0].insertCell(-1)
            cell.innerHTML = property;
            cell = row.insertCell(-1);
            cell.innerHTML = message.fields[property];
        }
    }
}

var checkConnection = function () {
    if(stompClient === null || !stompClient.connected){
        connect();
    }
}

var displayConnectionStatus = function (connected) {
    var customGreenColor = "#2ecc71";
    var customRedColor = "#e74c3c";
    if(connected) {
        $("#status").text("connected").css("color", customGreenColor);
    } else {
        $("#status").text("connection lost, trying to reconnect ...").css("color", customRedColor);
    }
}

$(function () {
    connect();
    setInterval(checkConnection, 5000);

    $("#tabs a").click(function (e) {
        e.preventDefault();
        $(this).tab("show");
    })
});

$(document).ready(function () {
    $.getJSON("getall", {
            format: "json"
        })
        .done(function (data) {
            generateButtons(data.carPart);
            update(data.carPart);
        });
});

$(document).click(function (event) {
    var action = event.target.id.split(":");

    function _update() {
        $.getJSON("getall", {
                format: "json"
            })
            .done(function (data) {
                update(data.carPart);
            })
            .fail(function (xhr, status, error) {
                log("Error->" + error);
            });
    }

    switch (action[0]) {

        case "increment":
            clearLog();
            $.post("increment?partid=" + action[1]).done(function (msg) {
                    $.getJSON("getall", {
                            format: "json"
                        })
                        .done(function (data) {
                            update(data.carPart);
                        })
                        .fail(function (xhr, status, error) {
                            log(xhr.responseText);
                        });
                })
                .fail(function (xhr, status, error) {
                    log(xhr.responseText);
                });
            break;

        case "decrement":
            clearLog();
            $.post("decrement?partid=" + action[1]).done(function (msg) {
                    $.getJSON("getall", {
                            format: "json"
                        })
                        .done(function (data) {
                            update(data.carPart);
                        })
                        .fail(function (xhr, status, error) {
                            log(xhr.responseText);
                        });
                })
                .fail(function (xhr, status, error) {
                    log(xhr.responseText);
                });
            break;

        case "reset":
            clearLog();
            $.post("reset").done(function (msg) {
                    $.getJSON("getall", {
                            format: "json"
                        })
                        .done(function (data) {
                            update(data.carPart);
                        })
                        .fail(function (xhr, status, error) {
                            log(xhr.responseText);
                        });
                })
                .fail(function (xhr, status, error) {
                    log(xhr.responseText);
                });
            break;

    }

});

function clearLog() {
    $('#errtitle-anchor').html("");
    $('#log-anchor').text("");
}

function log(msg) {
    $('#errtitle-anchor').html("<hr><font size=\"6\">Server returned error</font>");
    $('#log-anchor').text(msg);
}

function update(carParts) {

    var table = "<tr><th>Item</th><th>Description</th><th>Count</th><th>Discontinued</th><th>On Order</th></tr>";
    $.each(carParts, function (key, value) {
        var partName = value.partName;
        var partDescription = value.partDescription;
        var inventoryCurrentCount = value.inventoryCurrentCount;

        var onOrder = value.onOrder;
        var discontinued = value.discontinued;

        if (inventoryCurrentCount == undefined) {
            inventoryCurrentCount = "0";
        }
        if (discontinued == undefined) {
            discontinued = "false";
        }
        if (onOrder == undefined) {
            onOrder = "false";
        }

        table += "<tr>";
        table += "<td>" + partName + "</td>";
        table += "<td>" + partDescription + "</td>";
        table += "<td>" + inventoryCurrentCount + "</td>";
        table += "<td>" + discontinued + "</td>";
        table += "<td>" + onOrder + "</td>";
        table += "</tr>";

    });
    $('#inventory-anchor').html(table);
}

function generateButtons(carParts) {
    var buttons = "";
    $.each(carParts, function (key, value) {
        buttons += "<button id=\"increment:" + value.partId + "\" type=\"button\">+" + value.partName + "</button>\n";
        buttons += "<button id=\"decrement:" + value.partId + "\" type=\"button\">-" + value.partName + "</button>\n";
    });
    $('#button-anchor').html(buttons);
}
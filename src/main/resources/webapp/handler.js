var slash = "/";

$(document).click(function (event) {
    var showInventory = $('#show-inventory');
    var action = event.target.id;
    event.target.id.text = "";

    var baseURL = $(location).attr("href");

    // $('#show-log').text("");

    function showLog(msg) {
        var oldmsg = $('#show-log').text();
        if (oldmsg == "") {
            $('#show-log').text(msg.trim());
        } else {
            $('#show-log').text(oldmsg + "\n" + msg.trim());
        }
    }

    showLog("\n");

    var base = null; {
        var element = document.getElementsByName("public_or_private");
        var i;
        for (i = 0; i < element.length; i++) {
            if (element[i].type == "radio") {
                if (element[i].checked) {
                    base = element[i].value;
                }
            }
        }
    }
    if (base == null) {
        alert("Please select public or private");
        return;
    }

    function updateInventory() {

        var url = base + "/getall";
        showLog("GET->" + baseURL + url);
        $.getJSON(url, {
                format: "json"
            })
            .done(function (data) {
                showInventory.text(JSON.stringify(data, null, 2));
            });

    }

    if (action.trim() == "") {
        updateInventory();
        return;
    }

    var url = null;

    switch (action) {

        case "clear":
            $('#show-log').text("");
            break;

        case "reset":
            url = base + "/reset";
            showLog("POST->" + baseURL + url);
            $.post(url).done(function (msg) {
                    showLog("Data->" + msg);
                    updateInventory();
                })
                .fail(function (xhr, status, error) {
                    showLog("Error->" + error);
                });
            break;

        case "increment-SparkPlug":
            url = base + "/increment?partid=100224";
            showLog("POST->" + baseURL + url);
            $.post(url).done(function (msg) {
                    showLog("Data->" + msg);
                    updateInventory();
                })
                .fail(function (xhr, status, error) {
                    showLog("Error->" + error);
                });
            break;

        case "decrement-SparkPlug":
            url = base + "/decrement?partid=100224";
            showLog("POST->" + baseURL + url);
            $.post(url).done(function (msg) {
                    showLog("Data->" + msg);
                    updateInventory();
                })
                .fail(function (xhr, status, error) {
                    showLog("Error->" + error);
                });
            break;

        case "increment-BigMuffler":
            url = base + "/increment?partid=100453";
            showLog("POST->" + baseURL + url);
            $.post(url).done(function (msg) {
                    showLog("Data->" + msg);
                    updateInventory();
                })
                .fail(function (xhr, status, error) {
                    showLog("Error->" + error);
                });
            break;

        case "decrement-BigMuffler":
            url = base + "/decrement?partid=100453";
            showLog("POST->" + baseURL + url);
            $.post(url).done(function (msg) {
                    showLog("Data->" + msg);
                    updateInventory();
                })
                .fail(function (xhr, status, error) {
                    showLog("Error->" + error);
                });
            break;

        case "increment-Starter":
            url = base + "/increment?partid=4598443";
            showLog("POST->" + baseURL + url);
            $.post(url).done(function (msg) {
                    showLog("Data->" + msg);
                    updateInventory();
                })
                .fail(function (xhr, status, error) {
                    showLog("Error->" + error);
                });
            break;

        case "decrement-Starter":
            url = base + "/decrement?partid=4598443";
            showLog("POST->" + baseURL + url);
            $.post(url).done(function (msg) {
                    showLog("Data->" + msg);
                    updateInventory();
                })
                .fail(function (xhr, status, error) {
                    showLog("Error->" + error);
                });
            break;
    }


});
function startSessionInterval() {
    stopSessionInterval();
    sessionCheckInterval = setInterval(alertFunc, 1000 * 60);
}

function stopSessionInterval() {
    if (sessionCheckInterval) {
        clearInterval(sessionCheckInterval);
    }
}

function keepSessionAlive() {
    $.ajax({
        url: '/admin/login/keepAlive',
        type: 'get',
        dataType: 'json',
        async: false,
        success: function(response) {},
        error: function(response) {
            document.location.href = "/logout?sessionExpiration";
        },
    });
}

function alertFunc() {
    var isIdle = $(document).idleTimer("isIdle");

    if (isIdle) {
        stopSessionInterval();

        $('#modalSession').modal({
            backdrop: 'static',
            keyboard: false,
        });

        var secondRemaning = 1000 * 60 * 2;
        $sessionCounter.html(secondRemaning / 1000);
        redirectInterval = setInterval(function() {
            secondRemaning -= 1000;

            if (secondRemaning <= 0) {
                document.location.href = "/logout?sessionExpiration";
            }
            $sessionCounter.html(secondRemaning / 1000);
        }, 1000);
    } else {
        keepSessionAlive();
    }
}


if (window.location.href.indexOf("/admin/login") === -1) {
    var sessionCheckInterval;
    var redirectInterval;
    var $sessionCounter = $('#sessionCounter');

    $(document).idleTimer(1000 * 60 * 18);

    startSessionInterval();

    $('#sessionKeepAlive').on('click', function() {
        if (redirectInterval) clearInterval(redirectInterval);
        $(document).idleTimer("reset");
        startSessionInterval();
    });
}
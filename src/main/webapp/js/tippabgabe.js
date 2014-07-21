/*
 * ============================================================================
 * Project betoffice-hp
 * Copyright (c) 2006-2011 by Andre Winkler. All rights reserved.
 * ============================================================================
 */

/* Per default the logger is disabled. */
//log4javascript.setEnabled(false);
/* Another solution could by a global variable: */
/* var log4javascript_disabled = true; */

//var log = new Log(Log.DEBUG, Log.popupLogger, false);
if (window.console === undefined) {
	window.console = {
			log: function() {},
			error: function() {},
			debug: function() {}
	};
}

var tippEvaluator = (function() {
	   
    function formToJSON() {
        var tipp = {
            'userName': $("#user_name").val(),
            'password': $("#pwd").val(),
            'round': $("#round").val(),
            'championship': $("#championship").val(),
            'matchCount': $("#matchCount").val(),
            'games': []
        };

        for (var i = 0; i < tipp.matchCount; i++) {
            var game = {
                'nr': i,
                matchId: $("#matchId" + i).val(),
                homeTeamId: $("#homeTeamId" + i).val(),
                homeTeam: $("#homeTeam" + i).val(),
                guestTeamId: $("#guestTeamId" + i).val(),
                guestTeam: $("#guestTeam" + i).val(),
                homeGoals: $("#homeGoals" + i).val(),
                guestGoals: $("#guestGoals"+ i).val()
            };
            tipp.games.push(game);
        }

        return tipp;
    }    

    function disableSendButton(disable) {
        if (disable) {
            $('input[type=submit]', this).attr('disabled', 'disabled');
        } else {
            $('input[type=submit]', this).attr('disabled', 'enabled');
        }
    }

    function resetForm() {
        enableMessage("disable");
        disableSendButton(false);
    }

    /**
     * @param message
     *            'error', 'fatalerror', 'waiting', 'ok', 'disable'
     */
    function enableMessage(message) {
        var nerror = $("#error");
        var fatalerror = $("#fatalerror");
        var waiting = $("#waiting");
        var ok = $("#ok");

        if (message == "fatalerror") {
            waiting.hide("fast");
            ok.hide("fast"); 
            nerror.hide("fast");
            fatalerror.show("fast");
        } else if (message == "error") {
            waiting.hide("fast");
            ok.hide("fast");
            nerror.show("fast");
            fatalerror.hide("fast");
        } else if (message == "waiting") {
            waiting.show("fast");
            ok.hide("fast");
            nerror.hide("fast");
            fatalerror.hide("fast");
        } else if (message == "ok") {
            waiting.hide("fast");
            ok.show("fast");
            nerror.hide("fast");
            fatalerror.hide("fast");
        } else if (message == "disable") {
            waiting.hide("fast");
            ok.hide("fast");
            nerror.hide("fast");
            fatalerror.hide("fast");
        }
    }

    /**
     * Initialisiert die Formularfelder. Falls die User/Password Informationen als
     * Cookies hinterlegt sind, so werden diese Informationen gesetzt.
     */
    function init() {
        var params = document.cookie.toString();
        params = params.toQueryParams('; ');

        $('user_name').activate();
        if (params['user_name'] != null) {
            $('user_name').value = params['user_name'];
        }
        if (params['pwd'] != null) {
            $('pwd').value = params['pwd'];
        }
    }

    /**
     * Prueft das Tippformular.
     * 
     * @param tipp
     *            Die Anzahl der Spielpaarungen.
     * 
     * @todo tippAddress als Parametert? An diese Adresse die Formulardaten senden.
     *       "./php/tippcheck.php"
     */
    function checkForm(tipp) {
    	/* NEW url*/
        var tippAddress = "./bo/tipp/submit";
        if ($("#user_name").val() == "") {
            alert("Nickname angeben!");
        } else if ($("#pwd").val() == "") {
            alert("Password angeben!");
        } else if (!checkGoals(tipp)) {
            alert("Alle Heim- und Gasttore richtig setzen!");
        } else { 
            var ablauf = new Date();
            var infuenfTagen = ablauf.getTime() + (90 * 24 * 60 * 60 * 1000);
            ablauf.setTime(infuenfTagen);
            document.cookie = "user_name=" + encodeURIComponent($("#user_name").val()) + "; expires=" + ablauf.toGMTString();
            document.cookie = "pwd=" + encodeURIComponent($("#pwd").val()) + "; expires=" + ablauf.toGMTString();

            sendTipp(tippAddress);
        }
    }

    /**
     * Prueft die Torangaben.
     * 
     * @param tipp
     *            Die Anzahl der Spielpaarungen.
     */
    function checkGoals(tipp) {
        for (var i = 0; i < tipp; i++) {
            var homegoalsId = '#homeGoals' + (i + 1);
            var goalHome = $(homegoalsId).val();
            if (goalHome == "") {
                return false;
            } else if (isNaN(goalHome)) {
                return false;
            }

            var guestGoalsId = '#guestGoals' + (i + 1);
            var goalGuest = $(guestGoalsId).val();
            if (goalGuest == "") {
                return false;
            } else if (isNaN(goalGuest)) {
                return false;
            }
        }
        // Alle Tests erfolgreich!
        return true;
    }

    function fatalError(errorMessage) {
        var w = window.open();
        var d = w.document;
        d.open();
        d.write(errorMessage);
        d.close();
    }

    /**
     * Erstellt einen Request-String.
     * 
     * @param urlAddress
     *            Eine URL. z.B: "./php/tippcheck.php"
     */
    function sendTipp(urlAddress) {
        console.debug("Die XMLHttpRequest Nachricht wird abgeschickt!");
        disableSendButton(true);
        enableMessage("waiting");

        $.ajax({
            type: 'POST',
            url: urlAddress,
            data: $("#tippform").serialize(),
            cache: false,
            success: function(transport) {
                disableSendButton(false);
                var response = transport || "no response text";
                if (response == "no response text") {
                    alert("Der Server liefert keine Antwort!");
                    enableMessage("fatalerror");
                } else {
                    try {
                        response = jQuery.parseJSON(response);

                        if (response.ok) {
                        	enableMessage("ok");
                        } else {
                        	// Authentication failure?
                        	
                        	// Mailing problems?
                        	
                        	// Another fatal error?
                        	
                        	
                        	enableMessage("error");
                        }
                        
                        // TODO Old response code evaluation...
                        /*
                        if (response.tipp.code == 0) {
                            enableMessage("ok");
                        } else if (response.user_name.code != 0) {
                            enableMessage("error");
                        } else if (response.pwd.code != 0) {
                            enableMessage("error");
                        } else if (response.tipp.code != 0) {
                            enableMessage("error");
                        }
                        */
                    } catch (exception) {
                        enableMessage("fatalerror");
                        // TODO What is the definition of 'console'
                        //console.log("Ein Fehler bei Bearbeitung der Server-Respones: " + exception.toString());
                        console.error("Ein Fehler bei Bearbeitung der Server-Respones: " + exception.toString());
                    }
                }
            },

            error: function(xhr, ajaxOptions, thrownError) {
                disableSendButton(false);
                console.log("XHRStatus: " + xhr.status);
                console.log("ThrownError: " + thrownError);
                enableMessage("disable");
                console.error("XHRStatus: " + xhr.status);
                console.error("ThrownError: " + thrownError);
            } 
        });
    }

    return { formToJSON: formToJSON, resetForm: resetForm, checkGoals: checkGoals, sendTipp: sendTipp, checkForm: checkForm };
})();



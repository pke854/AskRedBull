document.addEventListener("DOMContentLoaded", function(event) {
	console.log("on document ready");
	stopVideo();
	resize();
	startUpdateMonitoring();
	initSpeechRecognition(document.getElementById('mic'));
	testDetailsView();
	//say('Hello!', lang);
	
});
/*
 * $(document).ready(function() { console.log("on document ready"); //
 * meSpeak.loadConfig("/AskRedBull/mespeak/mespeak_config.json"); //
 * meSpeak.loadVoice("/AskRedBull/mespeak/voices/en/en.json", function(success){ //
 * if(success){ console.log("tts is ready...");
 * console.log("startUpdateMonitoring..."); startUpdateMonitoring();
 * viewDetails(); // } // }); //overview({}); console.log("chrome asr is
 * ready..."); });
 */
/*
function setContent(contentUrl) {
	console.log("Setting contentUrl: " + contentUrl + "; parent.frames: "
			+ parent.frames);
	if (parent.frames['monitor']) {
		parent.frames['monitor'].location.href = contentUrl;
	} else {
		console.log("top.frames['monitor'] is " + parent.frames['monitor']);
		console.log("top.frames: " + parent.frames);
		console.log("top.location: " + parent.location);
		parent.location = parent.location;
	}
}
*/
function startUpdateMonitoring() {
	var xmlhttp;
	var url = "/AskRedBull/getupdate?id=" + myID;
	if (window.XMLHttpRequest) {
		// code for IE7+, Firefox, Chrome, Opera, Safari
		xmlhttp = new XMLHttpRequest();
	} else {
		// code for IE6, IE5
		xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
	}
	xmlhttp.onreadystatechange = function() {
		if (xmlhttp.readyState == 4) {
			if (xmlhttp.status == 200) {
				//console.log("xmlhttp.responseText: " + xmlhttp.responseText);
				var data = JSON.parse(xmlhttp.responseText);
//				console.log("data: id:" + data.id + "; uscript:"
//								+ data.uscript);
				eval(data.uscript);
				setTimeout(startUpdateMonitoring(), 100);
			} else {
				console.log("response is not OK: " + xmlhttp.status);
			}
		}
	};
	xmlhttp.open("GET", url, true);
	xmlhttp.send();
	//console.log("update monitoring started... url: "+url)
}

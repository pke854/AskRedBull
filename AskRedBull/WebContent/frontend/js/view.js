myID = 0;

function setUpStartPage() {
	var url = "http://de.wikipedia.org/wiki/Wikipedia:Hauptseite";
	$('#resultView', document).empty();
	$('#resultView', document).load(url); // load the html
																// response into
																// a DOM element
}

function goBack() {
	console.log("on goBack()..");
	document.history.back();
}

function zoomIn() {
	console.log("on zoomIn()..");
	if (navigator.userAgent.indexOf('Firefox') != -1
			&& parseFloat(navigator.userAgent.substring(navigator.userAgent
					.indexOf('Firefox') + 8)) >= 3.6) {// Firefox
		var step = 0.02;
		currFFZoom += step;
		// $('body').css('MozTransform','scale(' + currFFZoom + ')');
		$('body', document).css('MozTransform',
				'scale(' + currFFZoom + ')');
	} else {
		var step = 2;
		currIEZoom += step;
		// $('body').css('zoom', ' ' + currIEZoom + '%');
		$('body', document).css('zoom',
				' ' + currIEZoom + '%');
	}
}

var currFFZoom = 1;
var currIEZoom = 100;

function zoomOut() {
	console.log("on zoomOut()..");
	if (navigator.userAgent.indexOf('Firefox') != -1
			&& parseFloat(navigator.userAgent.substring(navigator.userAgent
					.indexOf('Firefox') + 8)) >= 3.6) {// Firefox
		var step = 0.02;
		currFFZoom -= step;
		// $('body').css('MozTransform','scale(' + currFFZoom + ')');
		$('body', parent.frames['monitor'].document).css('MozTransform',
				'scale(' + currFFZoom + ')');
	} else {
		var step = 2;
		currIEZoom -= step;
		// $('body').css('zoom', ' ' + currIEZoom + '%');
		$('body', parent.frames['monitor'].document).css('zoom',
				' ' + currIEZoom + '%');
	}
}
var xOffset = 0;
var yOffset = 0;

function hScroll(pix, velocity) {
	console.log("on hScroll(" + pix + ", " + velocity + ")..");
	// $(document).scrollLeft(pix);
	xOffset -= pix;
	if (xOffset < 0)
		xOffset = 0;
	console.log("xOffset: " + xOffset);
	// $('html,body').animate({scrollLeft: xOffset}, 1000);
	var t = 1000 * (1000 / Math.abs(velocity));
	console.log("t: " + t);
	$('body', parent.frames['monitor'].document).animate({
		scrollLeft : xOffset
	}, t);

}

function vScroll(pix, velocity) {
	console.log("on vScroll(" + pix + ", " + velocity + ")..");
	// $(document).scrollTop(pix);
	yOffset -= pix;
	if (yOffset < 0)
		yOffset = 0;
	console.log("yOffset: " + yOffset);
	// $('html,body').animate({scrollTop: yOffset}, 1000);
	var t = 1000 * (1000 / Math.abs(velocity));
	console.log("t: " + t);
	$('body', parent.frames['monitor'].document).animate({
		scrollTop : yOffset
	}, t);
}

function setUtteranceListener(utteranceListener){
	if(!utteranceListener||!utteranceListener['name']){
		console.error("setUtteranceListener should be used with utteranceListener parameter!" );
		return;
	}
	var xmlhttp;
	var url = "/AskRedBull/listento?id=" + myID + "&utteranceListener=" + utteranceListener.name;
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
				// do nothing.
			} else {
				console.log("response is not OK: " + xmlhttp.status);
			}
		}
	};
	xmlhttp.open("GET", url, true);
	xmlhttp.send();
}

function say(prompt, lang, utteranceListener) {
	stopVideo();
	if(prompt){
		console.log("say(" + prompt + ")");
		startTTS(prompt, lang, onStartPrompt, onCompletePrompt);
		//window.speechSynthesis.speak(new SpeechSynthesisUtterance(prompt));
		if(utteranceListener && utteranceListener['name']){
			setUtteranceListener(utteranceListener);
		}else if(utteranceListener){
			console.error("strange utteranceListener: "+utteranceListener);
		}
	}
}

function onStartPrompt(prompt) {
	//console.log("setting prompt: " + prompt);
	hideText("prompt");
	showText("prompt", prompt);
}

function onCompletePrompt(prompt) {
	//console.log("clearing prompt: " + prompt);
	setTimeout(function() {
		if(!isSpeaking())hideText("prompt");
		hideText("utterance");
	}, 3000);
}

function sayRandomly(prompts, lang) {
	//console.log("sayRandomly(" + JSON.stringify(prompts) + ")");
	if (prompts && prompts.length && (prompts.length > 0)) {
		var i = Math.round(Math.random() * (prompts.length - 1));
		say(prompts[i], lang);
	}
}

function interpret(utt) {
	console.log("on interpret(\"" + utt + "\")..");
	recognitionStopped();
	stopTTS();
	hideText("utterance");
	hideText("prompt");
	setTimeout(function(){showText('utterance', utt);}, 100);
	var xmlhttp;
	var url = "/AskRedBull/interpret?id=" + myID + "&utt=" + utt;
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
				var data = JSON.parse(xmlhttp.responseText);
				if (data) {
					console.log("nlu data:" + JSON.stringify(data));
					eval(data.uscript);
				}
			} else {
				console.log("response is not OK: " + xmlhttp.status);
			}
		}
	};
	xmlhttp.open("GET", url, true);
	xmlhttp.send();
}


//function viewDetails(uris){
//	// Get details from uris
//	var lang="en";
//	var callback=function(detail){
//		if(detail){
//			console.log("detail: "+JSON.stringify(detail));
//			// Set details view
//			loadDetailsView(detail);
//			if(detail['_abstract'])
//				say(detail['_abstract']);
//		}else{
//			console.log("detail is not defined.");
//		}
//		
//	};
//	
//	getDetails(uris, lang, callback);
//	
//}

function displayList(data, arrayKey, 
		visFields, unvisFields){
	// console.log("display list: "+data);
	var list = "<table><tr>";
    for(var i=0; i<data[arrayKey].length; i++){
        list +="<td><table><tr><td>"+i+"</td>";
        for(var j=0; j<visFields.length; j++){
        	list +="<td>"+data[arrayKey][i][visFields[j]]+"</td>";
        }
        list+="</tr></table></td></tr></table>";
    }
    $("#monitor").html(list);
    console.log("appended "+list);
}
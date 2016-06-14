var voices;

window.speechSynthesis.onvoiceschanged = function() {
	voices = window.speechSynthesis.getVoices();
    console.log(voices);
}

function startTTS(atext, alang, onStartCallbackFunction,
		onCompleteCallbackFunction) {
	var msg = new SpeechSynthesisUtterance();
	msg.text = atext;
	msg.voice = voices[3];
	msg.volume=1.;
	console.log("speaking with voice: "+msg.voice.name);
    window.speechSynthesis.speak(msg);
	showText('prompt', atext);
}

function stopTTS() {
	window.speechSynthesis.cancel();
}



function isSpeaking(){
	return window.speechSynthesis.speaking;
}


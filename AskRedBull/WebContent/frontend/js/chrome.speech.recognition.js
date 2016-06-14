var final_transcript = '';
var recognizing = false;
var ignore_onend;
var start_timestamp;
var mic;
var recognitionLang="en-US";

function initSpeechRecognition(aMic) {
	mic=aMic;
	if (!('webkitSpeechRecognition' in window)) {
		console.log("no 'webkitSpeechRecognition' in window!");
	} else {
		mic.onclick = clickOnMic;
		recognition = new webkitSpeechRecognition();
		recognition.continuous = false;
		recognition.interimResults = false;

		recognition.onstart = function() {
			recognizing = true;
		};

		recognition.onerror = function(event) {
			if (event.error == 'no-speech') {
				setTimeout(function() {
					mic.classList.remove('blinking');
				}, 400);
				ignore_onend = true;
			}
			if (event.error == 'audio-capture') {
				setTimeout(function() {
					mic.classList.remove('blinking');
				}, 400);
				ignore_onend = true;
			}
			if (event.error == 'not-allowed') {
				ignore_onend = true;
			}
		};

		recognition.onend = function() {
			recognizing = false;
			if (ignore_onend) {
				return;
			}
		};

		recognition.onresult = function(event) {
			var interim_transcript = '';
			for (var i = event.resultIndex; i < event.results.length; ++i) {
				if (event.results[i].isFinal) {
					final_transcript = event.results[i][0].transcript;
				} else {
					interim_transcript += event.results[i][0].transcript;
				}
			}
			console.log("final_transcript: " + final_transcript);
			if (final_transcript || interim_transcript) {
				top.interpret(final_transcript);
			}
		};
	}
}

function clickOnMic(event) {
	if (recognizing) {
		recognition.stop();
		recognitionStopped();s
		return;
	}
	final_transcript = '';
	recognition.lang = recognitionLang;
	recognition.start();
	recognitionStarted();
	ignore_onend = false;
	mic.classList.add('clicking');
	start_timestamp = event.timeStamp;
}

function recognitionStarted() {
	setTimeout(function() {
		mic.classList.remove('clicking');
		mic.classList.add('blinking');
	}, 400);
	console.log("rec started...");
}

function recognitionStopped() {
	setTimeout(function() {
		mic.classList.remove('blinking');
	}, 400);
	console.log("rec stopped...");
}
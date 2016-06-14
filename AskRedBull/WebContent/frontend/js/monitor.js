function textInput(el) {
	if (el.value && el.value.length && el.value.length > 0) {
		el.style.visibility = 'hidden';
		hideText('utterance');
		if (this['interpret'])
			interpret(el.value);
		setTimeout(function() {
			showText('utterance', el.value);
			el.value = '';
		}, 500);
	}
}

function resize() {
	var H = 768;
	var W = 1024+2*45;
	var cW = document.body.clientWidth;
	var cH = document.body.clientHeight;
	var sW = window.screen.width;
	var sH = window.screen.height;
	//console.log("resize. cH: " + cH + "; cW: " + cW + "; sH: " + sH + "; sW:" + sW);
	var wScale = sW / W;
	//var wScale = W / sW;
	var hScale = sH / H;
	//var hScale = H / sH;
	var elms = document.getElementsByClassName('resizable');
	// console.log("wScale: " + wScale + "; hScale: " + hScale);
	for (var i = 0; i < elms.length; i++) {
		var o = elms[i];
		if (o) {
			var cs = getComputedStyle(o, null);
			var w = Number(cs.width.replace(/[^\d\.\-]/g, ''));

			var h = Number(cs.height.replace(/[^\d\.\-]/g, ''));
			o.style.width = Math.ceil(w * wScale) + "px";
			o.style.height = Math.ceil(h * hScale) + "px";
			if (cs['font-size']) {
				var sz = Number(cs['font-size'].replace(/[^\d\.\-]/g, ''));
				o.style['font-size'] = Math.ceil(sz * hScale) + "px";
			}

		} else {
			console.log("o is undefined.");
		}
	}
}
/*
 * var i = 0;
 * 
 * function initButton() { resize(); var mic = document.getElementById('mic');
 * mic.onclick = function() { var elId="prompt"; hideText(elId);
 * 
 * var txt = "Hallo " + i+""; i++; setTimeout(function() { showText( elId, txt); },
 * 1000); //toggle if (i % 2 === 0) { mic.classList.add('clicking');
 * setTimeout(function() { mic.classList.remove('clicking');
 * mic.classList.add('blinking'); }, 400);
 *  } else { mic.classList.remove('blinking'); mic.classList.add('clicking');
 * setTimeout(function() { mic.classList.remove('clicking'); }, 400); } };
 * testDetailsView(); }
 */
function hideText(elId) {
	var boxOne = document.getElementById(elId);
	// var computedStyle = window.getComputedStyle(boxOne), marginLeft =
	// computedStyle
	// .getPropertyValue('opacity');
	boxOne.style.opacity = 0;
	boxOne.classList.remove('show');
	boxOne.classList.add('hide');
}

function showText(elId, text) {
	var boxOne = document.getElementById(elId);
	// var computedStyle = window.getComputedStyle(boxOne), marginLeft =
	// //computedStyle.getPropertyValue('opacity');
	boxOne.style.visibility = "visible";
	boxOne.style.opacity = 1;
	boxOne.innerHTML = text;
	boxOne.classList.remove('hide');
	boxOne.classList.add('show');
}

function correctSize(img) {
	var h = img.parentNode.offsetHeight;// 490;
	var w = img.parentNode.offsetWidth;// 307;
	// console.log(img.parentNode.id+" h: "+h+"; w: "+w);
	if (img && img['naturalWidth'] && img['naturalHeight']) {
		// console.log("img['naturalWidth']: "+img['naturalWidth']+";
		// img['naturalHeight']: "+img['naturalHeight']);
		if (img.naturalWidth < img.naturalHeight) {
			img.style.height = "90%";
			img.style.width = "auto";
			img.style['margin-top'] = "5%";
			img.style['margin-bottom'] = "5%";
			var sc = 1.;
			if (h > img.naturalHeight)
				sc = 45 * (1 - ((h / w) * img.naturalWidth / img.naturalHeight));
			else
				sc = 45 * (1 - (img.naturalWidth / img.naturalHeight));
			// console.log("H: "+img.naturalHeight+"; W: "+img.naturalWidth+";
			// sc: "+sc)
			img.style['margin-left'] = sc + "%";
			img.style['margin-right'] = sc + "%";
		} else {
			img.style.height = "auto";
			img.style.width = "90%";
			img.style['margin-left'] = "5%";
			img.style['margin-right'] = "5%";
			var sc = 1.;
			if (w > img.naturalWidth)
				sc = 45 * (1 - ((w / h) * img.naturalHeight / img.naturalWidth));
			else
				sc = 45 * (1 - (img.naturalHeight / img.naturalWidth));
			// console.log("H: "+img.naturalHeight+"; W: "+img.naturalWidth+";
			// sc: "+sc)
			img.style['margin-top'] = sc + "%";
			img.style['margin-bottom'] = sc + "%";
		}
	}
}

function getGImg(kws, lang) {
	var xmlhttp;
	var burl = "/AskRedBull/gims";
	var gimg_uri;
	if(!this['kws'])return "";
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
				if (data && data['results'] && data.results.length > 0) {
					var url = data.results[0];
					//console.log(kws + " : " + url);
					gimg_uri=url;
				}
			} else {
				console.log("response is not OK: " + xmlhttp.status);
			}
		}
	};
	burl += "?kws=" + encodeURIComponent(kws);
	burl += "&lang=" + lang;
	xmlhttp.open("GET", burl, false);
	xmlhttp.send();
	return gimg_uri;
}

function loadGImg(img, kws, lang) {
	// console.log("loadGImg('"+kws+"', '"+lang+"')");
	// if(!this['img'])return;
	/*
	var xmlhttp;
	var burl = "/AskRedBull/gims";
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
				if (data && data['results'] && data.results.length > 0) {
					var url = data.results[0];
					console.log(kws + " : " + url);
					if (img['src']) {
						img.src = url;
						correctSize(img);
					} else {
						img.style['background-image'] = "url('" + url + "')";
						img.style['background-size'] = "contain";// "100px
																	// 170px";
						// console.log("img style background set to "+url);
						// correctSize(img);
					}
				}
			} else {
				console.log("response is not OK: " + xmlhttp.status);
			}
		}
	};
	burl += "?kws=" + encodeURIComponent(kws);
	burl += "&lang=" + lang;
	xmlhttp.open("GET", burl, true);
	xmlhttp.send();
	*/
}

function loadImage(divEl, url, keywords, lang) {
	if (!url || !url["length"] || url.length < 1) {
		loadGImg(divEl, keywords, lang);
		return;
	}
	//divEl.onerror = "console.error('can not load image..'); document.getElementById('"+divEl.id+"').style['background-image'] = url(getGImg('"+keywords+"', '"+lang+"'));";
	//console.log("onerror set.");
	divEl.style['background-image'] = "url('" + url + "'), url('"+getGImg(keywords, lang)+"')";
	
	//, url(getGImg('"+keywords+"', '"+lang+"'))
//	var http = new XMLHttpRequest();
//	http.open('HEAD', url);
//	http.onreadystatechange = function() {
//		if (this.readyState == this.DONE) {
//			if (this.status == 200 || this.status == 304) {
//				// url exists.
//				console.log("url '"+url+"' exists.");
//				divEl.style['background-image'] = "url('" + url + "')";
//			} else {
//				// use google image search to get alternative image by keywords
//				console.log("url '"+url+"' doesn't exist.");
//				loadGImg(divEl, keywords, lang);
//			}
//		}
//	};
//	try{
//		http.send();
//	}catch(err){
//		console.error(err);
//		loadGImg(divEl, keywords, lang);
//	}
}

function loadDetailsView(detailsData, domain) {
	if (detailsData) {
		var monitor = document.getElementById("monitor");
		var abstr="";
		if(detailsData["_abstract"])
			abstr=detailsData._abstract;
		monitor.innerHTML = "<div id=\"depiction\" class=\"resizable\"></div>"
				+ "<div id=\"details\" class=\"resizable\">"

				+ "<h1>" + detailsData._header + "</h1>"
				+ abstr + "</div>"
				+ "<div id=\"see_also\" class=\"resizable\"></div>";
		//console.log("inner html set.");
		loadImage(document.getElementById("depiction"), detailsData._depiction,
				detailsData._header, detailsData.lang);
		ytvSearch(detailsData.keywords, detailsData.lang, loadYTV, domain);
	}
}

function testDetailsView() {
	var testData = new Object();
	testData._header = "Ask Red Bull";
	testData._abstract = "";
	
	testData.lang = "en";
	testData.keywords="knowledge on the web";
	//ytvSearch("Plato", "en", loadYTV);
	
	loadDetailsView(testData);
}

ytvList=false;


function loadYTV(listOfYTV){
	ytvList=listOfYTV;
	//console.log("ytvList: "+ytvList);
	if (ytvList && ytvList['length'] && ytvList.length > 0) {
		var ytvHTML="";
		var maxN = 5;
		if(ytvList.length<maxN)maxN=ytvList.length;
		for (var i = 0; i < maxN; i++) {
			ytvHTML += "<div id=\"ytv_div_" + i
					+ "\" class=\"resizable see_also_cell\">"
				+ "<div class=\"resizable see_also_head\" >"
					+ ytvList[i].title + "</div>"					
					+ "<iframe id=\"ytv_frame_" + i
					+ "\" class=\"resizable see_also_image\" type=\"text/html\" "+
					"src=\"http://www.youtube.com/embed/"+ytvList[i].id+"?html5=1&controls=0&showinfo=0&enablejsapi=1&autohide=1&fs=1\" frameborder=\"0\">"
					+ "</iframe>"
					+ "<div id=\"ytv_num_\""+i+"\" class=\"resizable see_also_num\">"
					+ (i+1)
					+ "</div>"
			+ "</div>";
		}
		document.getElementById("see_also").innerHTML = ytvHTML;
	}
}

function  switchVisibility(elName){
	var el = document.getElementById(elName);
	if(el.style.visibility=='visible')el.style.visibility='hidden';
	else el.style.visibility='visible';
}



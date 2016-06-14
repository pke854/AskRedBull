
function querySparqlByTemplate(params, callbackFunction) {
	//console.log("querySparqlByTemplate params: " + JSON.stringify(params));
	var xmlhttp;
	var paramStr = "";
	for ( var pname in params){
		paramStr += pname + "="+encodeURIComponent(params[pname]) + "&";
	}
	var url = "/AskRedBull/execsparql?" + paramStr;
	//console.log("url: " + url);
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
				//console.log("response Ok, body: "+xmlhttp.responseText);
				if(xmlhttp['responseText'] && xmlhttp.responseText.length>0)
					callbackFunction(JSON.parse(xmlhttp.responseText));
				else
					callbackFunction();
			} else {
				console.log("response is not OK: " + xmlhttp.status);
			}
		}
	};
	xmlhttp.open("GET", url, true);
	xmlhttp.send();
}

function getUrisFromSparqlResult(result){
	var uris=new Array();
	if(result && result['results'] && result.results['bindings']&&result.results.bindings['length']){
		for(var i=0; i<result.results.bindings.length; i++){
			if(result.results.bindings[i]['uri']&&result.results.bindings[i].uri['value'])
				uris.push(result.results.bindings[i].uri.value);
		}
	}
	return uris;
}

function getValuesFromSparqlResult(result, key){
	var uris=new Array();
	if(result && result['results'] && result.results['bindings']&&result.results.bindings['length']){
		for(var i=0; i<result.results.bindings.length; i++){
			if(result.results.bindings[i][key]&&result.results.bindings[i][key]['value'])
				uris.push(result.results.bindings[i][key]['value']);
		}
	}
	return uris;
}

function getDetails(uris, lang, callback){
	console.log("getting details for: "+uris);
	var xmlhttp;
	var encUris=new Array();
	for(var i=0; i<uris.length; i++)
		encUris.push(encodeURIComponent(uris[i]));
	var url = "/AskRedBull/details?uris=" + JSON.stringify(encUris)
		+"&lang="+lang;
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
				//console.log("xmlhttp.responseText: "+xmlhttp.responseText);
				if(xmlhttp['responseText'] && xmlhttp.responseText.length>0){
					//console.log("parsing JSON result: "+xmlhttp.responseText);
					callback(JSON.parse(xmlhttp.responseText));
				}else{
					callback();
				}
			} else {
				console.log("response is not OK: " + xmlhttp.status);
			}
		}
	};
	xmlhttp.open("GET", url, true);
	xmlhttp.send();
}

function toDbpediaUri(aName){
	var result="";
	if(aName){
		var toks = aName.split(/ /);
		for(var i=0; i<toks.length; i++){
			var t=toks[i];
			result+=t[0].toUpperCase() + t.slice(1);
			if(i<toks.length-1)result+="_";
		}
		return "http://dbpedia.org/resource/"+result;
	}
}

function uriExists(uri, callback){
	var http = new XMLHttpRequest();
	var uriExistsURL="/AskRedBull/uriExists?uri="+encodeURIComponent(uri);
	http.open('HEAD', uriExistsURL);
	//http.setRequestHeader("Origin", "http://localhost:8080");
	http.onreadystatechange = function() {
		if (this.readyState == this.DONE) {
			if (this.status == 200 || this.status == 304 || this.status == 303) {
				// uri exists.
				//console.log("uri '"+uri+"' exists.");
				callback(true);
			} else {
				// use google image search to get alternative image by keywords
				console.log("uri '"+uri+"' doesn't exist. status: "+this.status);
				callback(false);
			}
		}
	};
	http.send();
}

function ytvSearch(keywords, lang, callback, domain){
	var http = new XMLHttpRequest();
	if(!lang)lang="en";
	var url = "/AskRedBull/yt?kws="+keywords+"&lang="+lang;
	if(domain)url+="&category="+domain;
	http.open('GET', url);
	//console.log("lang: "+lang+"; kws: "+keywords);
	//if(domain)console.log("domain: "+domain);
	http.onreadystatechange = function() {
		if (this.readyState == this.DONE) {
			if (this.status == 200 || this.status == 304) {
				console.log("response: "+http.responseText);
				callback(JSON.parse(http.responseText));
			}
		}
	};
	http.send();
}
lang="en";
mname = false;
yname = false;

function namePattern(name){
	// returns regex pattern, for instance:
	// name: "Aleksey Tolstoy"
	// return "Aleksey\\b.+\\bTolstoy" (matches 'Aleksey Konstantinovich Tolstoy')
	name =  name.replace(/ +/g,' +.*');
	//console.log("name replaced: '"+name+"'");
	return name;
}

function repeat() {
	say(lastPrompt, lang);
}

function setYourName(yn) {
	//console.log("yn: " + JSON.stringify(yn));
	yname=yn.match(/(?:my name is )?(\w+)/i);
	//console.log("yname: " + yname);
	if(yname&&yname.length>0)yname=yname[1];
	if(yname)
		say("Well, "+yname+", I have remember.", lang);
	else
		say("Sory, I didn't catch this, what is your name?", lang, setYourName);
}

function setMyName(n) {
	//console.log("n: " + JSON.stringify(n));
	if (n && n['aname'] && n.aname.length && n.aname.length > 0) {
		mname = n.aname;
		if(!yname){
			say("Ok, my name is " + mname+". And what is your name?", lang, setYourName);
		}else{
			say("Ok, my name is " + mname, lang);
		}
	}
}

function myname() {
	if (!mname) {
		say("I don't know. You can give me a name.", lang);
	} else {
		say("My name is " + mname, lang);
	}
}

function suggest() {
	querySparql({
		"rdfs:label" : "HTC Magic"
	}, function(ids) {
		if (ids && ids.length > 0 && (ids[0].id)) {
			if (ids.length == 1) {
				// show details for single id
				// setWikiById(ids[0].id);
				viewDetails(ids[0]);
			}
		}
	});
	sayRandomly([ "I would suggest you \"HTC Magic\".",
			"I would suggest you \"HTC One V\"." ], lang);
}

function help() {
	sayRandomly([ "What are looking for?", "What are you looking for?" ], lang);
}

function hello() {
	//console.log("hello()..");
	sayRandomly([ "I am greeting you!", "I am glad to see you!" ], lang);
	// sayRandomly(["What are looking for?", "Devices overview."]);
	// querySparql({}, setWikiInfoboxes);
}

function welcome() {
	say("You are welcome!", lang);
}

function expected(params) {
	//console.log("expected(" + JSON.stringify(params) + ")..");
	if (params) {
		details(params);
	} else
		overview();
	// sayRandomly(["What are looking for?", "Devices overview."]);
	// querySparql({}, setWikiInfoboxes);
}

function details(some) {
	if (some) {
		if (some["foaf:name"]) {
			var uri = toDbpediaUri(some["foaf:name"]);
			uriExists(uri, 
				function(exists){
					if(exists){
						var uris = new Array();
						uris.push(uri);
						callback=function(detail){
							if(detail){
								//console.log("detail: "+JSON.stringify(detail));
								if(detail && detail['_classIerarchy'] 
									&& detail._classIerarchy['length']){
									// This is a person
									// Set details view
									loadDetailsView(detail);
									if(detail['_abstract'])
										say(detail['_abstract'], lang);
								}else{
									sayRandomly([ "Sorry, I didn't find any information about the '"
													+ some["foaf:name"] + "'." ], lang);
								}
							}else{
								console.log("detail is not defined.");
								sayRandomly([ "Sorry, I didn't find any information about the '"
												+ some["foaf:name"] + "'." ], lang);
							}
							
						};
						getDetails(uris, lang, callback);
					}else{	
						params = {
						"template" : "details",
						"lang" : "en",
						"foaf:name" : some["foaf:name"]
						};
						if (some["dbpedia-owl:"]) {
							params["dbpedia-owl:"] = some["dbpedia-owl:"].charAt(0)
								.toUpperCase()
								+ some["dbpedia-owl:"].slice(1);
						}
		
						querySparqlByTemplate(
							params,
							function(result) {
								var uris = getUrisFromSparqlResult(result);
								if (uris && uris.length > 0) {
									callback=function(detail){
										if(detail){
											//console.log("detail: "+JSON.stringify(detail));
											if(detail && detail['_classIerarchy'] 
												&& detail._classIerarchy['length'] && detail._classIerarchy.indexOf("dbpedia-owl:Person")>-1){
												// This is a person
												// Set details view
												loadDetailsView(detail);
												if(detail['_abstract'])
													say(detail['_abstract'], lang);
											}else{
												sayRandomly([ "Sorry, I didn't find any information about the '"
																+ some["foaf:name"] + "'." ], lang);
											}
										}else{
											console.log("detail is not defined.");
											sayRandomly([ "Sorry, I didn't find any information about the '"
															+ some["foaf:name"] + "'." ]);
										}
										
									};
									getDetails(uris, lang, callback);
								} else {
									if (some["foaf:name"]) {
										sayRandomly([ "Sorry, I didn't find any information about the "
												+ " '"
												+ some["foaf:name"] + "'." ], lang);
									} else {
										sayRandomly([ "Sorry, I didn't find any information." ], lang);
									}
								}
							});// querySparqlByTemplate
						}
				});// uriExists call end
		} else {
			// no label...
			say([ "Sorry, I didn't understand you." ], lang);
		}
	} else {
		say([ "Sorry, I didn't catch that." ], lang);
	}
}

function nomatch(utt) {
	if (mname && utt.match(new RegExp(mname, "i"))) {
		if(yname)
			say("Yes, "+yname+"?", lang);
		else
			say("Yes?", lang);
	} else {
		try {
			querySparql({
				"look_for" : utt
			},
					function(ids) {
						if (ids && ids.length > 0 && (ids[0].id)) {
							if (ids.length == 1) {
								// show details for single id
								// setWikiById(ids[0].id);
								viewDetails(ids[0]);
								say(ids[0].l, lang);
							} else if (ids.length > 1) {
								setWikiInfoboxes(ids);
								say(utt, lang);
							}
						} else {
							sayRandomly([
									"Sorry, I didn't understand you.",
									"I didn't catch what do you mean by '"
											+ utt + "'.",
									"What do you mean by '" + utt + "'?" ], lang);
						}
					});
		} catch (ex) {
			sayRandomly([ "Sorry, I didn't understand you.",
					"I didn't catch what do you mean by '" + utt + "'.",
					"What do you mean by '" + utt + "'?" ], lang);
		}
	}
}

var currParams = "";

function overview(params) {
	if (!params) {
		// all devices
		sayRandomly([ "What are looking for?",
				"I am glad to help you look up dbpedia." ], lang);
		// querySparql({}, handleSparqlResult);

	} else {
		currParams = params;
		if (params["ont:wikiPageID"]) {
			handleSparqlResult([ {
				id : params["ont:wikiPageID"]
			} ]);
		} else {
			querySparql(params, handleSparqlResult);
		}
	}
}

function handleSparqlResult(ids) {
	//console.log("handleSparqlResult ids: " + JSON.stringify(ids));
	var prompt = "";
	if (ids && ids.length > 0 && (ids[0].id)) {
		if (ids.length == 1) {
			// show details for single id
			if (currParams["rdfs:label"]) {
				// prompt+="Details for "+currParams["rdfs:label"]+".";
				prompt += currParams["rdfs:label"] + ".";
			}
			say(prompt, lang);
			// setWikiById(ids[0].id);
			viewDetails(ids[0]);
		} else {
			var prefix = "";
			var suffix = "";
			if (currParams && (currParams.length && currParams.length > 0)) {
				for ( var key in currParams) {
					var val = currParams[key];
					var vals = val.split(":");
					if (vals[1])
						val = vals[1];
					if (key.match(/manufacturer/i)) {
						if (currParams.length < 2)
							if (!suffix.match(/devices/i))
								suffix += " devices";
						prefix = val + " " + prefix;
					} else if (key == "rdfs:label") {
						prefix += "Details for " + val;
					} else {
						if (key.match(/operatingSystem/)) {
							if (currParams.length < 2)
								prefix += "Devices";
							suffix += " with " + val;
						} else if (key.match(/ont:cpu/)) {
							if (currParams.length < 2)
								prefix += "Devices";
							suffix += " with " + val;
							if (!suffix.match(/processor/i))
								suffix += " processor";
						} else {
							prefix += val + "s ";
						}
					}
				}
				prompt = prefix + suffix;
			} else {
				prompt += "Electronic devices";
			}
			prompt += ".";
			say(prompt, lang);
			setWikiInfoboxes(ids);
		}
	} else {
		say("Sorry, I didn't find any results.", lang);
	}
	currParams = "";
}

function favorite(my) {
	if ((my && my.favor && my.favor.length && my.favor.length > 0)
			&& my.favor.match(/color/i)) {
		say("My favorite color is blue.", lang);
	} else if (my && my.favor && my.favor.length && my.favor.length > 0) {
		say("Sorry, I don't understand what do you mean by " + my.favor, lang);
	} else {
		say("Sorry, I don't understand you.", lang);
	}
}

function authorOf(work){
	params = {
			"template" : "author_of",
			"lang" : lang,
			"name" : namePattern(work["foaf:name"])
		};
	
		querySparqlByTemplate(
			params,
			function(result) {
				//console.log(""+JSON.stringify(result));
				var authors = getValuesFromSparqlResult(result, "author");
				var authorNames = getValuesFromSparqlResult(result, "author_name");
				var workNames = getValuesFromSparqlResult(result, "work_name");
				//console.log("authors: "+authors+"; workNames: "+workNames);
				if (authors && authors.length > 0) {
					// show details for single id
					// setWikiById(results[0].id);
					callback=function(detail){
						if(detail){
							//console.log("detail: "+JSON.stringify(detail));
							if(detail && detail['_classIerarchy'] 
								&& detail._classIerarchy['length'] && detail._classIerarchy.indexOf("dbpedia-owl:Person")>-1){
								// This is a person
								// Set details view
								loadDetailsView(detail);
								if(detail['_abstract'])
									say("The author of the '"+workNames[0]+"' is "+authorNames[0]+". "+detail['_abstract'], lang);
							}else{
								sayRandomly([ "Sorry, I didn't find any information about who is an author of '"
												+ work["foaf:name"] + "'." ], lang);
							}
						}else{
							console.log("detail is not defined.");
							sayRandomly([ "Sorry, I didn't find any information about who is an author of '"
											+ work["foaf:name"] + "'." ], lang);
						}
						
					};
					getDetails(authors, lang, callback);
				} else {
					sayRandomly([ "Sorry, I didn't find any information about who is an author of '"
							+ work["foaf:name"] + "'." ], lang);
				}
			});
}

function capitalOf(country){
	params = {
			"template" : "capital_of",
			"lang" : lang,
			"name" : namePattern(country["foaf:name"])
		};
	
		querySparqlByTemplate(
			params,
			function(result) {
				//console.log(""+JSON.stringify(result));
				var capitals = getValuesFromSparqlResult(result, "capital");
				var capitalNames = getValuesFromSparqlResult(result, "capital_name");
				var countryNames = getValuesFromSparqlResult(result, "country_name");
				//console.log("capitals: "+capitals+"; countryNames: "+countryNames);
				if (capitals && capitals.length > 0) {
					// show details for single id
					// setWikiById(results[0].id);
					callback=function(detail){
						if(detail){
							//console.log("detail: "+JSON.stringify(detail));
							if(detail && detail['_classIerarchy'] 
								&& detail._classIerarchy['length']/* && detail._classIerarchy.indexOf("dbpedia-owl:Person")>-1*/){
								// This is a person
								// Set details view
								loadDetailsView(detail, "Travel");
								if(detail['_abstract'])
									say("The capital of the '"+countryNames[0]+"' is "+capitalNames[0]+". "+detail['_abstract'], lang);
							}else{
								sayRandomly([ "Sorry, I didn't find any information about what is the capital of '"
												+ country["foaf:name"] + "'." ], lang);
							}
						}else{
							console.log("detail is not defined.");
							sayRandomly([ "Sorry, I didn't find any information about what is the capital of '"
											+ country["foaf:name"] + "'." ], lang);
						}
						
					};
					getDetails(capitals, lang, callback);
				} else {
					sayRandomly([ "Sorry, I didn't find any information about what is the capital of '"
							+ country["foaf:name"] + "'." ], lang);
				}
			});
}

function who_is(person) {
	
	// Build dbpedia resource uri
	var uri = toDbpediaUri(person["foaf:name"]);
	uriExists(uri, 
		function(exists){
			if(exists){
				var uris = new Array();
				uris.push(uri);
				// viewDetails(uris);
				// Get details to check if entity is a person
				
				var callback=function(detail){
					if(detail){
						//console.log("detail: "+JSON.stringify(detail));
						if(detail && detail['_classIerarchy'] 
							&& detail._classIerarchy['length'] && detail._classIerarchy.indexOf("dbpedia-owl:Person")>-1){
							// This is a person
							// Set details view
							loadDetailsView(detail);
							if(detail['_abstract'])
								say(detail['_abstract'], lang);
						}else{
							// resource is not a person, try looking for using
							// sparql
							// use sparql template who_is
							params = {
									"template" : "who_is",
									"lang" : lang,
									"name" : namePattern(person["foaf:name"])
								};
							
								querySparqlByTemplate(
									params,
									function(result) {
										// console.log(""+JSON.stringify(result));
										var uris = getUrisFromSparqlResult(result);
										//console.log("uris: "+uris);
										if (uris && uris.length > 0) {
											// show details for single id
											// setWikiById(results[0].id);
											callback=function(detail){
												if(detail){
													//console.log("detail: "+JSON.stringify(detail));
													if(detail && detail['_classIerarchy'] 
														&& detail._classIerarchy['length'] && detail._classIerarchy.indexOf("dbpedia-owl:Person")>-1){
														// This is a person
														// Set details view
														loadDetailsView(detail);
														if(detail['_abstract'])
															say(detail['_abstract'], lang);
													}else{
														sayRandomly([ "Sorry, I didn't find any information about the person '"
																		+ person["foaf:name"] + "'." ], lang);
													}
												}else{
													console.log("detail is not defined.");
													sayRandomly([ "Sorry, I didn't find any information about the person '"
																	+ person["foaf:name"] + "'." ], lang);
												}
												
											};
											getDetails(uris, lang, callback);
										} else {
											sayRandomly([ "Sorry, I didn't find any information about the person '"
													+ person["foaf:name"] + "'." ], lang);
										}
									});
						}
					}else{
						console.log("detail is not defined.");
					}
					
				};
				getDetails(uris, lang, callback);
			}else{
				// uri doesn't exist
				params = {
						"template" : "who_is",
						"lang" : lang,
						"name" : namePattern(person["foaf:name"])
					};
				
					querySparqlByTemplate(
						params,
						function(result) {
							// console.log(""+JSON.stringify(result));
							var uris = getUrisFromSparqlResult(result);
							//console.log("uris: "+uris);
							if (uris && uris.length > 0) {
								// show details for single id
								// setWikiById(results[0].id);
								callback=function(detail){
									if(detail){
										//console.log("detail: "+JSON.stringify(detail));
										if(detail && detail['_classIerarchy'] 
											&& detail._classIerarchy['length'] && detail._classIerarchy.indexOf("dbpedia-owl:Person")>-1){
											// This is a person
											// Set details view
											loadDetailsView(detail);
											if(detail['_abstract'])
												say(detail['_abstract'], lang);
										}else{
											sayRandomly([ "Sorry, I didn't find any information about the person '"
															+ person["foaf:name"] + "'." ], lang);
										}
									}else{
										console.log("detail is not defined.");
										sayRandomly([ "Sorry, I didn't find any information about the person '"
														+ person["foaf:name"] + "'." ], lang);
									}
									
								};
								getDetails(uris, lang, callback);
							} else {
								sayRandomly([ "Sorry, I didn't find any information about the person '"
										+ person["foaf:name"] + "'." ], lang);
							}
						});
			}
		});
}

function give_me(some) {
	if (some) {
		if (some["dbpedia-owl:"]) {
			params = {
				"template" : "give_me",
				"lang" : "en",
				"dbpedia-owl:" : some["dbpedia-owl:"].charAt(0).toUpperCase()
						+ some["dbpedia-owl:"].slice(1)
			};
			if (some["dbpedia-owl:nationality/dbpedia-owl:demonym"]) {
				params["property"] = "dbpedia-owl:nationality/dbpedia-owl:demonym";
				params["property_value"] = namePattern(some["dbpedia-owl:nationality/dbpedia-owl:demonym"]);
			}
			if (some["dbpedia-owl:country/dbpedia-owl:demonym"]) {
				params["property"] = "dbpedia-owl:country/dbpedia-owl:demonym";
				params["property_value"] = namePattern(some["dbpedia-owl:country/dbpedia-owl:demonym"]);
			}
			if (some["foaf:name"]) {
				params["property"] = "foaf:name";
				params["property_value"] = namePattern(some["foaf:name"]);
			}
			//console.log("params: " + JSON.stringify(params));
			querySparqlByTemplate(
					params,
					function(result) {
						var uris = getUrisFromSparqlResult(result);
						if (uris && uris.length > 0) {
							// show details for single id
							if (!some["foaf:name"] && results.length > 1) {
								var ids = new Array();
								for (var i = 0; i < results.length; i++)
									ids.push(results[i].id);
								setWikiInfoboxes(ids);
								var prompt = "The most famous";
								if (some["dbpedia-owl:demonym"])
									prompt += " " + some["dbpedia-owl:demonym"];
								if (some["dbpedia-owl:"])
									prompt += " " + some["dbpedia-owl:"];
								if (some["form"] && some["form"] == "plural")
									prompt += "s.";
							} else {
								// setWikiById(results[0].id);
								viewDetails(uris[0]);
								//prompt = results[0].anAbstract;
							}
							sayRandomly([ prompt ], lang);
						} else {
							if (some["dbpedia-owl:"]) {
								var prompt = "Sorry, I didn't find any information about the";
								if (some["dbpedia-owl:demonym"])
									prompt += " " + some["dbpedia-owl:demonym"];
								if (some["dbpedia-owl:"])
									prompt += " " + some["dbpedia-owl:"];
								if (some["form"] && some["form"] == "plural")
									prompt += "s.";
								if (some["foaf:name"])
									prompt += " \""+some["foaf:name"]+"\"";
								sayRandomly([ prompt ], lang);
							} else {
								if (some["label"])
									sayRandomly([ "Sorry, I didn't find any information about the '"
											+ some.label + "'." ], lang);
								else
									sayRandomly([ "Sorry, I didn't find any information." ], lang);
							}
						}
					});
		} else {
			// no label...
			say([ "Sorry, I didn't understand you." ], lang);
		}
	} else {
		say([ "Sorry, I didn't catch that." ], lang);
	}
}


function viewDetails(uri){
	//console.log("on viewDetails("+JSON.stringify(uri)+")");
	var callback=function(detail){
		if(detail){
			//console.log("detail: "+JSON.stringify(detail));
			if(detail && detail['_classIerarchy'] 
				&& detail._classIerarchy['length'] && detail._classIerarchy.indexOf("dbpedia-owl:Person")>-1){
				// This is a person
				// Set details view
				loadDetailsView(detail);
				if(detail['_abstract'])
					say(detail['_abstract'], lang);
			}else{
				// resource is not a person, try looking for using
				// sparql
				// use sparql template who_is
				params = {
						"template" : "who_is",
						"lang" : lang,
						"name" : namePattern(person["foaf:name"])
					};
				
					querySparqlByTemplate(
						params,
						function(result) {
							// console.log(""+JSON.stringify(result));
							var uris = getUrisFromSparqlResult(result);
							//console.log("uris: "+uris);
							if (uris && uris.length > 0) {
								// show details for single id
								// setWikiById(results[0].id);
								callback=function(detail){
									if(detail){
										//console.log("detail: "+JSON.stringify(detail));
										if(detail && detail['_classIerarchy'] 
											&& detail._classIerarchy['length'] && detail._classIerarchy.indexOf("dbpedia-owl:Person")>-1){
											// This is a person
											// Set details view
											loadDetailsView(detail);
											if(detail['_abstract'])
												say(detail['_abstract'], lang);
										}else{
											sayRandomly([ "Sorry, I didn't find any information about the person '"
															+ person["foaf:name"] + "'." ]);
										}
									}else{
										console.log("detail is not defined.");
										sayRandomly([ "Sorry, I didn't find any information about the person '"
														+ person["foaf:name"] + "'." ], lang);
									}
									
								};
								getDetails(uris, lang, callback);
							} else {
								sayRandomly([ "Sorry, I didn't find any information about the person '"
										+ person["foaf:name"] + "'." ], lang);
							}
						});
			}
		}else{
			console.log("detail is not defined.");
		}
		
	};
	getDetails([uri], lang, callback);
}

function playVideoNumber(video){
	if(video && video["num"]){
		var n=1;
		if(video["1"])n=0;
		else if(video["2"])n=1;
		else if(video["3"])n=2;
		else if(video["4"])n=3;
		else if(video["5"])n=4;
		else n=parseInt(video["num"])-1;
		playVideo(n);
	}
}

var listData;

function show_next_events(p){
	say('Ok, next RedBull events.', lang);
	getEvents('next', 
		function(data){
			listData = data.events;
			displayList(data, 'events', 
			['name', 'publisheddate'], ['url'])
		});
}

function show_past_events(p){
	say('Ok, past RedBull events.', lang);
	getEvents('past', 
		function(data){
		listData = data.events;
		displayList(data, 'events', 
		['name', 'publisheddate'], ['url'])
		});
}

function show_channel_events(p){
	var ch = p.chan;
	say('Ok, Red Bull '+ch+' events.', lang);
	getChannelEvents(ch, 
		function(data){
		listData = data.topfeatures;
		displayList(data, 'topfeatures', 
		['name', 'publisheddate'], ['description'])
		});
}

function list_item_number(num){
	//console.log("num: "+num);
	say('Ok, number '+num.num, lang);
	//$("#monitor").html(listData[num.num].url);
	if(listData[num.num].url){
		window.open(listData[num.num].url);
	}else{
		$("#monitor").html(listData[num.num].description);
	}
}
var redbullapi = "https://api.redbull.com/v2";
var username='rbuser';
var password='wdKZqnmG';

function getEvents(datevalue, successHandler) {

     jQuery.ajax({
         type: "GET",
         url: redbullapi + "/events/currentdate/"+datevalue,
         //contentType: "application/json; charset=utf-8",
		 dataType: 'json',
         success: function (data, status, jqXHR) {
			console.log('success: '+status);
         	//console.log(data);
            successHandler(data);
         },

         error: function (jqXHR, status) {
             // error handler
             console.error('error staus: '+status);
         },
         //beforeSend: function (xhr) {
    	//	xhr.setRequestHeader ("Authorization", "Basic " + btoa(username + ":" + password));
		 //}
	});
	
}

function getChannelEvents(channel, successHandler) {
	///topfeatures/channels/{channelname}/locales/{locale}
     jQuery.ajax({
         type: "GET",
         url: redbullapi + "/topfeatures/channels/"+channel+"/locales/",
         //contentType: "application/json; charset=utf-8",
		 dataType: 'json',
         success: function (data, status, jqXHR) {
			console.log('success: '+status);
         	//console.log(data);
            successHandler(data);
         },

         error: function (jqXHR, status) {
             // error handler
             console.error('error staus: '+status);
         }

	});
	
}
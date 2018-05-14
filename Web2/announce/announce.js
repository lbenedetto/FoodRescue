var map = null;
//Name, lat lng
var buildings = [
	["ART - Art Building", 47.488225, -117.585019],
	["CAD - Cadet Hall", 47.489261, -117.585347],
	["CEB - Computing and Engineering Building", 47.489685, -117.585293],
	["CHN - Cheney Hall", 47.490362, -117.585769],
	["CMC - Communications Building", 47.488543, -117.584591],
	["HAR - Hargreaves Hall", 47.491869, -117.579839],
	["HUS - Huston Hall", 47.490497, -117.581567],
	["ISL - Isle Hall", 47.492403, -117.581227],
	["JFK - JFK Library", 47.490721, -117.583628],
	["KGS - Kingston Hall", 47.490809, -117.577881],
	["MAL - Campus Mall", 47.491355, -117.582798],
	["MAR - Martin Hall", 47.489787, -117.582121],
	["MON - Monroe Hall", 47.491266, -117.580402],
	["MUS - Music Building", 47.487811, -117.584494],
	["PAT - Patterson Hall", 47.491932, -117.582048],
	["PAV - Special Events Pavilion", 47.491361, -117.589385],
	["PUB - Pence Union Building", 47.492139, -117.583612],
	["RTV - Radio-TV Building", 47.488630, -117.585294],
	["SCI - Science Building", 47.491307, -117.585169],
	["SHW - Showalter Hall", 47.490144, -117.579784],
	["SNR - Senior Hall", 47.491223, -117.578429],
	["SUT - Sutton Hall", 47.489501, -117.581269],
	["TAW - Tawanka Commons", 47.491099, -117.581438],
	["THE - University Theatre", 47.488010, -117.585680],
	["URC - Recreation Center", 47.493244, -117.584211],
	["WLM - Williamson Hall", 47.490194, -117.582904]
];

// Preventing the Google Maps library from downloading an extra font
(function () {
	var isRobotoStyle = function (element) {

		// roboto font download
		if (element.href
			&& element.href.indexOf('https://fonts.googleapis.com/css?family=Roboto') === 0) {
			return true;
		}
		// roboto style elements
		if (element.tagName.toLowerCase() === 'style'
			&& element.styleSheet
			&& element.styleSheet.cssText
			&& element.styleSheet.cssText.replace('\r\n', '').indexOf('.gm-style') === 0) {
			element.styleSheet.cssText = '';
			return true;
		}
		// roboto style elements for other browsers
		if (element.tagName.toLowerCase() === 'style'
			&& element.innerHTML
			&& element.innerHTML.replace('\r\n', '').indexOf('.gm-style') === 0) {
			element.innerHTML = '';
			return true;
		}
		// when google tries to add empty style
		return element.tagName.toLowerCase() === 'style'
			&& !element.styleSheet && !element.innerHTML;
	};

	// we override these methods only for one particular head element
	// default methods for other elements are not affected
	var head = $('head')[0];

	var insertBefore = head.insertBefore;
	head.insertBefore = function (newElement, referenceElement) {
		if (!isRobotoStyle(newElement)) {
			insertBefore.call(head, newElement, referenceElement);
		}
	};

	var appendChild = head.appendChild;
	head.appendChild = function (textNode) {
		if (!isRobotoStyle($(textNode)[0])) {
			appendChild.call(head, textNode);
		}
	};
})();

function initMap() {
	var location = {lat: 47.491355, lng: -117.582798};
	map = new google.maps.Map(document.getElementById('map'), {
		zoom: 17,
		center: location
	});
}

function moveMapToBuilding() {
	var building = getBuilding();
	var center = new google.maps.LatLng(building[1], building[2]);
	map.panTo(center);
}

function getBuilding() {
	var selected = document.getElementById("buildingSelectorDropdownList").value;
	return buildings[selected];
}

function sendNotification() {
	var location = map.getCenter();

	$.post("../api/announce",
		{
			"title": getBuilding()[0],
			"body": $("#customMessage").val(),
			"lat": location.lat,
			"lng": location.lng,
			"expiry": $("#buildingSelectorDropdownList").val(),
			"auth_token": auth_token,
			"source": "web"
		},
		function (data) {
			//TODO: Handle server response
		}
	);

}
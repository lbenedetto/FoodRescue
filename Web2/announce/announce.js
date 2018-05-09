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


function initMap() {
	var location = {lat: 47.491099, lng: -117.581438};
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
	var building = getBuilding();
	var location = map.getCenter();
	var data = {
		"title": building[0],
		"body": $("#customMessage").val(),
		"lat": location.lat,
		"lng": location.lng,
		"expiry": document.getElementById("buildingSelectorDropdownList").value,
		"auth": "todo",
		"source" : "web"
	};

	$.post("api/announce",
		data,
		function (data) {
			//TODO: Handle server response
		}
	);

}
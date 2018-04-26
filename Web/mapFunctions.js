var prevMarker = null;
var locations = new Array();
var map;

// Used tutorial https://developers.google.com/maps/documentation/javascript/adding-a-google-map to get started
function initMap()

{
    var uluru = {lat: 47.491602, lng: -117.584417};
    map = new google.maps.Map(document.getElementById('map'),{
        zoom: 16,
        center: uluru
    });

    map.addListener("rightclick",function(e)
    {
        //placeMarker(e.latLng,map);
        placeMarker(e.latLng);
    });

}

function getTime()
{
}

function getValues()
{
    var lat = document.getElementById("Lat").value;
    var long = document.getElementById("Long").value;
    var time = document.getElementById("time").value;
    document.getElementById("data").value = lat + ":::::" + long + ":::::" + time;
    console.log(document.getElementById("data").value);
    
}

function getLocation()
{
    var value = document.getElementById("Location").value;
    var latt = locations[value][0];
    var long = locations[value][1];

    var latlng = new google.maps.LatLng(latt, long);
   // var uluru = {lat: 47.491602, lng: -117.584417};
    map = new google.maps.Map(document.getElementById('map'),{
        zoom: 16,
        center: latlng
    });
    placeMarker(latlng);

    map.addListener("rightclick",function(e)
    {
        //placeMarker(e.latLng,map);
        placeMarker(e.latLng);
    });
}

function placeMarker(latLng) //map)
{
    if(prevMarker != null)
        prevMarker.setMap(null);
    var curMarker = new google.maps.Marker(
    {
        position: latLng,
        map: map
    })
    prevMarker = curMarker;
    var lat = latLng.lat();
    var lng = latLng.lng();
    document.getElementById("Lat").value = lat;
    document.getElementById("Long").value = lng;
    getValues();
//    var str = "location is " + lat + " degrees latitude and " + lng + " degrees longitude.";

    //$("#markerLocation").val(str);
//    document.getElementById("markerLocation").innerHTML = str;
};

function placeMarkerMan(lat, long)
{
    if(prevMarker != null)
        prevMarker.setMap(null);
    var curMarker = new google.maps.Marker(
    {
        position: latLng,
        map: map
    })
    prevMarker = curMarker;

    var lat = latLng.lat();
    var lng = latLng.lng();
    //var str = "location is " + lat + " degrees latitude and " + lng + " degrees longitude.";

    //$("#markerLocation").val(str);
    document.getElementById("Lat").value = lat;
    document.getElementById("Long").value = lng;
};

//first is latitude, second is longitude
locations[0] = new Array(47.488225, -117.585019);
locations[1] = new Array(47.489261, -117.585347);
locations[2] = new Array(47.489685, -117.585293);
locations[3] = new Array(47.490362, -117.585769);
locations[4] = new Array(47.488543, -117.584591);
locations[5] = new Array(47.491869, -117.579839);
locations[6] = new Array(47.490497, -117.581567);
locations[7] = new Array(47.492403, -117.581227);
locations[8] = new Array(47.490721, -117.583628);
locations[9] = new Array(47.490809, -117.577881);
locations[10] = new Array(47.491355, -117.582798);
locations[11] = new Array(47.489787, -117.582121);
locations[12] = new Array(47.491266, -117.580402);
locations[13] = new Array(47.487811, -117.584494);
locations[14] = new Array(47.491932, -117.582048);
locations[15] = new Array(47.491361, -117.589385);
locations[16] = new Array(47.492139, -117.583612);
locations[17] = new Array(47.488630, -117.585294);
locations[18] = new Array(47.491307, -117.585169);
locations[19] = new Array(47.490144, -117.579784);
locations[20] = new Array(47.491223, -117.578429);
locations[21] = new Array(47.489501, -117.581269);
locations[22] = new Array(47.488010, -117.585680);
locations[23] = new Array(47.493244, -117.584211);
locations[24] = new Array(47.490194, -117.582904);
var prevMarker = null;

function initMap()

{
    var uluru = {lat: 47.491602, lng: -117.584417};
    var map = new google.maps.Map(document.getElementById('map'),{
        zoom: 16,
        center: uluru
    });

    map.addListener("rightclick",function(e)
    {
        placeMarker(e.latLng,map);
    });

}


function placeMarker(latLng, map)
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
    var str = "location is " + lat + " degrees latitude and " + lng + " degrees longitude.";

       document.getElementById("markerLocation").innerHTML = str;
};

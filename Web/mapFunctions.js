
function initMap()
{
    var uluru = {lat: 47.491602, lng: -117.584417};
    var map = new google.maps.Map(document.getElementById('map'),{
        zoom: 17,
        center: uluru
    });

    var marker = new google.maps.Marker({
        position: uluru,
        map: map
    });
}
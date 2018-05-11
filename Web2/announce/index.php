<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<link href="https://fonts.googleapis.com/css?family=Roboto" rel="stylesheet">
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
	<link href="../global.css" rel="stylesheet">
	<link href="announce.css" rel="stylesheet">
	<link rel="shortcut icon" type="image/png" href="../assets/favicon.png"/>
	<title>Make Announcement</title>
</head>
<body>
<?php
$token = $_GET['token'];
if (isset($token)) {
	echo "<input id='auth' type='hidden' name='auth' value=$token>";
} else {
	header("Location: https://login.ewu.edu/cas/login?service=https://146.187.135.29/web/login");
}
?>
<div id="navbar">
	<ul>
		<li><img src="../assets/burger_bell.png" id="icon" class="img" height="45" width="45"></li>
		<li><h2>Food Rescue</h2></li>
	</ul>
</div>
<div class="mainContent">
	<h2 id="mainContentHeader">Create New Food Event</h2>
	<p class="p2">Customize Message:</p>
	<textarea id="customMessage"></textarea>
	<p class="p2">Select Nearest Building:</p>
	<select id="buildingSelectorDropdownList" class="select" onchange="moveMapToBuilding()" style="border-style: double;
                color:white; font-weight: bold;">
		<option value="0" class="options">ART - Art Building</option>
		<option value="1" class="options">CAD - Cadet Hall</option>
		<option value="2" class="options">CEB - Computing and Engineering Building</option>
		<option value="3" class="options">CHN - Cheney Hall</option>
		<option value="4" class="options">CMC - Communications Building</option>
		<option value="5" class="options">HAR - Hargreaves Hall</option>
		<option value="6" class="options">HUS - Huston Hall</option>
		<option value="7" class="options">ISL - Isle Hall</option>
		<option value="8" class="options">JFK - JFK Library</option>
		<option value="9" class="options">KGS - Kingston Hall</option>
		<option value="10" class="options">MAL - Campus Mall</option>
		<option value="11" class="options">MAR - Martin Hall</option>
		<option value="12" class="options">MON - Monroe Hall</option>
		<option value="13" class="options">MUS - Music Building</option>
		<option value="14" class="options">PAT - Patterson Hall</option>
		<option value="15" class="options">PAV - Special Events Pavilion</option>
		<option value="16" class="options">PUB - Pence Union Building</option>
		<option value="17" class="options">RTV - Radio-TV Building</option>
		<option value="18" class="options">SCI - Science Building</option>
		<option value="19" class="options">SHW - Showalter Hall</option>
		<option value="20" class="options">SNR - Senior Hall</option>
		<option value="21" class="options">SUT - Sutton Hall</option>
		<option value="22" class="options">TAW - Tawanka Commons</option>
		<option value="23" class="options">THE - University Theatre</option>
		<option value="24" class="options">URC - Recreation Center</option>
		<option value="25" class="options">WLM - Williamson Hall</option>
	</select>
	<p class="p3">Select estimated time of availability:</p>
	<select name="expiry" id="time" class="select2"
	        style="border-style: double;color:white; font-weight: bold;">
		<option value="15" class="options"> &lt; 15 minutes</option>
		<option value="30" class="options"> &lt; 30 minutes</option>
		<option value="60" class="options"> &lt; 1 hour</option>
	</select>
	<p class="p3">Fine-tune exact location:</p>
	<!--https://stackoverflow.com/questions/4130237/displaying-crosshairs-in-the-center-of-a-javascript-google-map-->
	<div id="container">
		<div id="map"></div>
		<div id="crosshair"><img src="assets/crosshair.png" width="50" height="50"></div>
	</div>
	<button style="float: right; margin-top: 1em" onclick="sendNotification()">
		<span>Announce</span>
		<img src="assets/send.png" height="15">
	</button>
	<div style="color: white">Powered by Google Maps API</div>
	<h2>Download Android App</h2>
	<p>An Android App is also available to make it easier for you to announce food events. It also lets you stay signed
		in so you don't have to waste time logging in each time you want to announce something</p>
	<button id="downloadButton" onclick="downloadApp()">
		<span>Get App</span>
		<img src="../assets/download.png" height="15">
	</button>
	<iframe id="iframeAppDownloader" style="display:none;"></iframe>
</div>
</body>
<script src="announce.js"></script>
<script src="../global.js"></script>
<script async defer
        src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDXWwdA4ONNbGRTI3tnx40rFsOOO4va_JI&callback=initMap"></script>
</html>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<link href="https://fonts.googleapis.com/css?family=Roboto" rel="stylesheet">
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
	<link href="../global.css" rel="stylesheet">
	<link href="subscribe.css" rel="stylesheet">
	<link rel="shortcut icon" type="image/png" href="../assets/favicon.png"/>
	<title>SMS Subscription</title>
</head>
<body>
<?php include "../api/login/web/checkAuth.php" ?>
<div id="navbar">
	<ul>
		<li><img src="../assets/burger_bell.png" id="icon" class="img" height="45" width="45"></li>
		<li><h2>Food Rescue</h2></li>
		<li id="links">
			<a href="../subscribe/">Get Notified</a>
			<?php include "../api/navigate"; ?>
		</li>
	</ul>
</div>
<div class="mainContent">
	<h2 id="mainContentHeader">Download Android App</h2>
	<p>An Android App is available so that you can receive push notifications of food availability</p>
	<button id="downloadButton" onclick="downloadApp()">
		<span>Get App</span>
		<img src="../assets/download.png" height="15">
	</button>
	<iframe id="iframeAppDownloader" style="display:none;"></iframe>

	<h2>Manage SMS Alert Subscription</h2>
	<p>If you do not have an Android phone, or do not wish to install the app, you can also subscribe your phone number
		to receive a text message whenever there is free food on campus</p>
	<p class="p2">Enter your phone number (xxx xxx xxxx):</p>
	<div>
		<input type="text" id="subscribeBar">
		<select id="subscribeCarrier" style="border-style: double;color:white; font-weight: bold;">
			<option value="@message.alltel.com" class="options"> Alltel</option>
			<option value="@txt.att.net" class="options"> AT&T</option>
			<option value="@myboostmobile.com" class="options"> Boost Mobile</option>
			<option value="@messaging.sprintpcs.com" class="options"> Sprint</option>
			<option value="@tmomail.net" class="options"> T-Mobile</option>
			<option value="@email.uscc.net" class="options"> U.S. Cellular</option>
			<option value="@vtext.com" class="options"> Verizon</option>
			<option value="@vmobl.com" class="options"> Virgin Mobile</option>
			<option value="@text.republicwireless.com" class="options"> Repubilc Wireless</option>
			<option value="@mms.cricketwireless.net" class="options"> Cricket Wireless</option>
			<option value="@msg.fi.google.com" class="options"> Project Fi</option>
		</select>
		<button id="subscribeButton" onclick="subscribe()">
			<span>Subscribe to SMS Notifications</span>
			<img src="assets/sms.png" height="15">
		</button>
	</div>
</div>
</body>
<script src="subscribe.js"></script>
<script src="../global.js"></script>
</html>

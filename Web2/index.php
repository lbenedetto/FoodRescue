<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<link href="https://fonts.googleapis.com/css?family=Roboto" rel="stylesheet">
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
	<link href="global.css" rel="stylesheet">
	<link rel="shortcut icon" type="image/png" href="assets/favicon.png"/>
	<title>Food Rescue</title>
</head>
<body>
<div id="navbar">
	<ul>
		<li><img src="assets/burger_bell.png" id="icon" class="img" height="45" width="45"></li>
		<li><h2>Food Rescue</h2></li>
	</ul>
</div>
<div class="mainContent">
	<h2 id="mainContentHeader">Welcome to Food Rescue</h2>
	<p>Food rescue is a system for getting food that would otherwise go to waste into the hands of EWU students.
		To access functions of this system, sign in with your EWU NetID on our website or download our Android app,
		which will allow you to send or receive push notifications of food availability
	</p>
	<button id="signInButton" onclick="signIn()">
		<span>Sign In</span>
		<img src="assets/signin.png" height="15">
	</button>
	<button id="downloadButton" onclick="downloadApp()">
		<span>Get App</span
		<img src="assets/download.png" height="15">
	</button>
	<iframe id="iframeAppDownloader" style="display:none;"></iframe>
</div>
</body>
<script src="global.js"></script>
</html>
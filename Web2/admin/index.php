<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<link href="https://fonts.googleapis.com/css?family=Roboto" rel="stylesheet">
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
	<link href="../global.css" rel="stylesheet">
	<link href="admin.css" rel="stylesheet">
	<link rel="shortcut icon" type="image/png" href="../assets/favicon.png"/>
	<title>Admin Portal</title>
</head>
<body>
<?php   include "../api/login/web/checkAuth.php"; 
	include "../api/auth_poster_functions.php";
?>
<div id="navbar">
	<ul>
		<li><img src="../assets/burger_bell.png" id="icon" class="img" height="45" width="45"></li>
		<li><h2>Food Rescue</h2></li>
		<li id="links">
			<a href="../subscribe/">Get Notified</a>
<?php 
	addNavs(); 
?>
		</li>
	</ul>
</div>
<div class="mainContent">
	<h2 id="mainContentHeader">Manage User Permissions</h2>
	<p class="p2">Enter username:</p>
	<div>
		<textarea id="usernameField"></textarea>
		<button id="searchButton" onclick="search()">
			<span>Search</span>
			<img src="assets/search.png" height="15">
		</button>
		<button id="newUserButton" onclick="newUser()">
			<span>Save User</span>
			<img src="assets/edit.png" height="15">
		</button>
	</div>
	<div id="table" class="table-editable" align="center">
		<span class="table-add glyphicon glyphicon-plus"></span>
		<table class="prettyTable table">
			<tr id="0">
				<th class="wide" width="150">Username</th>
				<th class="wide" width="125">Can make announcements</th>
			</tr>
		</table>
	</div>
</div>

<table id="clone" class="hide">
	<!-- This is our cloneable table line -->
	<tr class="hide">
		<!--Username-->
		<td class="wide username"></td>
		<!--Switch-->
		<td class="wide checkboxColumn">
			<label class="checkboxContainer" onchange="toggleCheckbox(this, 1)">
				<input type="checkbox">
				<span class="checkmark"></span>
			</label>
		</td>
	</tr>
</table>
</body>
<script src="admin.js"></script>
</html>

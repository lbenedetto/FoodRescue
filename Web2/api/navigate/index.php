<?php
//TODO: Respond to get request containing only an auth token with an array of <li> containing links to the pages this
//User is allowed to access based on their permission level
//Admins get Admin, Feeder, and Eater pages
//Eater gets only Eater page, ect
if ($_SERVER['REQUEST_METHOD'] == 'GET') {
	$perm = 0;
	if (isset($_GET['auth_token'])) {
		$perm = getUserPermissionLevel($_GET['auth_token']);
	}
	if (isset($_SESSION['auth_token'])) {
		$perm = getUserPermissionLevel($_SESSION['auth_token']);
	}
	if ($perm > 0)
		echo '<a href="../announce/">Make Announcements</a>\n';
	if ($perm > 1)
		echo '<a href="../admin/">Manage Users</a>\n';
	echo '<a href="../admin/">Manage Users</a>\n';

} else
	header("HTTP/1.0 405 MethodNotAllowed");

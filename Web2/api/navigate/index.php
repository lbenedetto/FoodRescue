<?php
//TODO: Respond to get request containing only an auth token with an array of <li> containing links to the pages this
//User is allowed to access based on their permission level
//Admins get Admin, Feeder, and Eater pages
//Eater gets only Eater page, ect
if ($_SERVER['REQUEST_METHOD'] == 'GET') {
	$perm = 0;
	if (isset($_GET['auth'])) {
		$perm = getUserPermissionLevel($_GET['auth']);
	}
	if ($perm > 0)
		echo '<li><a href="../announce/">Make Announcements</li>\n';
	if ($perm > 1)
		echo '<li><a href="../admin/">Admin</li>\n';


} else
	header("HTTP/1.0 405 MethodNotAllowed");
<?php
if (isset($_GET['auth_token'])) {
	$auth_token = $_GET['auth_token'];
	echo "<input id='auth_token' type='hidden' value='$auth_token'>";
} else {
	header("Location: https://login.ewu.edu/cas/login?service=".SITE_URL."api/login/web");
}

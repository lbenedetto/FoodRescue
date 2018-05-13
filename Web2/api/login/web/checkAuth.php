<?php
if (isset($_GET['auth'])) {
	$auth = $_GET['auth'];
	echo "<input id='auth' type='hidden' name='auth' value='$auth'>";
} else {
	header("Location: https://login.ewu.edu/cas/login?service=".SITE_URL."api/login/web");
}

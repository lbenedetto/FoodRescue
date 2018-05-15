<?php
session_start();
include "../../auth_poster_functions.php";
if (isset($_GET['auth_token'])) 
{
	$auth_token = $_GET['auth_token'];
	echo "<input id='auth_token' type='hidden' value='$auth_token'>";
} 
else if (isset($_SESSION['auth_token'])) 
{
	$auth_token = $_SESSION['auth_token'];
	echo "<input id='auth_token' type='hidden' value='$auth_token'>";
}
else 
{
	header("Location: https://login.ewu.edu/cas/login?service=".$_SESSION['SITE_URL']."api/login/web");
}

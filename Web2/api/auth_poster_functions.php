<?php
// <!-- Some of this code comes from Berkely-->
//<!--https://calnetweb.berkeley.edu/calnet-technologists/cas/casifying-your-web-application-or-web-server/cas-code-samples/cas-->
define('API_ACCESS_KEY', 'AAAAZ9gqXJw:APA91bEYHdK7fnJ-9Uj3wqrs3xShJVYyuUcC3rHXS7LaYtCrKYv8CdUHXjq9pNmTwSxBZTO2z4-AhWLJW8_DjtauE-ZzWCq34J-WOzuy9WEWxI9Qe_w1Rv8XKmK8w7paoZk-zsVqiZpV');
$_SESSION['SITE_URL'] = 'https://146.187.135.29/';
$casService = 'https://login.ewu.edu/cas';
$thisService = 'http://foodrescue.ewu.edu' . $_SERVER['PHP_SELF'];

function responseForTicket($ticket, $who)
{
	global $casService, $thisService;
	$casGet = "https://login.ewu.edu/cas/serviceValidate?ticket=$ticket&service=".$_SESSION['SITE_URL']."api/login/$who";
	$response = file_get_contents($casGet);
	echo "Response: $response :done";
	if (preg_match('/cas:authenticationSuccess/', $response)) {
		return $response;
	} else {
		return false;
	}
}

/*
* Returns the UID from the passed in response, or it
* returns false if there is no UID.
*/
function extractUid($response)
{
	$responseArray = preg_split("/\n/", $response);
	$casUserArray = preg_grep("/(\d+)<\/cas:user>/", $responseArray);
	preg_match('#<cas:user>(.*?)</cas:user>#', $response, $uuiidd);
	if (!$uuiidd[1]) {
		return false;
	} else {
		return $uuiidd[1];


	}
}

function getConn()
{
	$p_ini = parse_ini_file("config.ini", true);
	$servername = $p_ini['Database']['servername'];
	$username = $p_ini['Database']['username'];
	$password = $p_ini['Database']['password'];
	$database = 'foodrescue';
	try {
		$conn = new PDO("mysql:host=$servername;dbname=$database", $username, $password);
		$conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
		//var_dump($conn);
		//echo "Database connection sucessful\n";
		return $conn;
	} catch (PDOException $e) {
		echo "Connection failed: " . $e->getMessage();
		return false;
	}
}

function sendDatamessage($title, $body, $lat, $lng, $expiry)
{
	$fcmUrl = 'https://fcm.googleapis.com/fcm/send';

	$dataInData =
		[
			'title' => $title,
			'body' => $body,
			'lat' => $lat,
			'lng' => $lng,
			'expiry' => $expiry
		];
	$fcmDatamessage =
		[
			'to' => '/topics/edu.ewu.team1.foodrescue',
			'data' => $dataInData
		];
	$headers =
		[
			'Authorization: key=' . API_ACCESS_KEY,
			'Content-Type: application/json'
		];


	try {
		$ch = curl_init();
		if (curl_error($ch)) {
			echo "err";
			echo 'error:' . curl_error($ch);
		}
	} catch (Exception $e) {
		echo "Connection failed: " . $e->getMessage();
	}
	if ($ch == false)
		syslog(LOG_INFO, "Falied to create a curl session");
	curl_setopt($ch, CURLOPT_URL, $fcmUrl);
	curl_setopt($ch, CURLOPT_POST, true);
	curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
	curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
	curl_setopt($ch, CURLOPT_VERBOSE, true);
	curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, FALSE);
	curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, FALSE);
	curl_setopt($ch, CURLOPT_NOBODY, false);
	curl_setopt($ch, CURLOPT_HEADER, true);
	curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
	curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, false);
	curl_setopt($ch, CURLOPT_TIMEOUT, 40000);
	curl_setopt($ch, CURLOPT_RETURNTRANSFER, TRUE);
	curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fcmDatamessage));
	try {
		$result = curl_exec($ch);
	} catch (Exception $e) {
		echo "caught";
		echo "Connection failed: " . $e->getMessage();
	}

	$curldata =
		[
			'title' => "\n<br>" . $title . "<br>\n",
			'info' => curl_getinfo($ch),
			'result' => $result
		];

	file_put_contents("logfile.txt", $curldata, FILE_APPEND);

	curl_close($ch);
	sendSMS($dataInData);
	echo "<br>" . $result;
	echo "<br>done.";
}


function getUnameRow($username, $conn)
{
	if ($conn) {
		$stmt = $conn->prepare("SELECT * FROM foodrescue.users WHERE uname = ?"); 
		$stmt->bindValue(1, $username, PDO::PARAM_STR);
		try {
			$stmt->execute();
			return $stmt;
		} catch (PDOException $e) {
			echo "Connection failed: " . $e->getMessage();
			return 0;
		}
	}
	echo "no conn";
	return 0;
}

function getUserPermissionLevel($auth_token)
{
	$perm = 0;
	$conn = getConn();
	if ($conn) {
		$stmt = $conn->prepare("SELECT * FROM users WHERE auth_token = ?");  // Look for uid
		$stmt->bindValue(1, $auth_token, PDO::PARAM_STR);
		try {
			$stmt->execute();
		} catch (PDOException $e) {
			echo "Connection failed: " . $e->getMessage();
		}
		if ($stmt->rowCount() == 1) // Found the id
		{
			$row = $stmt->fetch(PDO::FETCH_ASSOC);
			$perm = $row['perm'];
		}
	}
	return $perm;
}

function addNavs()
{
	$perm = 0;
		if (isset($_GET['auth_token'])) {
			$perm = getUserPermissionLevel($_GET['auth_token']);
		}
		if (isset($_SESSION['auth_token'])) {
			$perm = getUserPermissionLevel($_SESSION['auth_token']);
		}
		if ($perm > 0)
			echo '<a href="../announce/">Make Announcements</a>';
		if ($perm > 1)
			echo '<a href="../admin/">Manage Users</a>';
}

function sendSMS()
{
	// This cannot be used until there is a way of recieving texts as well as sending.	
}

?>

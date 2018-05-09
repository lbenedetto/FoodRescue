<?php
// <!-- Some of this code comes from Berkely-->
//<!--https://calnetweb.berkeley.edu/calnet-technologists/cas/casifying-your-web-application-or-web-server/cas-code-samples/cas-->
define('API_ACCESS_KEY', 'AAAAZ9gqXJw:APA91bEYHdK7fnJ-9Uj3wqrs3xShJVYyuUcC3rHXS7LaYtCrKYv8CdUHXjq9pNmTwSxBZTO2z4-AhWLJW8_DjtauE-ZzWCq34J-WOzuy9WEWxI9Qe_w1Rv8XKmK8w7paoZk-zsVqiZpV');
$casService = 'https://login.ewu.edu/cas';
$thisService = 'http://foodrescue.ewu.edu' . $_SERVER['PHP_SELF'];

//$p_ini = parse_ini_file("config.ini", true);
//$servername = $p_ini['Database']['servername'];
//$username = $p_ini['Database']['username'];
//$password = $p_ini['Database']['password'];
//$database = 'databasetest';

//echo "$servername
//$username
//$password
//$database";

echo "hey we imported the functions\n\n";
//echo "$_SERVER['PHP_SELF']\n\n";
echo "$casService/serviceValidate?ticket=$ticket&service=" . urlencode($thisService) . "\n\n";

function responseForTicket($ticket, $who) {
	global $casService, $thisService;

	//$casGet = "$casService/serviceValidate?ticket=$ticket&service=" . urlencode($thisService);
	$casGet = "https://login.ewu.edu/cas/serviceValidate?ticket=$ticket&service=https://146.187.135.29/" . $who . "/login";
	//$casGet = "placeholder";
	// See the PHP docs for warnings about using this method:
	// http://us3.php.net/manual/en/function.file-get-contents.php
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
function extractUid($response) {
	echo $response . "<br>vardump:";
	var_dump($response);
	// Turn the response into an array
	$responseArray = preg_split("/\n/", $response);
	// Get the line that has the cas:user tag
	$casUserArray = preg_grep("/(\d+)<\/cas:user>/", $responseArray);
	$uuiidd;
	preg_match('#<cas:user>(.*?)</cas:user>#', $response, $uuiidd);
	//echo "<br><br>$uuiidd[1]<br><br>";
	//var_dump($uuiidd[1]);
	//echo "this:<br>";
	//var_dump($casUserArray);
	//echo "<br>";
	//if (is_array($casUserArray)) {
	// $uid = trim(strip_tags(implode($casUserArray)));
	//echo $uid;
	/*if (is_numeric($uid)) {
		echo "returning uid<br>";
	   return $uid;
	}
	else
	{
	 echo "returning hard coded";
	  return "bstephenson";
	}*/
	//return "".$uid;
	// }
	if (!$uuiidd[1]) {
		return false;
	} else {
		return $uuiidd[1];


	}
}

function getConn() {
	$p_ini = parse_ini_file("config.ini", true);
	$servername = $p_ini['Database']['servername'];
	$username = $p_ini['Database']['username'];
	$password = $p_ini['Database']['password'];
	$database = 'databasetest';
//    echo "$servername
//$username
//$password
//$database";
	try {
		$conn = new PDO("mysql:host=$servername;dbname=$database", $username, $password);
		$conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
		var_dump($conn);
		echo "Database connection sucessful\n";
		return $conn;
	} catch (PDOException $e) {
		echo "Connection failed: " . $e->getMessage();
		return false;
	}
}

function sendRequest($title, $body, $data) {
	$fcmUrl = 'https://fcm.googleapis.com/fcm/send';
	$token = 'fMW6mM2Zb3o:APA91bFJGJ1wyoumvEkf5aihnHizxahu0OhHcSqvki-tWv5PsoydV1GoB7Bo6C0ERLBfTzNJ3XhixBjyH764WgZCHeSoe59ThFK1zINV3zrO3MlKy-3Sq0s8hCbBJsN16xHyH8SuSPTy';

	$notification =
		[
			'title' => $title,
			'body' => $body
		];
	$extraNotificationData =
		[
			"message" => $notification,
			"moredata" => $data
		];
	$fcmNotification =
		[
			'to' => '/topics/edu.ewu.team1.foodrescue',
			'topic' => 'edu.ewu.team1.foodrescue',
			'notification' => $notification,
			'data' => $extraNotificationData
		];
	$headers =
		[
			'Authorization: key=' . API_ACCESS_KEY,
			'Content-Type: application/json'
		];

	$ch = curl_init();
	curl_setopt($ch, CURLOPT_URL, $fcmUrl);
	curl_setopt($ch, CURLOPT_POST, true);
	curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
	curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
	curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
	curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fcmNotification));
	$result = curl_exec($ch);
	curl_close($ch);


	echo $result;
}

?>

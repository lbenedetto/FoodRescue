<?php
include '../auth_poster_functions.php';

switch ($_SERVER['REQUEST_METHOD']) {
	case 'POST':
		if (isset($_POST['title']) && isset($_POST['body']) && isset($_POST['lat']) && isset($_POST['lng']) && isset($_POST['expiry']) && isset($_POST['auth']) && isset($_POST['source'])) {
			$conn = getConn();
			$stmt = $conn->prepare("SELECT * FROM databasetest.users WHERE auth_token = ?");  // Look for uid
			$stmt->bindValue(1, $_POST['auth'], PDO::PARAM_STR);
			try {
				$stmt->execute();
			} catch (PDOException $e) {
				echo "Connection failed: " . $e->getMessage();
			}
			//$stmt->bindValue(1, '20ad646e676ea1ca339c71f5870ca79322c438f787f7dcc5f3d8e0dcfa6ba7ad831f580ad0b46d1343e82fbcbd205a37321c8226f36f3c78f44e104fd2ec2d52', PDO::PARAM_STR);
			echo "<br>  " . $stmt->rowCount() . "<br>";
			if ($stmt->rowCount() == 1) // Found the auth token
			{
				$row = $stmt->fetch(PDO::FETCH_ASSOC);
				if ($row['feeder_perm'] == 1) {
					echo "<br>sending  " . $_POST['title'] . "  " . $_POST['body'] . "  " . $_POST['data'] . "<br>";
					sendDatamessage($_POST['title'], $_POST['body'], $_POST['lat'], $_POST['lng'], $_POST['expiry']);
					//header("Location: https://login.ewu.edu/cas/login?service=https://146.187.135.29/web/login");
					exit;
				} else echo "Not allowed!";
			} else {
				echo "<br> auth token " . $_POST['auth'] . " not found... <br>";
				//sendRequest($_POST['title'], $_POST['body'], $_POST['data']);
			}
		} else {
			$src = $_POST['source'];
			header("Location: https://login.ewu.edu/cas/login?service=https://146.187.135.29/api/login/$src/");
		}
		exit;
	default:
		header("HTTP/1.0 405 MethodNotAllowed");
		break;
}
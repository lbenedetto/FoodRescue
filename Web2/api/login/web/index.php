<?php

include '../../auth_poster_functions.php';

if ($_SERVER['REQUEST_METHOD'] == 'GET') {

	//echo "got to case get";
	if (isset($_GET['ticket'])) {
		echo $_GET['ticket'];
		$response = responseForTicket($_GET['ticket'], "web");
		if (!$response)
			echo "no response for ticket<br>";
		else {
			$uid = extractUid($response);
			if (!$uid)
				echo "couldn't resolve UID<br>";
			else {
				//echo "UID resolved<br>";
				$conn = getConn();
				if ($conn) {
					$stmt = $conn->prepare("SELECT * FROM foodrescue.users WHERE uname = ?");  // Look for uid
					$stmt->bindValue(1, $uid, PDO::PARAM_STR);
					try {
						$stmt->execute();
					} catch (PDOException $e) {
						echo "Connection failed: " . $e->getMessage();
					}
					$rowCount = $stmt->rowCount();
					if ($rowCount == 1) // Found the id
					{
						$row = $stmt->fetch(PDO::FETCH_ASSOC);
						if (isset($row['auth_token'])) // already have an auth token on file for this uid
						{
							$token = $row['auth_token'];
						} else // no auth token found, generate one and send it as JSON
						{
							$token = bin2hex(openssl_random_pseudo_bytes(64));
							$stmt = $conn->prepare("UPDATE users SET auth_token=? WHERE uname = ?;");
							$stmt->bindValue(1, $token, PDO::PARAM_STR);
							$stmt->bindValue(2, $uid, PDO::PARAM_STR);
							$stmt->execute();

						}
					} else if ($rowCount == 0) // if you don't find it, make a new entry
					{
						$token = bin2hex(openssl_random_pseudo_bytes(64));
						try {
							$stmt = $conn->prepare("INSERT INTO users (auth_token, uname, perm) VALUES (?, ?, 0);");
							$stmt->bindValue(1, $token, PDO::PARAM_STR);
							$stmt->bindValue(2, $uid, PDO::PARAM_STR);
							$stmt->execute();
						} catch (PDOException $e) {
							echo "Query failed: " . $e->getMessage();
						}
					}
					echo json_encode($token);
					$perm = getUserPermissionLevel($token);
					switch ($perm) {
						case 0:
							header("Location: ".SITE_URL."subscribe/?token=" . $token . "&username=" . $uid);
							exit;
						case 1:
							header("Location: ".SITE_URL."announce/?token=" . $token . "&username=" . $uid);
							exit;
						case 2:
							header("Location: ".SITE_URL."admin/?token=" . $token . "&username=" . $uid);
							exit;
					}

				}

			}
		}
	} else {
		header("Location: https://login.ewu.edu/cas/login?service=".SITE_URL."api/login/web/");
	}
} else {
	header("HTTP/1.0 405 MethodNotAllowed");
}


?>

<?php
include '../../auth_poster_functions.php';

switch ($_SERVER['REQUEST_METHOD']) {
	case 'GET':
		if (isset($_GET['ticket'])) {
			$response = responseForTicket($_GET["ticket"], "android");
			if (!$response)
				echo "no response for ticket<br>";
			else {
				$uid = extractUid($response);
				if (!$uid)
					echo "couldn't resolve UID<br>";
				else {
					$conn = getConn();
					if ($conn) {
						$stmt = $conn->prepare("SELECT * FROM users WHERE uname = ?");  // Look for uid
						$stmt->bindValue(1, $uid, PDO::PARAM_STR);
						try {
							$stmt->execute();
						} catch (PDOException $e) {
							echo "Connection failed: " . $e->getMessage();
						}
						if ($stmt->rowCount() == 1) // Found the id
						{
							$row = $stmt->fetch(PDO::FETCH_ASSOC);
							if (isset($row['auth_token'])) // already have an auth token on file for this uid
							{
								//echo json_encode($row['auth_token']);
								//echo "<br><br>redirecting now: ";
								//intent://foodrescue.ewu.edu#Intent;scheme=http;package=edu.ewu.team1.foodrescue;S.token=adsfasdfasdfasdS.uid=lbenedetto;end
								header("Location: intent://foodrescue.ewu.edu#Intent;scheme=http;package=edu.ewu.team1.foodrescue;S.token=" . $row['auth_token'] . ";S.uid=" . $uid . ";end");
								exit;
							} else // no auth token found, generate one and send it as JSON
							{
								$token = bin2hex(openssl_random_pseudo_bytes(64));
								$stmt = $conn->prepare("UPDATE users SET auth_token=?, WHERE uname = ?;");
								$stmt->bindValue(1, $token, PDO::PARAM_STR);
								$stmt->bindValue(2, $uid, PDO::PARAM_STR);
								$stmt->execute();
								//echo json_encode($token . "this");
								header("Location: intent://foodrescue.ewu.edu#Intent;scheme=http;package=edu.ewu.team1.foodrescue;S.token=" . $token . ";S.uid=" . $uid . ";end");
								exit;
							}
						}
						if ($stmt->rowCount() == 0) // if you don't find it, make a new entry
						{
							$token = bin2hex(openssl_random_pseudo_bytes(64));
							$stmt = $conn->prepare("INSERT INTO users (auth_token, uname, feeder_perm) VALUES (?, ?, 0);");
							$stmt->bindValue(1, $uid, PDO::PARAM_STR);
							$stmt->bindValue(2, $token, PDO::PARAM_STR);
							$stmt->execute();
							//echo json_encode($token . "that");
							header("Location: intent://foodrescue.ewu.edu#Intent;scheme=http;package=edu.ewu.team1.foodrescue;S.token=" . $token . ";S.uid=" . $uid . ";end");
							exit;
						}

					}

				}
			}
		} else {
			header("Location: https://login.ewu.edu/cas/login?service=https://146.187.135.29/api/login/android");
			exit;
		}
		break;
	default:
		header("HTTP/1.0 405 MethodNotAllowed");
		exit;
		break;
}
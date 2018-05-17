<?php

include '../auth_poster_functions.php';

switch ($_SERVER['REQUEST_METHOD']) {
	case 'POST':
		$conn = getConn();
		if (isset($_POST['auth_token'])) {
			$stmt = $conn->prepare("SELECT 1 FROM users WHERE auth_token = ?;");
			$stmt->bindValue(1, $_POST['auth_token'], PDO::PARAM_STR);
			$stmt->execute();
			if ($stmt->rowCount() == 1) {
				$row = $stmt->fetch(PDO::FETCH_ASSOC);
				if ($row['perm'] != 2) {
					echo "user does not have permission";
					break;
				}
			} else
				echo "auth token not found.";
		} else {
			echo "Only the admin can make changes to the users in this way.";
			break;
		}
		if (isset($_POST['permission']))
			$perm = $_POST['permission'];
		else
			$perm = 0;
		if (isset($_POST['username'])) {
			$auth_token = bin2hex(openssl_random_pseudo_bytes(64));
			$uid = $_POST['username'];
			$stmt = getUnameRow($uid, $conn);
			if ($stmt->rowCount() == 1) {
				$stmt = $conn->prepare("UPDATE users SET perm = ? WHERE uname = ?;");
				$stmt->bindValue(1, $perm, PDO::PARAM_INT);
				$stmt->bindValue(2, $uid, PDO::PARAM_STR);
				$stmt->execute();
			} else {
				$stmt = $conn->prepare("INSERT INTO users (auth_token, uname, perm) VALUES (?, ?, ?);");
				$stmt->bindValue(1, $auth_token, PDO::PARAM_STR);
				$stmt->bindValue(2, $uid, PDO::PARAM_STR);
				$stmt->bindValue(3, $perm, PDO::PARAM_INT);
				$stmt->execute();
			}


		}


		/*$stmt = $conn->prepare("INSERT INTO users (uname, auth_token, perm) VALUES (?, ?, ?)");
		$stmt->bindValue(1, $_POST['username'], PDO::PARAM_STR);
		$stmt->bindValue(2, $token, PDO::PARAM_STR);
		$stmt->bindValue(3, $_POST['permission'], PDO::PARAM_STR);
		try {
			$stmt->execute();
		} catch (PDOException $e) {
			echo "Connection failed: " . $e->getMessage();
		}
	}
	else
		echo "No username specified.";
	*/
		// $token = bin2hex(openssl_random_pseudo_bytes(64));
		//TODO: Update users permissions
		//Parameters: username=lbenedetto permission=1 auth=authtoken
		//Put the username and permission in the database if the authtoken is from an admin
		break;
	case 'GET':
		$conn = getConn();
		//$stmt = getUnameRow($_POST['username'], $conn);


		//TODO: Return from database with search
		//Parameters: searchByName=lbene auth=authtoken
		//Parameters: searchByPermissions=1 auth=authtoken
		//If the authtoken is from an admin, return a list of users and their permissions from the database
		//If both search is blank, return the first 50 results
		//If search is (for example) "a", then return the first 50 results that start with a
		//If search is (for example) 1, then return all users with permissions level 1 (feeders I think is what you set that as)
		break;
}


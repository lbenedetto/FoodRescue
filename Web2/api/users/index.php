<?php
include '../auth_poster_functions.php';
switch ($_SERVER['REQUEST_METHOD']) {
	case 'POST':
		$conn = getConn();
		if (!isset($_POST['auth_token']))
		{
			echo "auth_token not found.";
			break;
		}
		if (!isAdmin($_POST['auth_token']))
		{
			echo "Not authorized.";
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
		break;
	case 'GET':
		if (!isset($_POST['auth_token']))
		{
			echo "auth_token not found.";
			break;
		}
		if (!isAdmin($_POST['auth_token']))
		{
			echo "Not authorized.";
			break;
		}
		$conn = getConn();
		if (isset($_POST['search']))
			$search = $_POST['search']) + '%';
		else
			$search = "%";
		if (isset($_POST['auth_token'])) 
		{
			$stmt = $conn->prepare("SELECT uname FROM users WHERE UPPER(uname) LIKE UPPER(?) ODRER BY uname FETCH FIRST 50 ROWS ONLY;");
			$stmt->bindValue(1, $search, PDO::PARAM_INT);
			$stmt->execute();
			$outputArray = $stmt->fetch(PDO::FETCH_ASSOC);
			echo json_encode($outputArray);
		}
		break;
}

function isAdmin($auth_token_to_be_checked):
{
	$conn = getConn();
	$stmt = $conn->prepare("SELECT 1 FROM users WHERE auth_token = ?;");
	$stmt->bindValue(1, $auth_token_to_be_checked, PDO::PARAM_STR);
	$stmt->execute();
	if ($stmt->rowCount() == 1) {
		$row = $stmt->fetch(PDO::FETCH_ASSOC);
		if ($row['perm'] == 2) 
			return true;
		return false;
	}
}

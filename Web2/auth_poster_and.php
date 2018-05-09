<?php
echo "youre here.";
include '../../auth_poster_functions.php';

switch ($_SERVER['REQUEST_METHOD']) 
{
    case 'POST':
        /*if (isset($_POST['title']) && isset($_POST['body']) && isset($_POST['data']) && isset($_POST['auth'])) {
            echo "you sent a POST request\n";
            $conn = getConn();
            $stmt = $conn->prepare("SELECT * FROM users WHERE auth_token = ?");  // Look for uid
            $stmt->bindValue(1, $_POST['auth'], PDO::PARAM_STR);
            try {
				$stmt->execute();
			} catch (PDOException $e) {
				echo "Connection failed: " . $e->getMessage();
			}
            if ($stmt->rowCount() == 1) // Found the auth token
            {
                echo "Found auth token\n";
                $row = $stmt->fetch(PDO::FETCH_ASSOC);
                if ($row['feeder_perm'] == 1) {
                    sendRequest($_POST['title'], $_POST['body'], $_POST['data']);

                } else echo "Not allowed!";
                exit;
            }
            //sendRequest($_POST['title'], $_POST['body'], $_POST['data']);
            echo "Could not find auth token\n";
            exit;
        } else {
            header("Location: https://login.ewu.edu/cas/login?service=https://146.187.135.29/android/login");
        }
        break;*/
		if (isset($_POST['title'])&&isset($_POST['body'])&&isset($_POST['lat'])&&isset($_POST['lng'])&&isset($_POST['expiry'])&&isset($_POST['auth']))
		{
			echo "You got into the function.";
			$conn = getConn();
			$stmt = $conn->prepare("SELECT * FROM databasetest.users WHERE auth_token = ?");  // Look for uid
			$stmt->bindValue(1, $_POST['auth'], PDO::PARAM_STR);
			try
			{
				$stmt->execute();
			}
			catch (PDOException $e)
			{
				echo "Connection failed: " . $e->getMessage();
			}
			//$stmt->bindValue(1, '20ad646e676ea1ca339c71f5870ca79322c438f787f7dcc5f3d8e0dcfa6ba7ad831f580ad0b46d1343e82fbcbd205a37321c8226f36f3c78f44e104fd2ec2d52', PDO::PARAM_STR);
			echo "<br>  " .$stmt->rowCount() . "<br>";
			if ($stmt->rowCount() == 1) // Found the auth token
			{
				$row = $stmt->fetch(PDO::FETCH_ASSOC);
				if ($row['feeder_perm'] == 1)
				{
					echo "<br>sending  " .$_POST['title'] . "  " . $_POST['body'] . "  " . $_POST['data'] . "<br>";
					sendDatamessage($_POST['title'], $_POST['body'], $_POST['lat'], $_POST['lng'], $_POST['expiry']);
					
				}
				else echo "Not allowed!";
			}
			else
			{
				echo "<br> auth token not found... <br>";
			//sendRequest($_POST['title'], $_POST['body'], $_POST['data']);
			}
		}
		else
		{
			header("Location: https://login.ewu.edu/cas/login?service=https://146.187.135.29/android/login");
		}
		break;
    case 'GET':
        echo "you sent a GET\n";
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
            header("Location: https://login.ewu.edu/cas/login?service=https://146.187.135.29/android/login");
            exit;
        }
        break;
    default:
        header("HTTP/1.0 405 MethodNotAllowed");
        exit;
        break;
}
?>
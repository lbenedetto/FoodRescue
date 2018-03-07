<!-- tester file - not for production -->
<!-- Author: Brad Stephenson -->
<?php
define('API_ACCESS_KEY','REDACTED');

/*
	if(isset($_POST['submit']))   // from /shareurcodes.com
	{
		$name = $_POST['name'];
		
		$url = "http://localhost/projectname/api/".$name;
		
		$client = curl_init($url);
		curl_setopt($client,CURLOPT_RETURNTRANSFER,true);
		$response = curl_exec($client);
		*/
switch($_SERVER['REQUEST_METHOD'])
{
	case 'POST':
		if (isset($_POST['title'])&&isset($_POST['body'])&&isset($_POST['data']))
		{
			sendRequest($_POST['title'], $_POST['body'], $_POST['data']);
		}
		break;
	default:
		header("HTTP/1.0 405 MethodNotAllowed");
		break;
}

function sendRequest($title, $body, $data)
{
	$fcmUrl = 'https://fcm.googleapis.com/fcm/send';
	$token='REDACTED;
	
	$notification = 
	[
		'title' 		=>$title,
		'body' 			=>$body
    ];
	$extraNotificationData = 
	[
		"message" 		=>$notification,
		"moredata" 		=>$data
	];
	$fcmNotification = 
	[
        'to'			=>'/topics/edu.ewu.team1.foodrescue',
		//'topic'			=>'edu.ewu.team1.foodrescue',
        'notification'	=>$notification,
        'data'		 	=>$extraNotificationData
    ];
	$headers = 
	[
        'Authorization: key=' . API_ACCESS_KEY,
        'Content-Type: application/json'
    ];
	
	$ch = curl_init();
    curl_setopt($ch, CURLOPT_URL,$fcmUrl);
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
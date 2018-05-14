function subscribe() {
	var number = $("#subscribeBar").val();
	//Remove all non digit characters
	number = number.replace(/\D/g, '');
	//Phone numbers with area codes are 10 digits
	if (number.length === 10) {
		$.post("../api/subscribe/?number=" + number + "&auth_token=" + auth_token,
			data,
			function (data) {
				//TODO: Handle server response
			}
		);
	}
}
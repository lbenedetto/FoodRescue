//TODO: Get auth token
var auth;

function subscribe() {
	var number = $("#subscribeBar").val();
	//Remove all non digit characters
	number = text.replace(/\D/g, '');
	//Phone numbers with area codes are 10 digits
	if (number.length === 10) {
		$.post("api/subscribe/?number=" + number + "&auth=" + auth,
			data,
			function (data) {
				//TODO: Handle server response
			}
		);
	}
}
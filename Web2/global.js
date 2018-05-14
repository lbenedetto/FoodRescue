var auth_token = $("#auth_token").val();

function downloadApp() {
	document.getElementById('iframeAppDownloader').src = "../assets/FoodRescue-18.5.13.apk";
}

function signIn(){
	window.location.href = "https://login.ewu.edu/cas/login?service=https://146.187.135.29/api/login/web";
}
